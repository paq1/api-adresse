package referencesExternes.fr.shared

case class InfoReferenceExterneFr(
    id: String,
    numeroRue: String,
    nomRue: String,
    codePostal: String,
    ville: String,
    pays: String,
    position: Option[(Double, Double)]
)
