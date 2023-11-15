package adressesExternes.fr.services

import adressesExternes.fr.states.ReferencesExternesFrState
import com.google.inject.Inject
import mongo.{BsonCodec, EntityMongoRepo}
import play.api.Configuration

import scala.concurrent.ExecutionContext

class AdressesExterneFrRepositoryMongo @Inject() (configuration: Configuration)(
    override implicit val executionContext: ExecutionContext
) extends EntityMongoRepo[ReferencesExternesFrState] {
  override def uri: String = configuration.underlying.getString("mongodb.uri")

  override def dbName: String =
    configuration.underlying.getString("mongodb.db_name")

  override def collectionName: String = configuration.underlying.getString(
    "mongodb.adresseExterneFr.collections.store"
  )

  override def bsonCodec: BsonCodec[ReferencesExternesFrState] =
    new AdresseExterneFrBsonCodec
}
