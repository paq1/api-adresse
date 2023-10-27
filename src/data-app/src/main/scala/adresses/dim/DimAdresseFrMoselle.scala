package adresses.dim

import adresses.job.SimpleJob
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders}

case class ModelDataAdresse(
    id: String,
    numeroRue: String,
    codePostal: String,
    ville: String,
    pays: String
)

object DimAdresseFrMoselle extends SimpleJob {

  implicit val ed: Encoder[ModelDataAdresse] =
    Encoders.product[ModelDataAdresse]

  override def run(): Unit = {
    val adresseFrMoselle: DataFrame =
      spark.read
        .options(Map("header" -> "true", "delimiter" -> ";"))
        .csv("data/input/adresses-57.csv")

    val selectColumnRenameDf = adresseFrMoselle
      .select(
        col("id"),
        col("numero").as("numeroRue"),
        col("nom_voie").as("nomRue"),
        col("code_postal").as("codePostal"),
        col("nom_commune").as("ville"),
        lit("france").as("pays")
      )

    val adresseAvecToutesLesValeursDefinieDf = selectColumnRenameDf
      .where(
        col("numeroRue").isNotNull and
          col("nomRue").isNotNull and
          col("codePostal").isNotNull and
          col("ville").isNotNull
      )

    adresseAvecToutesLesValeursDefinieDf
      .show(true)

    saveCsv(adresseAvecToutesLesValeursDefinieDf)
  }

  override def jobName: String = "dim-adresses-fr-57"

  private def saveCsv(df: DataFrame): Unit = {
    // MKDMKD fixme passer par hadoop pour save le csv
    val elements: List[ModelDataAdresse] = df
      .as[ModelDataAdresse]
      .collect()
      .toList
  }
}
