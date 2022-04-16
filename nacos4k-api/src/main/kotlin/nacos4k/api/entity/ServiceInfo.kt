package nacos4k.api.entity


/**
 * 服务信息。
 *
 * @see com.alibaba.nacos.api.naming.pojo.ServiceInfo
 * @author ForteScarlet
 */
public data class ServiceInfo(
    public val name: String,
    public val groupName: String,
    public val clusters: String,
    public val cacheMillis: Long = 1000,
    public val hosts: List<Instance> = emptyList(),
    public val lastRefTime: Long = 0,
    public val checksum: String = "",
    @Transient
    private val allIps: Boolean = false,
    @Transient
    private val reachProtectionThreshold: Boolean = false,
) {
    public val isAllIps: Boolean get() = allIps
    public val isReachProtectionThreshold: Boolean get() = reachProtectionThreshold

}


/**
 * Judge whether service info is validate.
 *
 * @return true if validate, otherwise false
 */
public fun ServiceInfo.validate(): Boolean {
    if (isAllIps) return true


    return hosts.any { it.isHealthy && it.weight > 0 }
}
