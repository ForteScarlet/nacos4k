import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nacos4k.server.NacosServer

suspend fun main() {

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

    GlobalScope.launch {
        delay(5000)
        server.stop()
    }

    server.start(wait = true)

}