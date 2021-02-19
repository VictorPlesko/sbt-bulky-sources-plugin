import sbt._
import Keys._

object BulkySourcesPlugin extends AutoPlugin {

  object autoImport {
    lazy val bulkyThresholdInLines = settingKey[Int]("Minimum number of lines ")
    lazy val bulkySources = taskKey[Seq[(Int, File)]]("Bulky sources and their number in the file.")
  }

  import autoImport._

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    bulkyThresholdInLines := 100,
    bulkySources := findBulkyLines((Compile / sources).value, bulkyThresholdInLines.value),
    Test / bulkySources := findBulkyLines((Test / sources).value, bulkyThresholdInLines.value)
  )

  def findBulkyLines(files: Seq[File], thresholdDefault: Int): Seq[(Int, File)] =
    files.map(file => (IO.readLines(file).length, file))
      .filter { case (thresholdFile, _) => thresholdFile >= thresholdDefault }
      .sorted
      .reverse
}
