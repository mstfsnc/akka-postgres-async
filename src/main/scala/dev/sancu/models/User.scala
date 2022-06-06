package dev.sancu.models

case class User(id: Long, email: String, age: Int)

case class CreateOrUpdateUser(email: String, age: Int) {
  require(email.nonEmpty, "E-mail cannot be empty")
  require(age > 0, "Age greater than zero")
}