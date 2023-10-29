package shared.tools

import org.apache.spark.sql.DataFrame

trait CanSaveDataFrame {
  def saveDataFrame(df: DataFrame, dest: String): Unit
}
