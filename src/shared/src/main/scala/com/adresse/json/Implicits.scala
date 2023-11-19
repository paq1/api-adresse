package com.adresse.json

import adressesExternes.fr.events.{
  ReferenceExterneFrCreated,
  ReferencesExternesFrEvent
}
import adressesExternes.fr.research.models.ResearchAdresseOut
import adressesExternes.fr.states.{
  CreateReferenceExterneFrState,
  ReferencesExternesFrState
}
import play.api.libs.json._
import referencesExternes.fr.commands.{
  AdresseExterneFrCommand,
  CreateAdresseExterneFrCommand
}
import referencesExternes.fr.shared.InfoReferenceExterneFr

object Implicits {
  // Fixme rendre générique via des schemas (voir framework foyer)
  implicit val oreadInfoExterneFr: Reads[InfoReferenceExterneFr] =
    (json: JsValue) => {
      JsSuccess(
        InfoReferenceExterneFr(
          id = (json \ "id").as[String],
          numeroRue = (json \ "numeroRue").as[String],
          nomRue = (json \ "nomRue").as[String],
          codePostal = (json \ "codePostal").as[String],
          ville = (json \ "ville").as[String],
          pays = (json \ "pays").as[String],
          position =
            ((json \ "x").asOpt[Double], (json \ "y").asOpt[Double]) match {
              case (Some(x), Some(y)) => Some((x, y))
              case _                  => None
            }
        )
      )
    }

  implicit val oreadCreateCommand: Reads[CreateAdresseExterneFrCommand] =
    (json: JsValue) => {
      JsSuccess(
        CreateAdresseExterneFrCommand(
          info = json.as[InfoReferenceExterneFr]
        )
      )
    }

  implicit val owriteCreateEvent: Writes[ReferencesExternesFrEvent] = {
    case ReferenceExterneFrCreated(infoReferenceExterneFr, at, by) =>
      Json.obj(
        "numeroRue" -> infoReferenceExterneFr.numeroRue,
        "nomRue" -> infoReferenceExterneFr.nomRue,
        "at" -> at,
        "by" -> by
      )
    case _ => Json.obj()
  }

  implicit val owriteResearchAdresseOut: Writes[ResearchAdresseOut] = { (res) =>
    {
      Json.obj(
        "id" -> res.id,
        "nomRue" -> res.nomRue,
        "numeroRue" -> res.numeroRue,
        "codePostal" -> res.codePostal,
        "ville" -> res.ville,
        "pays" -> res.pays
      )
    }
  }

  implicit val owriteCreateCommandeFr: Writes[AdresseExterneFrCommand] = {
    case CreateAdresseExterneFrCommand(info) =>
      Json.obj(
        "id" -> info.id,
        "numeroRue" -> info.numeroRue,
        "nomRue" -> info.nomRue,
        "codePostal" -> info.codePostal,
        "ville" -> info.ville,
        "pays" -> info.pays
      )
    case _ => Json.obj()
  }

  implicit val owriteCreateState: Writes[ReferencesExternesFrState] = {
    case CreateReferenceExterneFrState(infoReferenceExterneFr, kind) =>
      Json.obj(
        "numeroRue" -> infoReferenceExterneFr.numeroRue,
        "nomRue" -> infoReferenceExterneFr.nomRue,
        "kind" -> kind
      )
    case _ => Json.obj()
  }
}
