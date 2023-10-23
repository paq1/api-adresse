package adresses.dim


import org.apache.spark.sql.{DataFrame, SparkSession}

trait CanComputeDim {
  def compute(spark: SparkSession): DataFrame
}
