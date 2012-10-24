/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.chrisrichardson.vertxclock

import org.cloudfoundry.runtime.env.CloudEnvironment
import org.vertx.java.core.Handler
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.http.HttpServer
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.core.json.JsonObject
import org.vertx.java.core.sockjs.SockJSServer
import org.vertx.java.core.sockjs.SockJSSocket
import org.vertx.java.deploy.Verticle
import java.util.Date

class VertxClock extends Verticle {

  implicit def toHandler[T, U](fn : T => U) = new Handler[T] {
      def handle( req : T) = fn(req)
  }

  val env = new CloudEnvironment()

  def start() {
    val server = vertx.createHttpServer()

    def staticContent(req : HttpServerRequest) = req.response.sendFile( req.path match {
        case "/" => "public/index.html"
        case _ => "public/" + req.path
      })

    server.requestHandler(staticContent _)

    val sockServer = vertx.createSockJSServer(server)

    def sockJsHandler( sock :  SockJSSocket) = {
        val timerID = vertx.setPeriodic(1000, { (timerID : java.lang.Long) => 
                sock.writeBuffer(new Buffer(new Date().toString))
        })

        sock.endHandler { (data : Void) =>
           System.out.println("Cancelling timer") 
           vertx.cancelTimer(timerID)
        }

    }
    
    sockServer.installApp(new JsonObject().putString("prefix", "/testapp"),  sockJsHandler _)


    server.listen(portToUse)
  }

  def portToUse = if (env.isCloudFoundry())  env.getInstanceInfo().getPort() else 8080
  
}
