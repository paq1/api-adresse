package adresses.dim
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, lit}

object DimAdresseFrMoselle extends CanComputeDim {
  override def compute(spark: SparkSession): DataFrame = {
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
  }
}
