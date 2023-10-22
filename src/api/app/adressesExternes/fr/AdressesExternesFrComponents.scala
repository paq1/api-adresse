package adressesExternes.fr

import adressesExternes.fr.controllers.AdresseExterneFrWriteController
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

trait AdressesExternesFrComponents {

  def controllerComponents: ControllerComponents
  def executionContext: ExecutionContext

  val adressesExternesFrController = new AdresseExterneFrWriteController(
    controllerComponents
  )(executionContext)

}
