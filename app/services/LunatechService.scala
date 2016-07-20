package services

import scala.collection.mutable
import scala.concurrent.Future
import kantan.csv.ops._
import scala.concurrent.ExecutionContext.Implicits.global
// kantan.csv syntax
import kantan.csv.generic._ // case class decoder derivation

import models._

trait CSVParser {
  def getCountry(country: String): Either[String, String] = {
    val rawData: java.net.URL = getClass.getResource("/resources/countries.csv")
    val iterator = rawData.asCsvReader[Country](',', header = true)
    val filtered = iterator.filterResult(_.code == country).mapResult(_.code)
    filtered.hasNext match {
      case true => Right(filtered.next.get)
      case _ => Left("country not found")
    }
  }

  def getAirports(countryCode: String) = {
    val rawData: java.net.URL = getClass.getResource("/resources/airports.csv")
    val iterator = rawData.asCsvReader[Airport](',', header = true)
    val filtered = iterator.filterResult(_.iso_country == countryCode)
    filtered map { result =>
      result.get
    } toList
  }

  def getRunways(airports: List[Airport]): List[(Airport, List[Runway])] = {
    val rawData: java.net.URL = getClass.getResource("/resources/runways.csv")
    val iterator = rawData.asCsvReader[Runway](',', header = true)
    val airportRefs = airports.map(_.id)
    val filtered = iterator.filterResult(runway => airportRefs.contains(runway.airport_ref))
    val airportMap = scala.collection.mutable.Map[Long, List[Runway]]()
    filtered foreach { result =>
      airportMap.contains(result.get.airport_ref) match {
        case true => airportMap(result.get.airport_ref) = airportMap(result.get.airport_ref) ::: List(result.get)
        case false => airportMap(result.get.airport_ref) = List(result.get)
      }
    }
    var result = List[(Airport, List[Runway])]()
    airports.foreach { airport =>
      result :::= List((airport, airportMap.getOrElse(airport.id, List())))
    }
    result
  }

  def airportByCountry: mutable.Map[String, List[Airport]] = {
    val rawData: java.net.URL = getClass.getResource("/resources/airports.csv")
    val iterator = rawData.asCsvReader[Airport](',', header = true)
    val result = scala.collection.mutable.Map[String, List[Airport]]()
    iterator foreach { airport =>
      result.contains(airport.get.iso_country) match {
        case true => result(airport.get.iso_country) = result(airport.get.iso_country) ::: List(airport.get)
        case false => result(airport.get.iso_country) = List(airport.get)
      }
    }
    result
  }

  def runwayByAirport = {
    val rawData: java.net.URL = getClass.getResource("/resources/runways.csv")
    val iterator = rawData.asCsvReader[Runway](',', header = true)
    val result = scala.collection.mutable.Map[Long, List[Runway]]()
    iterator foreach { runway =>
      result.contains(runway.get.airport_ref) match {
        case true => result(runway.get.airport_ref) = result(runway.get.airport_ref) ::: List(runway.get)
        case false => result(runway.get.airport_ref) = List(runway.get)
      }
    }
    result
  }
}

object LunatechService extends CSVParser {

  /*
  Optimized for single pass through csv
   */
  def queryAirport(country: String): QueryResult = {
    getCountry(country.toUpperCase) match {
      case Right(code) => {
        val airports = getAirports(code)
        QueryResult(code, getRunways(airports))
      }
      case Left(err) => {
        QueryResult(country, List())
      }
    }
  }

  def generateReport: Future[ReportResult] = {
    val rawData: java.net.URL = getClass.getResource("/resources/countries.csv")
    val iterator = rawData.asCsvReader[Country](',', header = true)
    val countryAirports = scala.collection.mutable.Map[String, Int]()
    val countryRunway = scala.collection.mutable.Map[String, Set[String]]()

    // define futures here so for comprehension is async
    val futureAirportByCountry = Future {
      airportByCountry
    }
    val futureRunwayByAirport = Future {
      runwayByAirport
    }
    for {
      airports <- futureAirportByCountry
      runways <- futureRunwayByAirport
    } yield {
      iterator foreach { country =>
        // airport count
        countryAirports(country.get.code) = airports.getOrElse(country.get.code, List()).size

        // runway types
        val surfaces = airports.getOrElse(country.get.code, List()).flatMap { airport =>
          runways.getOrElse(airport.id, List()).map(_.surface)
        }
        countryRunway(country.get.code) = surfaces.toSet
        //        Logger.debug("iteration")
      }
      val sortedCountryAirports = countryAirports.toList sortBy(- _._2)
      // because I know there are more than 20 I can slice
      ReportResult(sortedCountryAirports.take(10), sortedCountryAirports.drop(sortedCountryAirports.length - 10), countryRunway)
    }
  }
}
