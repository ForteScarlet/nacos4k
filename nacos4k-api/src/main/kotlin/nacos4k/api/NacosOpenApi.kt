package nacos4k.api

import io.ktor.client.*


/**
 * Nacos Open API.
 *
 * see [Nacos open-api](https://nacos.io/zh-cn/docs/open-api.html) 。
 *
 * @author ForteScarlet
 */
public interface NacosOpenApi<out Result> {

    /**
     * 发送请求，并得到响应结果。
     */
    public suspend fun request(client: HttpClient, server: String): Result


}

/**
 * [NacosOpenApi] 辅助的抽象类。
 */
public abstract class BaseNacosOpenApi<out Result> : NacosOpenApi<Result> {
    /**
     * api地址，例如 `/nacos/v1/ns/instance`.
     */
    protected abstract val apiPath: String


    /**
     * 会预处理api的请求路径。
     */
    override suspend fun request(client: HttpClient, server: String): Result {
        val url = buildString {
            append(server)
            val serverEnd = server.endsWith('/')
            val apiStart = apiPath.startsWith('/')
            when {
                serverEnd && apiStart -> {
                    append(apiPath.substring(1))
                }
                !serverEnd && !apiStart -> {
                    append('/').append(apiPath)
                }
                else -> {
                    append(apiPath)
                }
            }
        }

        return client.doRequest(url)
    }


    /**
     * 构建请求。
     *
     * @param url 完整的请求路径
     */
    protected abstract suspend fun HttpClient.doRequest(url: String): Result

}


