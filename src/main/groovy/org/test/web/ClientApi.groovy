package org.test.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpClient
import org.vertx.groovy.core.http.HttpClientResponse

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by samart on 6/14/14.
 */
class ClientApi {

    private static Logger log = LoggerFactory.getLogger(ClientApi.class)

    HttpClient httpClient

    public ClientApi() {

        httpClient = Vertx.newVertx().createHttpClient()
        httpClient.setKeepAlive(false)
        httpClient.setMaxPoolSize(20)
        httpClient.host = 'localhost'
        httpClient.port = ExternalService.PORT
        // and ssl stuff..

    }

    String sendHttpPost(String requestId, String payload) {


        def latch = new CountDownLatch(1)
        def content = null
        log.info("Sending POST request with id $requestId")
        httpClient.post("/external/service/$requestId") { HttpClientResponse response ->

            log.info("GOT POST RESPONSE for $requestId WITH STATUS $response.statusCode.")
            if (response.statusCode == 200) {
                response.bodyHandler { buffer ->
                    content = buffer.toString()
                    log.info(" ======  SUCCESS - we got the response for $requestId in the client  =======")
                    latch.countDown()

                }

            } else {
                log.error("got unexpected response  for $requestId with status $response.statusCode")
                content = "ERROR for request with id $requestId"
                latch.countDown()
            }

        }.end(payload)
        latch.await(2, TimeUnit.SECONDS)
        log.info("Returning response for $requestId as $content")
        return content


    }
}
