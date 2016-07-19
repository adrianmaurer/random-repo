package controllers

import javax.inject._
import akka.actor.ActorSystem
import models._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, ExecutionContext}
import kantan.csv.ops._

import scala.util.{Failure, Success}

// kantan.csv syntax
import kantan.csv.generic._ // case class decoder derivation


@Singleton
class HomeController @Inject() (val messagesApi: MessagesApi, actorSystem: ActorSystem)(implicit exec: ExecutionContext)
  extends Controller with I18nSupport {

  val queryForm = Form(
    mapping(
      "country" -> nonEmptyText
    )(QueryData.apply)(QueryData.unapply)
  )

  def index = Action {
    Ok(views.html.index(queryForm))
  }

  def queryAirport = Action { implicit request =>
    queryForm.bindFromRequest fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      queryData => {
        // TODO: create pipline aggregator using actors
        // TODO: stream csv files and short circuit for country
        getCountry(queryData.country.toUpperCase) match {
          case Right(code) => {
            val airports = getAirports(code)
            val result = QueryResult(code, getRunways(airports))
            Ok(views.html.queryResult(result))
          }
          case Left(err) => {
            BadRequest
          }
          case _ => {
            BadRequest
          }
        }
      })
  }

  def report = Action {
    Ok(views.html.index(queryForm))
  }

  private def getCountry(country: String): Either[String, String] = {
    val rawData: java.net.URL = getClass.getResource("/resources/countries.csv")
    val iterator = rawData.asCsvReader[Country](',', header = true)
    val filtered = iterator.filterResult(_.code == country).mapResult(_.code)
    filtered.hasNext match {
      case true => Right(filtered.next.get)
      case _ => Left("country not found")
    }
  }

  private def getAirports(countryCode: String) = {
    val rawData: java.net.URL = getClass.getResource("/resources/airports.csv")
    val iterator = rawData.asCsvReader[Airport](',', header = true)
    val filtered = iterator.filterResult(_.iso_country == countryCode)
    filtered map { result =>
      result.get
    } toList
  }

  private def getRunways(airports: List[Airport]): List[(Airport, List[Runway])] = {
    val rawData: java.net.URL = getClass.getResource("/resources/runways.csv")
    val iterator = rawData.asCsvReader[Runway](',', header = true)
    val airportRefs = airports.map(_.id)
    val filtered = iterator.filterResult(runway => airportRefs.contains(runway.airport_ref))
    val airportMap = scala.collection.mutable.Map[Long, List[Runway]]()
    val runways = filtered map { result =>
      result.get
    } toList
    // TODO: optimize by using list buffer and removing as assigned or store in map
    var result = List[(Airport, List[Runway])]()
    airports.foreach { airport =>
      result :::= List((airport, runways.filter(_.airport_ref == airport.id)))
    }
    result
  }
}
