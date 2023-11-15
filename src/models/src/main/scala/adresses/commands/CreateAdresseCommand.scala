package adresses.commands

case class CreateAdresseCommand(
    numeroRue: String,
    nomRue: String,
    codePostal: String,
    ville: String,
    pays: String,
    referenceExterne: Option[String]
) extends AdresseCommand
