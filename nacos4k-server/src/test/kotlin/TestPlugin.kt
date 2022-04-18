import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

val FooPlugin = createApplicationPlugin("FooPlugin") {
    println("applicationConfig?.port: ${applicationConfig?.port}")

    on(MonitoringEvent(ApplicationStarted)) { application ->
        println("ApplicationStarted: application.environment.config.port: ${application.environment.config.port}")
    }
}


fun main() {
    embeddedServer(Netty, port = 12345) {
        install(FooPlugin)
    }.start(wait = true)
}