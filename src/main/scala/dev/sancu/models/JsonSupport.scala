package dev.sancu.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)
  implicit val createOrUpdateUserFormat: RootJsonFormat[CreateOrUpdateUser] = jsonFormat2(CreateOrUpdateUser)
  implicit val productFormat: RootJsonFormat[Product] = jsonFormat3(Product)
  implicit val createOrUpdateProductFormat: RootJsonFormat[CreateOrUpdateProduct] = jsonFormat2(CreateOrUpdateProduct)
}
