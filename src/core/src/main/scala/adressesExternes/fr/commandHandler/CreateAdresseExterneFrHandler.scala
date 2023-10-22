package adressesExternes.fr.commandHandler

import adressesExternes.fr.events.{
  ReferenceExterneFrCreated,
  ReferencesExternesFrEvent
}
import adressesExternes.fr.services.repository.AdresseExterneFrRepository
import cats.data.Validated.Valid
import errors.data.ValidatedErr
import referencesExternes.fr.commands.CreateAdresseExterneFrCommand

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class CreateAdresseExterneFrHandler(
    referenceExterneFrRepository: AdresseExterneFrRepository
)(implicit ec: ExecutionContext) {
  def onCommand(
      cmd: CreateAdresseExterneFrCommand
  ): Future[ValidatedErr[ReferencesExternesFrEvent]] = Future.successful(
    Valid(
      ReferenceExterneFrCreated(
        infoReferenceExterneFr = cmd.info,
        by = "sys:bot",
        at =
          Instant.now() // MKDMKD fixme a mettre dans un service ou dans un ctx (pour la testabilité)
      )
    )
  )
}
