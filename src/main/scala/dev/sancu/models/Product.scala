package dev.sancu.models

import org.apache.solr.common.{SolrDocument, SolrInputDocument}

case class Product(id: String, name: String, price: Double)

case class CreateOrUpdateProduct(name: String, price: Double) {
  require(name.nonEmpty, "Name cannot be empty")
  require(price > 0, "Price greater than zero")
}

object ProductTransformer {
  def apply(doc: SolrDocument): Product = {
    Product(
      id = doc.get("id").asInstanceOf[String],
      name = doc.get("name").asInstanceOf[String],
      price = doc.get("price").asInstanceOf[Double]
    )
  }
}

object SolrTransformer {
  def apply(product: CreateOrUpdateProduct): SolrInputDocument = {
    val doc = new SolrInputDocument()
    doc.setField("name", product.name)
    doc.setField("price", product.price)
    doc
  }
}

