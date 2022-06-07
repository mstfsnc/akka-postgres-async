package dev.sancu.helpers

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import java.io.Closeable
import org.apache.solr.client.solrj.impl.Http2SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.{QueryResponse, UpdateResponse}
import org.apache.solr.common.SolrInputDocument

class SolrAsyncClient(url: String)(implicit ec: ExecutionContext) extends Closeable {

  private val client: Http2SolrClient = new Http2SolrClient.Builder(url).build()

  def query(collection: String, params: SolrQuery): Future[QueryResponse] = {
    Future(client.query(collection, params))
  }

  def add(collection: String, docs: SolrInputDocument*): Future[UpdateResponse] =
    Future(client.add(collection, docs.asJava))

  def delete(collection: String, id: String): Future[UpdateResponse] =
    Future(client.deleteById(collection, id))

  override def close(): Unit = client.close()
}
