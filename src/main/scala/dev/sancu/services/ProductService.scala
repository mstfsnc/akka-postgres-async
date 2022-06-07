package dev.sancu.services

import dev.sancu.helpers.SolrAsyncClient
import dev.sancu.models.{Product, CreateOrUpdateProduct, ProductTransformer, SolrTransformer}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import java.util.UUID
import org.apache.solr.client.solrj.SolrQuery

class ProductService()(implicit solrAsyncClient: SolrAsyncClient, ec: ExecutionContext) {
  private val core = "products"

  def get: Future[List[Product]] = {
    val query = new SolrQuery("*:*")
    solrAsyncClient.query(core, query).map(_.getResults.asScala.toList).map { result =>
      result.map(ProductTransformer.apply)
    }
  }

  def find(id: UUID): Future[Option[Product]] = {
    val query = new SolrQuery("*:*").addFilterQuery(s"id:$id").setRows(1)
    solrAsyncClient.query(core, query).map(_.getResults.asScala.headOption).map(_.map(ProductTransformer.apply))
  }

  def create(product: CreateOrUpdateProduct): Future[CreateOrUpdateProduct] = {
    solrAsyncClient.add(core, SolrTransformer.apply(product)).map { response =>
      Option(response.getException) match {
        case Some(t) => throw t
        case _ => product
      }
    }
  }

  def delete(id: UUID): Future[Int] = {
    solrAsyncClient.delete(core, id.toString).map { response =>
      Option(response.getException) match {
        case Some(t) => throw t
        case _ => 0
      }
    }
  }
}
