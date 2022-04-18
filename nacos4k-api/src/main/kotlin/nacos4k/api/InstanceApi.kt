package nacos4k.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import nacos4k.api.entity.Instance
import nacos4k.api.entity.ServiceInfo


/**
 * 注册实例。
 * ```
 * POST /nacos/v1/ns/instance
 * ```
 *
 *
 * | 名称 |  类型 |  是否必选 |  描述 |
 * | --- | ---- | -------- | ---- |
 * | serviceName |  字符串 |  是 |  服务名 |
 * | ip |  字符串 |  是 |  服务实例IP |
 * | port |  int |  是 |  服务实例port |
 * | groupName |  字符串 |  否 |  分组名 |
 * | clusterName |  字符串 |  否 |  集群名称 |
 * | namespaceId |  字符串 |  否 |  命名空间ID |
 * | ephemeral |  boolean |  否 |  是否临时实例 |
 *
 */
public class RegisterInstance @JvmOverloads constructor(
    private val serviceName: String,
    private val ip: String,
    private val port: Int,
    private val groupName: String? = null,
    private val clusterName: String? = null,
    private val namespaceId: String? = null,
    private val ephemeral: Boolean? = null
) : BaseNacosOpenApi<String>() {
    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): String {
        return post(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("ip", ip)
        parameter("port", this@RegisterInstance.port)
        parameter("groupName", groupName)
        parameter("clusterName", clusterName)
        parameter("namespaceId", namespaceId)
        parameter("ephemeral", ephemeral)
    }

    public companion object {
        public const val API: String = "nacos/v1/ns/instance"
    }
}

/**
 * ## 注销实例.
 *
 * 删除服务下的一个实例。
 *
 * ```
 * DELETE /nacos/v1/ns/instance
 * ```
 *
 *
 * | 名称 | 类型 | 是否必选 | 描述 |
 * | --- | ---- | ------ | ---- |
 * | serviceName | 字符串 | 是 | 服务名 |
 * | ip | 字符串 | 是 | 服务实例IP |
 * | port | int | 是 | 服务实例port |
 * | groupName | 字符串 | 否 | 分组名 |
 * | clusterName | 字符串 | 否 | 集群名称 |
 * | namespaceId | 字符串 | 否 | 命名空间ID |
 * | ephemeral | boolean | 否 | 是否临时实例 |
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class DestroyInstance @JvmOverloads constructor(
    private val serviceName: String,
    private val ip: String,
    private val port: Int,
    private val groupName: String? = null,
    private val clusterName: String? = null,
    private val namespaceId: String? = null,
    private val ephemeral: Boolean? = null
) : BaseNacosOpenApi<String>() {
    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): String {
        return delete(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("ip", ip)
        parameter("port", this@DestroyInstance.port)
        parameter("groupName", groupName)
        parameter("clusterName", clusterName)
        parameter("namespaceId", namespaceId)
        parameter("ephemeral", ephemeral)
    }

    public companion object {
        public const val API: String = "nacos/v1/ns/instance"
    }
}


/**
 * ## 修改实例
 *
 * 修改服务下的一个实例。
 *
 * ```
 * PUT /nacos/v1/ns/instance
 * ```
 *
 *
 * | 名称 | 类型 | 是否必选 | 描述 |
 * | --- | ---- | ------ | ---- |
 * | serviceName | 字符串 | 是 | 服务名 |
 * | ip | 字符串 | 是 | 服务实例IP |
 * | port | int | 是 | 服务实例port |
 * | groupName | 字符串 | 否 | 分组名 |
 * | clusterName | 字符串 | 否 | 集群名称 |
 * | namespaceId | 字符串 | 否 | 命名空间ID |
 * | weight | double | 否 | 权重 |
 * | metadata | JSON | 否 | 扩展信息 |
 * | enabled | boolean | 否 | 是否打开流量 |
 * | ephemeral | boolean | 否 | 是否临时实例 |
 *
 */
public class EditInstance @JvmOverloads constructor(
    private val serviceName: String,
    private val ip: String,
    private val port: Int,
    private val groupName: String? = null,
    private val clusterName: String? = null,
    private val namespaceId: String? = null,

    private val weight: Double? = null,
    /**
     * metadata 的 json字符串.
     */
    private val metadata: String? = null,
    private val enabled: String? = null,

    private val ephemeral: Boolean? = null
) : BaseNacosOpenApi<String>() {
    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): String {
        return put(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("ip", ip)
        parameter("port", this@EditInstance.port)
        parameter("groupName", groupName)
        parameter("clusterName", clusterName)
        parameter("namespaceId", namespaceId)
        parameter("weight", weight)
        parameter("metadata", metadata)
        parameter("enabled", enabled)
        parameter("ephemeral", ephemeral)
    }


    public companion object {
        public const val API: String = "nacos/v1/ns/instance"
    }
}


