package org.test.web

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.platform.Verticle

/**
 * Created by samart on 6/14/14.
 */
@Slf4j
class HttpClientVerticle extends Verticle {


    static ClientApi clientApi

    def start() {

        log.info("Starting client verticle")

        if (!clientApi) {
            clientApi = new ClientApi()
        }

        def route = new RouteMatcher()

        route.get("/api/doPost") { HttpServerRequest request ->
             vertx.eventBus.send("businessLogic", "{}") { Message message ->

                 request.response.end(message.body().toString())


             }


        }

        vertx.eventBus.registerHandler("businessLogic") { Message message ->

            log.info("Business logic is executing...")

            try {
                String externalServiceResponse = clientApi.sendHttpPost(new JsonBuilder([request:"hello"]).toString())

                message.reply(externalServiceResponse)
            } catch (Exception e) {
                log.error(e.toString(), e)
            }


        }

        vertx.createHttpServer().requestHandler(route.asClosure()).listen(9903)


    }
}
