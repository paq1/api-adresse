package shared.tools
import org.apache.spark.sql.{DataFrame, SaveMode}

case class SaverParquet() extends CanSaveDataFrame {
  override def saveDataFrame(
      df: DataFrame,
      dest: String
  ): Unit = {
    df
      .coalesce(1)
      .write
      .options(
        Map("header" -> "true")
      )
      .mode(SaveMode.Overwrite)
      .parquet(dest)
  }
}
