package adresses.job

import adresses.dim.DimAdresseFrMoselle

object JobCreateAdresseFrMoselle extends SimpleJob {
  override def run(): Unit = {

    DimAdresseFrMoselle
      .compute(spark)
      .show(true)

    // todo call l'api pour créer ces adresses

    // MKDMKD fixme ecrire la nouvelle donnée dans le csv output
//    adresseAvecToutesLesValeursDefinieDf
//      .coalesce(1)
//      .write
//      .options(
//        Map("header" -> "true")
//      )
//      .format("csv")
//      .mode(SaveMode.Overwrite)
//      .csv("data/output/adresses-57")
  }

  override def jobName: String = "dim-adresses-fr-57"
}
