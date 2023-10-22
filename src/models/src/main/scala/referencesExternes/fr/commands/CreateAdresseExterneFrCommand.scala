package referencesExternes.fr.commands

import referencesExternes.fr.shared.InfoReferenceExterneFr

case class CreateAdresseExterneFrCommand(
    info: InfoReferenceExterneFr
) extends AdresseExterneFrCommand {}
object CreateAdresseExterneFrCommand {
  // MKDMKD todo mettre ici les converters json (neasted le champs info)
}
