package nacos4k.server

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import kotlinx.coroutines.*
import nacos4k.api.DestroyInstance
import nacos4k.api.InstanceBeat
import nacos4k.api.RegisterInstance
import org.slf4j.LoggerFactory
import java.net.InetAddress

private val logger = LoggerFactory.getLogger("nacos4k.server")

/**
 * Nacos服务端插件。
 *
 */
public val NacosServer: ApplicationPlugin<NacosServerConfig> = createApplicationPlugin(
    name = "NacosServer",
    createConfiguration = ::NacosServerConfig
) {
    var beatJob: Job? = null

    val appPort = application.environment.config.port

    val configPort = pluginConfig.port

    fun getPort(): Int {
        if (configPort > 0) {
            return configPort
        }

        logger.debug("Plugin config's port <= 0, Try to get the port number automatically.")

        // try to get the engine port.
        // cannot use application.environment, for the reason see https://youtrack.jetbrains.com/issue/KTOR-4176
        val environment = application.environment
        val isApplicationEngineEnvironmentReloading = kotlin.runCatching {
            environment is io.ktor.server.engine.ApplicationEngineEnvironmentReloading
        }

        return if (isApplicationEngineEnvironmentReloading.getOrElse { false }) {
            environment as io.ktor.server.engine.ApplicationEngineEnvironmentReloading
            environment.connectors.first().port
        } else {
            val exception = isApplicationEngineEnvironmentReloading.exceptionOrNull()

            if (exception == null) {
                logger.warn(
                    "application.environment !is ApplicationEngineEnvironmentReloading. Try using application.environment.config.port: {}",
                    appPort
                )
            } else {
                logger.warn(
                    "application.environment !is ApplicationEngineEnvironmentReloading, the reason: {}. Try using application.environment.config.port: {}",
                    exception,
                    appPort
                )
            }

            appPort
        }
    }

    val port by lazy(::getPort)

    val serverAddress = pluginConfig.serverAddress

    val serviceName = pluginConfig.serviceName
    val ip = pluginConfig.ip ?: currentIp()

    val groupName = pluginConfig.groupName
    val clusterName = pluginConfig.clusterName
    val namespaceId = pluginConfig.namespaceId
    val ephemeral = pluginConfig.ephemeral ?: run {
        if (application.developmentMode) {
            logger.debug("application.developmentMode is true, and no pluginConfig.ephemeral is configured, ephemeral will be true.")
            true
        } else null
    }

    val beatInterval = pluginConfig.beatInterval


    val client = pluginConfig.client ?: HttpClient().also {
        logger.warn("HttpClient is not configured, will use `HttpClient()` ")
    }

    on(MonitoringEvent(ApplicationStarted)) { application ->
        logger.debug(
            "Registering service... serviceName={}, ip={}, port={}, groupName={}, namespaceId={}, ephemeral={}",
            serviceName,
            ip,
            port,
            groupName,
            namespaceId,
            ephemeral
        )

        application.launch {
            val register = RegisterInstance(
                serviceName = serviceName,
                ip = ip,
                port = port,
                groupName = groupName,
                clusterName = clusterName,
                namespaceId = namespaceId,
                ephemeral = ephemeral,
            )

            try {
                // request
                val registered = register.request(client, serverAddress)
                logger.info("Register current service {} to nacos success. response: {}", serviceName, registered)

                val beat = InstanceBeat(
                    serviceName = serviceName,
                    beat = """{"ip": "$ip", "port": $port, "serviceName": "$serviceName"}""",
                    groupName = groupName,
                    ephemeral = ephemeral,
                )


                logger.debug("Start beat job, beatInterval={}", beatInterval)

                beatJob = application.launch {
                    while (isActive) {
                        val beatResp = beat.request(client, serverAddress)
                        logger.debug("beat response: {}. next delay: {}", beatResp, beatInterval)
                        delay(beatInterval)
                    }
                }
            } catch (e: Exception) {
                logger.error("Register current service $serviceName to nacos failure.", e)
                application.dispose()
            }
        }
    }

    on(MonitoringEvent(ApplicationStopping)) {
        val destroy = DestroyInstance(
            serviceName = serviceName,
            ip = ip,
            port = port,
            groupName = groupName,
            clusterName = clusterName,
            namespaceId = namespaceId,
            ephemeral = ephemeral,
        )

        logger.info("Server is stopping, De-register nacos service instance...")
        beatJob?.cancel()
        beatJob = null
        runBlocking {
            kotlin.runCatching {
                val result = destroy.request(client, serverAddress)
                logger.info("Nacos destroy result: {}", result)
            }.getOrElse { e ->
                logger.error("Nacos destroy request failure: $e", e)
            }
        }
    }


}


public class NacosServerConfig {
    public var serverAddress: String = "http://localhost:8848"
    public var ip: String? = null
    public var port: Int = -1

    public lateinit var serviceName: String
    public var client: HttpClient? = null
    public var groupName: String? = null
    public var clusterName: String? = null
    public var namespaceId: String? = null
    public var ephemeral: Boolean? = null

    /**
     * 发送心跳请求的周期。
     */
    public var beatInterval: Long = 5000L
}


private fun currentIp(): String {
    return InetAddress.getLocalHost().hostAddress
}