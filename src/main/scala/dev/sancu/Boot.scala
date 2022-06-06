package dev.sancu

import dev.sancu.controllers.UserController
import dev.sancu.services.UserService
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import io.getquill.{PostgresAsyncContext, SnakeCase}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Boot extends App {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "App")
  implicit val ec: ExecutionContext = system.executionContext
  implicit lazy val ctx: PostgresAsyncContext[SnakeCase.type] = new PostgresAsyncContext(SnakeCase, "db")

  val userController = new UserController(new UserService())

  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(userController.routes)
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
