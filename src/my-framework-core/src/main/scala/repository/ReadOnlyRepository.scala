package repository

trait ReadOnlyRepository[F[_], E, ID, QUERY] {
  def fetchOne(id: ID): F[Option[E]]
  def fetchMany(query: QUERY): F[List[E]]
}
