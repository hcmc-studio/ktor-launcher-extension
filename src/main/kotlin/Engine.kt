import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import studio.hcmc.kotlin.protocol.io.ErrorDataTransferObject
import studio.hcmc.ktor.plugin.*
import studio.hcmc.ktor.routing.respondError

object Engine {
    class Builder private constructor(
        var port: Int,
        var nettyApplicationEngineConfiguration: NettyApplicationEngine.Configuration.() -> Unit = {},
        var jsonContentNegotiationConfiguration: JsonContentNegotiationConfiguration.() -> Unit = {},
        var doubleReceiveConfiguration: DoubleReceiveConfig.() -> Unit = {},
        var requestLoggingConfiguration: RequestLoggingConfiguration.() -> Unit = {},
        var responseLoggingConfiguration: ResponseLoggingConfiguration.() -> Unit = {},
        var statusPagesConfiguration: StatusPagesConfig.() -> Unit = {},
        var routingConfiguration: Routing.() -> Unit = {},
        var moduleConfiguration: Application.() -> Unit = {}
    ) {
        companion object {
            operator fun invoke(port: Int, configure: Builder.() -> Unit): NettyApplicationEngine {
                return Builder(port)
                    .apply(configure)
                    .build()
            }
        }

        private fun build(): NettyApplicationEngine {
            return embeddedServer(
                factory = Netty,
                port = port,
                host = "0.0.0.0",
                configure = { configure() },
                module = { module() }
            )
        }

        private fun NettyApplicationEngine.Configuration.configure() {
            requestQueueLimit = Int.MAX_VALUE
            responseWriteTimeoutSeconds = Int.MAX_VALUE
            nettyApplicationEngineConfiguration()
        }

        private fun Application.module() {
            install(JsonContentNegotiation) {
                jsonContentNegotiationConfiguration()
            }

            install(AcceptedAt) {

            }

            install(AutoHeadResponse) {

            }

            install(DoubleReceive) {
                doubleReceiveConfiguration()
            }

            install(RequestLogging) {
                requestLoggingConfiguration()
            }

            install(ResponseLogging) {
                responseLoggingConfiguration()
            }

            install(StatusPages) {
                statusPagesConfiguration()

                exception<ErrorDataTransferObject> { call, throwable ->
                    call.respondError(HttpStatusCode.fromValue(throwable.httpStatusCode), throwable)
                }

                exception<Throwable> { call, throwable ->
                    call.respondError(HttpStatusCode.InternalServerError, throwable)
                }
            }

            routing(routingConfiguration)

            moduleConfiguration()
        }
    }
}