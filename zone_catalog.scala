package timein

import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

private[timein] final case class City(name: String, zone: ZoneId, normalized_name: String)

private[timein] object ZoneCatalog {
  private val zone_ids = ZoneId.getAvailableZoneIds.toArray(new Array[String](0)).toVector.sorted
  private val zone_lookup = zone_ids.map(id => id.toLowerCase(Locale.ROOT) -> id).toMap
  private val legacy_zones = Vector(
    "EST" -> ZoneOffset.ofHours(-5),
    "HST" -> ZoneOffset.ofHours(-10),
    "MST" -> ZoneOffset.ofHours(-7),
    "ROC" -> ZoneId.of("Asia/Taipei")
  )

  val cities: Vector[City] = zone_ids.map { id =>
    val name = id.substring(id.lastIndexOf('/') + 1).replace("_", "")
    City(name, ZoneId.of(id), CitySearch.normalize(name))
  } ++ legacy_zones.map { case (name, zone) =>
    City(name, zone, CitySearch.normalize(name))
  }

  def find_direct(input: String): Option[(String, ZoneId)] = {
    zone_lookup.get(input.toLowerCase(Locale.ROOT)).map(id => (id, ZoneId.of(id)))
  }
}