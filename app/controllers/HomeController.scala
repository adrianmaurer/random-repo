package controllers

import javax.inject._

import akka.actor.ActorSystem
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class HomeController @Inject() (val messagesApi: MessagesApi, actorSystem: ActorSystem)(implicit exec: ExecutionContext)
  extends Controller with I18nSupport {

  import services.LunatechService

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
        // TODO: create pipeline aggregator using actors
        Ok(views.html.queryResult(LunatechService.queryAirport(queryData.country)))
      })
  }

  def report = Action.async {
    // define futures first so for comprehension runs async
    for {
      report <- LunatechService.generateReport
    } yield Ok(views.html.report(report))
  }
}
