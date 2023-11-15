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
import referencesExternes.fr.shared.InfoReferenceExterneFr

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

  implicit val oreadInfo: Reads[InfoReferenceExterneFr] =
    (json: JsValue) => {
      JsSuccess(
        InfoReferenceExterneFr(
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

  implicit val oread: Reads[CreateAdresseExterneFrCommand] =
    (json: JsValue) => {
      JsSuccess(
        CreateAdresseExterneFrCommand(
          info = json.as[InfoReferenceExterneFr]
        )
      )
    }

  implicit val owrite: Writes[ReferencesExternesFrEvent] = {
    case ReferenceExterneFrCreated(infoReferenceExterneFr, at, by) =>
      Json.obj(
        "numeroRue" -> infoReferenceExterneFr.numeroRue,
        "nomRue" -> infoReferenceExterneFr.nomRue,
        "at" -> at,
        "by" -> by
      )
    case _ => Json.obj()
  }

  implicit val owritestate: Writes[ReferencesExternesFrState] = {
    case CreateReferenceExterneFrState(infoReferenceExterneFr, kind) =>
      Json.obj(
        "numeroRue" -> infoReferenceExterneFr.numeroRue,
        "nomRue" -> infoReferenceExterneFr.nomRue,
        "kind" -> kind
      )
    case _ => Json.obj()
  }

  val createHandler = new CreateAdresseExterneFrHandler()

  def create(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      val event: Future[ValidatedErr[ReferencesExternesFrEvent]] =
        request.body.asJson
          .map { cmdJson =>
            val cmd = cmdJson.as[CreateAdresseExterneFrCommand]
            createHandler.onCommand(cmd)
          }
          .getOrElse(Future.successful(Invalid("bruh")))

      val state: Future[ValidatedErr[CreateReferenceExterneFrState]] = event
        .map {
          case Validated.Valid(a) =>
            a match {
              case ref: ReferenceExterneFrCreated => Valid(reducer(ref))
              case _                              => Invalid("Illegal transition state")
            }
          case i @ Invalid(e) => i
        }

      for {
        e: ReferencesExternesFrEvent <- event
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
      } yield Ok(
        Json.obj(
          "data" -> Json.obj("attributes" -> Json.toJson(e))
        )
      )
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

}
