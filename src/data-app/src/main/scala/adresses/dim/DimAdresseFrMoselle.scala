package adresses.dim

import adresses.job.SimpleJob
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.{DataFrame, SaveMode}

object DimAdresseFrMoselle extends SimpleJob {
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

    val nombreDeLigne = selectColumnRenameDf
      .count()

    val nbLigneApresWhere = adresseAvecToutesLesValeursDefinieDf
      .count()

    println(s"******************************* ng ligne = $nombreDeLigne")
    println(
      s"******************************* ng ligne apres where = $nbLigneApresWhere"
    )

    selectColumnRenameDf
      .show(true)

    // MKDMKD fixme ecrire la nouvelle donnée dans le csv output
//    transformDf.write
//      .mode(SaveMode.Overwrite)
//      .csv("data/output/adresses-57.csv")
//      .show(true)
  }

  override def jobName: String = "dim-adresses-fr-57"
}
