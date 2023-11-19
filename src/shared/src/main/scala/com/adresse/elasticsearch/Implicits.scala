package com.adresse.elasticsearch

import adressesExternes.fr.research.models.ResearchAdresseOut
import com.sksamuel.elastic4s.HitReader

import scala.util.Try

object Implicits {
  implicit val r: HitReader[ResearchAdresseOut] = (e) =>
    Try {
      ResearchAdresseOut(
        id = e.sourceField("id").toString,
        nomRue = e.sourceField("nomRue").toString,
        numeroRue = e.sourceField("numeroRue").toString,
        codePostal = e.sourceField("codePostal").toString,
        ville = e.sourceField("ville").toString,
        pays = e.sourceField("pays").toString
      )
    }
}
