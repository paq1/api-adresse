package adresses.data

import org.apache.spark.sql.{Encoder, Encoders}
import shared.tools.CanBeCsvLine

case class ModelDataAdresse(
    id: String,
    numeroRue: String,
    nomRue: String,
    codePostal: String,
    ville: String,
    pays: String
) extends CanBeCsvLine {
  override def createLine: String =
    s"$id;$numeroRue;$nomRue;$codePostal;$ville;$pays"
}

object ModelDataAdresse {
  implicit val encoder: Encoder[ModelDataAdresse] =
    Encoders.product[ModelDataAdresse]
}
