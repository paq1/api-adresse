package adressesExternes.fr

import adressesExternes.fr.controllers.AdresseExterneFrWriteController
import adressesExternes.fr.services.AdressesExterneFrRepositoryMongo
import com.adresse.elasticsearch.ElasticsearchAdresseFrService
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

  val researchAdresseFrService = new ElasticsearchAdresseFrService(
    "adressesfr",
    "http://192.168.0.17:9200"
  )(
    executionContext
  )

  val adressesExternesFrController = new AdresseExterneFrWriteController(
    controllerComponents,
    adressesExternesFrStoreRepository,
    researchAdresseFrService
  )(executionContext)

}
