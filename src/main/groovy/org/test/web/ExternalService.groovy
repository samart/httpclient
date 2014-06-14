package org.test.web

import groovy.util.logging.Slf4j
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.json.JsonObject

/**
 * Created by samart on 6/14/14.
 */
@Slf4j
class ExternalService extends Verticle {

    static final int PORT = 8888

    def start() {

        log.info("Staring server verticle")

        def router = new RouteMatcher()

        router.post("/external/service") { HttpServerRequest request ->

            def response = new JsonObject([response:[status:"Success", message:"this is the external service post response"]]).toString()
            request.response.end(response)

            log.info("External service is responding to post request with $response")


        }
        vertx.createHttpServer().requestHandler(router.asClosure()).listen(PORT)


    }
}
