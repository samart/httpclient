package org.test.web

import org.vertx.groovy.platform.Verticle


public class BootstrapVerticle extends Verticle {

    def start() {

        deployVerticles()



    }

    def deployVerticles() {

        container.deployWorkerVerticle("groovy:org.test.web.HttpClientVerticle", container.config,  10, true)
        container.deployWorkerVerticle("groovy:org.test.web.ExternalService", container.config,  10, true)


    }
}