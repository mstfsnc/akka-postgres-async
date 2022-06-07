package dev.sancu.services

import dev.sancu.models.{User, CreateOrUpdateUser}
import scala.concurrent.{ExecutionContext, Future}
import io.getquill.{PostgresAsyncContext, SnakeCase}

class UserService()(implicit postgresAsyncCtx: PostgresAsyncContext[SnakeCase.type], ec: ExecutionContext) {

  import postgresAsyncCtx._

  private val query = quote(querySchema[User]("users"))

  def get: Future[List[User]] = {
    postgresAsyncCtx.run(query)
  }

  def find(id: Long): Future[Option[User]] = {
    postgresAsyncCtx.run(query.filter(_.id == lift(id))).map(_.headOption)
  }

  def create(user: CreateOrUpdateUser): Future[User] = {
    postgresAsyncCtx.run(
      query.insert(_.email -> lift(user.email), _.age -> lift(user.age)).returning(user => user)
    )
  }

  def update(id: Long, user: CreateOrUpdateUser): Future[User] = {
    postgresAsyncCtx.run(
      query.filter(u => u.id == lift(id)).update(_.email -> lift(user.email), _.age -> lift(user.age)).returning(user => user)
    )
  }

  def delete(id: Long): Future[Int] = {
    postgresAsyncCtx.run(query.filter(u => u.id == lift(id)).delete).map(_ => 1)
  }
}
