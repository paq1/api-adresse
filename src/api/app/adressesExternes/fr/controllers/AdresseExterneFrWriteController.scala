package adressesExternes.fr.controllers

import adressesExternes.fr.commandHandler.CreateAdresseExterneFrHandler
import adressesExternes.fr.events.{
  ReferenceExterneFrCreated,
  ReferencesExternesFrEvent
}
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
    store: AdressesExterneFrRepositoryMongo
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
      val event: Future[ValidatedErr[ReferencesExternesFrEvent]] =
        createACreateEvent(request.body.asJson)

      val state: Future[ValidatedErr[ReferencesExternesFrState]] =
        fromEventToState(event)

      val savingEventsAndState =
        saveEventAndStateInStoreAndJournal(event, state)

      savingEventsAndState
        .map {
          case Valid((event, state)) =>
            Ok(
              Json.obj(
                "data" -> Json.obj("attributes" -> Json.toJson(event))
              )
            )
          case Invalid(e) =>
            InternalServerError(
              Json.obj(
                "error" -> Json.obj(
                  "status" -> "500",
                  "message" -> e.toString
                )
              )
            )
        }
  }

  def getAll(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      store
        .fetchMany(BsonDocument())
        .map { list =>
          val jsList = list.map(e => Json.toJson(e))
          Ok(
            JsArray(jsList)
          )
        }
  }

  private def saveEventAndStateInStoreAndJournal(
      event: Future[ValidatedErr[ReferencesExternesFrEvent]],
      state: Future[ValidatedErr[ReferencesExternesFrState]]
  ): Future[
    ValidatedErr[(ReferencesExternesFrEvent, ReferencesExternesFrState)]
  ] = {
    for {
      e <- event
        .flatMap {
          case Valid(a)   => Future.successful(a)
          case Invalid(e) => Future.failed(new Exception(s"$e"))
        }
      s <- state
        .flatMap {
          case Valid(a)   => Future.successful(a)
          case Invalid(e) => Future.failed(new Exception(s"$e"))
        }
      savingStore <- store.insertOne(s)
      savingJournal <- Future.successful(()) // MKDMKD todo save in journal
    } yield Valid((e, s))
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
      event: Future[ValidatedErr[ReferencesExternesFrEvent]]
  ): Future[ValidatedErr[CreateReferenceExterneFrState]] = {
    event
      .map {
        case Validated.Valid(a) =>
          a match {
            case ref: ReferenceExterneFrCreated => Valid(reducer(ref))
            case _                              => Invalid("Illegal transition state")
          }
        case i @ Invalid(e) => i
      }
  }

}
