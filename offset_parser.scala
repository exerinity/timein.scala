package timein

import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

private[timein] object OffsetParser {
  private val offset_pattern = "(?i)^(?:UTC|GMT)?([+-]?)([0-9]{1,2})(?::([0-9]{2}))?$".r

  def looks_like_offset(input: String): Boolean = {
    val upper = input.toUpperCase(Locale.ROOT)
    input.headOption.exists(c => c == '+' || c == '-' || c.isDigit) ||
      upper.startsWith("UTC+") || upper.startsWith("UTC-") ||
      upper.startsWith("GMT+") || upper.startsWith("GMT-") ||
      upper.matches("^(UTC|GMT)[0-9].*")
  }

  def parse_offset(input: String): Either[String, (String, ZoneId)] = {
    input match {
      case offset_pattern(sign, hours_text, minutes_text) =>
        val hours = hours_text.toInt
        val minutes = Option(minutes_text).fold(0)(_.toInt)
        val magnitude = hours * 60 + minutes
        val total = if (sign == "-") -magnitude else magnitude
        if (minutes < 60 && total >= -12 * 60 && total <= 14 * 60) {
          val zone = ZoneOffset.ofTotalSeconds(total * 60)
          Right((offset_label(total), zone))
        } else {
          Left(s"timein: invalid utc offset: $input. use a value such as +5, -4, or 5 (no plus/minus will resolve to forward offset)")
        }
      case _ => Left(s"timein: invalid utc offset: $input. use a value such as +5, -4, or 5 (no plus/minus will resolve to forward offset)")
    }
  }

  private def offset_label(total_minutes: Int): String = {
    val sign = if (total_minutes < 0) "-" else "+"
    val magnitude = math.abs(total_minutes)
    val hours = magnitude / 60
    val minutes = magnitude % 60
    if (minutes == 0) s"UTC$sign$hours"
    else f"UTC$sign$hours:$minutes%02d"
  }
}