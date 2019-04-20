package ch.keepcalm.demo.rsocket

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.transport.netty.client.TcpClientTransport
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.add
import org.springframework.http.MediaType
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class RsocketConsumer {
    @Bean
    fun rSocket(): RSocket? = RSocketFactory
            .connect()
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
            .frameDecoder(PayloadDecoder.ZERO_COPY)
            .transport(TcpClientTransport.create(7000))
            .start()
            .block()

    // Client
    @Bean
    fun requester(rSocketStrategies: RSocketStrategies): RSocketRequester =
            RSocketRequester.create(rSocket()!!,
                    MimeTypeUtils.APPLICATION_JSON, rSocketStrategies)

}

fun main(args: Array<String>) {
    runApplication<RsocketConsumer>(*args)
}

@RestController
class GreetingsRestController(var requester: RSocketRequester) {

    @GetMapping(value = ["/greet/sse/{name}"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun greetStream(@PathVariable name: String?): Publisher<GreetingsResponse> = name.let {
        requester
                .route("greet-stream")
                .data(GreetingsRequest(name = name!!))
                .retrieveFlux(GreetingsResponse::class.java)
    }

    @GetMapping(value = ["/greet/{name}"])
    fun greet(@PathVariable name: String?): Publisher<GreetingsResponse> = name.let {
        requester
                .route("greet")
                .data(GreetingsRequest(name = name!!))
                .retrieveMono(GreetingsResponse::class.java)
    }
}


data class GreetingsRequest(val name: String)
data class GreetingsResponse(val greetings: String)


//   _   _    _    _
//  | | | |  / \  | |
//  | |_| | / _ \ | |
//  |  _  |/ ___ \| |___
//  |_| |_/_/   \_\_____|
//
open class Index : RepresentationModel<Index>()
@RestController
@RequestMapping(value = ["/"], produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class IndexController {

    @GetMapping
    fun api(): Index = Index()
            .apply {
                add(IndexController::class) {
                    linkTo { api() } withRel IanaLinkRelations.SELF
                }
                add(GreetingsRestController::class) {
                    linkTo { greet(name = null) } withRel ("greet")
                }
                add(GreetingsRestController::class) {
                    linkTo { greetStream(name = null) } withRel ("greet-stream")
                }
            }
}
