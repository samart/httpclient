package org.test.web

import groovy.util.logging.Slf4j
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpClient
import org.vertx.groovy.core.http.HttpClientResponse

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by samart on 6/14/14.
 */
@Slf4j
class ClientApi {

    HttpClient httpClient

    public ClientApi() {

        httpClient = Vertx.newVertx().createHttpClient()
        httpClient.setKeepAlive(false)
        httpClient.setMaxPoolSize(20)
        httpClient.host = 'localhost'
        httpClient.port = ExternalService.PORT
        // and ssl stuff..

    }

    String sendHttpPost(String payload) {


        def latch = new CountDownLatch(1)
        def content = null
        httpClient.post("/external/service") { HttpClientResponse response ->

            log.info("GOT POST RESPONSE WITH STATUS $response.statusCode.")
            if (response.statusCode == 200) {
                response.bodyHandler { buffer ->
                    content = buffer.toString()
                    log.info(" ======  SUCCESS - we got the response in the client  =======")
                    latch.countDown()

                }

            } else {
                log.error("got unexpected response with status $response.statusCode")
                content = "error"
                latch.countDown()
            }

        }.end(payload)
        latch.await(10, TimeUnit.SECONDS)
        return content


    }
}
