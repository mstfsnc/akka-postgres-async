package dev.sancu

import dev.sancu.controllers.{ProductController, UserController}
import dev.sancu.services.{ProductService, UserService}
import dev.sancu.helpers.SolrAsyncClient
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.getquill.{PostgresAsyncContext, SnakeCase}

object Boot extends App {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "App")
  implicit val ec: ExecutionContext = system.executionContext
  implicit lazy val postgresAsyncCtx: PostgresAsyncContext[SnakeCase.type] = new PostgresAsyncContext(SnakeCase, "db")
  implicit lazy val solrAsyncClient: SolrAsyncClient = new SolrAsyncClient("http://0.0.0.0:8983/solr")


  val userController = new UserController(new UserService())
  val productController = new ProductController(new ProductService())

  object Router {
    val routes: Route = userController.routes ~ productController.routes
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(Router.routes)
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
