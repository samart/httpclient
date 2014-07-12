package org.test.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.json.JsonObject

/**
 * Created by samart on 6/14/14.
 */
class ExternalService extends Verticle {

    private static Logger log = LoggerFactory.getLogger(ExternalService.class)


    static final int PORT = 8888

    def start() {

        log.info("Staring server verticle")

        def router = new RouteMatcher()

        router.post("/external/service/:requestId") { HttpServerRequest request ->

            println request.getHeaders().get("Cookie")
            Map.Entry<String,String> cookie = request.getHeaders().entries.find { Map.Entry<String, String> entry ->
                entry.key == 'Cookie'
            }

            println "cookie $cookie"
            def paramsMap = [:]
            request.params.names.each {
                paramsMap.put(it, request.params.get(it))
            }
            println paramsMap
            request.bodyHandler { Buffer buffer ->
                println buffer.toString()
            }

            def id = request.params.get("requestId")
            def response = new JsonObject([response: [status: "Success", message: ("External service post response for request $id" as String)]]).toString()
            log.info("External service is responding to post request $id with $response")
            request.response.end(response)
            log.info("Sent response to request $id with $response")




        }
        vertx.createHttpServer().requestHandler(router.asClosure()).listen(PORT)



    }
}
