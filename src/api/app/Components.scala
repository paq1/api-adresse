import adresses.controllers.write.AdresseInterneWriteController
import adressesExternes.fr.AdressesExternesFrComponents
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes

class Components(context: Context)
    extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with controllers.AssetsComponents
    with AdressesExternesFrComponents {

  lazy val adresseWriteController = new AdresseInterneWriteController(
    controllerComponents
  )

  lazy val router: Router = new Routes(
    httpErrorHandler,
    adresseWriteController,
    adressesExternesFrController,
    assets
  )
}
