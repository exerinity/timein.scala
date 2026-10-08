package timein

import java.time.ZoneId

object Main {
  def main(args: Array[String]): Unit = {
    val input = args.mkString(" ").trim

    if (input.isEmpty) {
      System.err.println("timein: no input provided. try 'timein --help' help or 'timein london' for a city")
      System.exit(2)
    }

    if (input == "--help" || input == "-h") {
      println("usage: timein <city/timezone/offset|here>")
      println("examples: 'timein here' / 'timein new york' / 'timein Europe/London' / 'timein +5'")
      return
    }

    if (input.equalsIgnoreCase("here")) {
      TimeDisplay.print_time("here", ZoneId.systemDefault())
      return
    }

    if (input.equalsIgnoreCase("about")) {
      println("timein is a tool for checking the current time in another city or UTC offset.")
      println("you can find the original C version at https://github.com/exerinity/timein, or this at https://github.com/exerinity/timein.scala")
      return
    }

    resolve(input) match {
      case Right((label, zone)) => TimeDisplay.print_time(label, zone)
      case Left(message) =>
        System.err.println(message)
        System.exit(2)
    }
  }

  private def resolve(input: String): Either[String, (String, ZoneId)] = {
    if (OffsetParser.looks_like_offset(input)) {
      OffsetParser.parse_offset(input)
    } else {
      ZoneCatalog.find_direct(input) match {
        case Some(zone) => Right(zone)
        case None if input.contains('/') => Left(s"timein: unknown timezone: $input")
        case None => CitySearch.find_city(input).map(city => (city.name, city.zone))
      }
    }
  }
}
