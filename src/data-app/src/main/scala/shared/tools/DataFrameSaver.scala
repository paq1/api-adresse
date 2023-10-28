package shared.tools
import org.apache.spark.sql.{DataFrame, Encoder}

import java.io.{File, FileWriter}
import java.nio.file.Paths

class DataFrameSaver extends CanSaveDataFrame {
  override def saveDataFrame[T <: CanBeCsvLine](
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

    val head = df.columns.toList.mkString(";")
    val corps = df
      .as[T]
      .collect()
      .toList
      .take(2)
      .map(_.createLine)
      .mkString("\n")

    val content = s"$head\n$corps"

    writeFile(content, dest)
  }

  private def writeFile(content: String, dest: String): Unit = {

    val projectLocalPath = new File(
      "."
    ).getCanonicalPath + dest
    val file = new File(projectLocalPath)
    file.createNewFile()
    val fileWriter = new FileWriter(file)
    fileWriter.write(content)
    fileWriter.close()
  }
}
