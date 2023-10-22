package adresses.commands

case class CreateAdresseCommand(
    numeroRue: String,
    nomRue: String,
    codePostal: String,
    ville: String,
    pays: String,
    referenceExterne: Option[String]
) extends AdresseCommand {}
object CreateAdresseCommand {
  // MKDMKD todo mettre ici les converters json
}