package models

case class ReportResult(
                         topAirports: List[(String, Int)],
                         bottomAirports: List[(String, Int)],
                         runways: scala.collection.mutable.Map[String, Set[String]]
                       )
