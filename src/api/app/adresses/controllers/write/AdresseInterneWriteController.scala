package adresses.controllers.write

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{
  Action,
  AnyContent,
  BaseController,
  ControllerComponents,
  Request
}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AdresseInterneWriteController @Inject() (
    override val controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends BaseController {

  def create(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      Future.successful(
        Ok(
          Json.obj(
            "data" -> Json.obj("attributes" -> Json.obj("result" -> "OK"))
          )
        )
      )
  }

}
