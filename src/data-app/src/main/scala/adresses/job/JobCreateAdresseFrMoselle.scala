package adresses.job

import adresses.data.ModelDataAdresse
import adresses.dim.DimAdresseFrMoselle
import com.adresse.json.Implicits._
import play.api.libs.json.Json
import referencesExternes.fr.commands.{
  AdresseExterneFrCommand,
  CreateAdresseExterneFrCommand
}
import referencesExternes.fr.shared.InfoReferenceExterneFr
import shared.tools.SaverParquet
import sttp.client4.quick._

object JobCreateAdresseFrMoselle extends SimpleJob {
  override def jobName: String = "dim-adresses-fr-57"

  override def run(): Unit = {

    val moselleAdresseDf = DimAdresseFrMoselle
      .compute(spark)

    dataFrameSaver
      .saveDataFrame(
        moselleAdresseDf,
        "/data/output/adresse-57"
      )

    moselleAdresseDf
      .as[ModelDataAdresse]
      .take(10)
      .foreach(createAdresseWithApi)

    // todo (call l'api / topic kafka) pour créer ces adresses
  }

  private def createAdresseWithApi(adresse: ModelDataAdresse): Unit = {

    val commande: AdresseExterneFrCommand = CreateAdresseExterneFrCommand(
      info = InfoReferenceExterneFr(
        numeroRue = adresse.numeroRue,
        nomRue = adresse.nomRue,
        codePostal = adresse.codePostal,
        ville = adresse.ville,
        pays = adresse.pays,
        id = adresse.id,
        position = None
      )
    )

    val response = quickRequest
      .post(
        uri"http://localhost:9000/adresse-externe-fr"
      )
      .header("Content-Type", "application/json")
      .body(Json.stringify(Json.toJson(commande)).getBytes)
      .send()
  }

  private val dataFrameSaver = new SaverParquet
}
