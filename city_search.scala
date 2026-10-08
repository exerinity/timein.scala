package com.exerinity.timein

import java.text.Normalizer
import java.util.Locale

private[timein] object CitySearch {
  def find_city(input: String): Either[String, City] = {
    val query = normalize(input)
    if (query.isEmpty) return Left(s"timein: no matches for: '$input'")

    val exact = ZoneCatalog.cities.filter(_.normalized_name == query)
    if (exact.nonEmpty) return select_city(input, exact)

    val prefix = ZoneCatalog.cities.filter(_.normalized_name.startsWith(query))
    if (prefix.nonEmpty) return select_city(input, prefix)

    val substring = ZoneCatalog.cities.filter(_.normalized_name.contains(query))
    if (substring.nonEmpty) return select_city(input, substring)

    val ranked = ZoneCatalog.cities.map(city => city -> edit_distance(query, city.normalized_name))
    val best_distance = ranked.map(_._2).min
    val best = ranked.collect { case (city, distance) if distance == best_distance => city }
    val allowed_distance = math.min(3, math.max(1, math.round(query.length * 0.3f)))
    val best_ratio = best_distance.toDouble / math.max(query.length, best.head.normalized_name.length)

    if (best_distance <= allowed_distance && best_ratio <= 0.34)
      select_city(input, best)
    else
      Left(s"timein: no matches for: $input")
  }

  def normalize(input: String): String = {
    val decomposed = Normalizer.normalize(input.toLowerCase(Locale.ROOT), Normalizer.Form.NFKD)
    val result = new java.lang.StringBuilder
    decomposed.codePoints().forEach { code_point =>
      if (Character.isLetterOrDigit(code_point)) result.appendCodePoint(code_point)
    }
    result.toString
  }

  private def select_city(input: String, matches: Vector[City]): Either[String, City] = {
    val distinct = matches
      .groupBy(city => (city.normalized_name, city.zone.getRules))
      .values
      .map(_.minBy(_.zone.getId))
      .toVector
      .sortBy(city => (city.name, city.zone.getId))

    if (distinct.length == 1) Right(distinct.head)
    else {
      val choices = distinct.take(8).map(city => s"${city.name} (${city.zone.getId})").mkString(", ")
      val remainder = if (distinct.length > 8) s", and ${distinct.length - 8} more" else ""
      Left(s"timein: ambiguous location: '$input'. please supply one of these IANA zone IDs: $choices$remainder")
    }
  }

  private def edit_distance(left: String, right: String): Int = {
    val previous = Array.tabulate(right.length + 1)(identity)
    val current = new Array[Int](right.length + 1)

    for (i <- 1 to left.length) {
      current(0) = i
      for (j <- 1 to right.length) {
        val substitution = if (left.charAt(i - 1) == right.charAt(j - 1)) 0 else 1
        current(j) = math.min(
          math.min(current(j - 1) + 1, previous(j) + 1),
          previous(j - 1) + substitution
        )
      }
      Array.copy(current, 0, previous, 0, current.length)
    }

    previous(right.length)
  }
}