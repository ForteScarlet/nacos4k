package nacos4k.api.entity


/**
 * 一个服务实例 （的响应结果）
 *
 * @see com.alibaba.nacos.api.naming.pojo.Instance
 * @author ForteScarlet
 */
public data class Instance(
    /**
     * unique id of this instance.
     */
    public val instanceId: String,
    /**
     * instance ip.
     */
    public val ip: String,
    /**
     * instance port.
     */
    public val port: Int,
    /**
     * instance weight.
     */
    public val weight: Double = 1.0,
    /**
     * instance health status.
     */
    private val healthy: Boolean = true,
    /**
     * If instance is enabled to accept request.
     */
    private val enabled: Boolean = true,
    /**
     * If instance is ephemeral.
     */
    private val ephemeral: Boolean = true,
    /**
     * cluster information of instance.
     */
    public val clusterName: String,
    /**
     * Service information of instance.
     */
    public val serviceName: String,
    /**
     * user extended attributes.
     */
    public val metadata: Map<String, String> = mapOf(),

) {
    /**
     * instance health status.
     */
    public val isHealthy: Boolean get() = healthy
    /**
     * If instance is enabled to accept request.
     */
    public val isEnabled: Boolean get() = enabled
    /**
     * If instance is ephemeral.
     */
    public val isEphemeral: Boolean get() = ephemeral

}
