package ch.keepcalm.demo.rsocket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream


@SpringBootApplication
class RsocketProducer

fun main(args: Array<String>) {
    runApplication<RsocketProducer>(*args)
}

data class GreetingsRequest(val name: String?)
class GreetingsResponse(greetings: String?) {
    var greetings: String = "Hello $greetings @${Instant.now()}"
        private set
        get
}

@Controller
class GreetingsRSocketController {

    var serviceHitCounter = AtomicInteger(1)

    @MessageMapping(value = ["greet-stream"])
    fun greetStream(request: GreetingsRequest) =
            Flux.fromStream(
                    Stream.generate<Any> {
                        GreetingsResponse(request.name + " ${serviceHitCounter.getAndIncrement()}")
                    }
            ).delayElements(Duration.ofSeconds(1))

    @MessageMapping(value = ["greet"])
    fun greet(request: GreetingsRequest): GreetingsResponse = GreetingsResponse(request.name)

}
