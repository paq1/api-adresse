package adressesExternes.fr.states

import referencesExternes.fr.shared.InfoReferenceExterneFr

case class CreateReferenceExterneFrState(
    infoReferenceExterneFr: InfoReferenceExterneFr,
    kind: String
) extends ReferencesExternesFrState
object CreateReferenceExterneFrState {
  // MKDMKD todo mettre ici les converters json (neasted le champs info)
}