import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import nacos4k.server.NacosServer
import kotlin.concurrent.thread

fun main() {

    val server = embeddedServer(Netty, port = 10001) {
        install(NacosServer) {
            serviceName = "test"
            serverAddress = "http://localhost:8848"
        }

        routing {
            get {
                call.respond("Hello World")
            }
        }
    }

    server.start(wait = true)

    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        server.stop()
    })


}