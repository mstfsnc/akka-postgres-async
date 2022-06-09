package dev.sancu

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringDeserializer

object SampleConsumer extends App {
  implicit val system: ActorSystem = ActorSystem("consumer")
  implicit val ec: ExecutionContext = system.dispatcher

  val config = ConfigFactory.load()
  val consumerConfig = config.getConfig("akka.kafka.consumer")
  val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)

  val consume = Consumer
    .plainSource(consumerSettings, Subscriptions.topics("sample-topic"))
    .runWith(Sink.foreach(println))

  consume onComplete {
    case Success(_) =>
      println("Done")
      system.terminate()

    case Failure(exception) =>
      println(exception.getMessage)
      system.terminate()

  }
}
