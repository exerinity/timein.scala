package com.exerinity.timein

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private[timein] object TimeDisplay {
  private val date_formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu", Locale.ENGLISH)
  private val time_12_formatter = DateTimeFormatter.ofPattern("h:mm:ss", Locale.ENGLISH)
  private val time_24_formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH)

  def print_time(label: String, zone: ZoneId): Unit = {
    val time = Instant.now().atZone(zone)
    val period = if (time.getHour < 12) "am" else "pm"
    val date = date_formatter.format(time)
    val time_12 = time_12_formatter.format(time)
    val time_24 = time_24_formatter.format(time)
    println(s"$label: $date - $time_12 $period / $time_24")
  }
}