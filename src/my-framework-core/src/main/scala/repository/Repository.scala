package repository

trait Repository[F[_], Data, ID, QUERY]
    extends ReadOnlyRepository[F, Data, ID, QUERY]
    with WriteOnlyRepository[F, Data, ID] {}
