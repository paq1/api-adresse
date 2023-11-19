package adressesExternes.fr.research.models

case class ResearchAdresseOut(
    id: String,
    nomRue: String,
    numeroRue: String,
    codePostal: String,
    ville: String,
    pays: String
)
