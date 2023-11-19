package adressesExternes.fr.research

import adressesExternes.fr.research.models.ResearchAdresseOut

import scala.concurrent.Future

trait ResearchAdresseFrReadService {
  def fulltext(queryString: String): Future[List[ResearchAdresseOut]]
}
