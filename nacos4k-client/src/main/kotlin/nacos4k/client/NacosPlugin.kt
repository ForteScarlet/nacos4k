@file:Suppress("unused")

package nacos4k.client

import com.alibaba.nacos.api.naming.pojo.ServiceInfo
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

internal val nacosSyncRequest = AttributeKey<Unit>("NacosSyncRequest")

public class Nacos private constructor(
    private val config: Config
) {
    private var syncJob: Job? = null

    @KtorDsl
    public class Config {
        public var serverAddress: String = "http://127.0.0.1:8848"
    }


    private fun setupNacosRequest(client: HttpClient) {
        client.requestPipeline.intercept(HttpRequestPipeline.Before) {
            if (context.attributes.getOrNull(nacosSyncRequest) != null) {
                return@intercept
            }

            println("context.url.host: ${context.url.host}")
        }
    }

    private fun initSyncJob(scope: HttpClient): Job {
        return scope.launch {
            while (scope.isActive) {
                println("Sync nacos")
                sync(scope)
                delay(5000)
            }
        }
    }

    private suspend fun sync(client: HttpClient) {
        val response = client.get(config.serverAddress) {
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


    public companion object : HttpClientPlugin<Config, Nacos> {
        override val key: AttributeKey<Nacos> = AttributeKey("Nacos")

        override fun install(plugin: Nacos, scope: HttpClient) {
            plugin.initSyncJob(scope)
            plugin.setupNacosRequest(scope)
        }

        override fun prepare(block: Config.() -> Unit): Nacos {
            val config = Config().apply(block)
            return Nacos(config)
        }
    }
}


