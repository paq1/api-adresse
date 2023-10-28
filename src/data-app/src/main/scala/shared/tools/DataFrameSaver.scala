package shared.tools
import org.apache.spark.sql.{DataFrame, Encoder}

class DataFrameSaver extends CanSaveDataFrame {
  override def saveDataFrame[T](
      df: DataFrame,
      dest: String
  )(implicit encoder: Encoder[T]): Unit = {

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

    val head = df.columns.toList

    println(s"head ========================================> $head")

    df
      .as[T]
      .collect()
      .toList
      .take(10)
      .foreach(affiche)
  }

  private def affiche[T](element: T): Unit = println(element)
}
