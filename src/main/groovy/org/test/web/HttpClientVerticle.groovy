package org.test.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.core.http.HttpClient
import org.vertx.groovy.core.http.HttpClientResponse
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.platform.Verticle

import java.util.concurrent.TimeUnit

/**
 * Created by samart on 6/14/14.
 */
class HttpClientVerticle extends Verticle {

    private static Logger log = LoggerFactory.getLogger(HttpClientVerticle.class)


    HttpClient httpClient


    def start() {

        log.info("Starting  client verticle")



        httpClient = Vertx.newVertx().createHttpClient()
        httpClient.setKeepAlive(true)
        httpClient.setMaxPoolSize(20)
        httpClient.host = 'localhost'
        httpClient.port = ExternalService.PORT
        // and ssl stuff..
        def route = new RouteMatcher()

        route.get("/api/doPost") { HttpServerRequest request ->
            vertx.eventBus.send("businessLogic", "{}") { Message message ->

                request.response.end(message.body().toString())


            }


        }

        vertx.eventBus.registerHandler("businessLogic") { Message message ->

            def requestId = UUID.randomUUID().toString()
            log.info("Business logic is executing...")

            def rawResponse = null

            httpClient.post("/external/service/$requestId") {
                HttpClientResponse response ->

                    log.info("GOT POST RESPONSE for $requestId WITH STATUS $response.statusCode.")
                    if (response.statusCode == 200) {
                        response.bodyHandler { buffer ->
                            rawResponse = buffer.toString()
                            log.info(" ======  SUCCESS - we got the response for $requestId in the client  =======")
                            message.reply(rawResponse)

                        }

                    } else {
                        log.error("got unexpected response  for $requestId with status $response.statusCode")
                      }

            }.putHeader("Cookie", "AUTH_TOKEN=SJKJSKJSKJKSJKJSKJSKJSKJSKJSKJS").end('{"payload":"the payload"}')

        }

        vertx.createHttpServer().requestHandler(route.asClosure()).listen(9903)
        log.info "Client verticle started"


    }
}
