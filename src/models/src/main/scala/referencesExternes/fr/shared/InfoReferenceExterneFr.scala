package referencesExternes.fr.shared

case class InfoReferenceExterneFr(
    numeroRue: String,
    nomRue: String,
    codePostal: String,
    ville: String,
    pays: String,
    position: Option[(Double, Double)]
)
