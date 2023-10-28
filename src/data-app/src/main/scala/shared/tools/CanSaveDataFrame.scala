package shared.tools

import org.apache.spark.sql.{DataFrame, Encoder}

trait CanSaveDataFrame {
  def saveDataFrame[T](df: DataFrame, dest: String)(implicit encoder: Encoder[T]): Unit
}
