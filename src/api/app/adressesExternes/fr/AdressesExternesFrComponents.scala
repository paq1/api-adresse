package adressesExternes.fr

import adressesExternes.fr.controllers.AdresseExterneFrWriteController
import adressesExternes.fr.services.AdressesExterneFrRepositoryMongo
import play.api.Configuration
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

trait AdressesExternesFrComponents {

  def controllerComponents: ControllerComponents
  def executionContext: ExecutionContext

  def configuration: Configuration

  val adressesExternesFrStoreRepository = new AdressesExterneFrRepositoryMongo(
    configuration
  )(executionContext)

  val adressesExternesFrController = new AdresseExterneFrWriteController(
    controllerComponents,
    adressesExternesFrStoreRepository
  )(executionContext)

}
