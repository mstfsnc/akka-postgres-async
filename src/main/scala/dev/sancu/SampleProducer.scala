package dev.sancu

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import akka.kafka.scaladsl.Producer
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

object SampleProducer extends App {
  implicit val system: ActorSystem = ActorSystem("producer")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = ConfigFactory.load()
  val producerConfig = config.getConfig("akka.kafka.producer")
  val producerSettings = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer)

  val produce: Future[Done] =
    Source(100 to 200)
      .map(value => new ProducerRecord[String, String]("sample-topic", value.toString))
      .runWith(Producer.plainSink(producerSettings))

  produce onComplete {
    case Success(_) =>
      println("Done")
      system.terminate()
    case Failure(exception) =>
      println(exception.getMessage)
      system.terminate()
  }
}