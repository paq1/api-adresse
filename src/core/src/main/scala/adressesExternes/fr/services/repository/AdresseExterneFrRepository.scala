package adressesExternes.fr.services.repository

import scala.concurrent.Future

trait AdresseExterneFrRepository[Element] {

  def insert(element: Element): Future[Unit]
  def findOne(id: String): Future[Unit]

  def findMany(): Future[List[Element]]

}
