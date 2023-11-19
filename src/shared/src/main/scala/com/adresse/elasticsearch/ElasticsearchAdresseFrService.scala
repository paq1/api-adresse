package com.adresse.elasticsearch

import adressesExternes.fr.research.ResearchAdresseFrService
import adressesExternes.fr.research.models.{
  ResearchAdresseIn,
  ResearchAdresseOut
}
import cats.data.Validated.{Invalid, Valid}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.cat.CatIndicesResponse
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s._
import errors.data.ValidatedErr

import scala.concurrent.{ExecutionContext, Future}

class ElasticsearchAdresseFrService(
    indexName: String = "adresseFr",
    urlElasticsearch: String = "http://192.168.0.17:9200"
)(implicit ec: ExecutionContext)
    extends ResearchAdresseFrService {

  val client: ElasticClient = ElasticClient(
    JavaClient(
      ElasticProperties(urlElasticsearch)
    ) // fixme mettre dans app conf
  )

  client.execute {
    catIndices()
  }.await match {
    case failure: RequestFailure => println(failure.error)
    case response: Response[Seq[CatIndicesResponse]] =>
      if (!response.result.exists(_.index == indexName)) {
        client.execute {
          createIndex("activities").mapping(
            properties(
              TextField("id"),
              TextField("nomRue"),
              TextField("numeroRue"),
              TextField("codePostal"),
              TextField("ville"),
              TextField("pays")
            )
          )
        }.await
        println(s"index $indexName created")
      }
  }

  override def fulltext(queryString: String): Future[List[ResearchAdresseOut]] =
    Future.successful(Nil)

  override def insert(
      adresse: ResearchAdresseIn
  ): Future[ValidatedErr[Unit]] = {
    client
      .execute {
        indexInto(indexName)
          .fields(
            "id" -> adresse.id,
            "nomRue" -> adresse.nomRue,
            "numeroRue" -> adresse.numeroRue,
            "codePostal" -> adresse.codePostal,
            "ville" -> adresse.ville,
            "pays" -> adresse.pays
          )
          .refresh(RefreshPolicy.Immediate)
      }
      .flatMap {
        case RequestSuccess(status, body, headers, result) =>
          Future.successful(Valid(()))
        case RequestFailure(status, body, headers, error) =>
          Future.successful(
            Invalid(
              s"status $status, message: ${body.getOrElse("")}"
            )
          )
      }
  }
}
