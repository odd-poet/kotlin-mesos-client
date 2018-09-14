package net.oddpoet.mesos.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.oddpoet.mesos.Scheduler
import net.oddpoet.mesos.SchedulerDriver
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder.post
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author mitchell.geek
 */

class HttpBasedSchedulerDriver : SchedulerDriver {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val http = HttpClients.custom()
            .build()

    override fun start(scheduler: Scheduler) {
        val subscribe = Mesos.SubscribeMessage.of("root", "example")
        http.execute(
                post("http://127.0.0.1:5050/api/v1/scheduler")
                        .setEntity(JsonEntity(subscribe))
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .build()
                        .loggingNSelf()
        ) { response ->
            log.debug("response: {}", response.statusLine)
            response.allHeaders.forEach {
                log.debug(" {}: {}", it.name, it.value)
            }

            RecordIO(response.entity.content).forEach { data ->
                log.debug("event: {}", String(data))
            }
        }
    }


    class JsonEntity<T : Any>(data: T) : StringEntity(toJson(data), ContentType.APPLICATION_JSON) {

        companion object {
            private val mapper = ObjectMapper().apply {
                registerModule(KotlinModule())
            }

            private fun <T : Any> toJson(data: T): String {
                return mapper.writeValueAsString(data)
            }
        }
    }

    fun HttpUriRequest.loggingNSelf(): HttpUriRequest {
        log.debug("request: {}", this)
        log.debug("header: {}", this.allHeaders)
        return this
    }

}


interface Mesos {

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    data class Subscribe(val frameworkInfo: FrameworkInfo)

    interface Typed {
        val type: String
    }

    data class FrameworkInfo(
            val user: String,
            val name: String)


    data class SubscribeMessage(
            val subscribe: Subscribe) : Typed {
        override val type: String = "SUBSCRIBE"

        companion object {
            fun of(user: String, name: String): SubscribeMessage {
                return SubscribeMessage(
                        Subscribe(
                                FrameworkInfo(user, name)
                        )
                )
            }
        }
    }

}