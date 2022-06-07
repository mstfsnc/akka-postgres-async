package dev.sancu.controllers

import dev.sancu.models.{CreateOrUpdateProduct, JsonSupport}
import dev.sancu.services.ProductService
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server
import akka.http.scaladsl.model.StatusCodes

class ProductController(productService: ProductService)(implicit ec: ExecutionContext) extends Directives with JsonSupport {
  val routes: server.Route = {
    concat(
      get {
        path("products") {
          onComplete(productService.get) {
            case Success(products) => complete(StatusCodes.OK, products)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        } ~ path("products" / JavaUUID) { productId =>
          onComplete(productService.find(productId)) {
            case Success(None) => complete(StatusCodes.NotFound)
            case Success(product) => complete(StatusCodes.OK, product)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      },
      post {
        path("products") {
          entity(as[CreateOrUpdateProduct]) { createProduct =>
            onComplete(productService.create(createProduct)) {
              case Success(product) => complete(StatusCodes.Created, product)
              case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
            }
          }
        }
      },
      delete {
        path("products" / JavaUUID) { productId =>
          onComplete(productService.delete(productId)) {
            case Success(_) => complete(StatusCodes.Accepted)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    )
  }
}