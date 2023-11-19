package adressesExternes.fr.research

import adressesExternes.fr.research.models.ResearchAdresseIn
import errors.data.ValidatedErr

import scala.concurrent.Future

trait ResearchAdresseFrWriteService {
  def insert(adresse: ResearchAdresseIn): Future[ValidatedErr[Unit]]

  def deleteAll(): Future[ValidatedErr[Unit]]
}
