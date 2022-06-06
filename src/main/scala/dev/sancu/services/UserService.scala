package dev.sancu.services

import dev.sancu.models.{User, CreateOrUpdateUser}
import scala.concurrent.{ExecutionContext, Future}
import io.getquill.{PostgresAsyncContext, SnakeCase}

class UserService()(implicit ctx: PostgresAsyncContext[SnakeCase.type], ec: ExecutionContext) {

  import ctx._

  private val query = quote(querySchema[User]("users"))

  def get: Future[List[User]] = {
    ctx.run(query)
  }

  def find(id: Long): Future[Option[User]] = {
    ctx.run(query.filter(_.id == lift(id))).map(_.headOption)
  }

  def create(user: CreateOrUpdateUser): Future[User] = {
    ctx.run(
      query.insert(_.email -> lift(user.email), _.age -> lift(user.age)).returning(user => user)
    )
  }

  def update(id: Long, user: CreateOrUpdateUser): Future[User] = {
    ctx.run(
      query.filter(u => u.id == lift(id)).update(_.email -> lift(user.email), _.age -> lift(user.age)).returning(user => user)
    )
  }

  def delete(id: Long): Future[Int] = {
    ctx.run(query.filter(u => u.id == lift(id)).delete).map(_ => 1)
  }
}
