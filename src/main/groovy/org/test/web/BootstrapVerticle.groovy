package org.test.web

import org.vertx.groovy.core.AsyncResult
import org.vertx.groovy.platform.Verticle


public class BootstrapVerticle extends Verticle {

    def start() {

        container.logger.info("Starting up")
        deployVerticles()



    }

    def deployVerticles() {

        container.deployWorkerVerticle("groovy:org.test.web.HttpClientVerticle", container.config,  1, false, {  result ->
            if (result.succeeded) {
                // this staged deployment allows one verticle to do one time shared resource initialization.
                container.logger.info("Deploying additional http client workers")
                container.deployWorkerVerticle("groovy:org.test.web.HttpClientVerticle", container.config,  9, false)
            }
        })
        container.deployWorkerVerticle("groovy:org.test.web.ExternalService", container.config,  10, false)


    }
}