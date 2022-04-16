import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.extra
import java.util.Properties

fun ExtraPropertiesExtension.loadLocalProperties(project: Project) {

    val file = project.file("local.properties").takeIf { it.exists() } ?: return

    val prop = file.reader().use {
        Properties().apply { load(it) }
    }

    prop.stringPropertyNames().forEach { key ->
        val value = prop.getProperty(key)
        set(key, value)
        println("[Load Local Prop]: Load property '$key'")
    }


}

