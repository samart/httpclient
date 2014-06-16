package org.test.web

import groovy.json.JsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.platform.Verticle

/**
 * Created by samart on 6/14/14.
 */
class HttpClientVerticle extends Verticle {

    private static Logger log = LoggerFactory.getLogger(HttpClientVerticle.class)


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

            def requestId = UUID.randomUUID().toString()
            log.info("Business logic is executing...")

            try {
                String externalServiceResponse = clientApi.sendHttpPost(requestId, new JsonBuilder([request: "hello"]).toString())

                message.reply(externalServiceResponse)
            } catch (Exception e) {
                log.error(e.toString(), e)
            }


        }

        vertx.createHttpServer().requestHandler(route.asClosure()).listen(9903)


    }
}
