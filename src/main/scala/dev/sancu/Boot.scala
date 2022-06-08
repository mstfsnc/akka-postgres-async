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
import com.typesafe.config.ConfigFactory
import io.getquill.{PostgresAsyncContext, SnakeCase}

object Boot extends App {
  val config = ConfigFactory.load()

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "App")
  implicit val ec: ExecutionContext = system.executionContext
  implicit lazy val postgresAsyncCtx: PostgresAsyncContext[SnakeCase.type] = new PostgresAsyncContext(SnakeCase, "db")
  implicit lazy val solrAsyncClient: SolrAsyncClient = new SolrAsyncClient(config.getString("solr.url"))

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
