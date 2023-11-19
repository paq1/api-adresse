package adressesExternes.fr.controllers

import adressesExternes.fr.commandHandler.CreateAdresseExterneFrHandler
import adressesExternes.fr.events.{
  ReferenceExterneFrCreated,
  ReferencesExternesFrEvent
}
import adressesExternes.fr.research.ResearchAdresseFrService
import adressesExternes.fr.research.models.ResearchAdresseIn
import adressesExternes.fr.services.AdressesExterneFrRepositoryMongo
import adressesExternes.fr.states.{
  CreateReferenceExterneFrState,
  ReferencesExternesFrState
}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.google.inject.Inject
import errors.data.ValidatedErr
import org.mongodb.scala.bson.BsonDocument
import play.api.libs.json._
import play.api.mvc._
import referencesExternes.fr.commands.CreateAdresseExterneFrCommand
import com.adresse.json.Implicits._

import scala.concurrent.{ExecutionContext, Future}
class AdresseExterneFrWriteController @Inject() (
    override val controllerComponents: ControllerComponents,
    store: AdressesExterneFrRepositoryMongo,
    researchAdresseFrService: ResearchAdresseFrService
)(implicit ec: ExecutionContext)
    extends BaseController {

  def reducer(
      event: ReferenceExterneFrCreated
  ): CreateReferenceExterneFrState = {
    CreateReferenceExterneFrState(
      infoReferenceExterneFr = event.infoReferenceExterneFr,
      kind = "create"
    )
  }

  private val createHandler = new CreateAdresseExterneFrHandler()

  def create(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      for {
        event <- createACreateEvent(request.body.asJson).flatMap {
          case Valid(a)   => Future.successful(a)
          case Invalid(e) => Future.failed(new Exception(e))
        }
        state <- fromEventToState(event).flatMap {
          case Valid(a)   => Future.successful(a)
          case Invalid(e) => Future.failed(new Exception(e))
        }
        savingMongo <- saveEventAndStateInStoreAndJournal(event, state)
          .flatMap {
            case Valid(a)   => Future.successful(a)
            case Invalid(e) => Future.failed(new Exception(e))
          }
        savingElk <- saveStateInElasticsearch(state)
          .flatMap {
            case Valid(a)   => Future.successful(a)
            case Invalid(e) => Future.failed(new Exception(e))
          }
      } yield {
        Ok(
          Json.obj(
            "data" -> Json.obj("attributes" -> Json.toJson(event))
          )
        )
      }
  }

  def getAll(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      researchAdresseFrService
        .fulltext("1 Rue des Ronds Champs 57560")
//        .fetchMany(BsonDocument())
        .map { list =>
          val jsList = list.map(e => Json.toJson(e))
          Ok(
            JsArray(jsList)
          )
        }
  }

  def deleteAll(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      // todo ajouter la suppression dans mongo aussi
      researchAdresseFrService
        .deleteAll()
        //        .fetchMany(BsonDocument())
        .map {
          case Valid(a) =>
            Ok(
              Json.obj(
                "data" -> Json.obj(
                  "suppression" -> "succeed"
                )
              )
            )
          case Invalid(e) =>
            InternalServerError(
              Json.obj(
                "error" -> Json.obj(
                  "message" -> s"$e"
                )
              )
            )
        }
  }

  private def saveStateInElasticsearch(
      state: CreateReferenceExterneFrState
  ): Future[
    ValidatedErr[Unit]
  ] = {
    researchAdresseFrService
      .insert(
        ResearchAdresseIn(
          id = state.infoReferenceExterneFr.id,
          nomRue = state.infoReferenceExterneFr.nomRue,
          numeroRue = state.infoReferenceExterneFr.numeroRue,
          codePostal = state.infoReferenceExterneFr.codePostal,
          ville = state.infoReferenceExterneFr.codePostal,
          pays = state.infoReferenceExterneFr.pays
        )
      )
  }

  private def saveEventAndStateInStoreAndJournal(
      event: ReferencesExternesFrEvent,
      state: ReferencesExternesFrState
  ): Future[
    ValidatedErr[(ReferencesExternesFrEvent, ReferencesExternesFrState)]
  ] = {
    for {
      savingStore <- store.insertOne(state)
      savingJournal <- Future.successful(()) // MKDMKD todo save in journal
    } yield Valid((event, state))
  }

  private def createACreateEvent(
      body: Option[JsValue]
  ): Future[ValidatedErr[ReferencesExternesFrEvent]] = {
    body
      .map { cmdJson =>
        val cmd = cmdJson.as[CreateAdresseExterneFrCommand]
        createHandler.onCommand(cmd)
      }
      .getOrElse(Future.successful(Invalid("on ne recoit pas de body")))
  }

  private def fromEventToState(
      event: ReferencesExternesFrEvent
  ): Future[ValidatedErr[CreateReferenceExterneFrState]] = {
    event match {
      case e @ ReferenceExterneFrCreated(infoReferenceExterneFr, at, by) =>
        Future.successful(Valid(reducer(e)))
      case _ => Future.successful(Invalid("Illegal event -> state"))
    }

  }

}
