package mongo

import org.mongodb.scala.bson._
import org.mongodb.scala.{MongoClient, MongoDatabase}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait EntityMongoRepo[S] {

  def uri: String
  def collectionName: String
  def dbName: String

  def bsonCodec: BsonCodec[S]

  private val clientMongo = MongoClient(uri)
  private val db: MongoDatabase = clientMongo.getDatabase(dbName)
  private val collection = db.getCollection(collectionName)

  def fetchMany()(implicit
      ec: ExecutionContext
  ): Future[List[S]] = collection
    .find()
    .map { element =>
      bsonCodec
        .asScala(element.toBsonDocument())
    }
    .foldLeft(List.empty[S]){ (acc, current) =>
      if (current.isDefined) {
        acc :+ current.get
      } else {
        acc
      }
    }
    .toFuture()
    .map(_.toList)

  def fetchOne(identifiant: String)(implicit
      ec: ExecutionContext
  ): Future[Option[S]] = collection
    .find(Document("id" -> identifiant))
    .toFuture()
    .map(_.headOption.flatMap(x => bsonCodec.asScala(x.toBsonDocument())))

  def insert(
      element: S,
      indexMetier: String = UUID.randomUUID().toString
  )(implicit ec: ExecutionContext): Future[String] =
    collection
      .insertOne(
        Document(
          "id" -> indexMetier,
          "data" -> bsonCodec.toBson(element)
        )
      )
      .toFuture()
      .map(_ => indexMetier)
}
