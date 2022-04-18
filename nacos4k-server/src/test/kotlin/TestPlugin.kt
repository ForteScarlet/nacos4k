import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

val FooPlugin = createApplicationPlugin("FooPlugin") {
    val monitor = application.environment.monitor

    monitor.subscribe(ApplicationStarted) {
        println("1. ApplicationStarted")
    }
    monitor.subscribe(ApplicationStopPreparing) {
        println("2. ApplicationStopPreparing")
    }
    monitor.subscribe(ApplicationStopping) {
        println("3. ApplicationStopping")
    }
    monitor.subscribe(ApplicationStopped) {
        println("4. ApplicationStopped")
    }
}

@OptIn(DelicateCoroutinesApi::class)
val scope = CoroutineScope(newSingleThreadContext("shutdown"))

fun main() {
    val engine = embeddedServer(Netty, port = 12345) {
        install(FooPlugin)
    }

    engine.start(true)
}