package adressesExternes.fr.states

import referencesExternes.fr.shared.InfoReferenceExterneFr

case class CreateReferenceExterneFrState(
    infoReferenceExterneFr: InfoReferenceExterneFr,
    kind: String
) extends ReferencesExternesFrState