/**
 * ## 查询实例列表
 *
 * 查询服务下的实例列表
 *
 * ```
 * GET /nacos/v1/ns/instance/list
 * ```
 *
 *
 *
 *
 * ## 请求参数
 * | 名称 | 类型 |  是否必选 | 描述 |
 * | --- | ---- | ------ | ---- |
 * | serviceName | 字符串 | 是 | 服务名 |
 * | groupName | 字符串 | 否 | 分组名 |
 * | namespaceId | 字符串 | 否 | 命名空间ID |
 * | clusters | 字符串 多个集群用逗号分隔 | 否 | 集群名称 |
 * | healthyOnly | boolean |  否 默认为false | 是否只返回健康实例 |
 *
 *
 */
public class InstanceList @JvmOverloads constructor(
    private val serviceName: String,
    private val groupName: String? = null,
    private val namespaceId: String? = null,
    private val clusters: List<String> = emptyList(),
    private val healthyOnly: Boolean? = null,
) : BaseNacosOpenApi<ServiceInfo>() {

    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): ServiceInfo {
        return get(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("groupName", groupName)
        parameter("namespaceId", namespaceId)
        parameter("clusters", clusters.joinToString(",").ifEmpty { null })
        parameter("healthyOnly", healthyOnly)
    }

    public companion object {
        public const val API: String = "/nacos/v1/ns/instance/list"
    }
}


/**
 * ## 查询实例详情
 *
 * 查询一个服务下个某个实例详情。
 *
 * ```
 * GET /nacos/v1/ns/instance
 * ```
 *
 * | 名称 |  类型 | 是否必选 | 描述 |
 * | --- | ---- | ------ | ---- |
 * | serviceName |  字符串 | 是 | 服务名 |
 * | ip |  字符串 | 是 | 实例IP |
 * | port |  字符串 | 是 | 实例端口 |
 * | groupName |  字符串 | 否 | 分组名 |
 * | namespaceId |  字符串 | 否 | 命名空间ID |
 * | cluster |  字符串 | 否 | 集群名称 |
 * | healthyOnly |  boolean | 否，默认为false | 是否只返回健康实例 |
 * | ephemeral |  boolean | 否 | 是否临时实例 |
 *
 *
 *
 *
 *
 */
public class GetInstance(
    private val serviceName: String,
    private val ip: String,
    private val port: Int,
    private val groupName: String? = null,
    private val namespaceId: String? = null,
    private val clusters: List<String> = emptyList(),
    private val healthyOnly: Boolean? = null,
    private val ephemeral: Boolean? = null
) : BaseNacosOpenApi<Instance>() {

    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): Instance {
        return get(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("ip", ip)
        parameter("port", this@GetInstance.port)
        parameter("groupName", groupName)
        parameter("namespaceId", namespaceId)
        parameter("clusters", clusters.joinToString(",").ifEmpty { null })
        parameter("healthyOnly", healthyOnly)
        parameter("ephemeral", ephemeral)
    }

    public companion object {
        public const val API: String = "/nacos/v1/ns/instance"
    }
}


/**
 * ## 发送实例心跳
 *
 * 发送某个实例的心跳
 *
 * ```
 * PUT /nacos/v1/ns/instance/beat
 * ```
 *
 *## 请求参数
 *
 * |名称 | 类型 | 是否必选 | 描述 |
 * | --- | ---- | ------ | ---- |
 * | serviceName | 字符串 | 是 | 服务名 |
 * | beat | JSON格式字符串 | 是 | 实例心跳内容 |
 * | groupName | 字符串 | 否 | 分组名 |
 * | ephemeral | boolean | 否 | 是否临时实例 |
 *
 *
 *
 */
public class InstanceBeat(
    private val serviceName: String,
    private val beat: String,
    private val groupName: String? = null,
    private val ephemeral: Boolean? = null
) : BaseNacosOpenApi<String>() {
    override val apiPath: String
        get() = API

    override suspend fun HttpClient.doRequest(url: String): String {
        return put(url) {
            appendParameters()
        }.body()
    }

    private fun HttpRequestBuilder.appendParameters() {
        parameter("serviceName", serviceName)
        parameter("beat", beat)
        parameter("groupName", groupName)
        parameter("ephemeral", ephemeral)
    }

    override fun toString(): String = "InstanceBeat(serviceName=$serviceName, beat=$beat, groupName=$groupName, ephemeral=$ephemeral)"

    public companion object {
        public const val API: String = "/nacos/v1/ns/instance/beat"
    }
}


















