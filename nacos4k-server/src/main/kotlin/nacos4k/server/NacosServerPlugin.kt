package nacos4k.server

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import nacos4k.api.InstanceBeat
import nacos4k.api.RegisterInstance
import org.slf4j.LoggerFactory
import java.net.InetAddress

/**
 * Nacos服务端插件。
 *
 */
public val NacosServer: ApplicationPlugin<NacosServerConfig> = createApplicationPlugin(
    name = "NacosServer",
    createConfiguration = ::NacosServerConfig
) {
    val logger = LoggerFactory.getLogger("nacos4k.server")

    var beatJob: Job? = null

    val appPort = application.environment.config.port
    val port = pluginConfig.port.takeIf { it > 0 } ?: appPort
    val serverAddress = pluginConfig.serverAddress

    val serviceName = pluginConfig.serviceName
    val ip = pluginConfig.ip ?: currentIp()

    val groupName = pluginConfig.groupName
    val clusterName = pluginConfig.clusterName
    val namespaceId = pluginConfig.namespaceId
    val ephemeral = pluginConfig.ephemeral

    val beatInterval = pluginConfig.beatInterval


    on(MonitoringEvent(ApplicationStarted)) { application ->

        val register = RegisterInstance(
            serviceName = serviceName,
            ip = ip,
            port = port,
            groupName = groupName,
            clusterName = clusterName,
            namespaceId = namespaceId,
            ephemeral = ephemeral,
        )

        logger.debug(
            "Registering service... serviceName={}, ip={}, port={}, groupName={}, namespaceId={}, ephemeral={}",
            serviceName,
            ip,
            port,
            groupName,
            namespaceId,
            ephemeral
        )

        val client = pluginConfig.client ?: HttpClient().also {
            logger.warn("HttpClient is not configured, will use `HttpClient()` ")
        }


        application.launch {
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


    on(MonitoringEvent(ApplicationStopped)) {
        println("Server is stopped")
        beatJob?.cancel()
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