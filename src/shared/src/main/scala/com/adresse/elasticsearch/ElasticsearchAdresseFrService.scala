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
import com.adresse.elasticsearch.Implicits._

import scala.concurrent.{ExecutionContext, Future}

class ElasticsearchAdresseFrService(
    indexName: String = "adressesfr",
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
          createIndex(indexName).mapping(
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

  override def fulltext(
      queryString: String
  ): Future[List[ResearchAdresseOut]] = {
    Future.successful(Nil)
    client
      .execute(
        search(indexName)
          .rawQuery(
            s"""
              |{
              |  "multi_match" : {
              |    "query":    "$queryString",
              |    "fields": [ "numeroRue^5", "nomRue^4", "codePostal^3", "ville^1" ]
              |  }
              |}
              |""".stripMargin
          )
      )
      .flatMap {
        case RequestSuccess(status, body, headers, result) =>
          Future.successful(
            result.hits.hits.toList
              .flatMap { hit =>
                hit
                  .safeTo[ResearchAdresseOut]
                  .toOption
              }
          )

        case RequestFailure(status, body, headers, error) =>
          Future.failed(
            new Exception("erreur elk")
          )
      }
  }

  override def insert(
      adresse: ResearchAdresseIn
  ): Future[ValidatedErr[Unit]] = {
    client
      .execute {
        indexInto(indexName)
          .fields(
            "id" -> adresse.id,
            "nomRue" -> adresse.nomRue.toLowerCase,
            "numeroRue" -> adresse.numeroRue.toLowerCase,
            "codePostal" -> adresse.codePostal.toLowerCase,
            "ville" -> adresse.ville.toLowerCase,
            "pays" -> adresse.pays.toLowerCase
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

  override def deleteAll(): Future[ValidatedErr[Unit]] = client
    .execute(
      deleteByQuery(
        indexName,
        matchAllQuery()
      )
    )
    .map {
      case RequestSuccess(status, body, headers, result) => Valid(())
      case RequestFailure(status, body, headers, error) =>
        Invalid(
          "[elasticsearch] erreur lors de la suppression -- match all"
        )
    }
}
