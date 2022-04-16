import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.Properties

fun Project.loadLocalProperties() {
    val file = file("local.properties").takeIf { it.exists() } ?: return

    val prop = file.reader().use {
        Properties().apply { load(it) }
    }

    prop.stringPropertyNames().forEach { key ->
        val value = prop.getProperty(key)
        extra[key] = value
        println("[Load Local Prop]: Load property '$key'")
    }


}
