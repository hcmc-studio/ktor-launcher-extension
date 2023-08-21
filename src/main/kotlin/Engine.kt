import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import studio.hcmc.ktor.plugin.AcceptedAt
import studio.hcmc.ktor.plugin.RequestLogging

object Engine {
    fun start(
        port: UShort,
        json: Json = Json
    ): NettyApplicationEngine {
        val engine = embeddedServer(
            factory = Netty,
            port = port.toInt(),
            host = "0.0.0.0",
            configure = {
                requestQueueLimit = Int.MAX_VALUE
                responseWriteTimeoutSeconds = Int.MAX_VALUE
            },
            module = {
                configureSerialization(json)
            }
        )

        return engine
    }

    class Builder(
        var port: UShort,
        var jsonBuilder: JsonBuilder.() -> Unit = {},
        var contentNegotiationConfiguration: ContentNegotiationConfig.() -> Unit = {},
        var doubleReceiveConfiguration: DoubleReceiveConfig.() -> Unit = {}
        ) {
        companion object {
            operator fun invoke(port: UShort, configure: Builder.() -> Unit): NettyApplicationEngine {
                return Builder(port)
                    .apply(configure)
                    .build()
            }
        }

        private fun build(): NettyApplicationEngine {
            return embeddedServer(
                factory = Netty,
                port = port.toInt(),
                host = "0.0.0.0",
                configure = {
                    requestQueueLimit = Int.MAX_VALUE
                    responseWriteTimeoutSeconds = Int.MAX_VALUE
                },
                module = { module() }
            )
        }

        private fun Application.module() {
            val json = Json {
                jsonBuilder()
            }

            attributes.put(serializerKey, json)

            install(ContentNegotiation) {
                json(json)
                contentNegotiationConfiguration()
            }

            install(AcceptedAt) {

            }

            install(AutoHeadResponse) {

            }

            install(DoubleReceive) {
                doubleReceiveConfiguration()
            }

            install(RequestLogging) {

            }
        }
    }
}