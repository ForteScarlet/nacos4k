@file:Suppress("unused")

package nacos4k.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import nacos4k.api.InstanceList
import nacos4k.api.entity.Instance
import nacos4k.api.entity.ServiceInfo
import org.slf4j.LoggerFactory

internal val nacosSyncRequest = AttributeKey<Unit>("NacosSyncRequest")

/**
 * Nacos client 插件。
 */
public class NacosClient private constructor(
    private val serverAddress: String,
    private val groupName: String?,
    private val namespaceId: String?,
    private val clusters: List<String>,
    private val healthyOnly: Boolean,

    private val hostSelector: NacosHostSelector,
    private val syncClient: HttpClient?,
) {
    private var syncJob: Job? = null

    @KtorDsl
    public class Config {
        public var serverAddress: String = "http://127.0.0.1:8848"
        public var groupName: String? = null
        public var namespaceId: String? = null
        public var clusters: List<String> = emptyList()
        public var healthyOnly: Boolean = false

        public var syncClient: HttpClient? = null
        public var hostSelector: NacosHostSelector = NacosHostSelector.Random
    }


    private fun setupNacosRequest(client: HttpClient) {
        client.requestPipeline.intercept(HttpRequestPipeline.Before) {
            if (context.attributes.getOrNull(nacosSyncRequest) != null) {
                return@intercept
            }


            val host = context.url.host
            logger.debug("context.url.host: {}", host)
            val foundInstance = client.findServiceHost(host) ?: throw NoSuchElementException("Host for service '$host'")
            logger.debug("Found instance: {}", foundInstance)
            context.url.host = foundInstance.ip
        }
    }

    private suspend fun HttpClient.findServiceHost(serviceName: String): Instance? {
        val serviceInfoApi = InstanceList(
            serviceName = serviceName,
            groupName,
            namespaceId,
            clusters,
            healthyOnly
        )

        val serviceInfo = serviceInfoApi.request(this, serverAddress)
        return hostSelector(serviceInfo)
    }


    private fun initSyncJob(scope: HttpClient): Job {
        // TODO
        return scope.launch {
            while (scope.isActive) {
                logger.debug("Sync nacos")
                sync(scope)
                delay(5000)
            }
        }
    }

    private suspend fun sync(client: HttpClient) {
        // TODO
        val response = client.get(serverAddress) {
            url {
                path("nacos", "v1", "ns", "instance", "list")
                parameter("serviceName", "gateway")
            }
            attributes.put(nacosSyncRequest, Unit)
        }


        try {
            val body = response.body<ServiceInfo>()
            println(body)
            println(body.hosts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    public companion object : HttpClientPlugin<Config, NacosClient> {
        private val logger = LoggerFactory.getLogger("nacos4k.client")
        override val key: AttributeKey<NacosClient> = AttributeKey("NacosClient")

        override fun install(plugin: NacosClient, scope: HttpClient) {
            // plugin.initSyncJob(plugin.syncClient ?: scope)
            plugin.setupNacosRequest(scope)
        }

        override fun prepare(block: Config.() -> Unit): NacosClient {
            val config = Config().apply(block)
            return NacosClient(
                serverAddress = config.serverAddress,
                groupName = config.groupName,
                namespaceId = config.namespaceId,
                clusters = config.clusters,
                healthyOnly = config.healthyOnly,
                hostSelector = config.hostSelector,
                syncClient = config.syncClient,
            )
        }
    }
}


/**
 * 服务选择器。
 */
public fun interface NacosHostSelector : (ServiceInfo) -> Instance? {


    public object Random : NacosHostSelector {
        override fun invoke(serviceInfo: ServiceInfo): Instance? {
            return serviceInfo.hosts.randomOrNull()
        }

    }


}
