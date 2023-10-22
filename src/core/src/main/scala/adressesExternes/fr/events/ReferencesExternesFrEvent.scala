package adressesExternes.fr.events

import java.time.Instant

trait ReferencesExternesFrEvent {
  def at: Instant
  def by: String
}
