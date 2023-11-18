package adressesExternes.fr.services

import adressesExternes.fr.states.{
  CreateReferenceExterneFrState,
  ReferencesExternesFrState
}
import mongo.BsonCodec
import org.mongodb.scala.bson.{BsonDocument, Document}
import referencesExternes.fr.shared.InfoReferenceExterneFr

class AdresseExterneFrBsonCodec extends BsonCodec[ReferencesExternesFrState] {
  override def asScala(
      bsonDocument: BsonDocument
  ): Option[ReferencesExternesFrState] = {
    val documentData = bsonDocument.get("data").asDocument()
    val kind = documentData.get("kind").asString().getValue
    val numeroRue = documentData.get("numeroRue").asString().getValue
    val nomRue = documentData.get("nomRue").asString().getValue
    val codePostal = documentData.get("codePostal").asString().getValue
    val ville = documentData.get("ville").asString().getValue
    val pays = documentData.get("pays").asString().getValue
    val id = documentData.get("id").asString().getValue

    val info = InfoReferenceExterneFr(
      id = id,
      numeroRue = numeroRue,
      nomRue = nomRue,
      codePostal = codePostal,
      ville = ville,
      pays = pays,
      position = None
    )

    kind match {
      case kind @ "create" =>
        Some(CreateReferenceExterneFrState(info, kind))
      case _ => None

    }
  }

  override def toBson(element: ReferencesExternesFrState): BsonDocument =
    element match {
      case CreateReferenceExterneFrState(infoReferenceExterneFr, kind) =>
        Document(
          "kind" -> element.kind,
          "id" -> infoReferenceExterneFr.id,
          "numeroRue" -> infoReferenceExterneFr.numeroRue,
          "nomRue" -> infoReferenceExterneFr.nomRue,
          "codePostal" -> infoReferenceExterneFr.codePostal,
          "ville" -> infoReferenceExterneFr.ville,
          "pays" -> infoReferenceExterneFr.pays
        ).toBsonDocument()
      case _ => Document().toBsonDocument()
    }
}
