package controllers

import akka.actor.ActorSystem
import akka.actor.TypedActor.dispatcher
import models._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.mvc.{request, _}
import play.api.libs.ws._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.collection.mutable
import play.api.libs.concurrent.CustomExecutionContext

// Make sure to bind the new context class to this trait using one of the custom
// binding techniques listed on the "Scala Dependency Injection" documentation page
/*
trait MyExecutionContext extends ExecutionContext

class MyExecutionContextImpl (system: ActorSystem)
  extends CustomExecutionContext(system, "my.executor")
    with MyExecutionContext
*/

/*
class HomeController @Inject() (myExecutionContext: MyExecutionContext, val controllerComponents: ControllerComponents)
  extends BaseController {
  def index = Action.async {
    Future {
      // Call some blocking API
      Ok("result of blocking call")
    }(myExecutionContext)
  }
}
*/

class WeatherController @Inject() ( val controllerComponents: ControllerComponents, ws: WSClient)
  extends BaseController {

  implicit val weatherListJson = Json.format[WeatherItem]
  private val weatherList = new mutable.ListBuffer[WeatherItem]()
  weatherList += WeatherItem(1, "snowing", 5, "cold")
  weatherList += WeatherItem(2, "sunny", 80, "hot")

  def getAll(): Action[AnyContent] = Action {
    if (weatherList.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(weatherList))
    }
  }

  def getById(itemId: Long) = Action {
    val foundItem = weatherList.find(_.id == itemId)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  def getResponseByCoord(lan: String, lon: String) =  Action.async {
    val APIkey = "6cf82f448e453a36ec928ce3b801279e"
    val url = s"https://api.openweathermap.org/data/2.5/weather"
    val request: WSRequest = ws.url(url)
    val complexRequest: WSRequest =
      request
        .addQueryStringParameters( "lat" -> lan, "lon" -> lon, "appid" -> APIkey )
        .addHttpHeaders("Accept" -> "application/json")

    val res = complexRequest.get() map {
      resp => Ok( resp.body )
      /*
      case x: WSResponse if x.status == 200 => {
        val apiResponse = Json.toJson(x.body).as[ApiResponse]
        val wsResp = WeatherResponse(x.status, "success", apiResponse.weather.head.main, apiResponse.main.temp, tempType(apiResponse.main.temp))
        Ok(Json.toJson(wsResp))
      }
      case _ => NotFound("ERROR") */
    }
    //val res = Future( Ok("test"))
    res
  }

  def tempType(temp: Double): String = {
    if (temp > 303 ) "hot"
    else if (temp < 273) "cold"
    else "cool"
  }

/*  def queryWeather( lat: String, lon: String, APIkey: String): Future[ Result] = {
    //takes in Lat and Long
    val url = s"https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${APIkey}"
    val request: WSRequest = ws.url(url)
    val complexRequest: WSRequest =
      request
        .addHttpHeaders("Accept" -> "application/json")
        .withRequestTimeout(10000.millis)

    complexRequest.get() map {
      resp =>
        resp match {
          case x: WSResponse if x.status == 200 => {
            val apiResponse = Json.toJson(x.body).as[ApiResponse]
            val wsResp = WeatherResponse( x.status, "success", apiResponse.weather.head.main, apiResponse.main.temp, tempType(apiResponse.main.temp))
            Ok( Json.toJson( wsResp ))
          }
          case _ => Ok( "NotFound ")
        }
    }
  }

 */
}