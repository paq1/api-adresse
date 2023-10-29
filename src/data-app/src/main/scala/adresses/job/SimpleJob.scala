package adresses.job

import org.apache.spark.sql.SparkSession

trait SimpleJob {
  def run: Unit
  def jobName: String
  def spark: SparkSession = SparkSession.builder
    .master("local[*]")
    .appName(jobName)
    .getOrCreate

}
