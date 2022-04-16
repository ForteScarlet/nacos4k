package nacos4k.api.entity


/**
 *
 * Service.
 *
 * @see com.alibaba.nacos.api.naming.pojo.Service
 *
 * @author ForteScarlet
 */
public data class Service(
    public val name: String,
    public val protectThreshold: Float = 0.0F,
    public val appName: String,
    public val groupName: String,
    public val metadata: Map<String, String> = emptyMap()
)