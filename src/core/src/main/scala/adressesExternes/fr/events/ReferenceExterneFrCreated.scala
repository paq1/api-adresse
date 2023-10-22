package adressesExternes.fr.events

import referencesExternes.fr.shared.InfoReferenceExterneFr

import java.time.Instant

case class ReferenceExterneFrCreated(
    infoReferenceExterneFr: InfoReferenceExterneFr,
    at: Instant,
    by: String
) extends ReferencesExternesFrEvent
object ReferenceExterneFrCreated {
  // MKDMKD todo mettre ici les converters json (neasted le champs info)
}
