package controllers

import models._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.libs.ws._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Result}
import javax.inject.{Inject, Singleton}

import scala.collection.mutable
import scala.util.Try

class WeatherController @Inject() ( val controllerComponents: ControllerComponents, ws: WSClient)
                                  (implicit ec: ExecutionContext) extends BaseController {

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

  def getCurrentResponseByCoord(lan: String, lon: String) =  Action.async {
    val res = queryCurrentWeather(lan, lon)
    res
  }

  def getOneTimeResponseByCoord(lan: String, lon: String) =  Action.async {
    val res = queryOneTimeWeather(lan, lon)
    res
  }

  def tempType(temp: Double): String = temp match {
    case x if (x > 303 ) => "hot";
    case x if (x < 273) => "cold";
    case _ => "moderate"
  }

  def queryOneTimeWeather( lat: String, lon: String ): Future[ Result] = {
    val APIkey = "6cf82f448e453a36ec928ce3b801279e"
    val url = s"https://api.openweathermap.org/data/2.5/onecall"
    val request: WSRequest = ws.url(url)
    val complexRequest: WSRequest =
      request
        .addQueryStringParameters( "lat" -> lat, "lon" -> lon, "exclude" -> "minutely,hourly,daily", "appid" -> APIkey )
        .addHttpHeaders("Accept" -> "application/json")

    complexRequest.get() map {
      //resp => Ok( resp.body )
      case x: WSResponse if x.status == 200 => {
        val apiResponse = Json.parse(x.body).as[OneTimeApiResponse]

        val wsResp = OneTimeWeatherResponse(
          x.status,
          Try(apiResponse.current.weather.headOption.toString).getOrElse("None"),
          apiResponse.current.temp - 273,
          tempType(apiResponse.current.temp),
          Try(apiResponse.alerts.headOption.toString).getOrElse("None")
        )
        Ok(Json.toJson(wsResp))
      }
      case _ => NotFound("ERROR")
    }
  }

  def queryCurrentWeather( lat: String, lon: String ): Future[ Result] = {
    val APIkey = "6cf82f448e453a36ec928ce3b801279e"
    val url = s"https://api.openweathermap.org/data/2.5/weather"
    val request: WSRequest = ws.url(url)
    val complexRequest: WSRequest =
      request
        .addQueryStringParameters( "lat" -> lat, "lon" -> lon, "exclude" -> "minutely,hourly,daily", "appid" -> APIkey )
        .addHttpHeaders("Accept" -> "application/json")

    complexRequest.get() map {
      //resp => Ok( resp.body )
      case x: WSResponse if x.status == 200 => {
        val apiResponse = Json.parse(x.body).as[CurrentApiResponse]
        val wsResp = CurrentWeatherResponse(
          x.status,
          Try(apiResponse.weather.head.main).getOrElse("None"),
          apiResponse.main.temp - 273,
          tempType(apiResponse.main.temp),

        )
        Ok(Json.toJson(wsResp))
      }
      case _ => NotFound("ERROR")
    }
  }

}