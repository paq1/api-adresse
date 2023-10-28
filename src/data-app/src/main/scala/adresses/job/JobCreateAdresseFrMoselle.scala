package adresses.job

import adresses.data.ModelDataAdresse
import adresses.dim.DimAdresseFrMoselle
import shared.tools.DataFrameSaver

object JobCreateAdresseFrMoselle extends SimpleJob {

  val dataFrameSaver = new DataFrameSaver
  override def run(): Unit = {

    val moselleAdresseDf = DimAdresseFrMoselle
      .compute(spark)

    dataFrameSaver
      .saveDataFrame[ModelDataAdresse](moselleAdresseDf, "")

    // todo (call l'api / topic kafka) pour créer ces adresses
  }

  override def jobName: String = "dim-adresses-fr-57"
}
