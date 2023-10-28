package adresses.data

import org.apache.spark.sql.{Encoder, Encoders}

case class ModelDataAdresse(
    id: String,
    numeroRue: String,
    codePostal: String,
    ville: String,
    pays: String
)

object ModelDataAdresse {
  implicit val encoder: Encoder[ModelDataAdresse] =
    Encoders.product[ModelDataAdresse]
}
