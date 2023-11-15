package mongo

import cats.data.Validated.Valid
import errors.data.ValidatedErr
import org.mongodb.scala.bson._
import org.mongodb.scala.{MongoClient, MongoDatabase}
import repository.Repository

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait EntityMongoRepo[S] extends Repository[Future, S, String, BsonDocument] {

  def executionContext: ExecutionContext
  def uri: String
  def collectionName: String
  def dbName: String

  def bsonCodec: BsonCodec[S]

  private val clientMongo = MongoClient(uri)
  private val db: MongoDatabase = clientMongo.getDatabase(dbName)
  private val collection = db.getCollection(collectionName)

  def fetchMany(query: BsonDocument): Future[List[S]] = collection
    .find()
    .map { element =>
      bsonCodec
        .asScala(element.toBsonDocument())
    }
    .foldLeft(List.empty[S]) { (acc, current) =>
      if (current.isDefined) {
        acc :+ current.get
      } else {
        acc
      }
    }
    .toFuture()
    .map(_.toList)(executionContext)

  def fetchOne(id: String): Future[Option[S]] = collection
    .find(Document("id" -> id))
    .toFuture()
    .map(_.headOption.flatMap(x => bsonCodec.asScala(x.toBsonDocument())))(
      executionContext
    )

  def insertOne(
      element: S
  ): Future[ValidatedErr[Unit]] =
    collection
      .insertOne(
        Document(
          "id" -> UUID.randomUUID().toString,
          "data" -> bsonCodec.toBson(element)
        )
      )
      .toFuture()
      .map(_ => Valid(()))(executionContext)
}
