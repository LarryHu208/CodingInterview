package Services

import models._
import play.api.libs.json.Json
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.mvc.{ControllerComponents,BaseController}
import play.api.libs.ws._

class WeatherService @Inject() (ws: WSClient, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {

  def tempType(temp: Double): String = {
    if (temp > 303 ) "hot"
    else if (temp < 273) "cold"
    else "cool"
  }

  def queryWeather( lat: String, lon: String, APIkey: String): Future[ WeatherResponse ] = {
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
            WeatherResponse(x.status, apiResponse.weather.head.main, apiResponse.main.temp, tempType(apiResponse.main.temp))
          }
          case _ => WeatherResponse(500, "error")
        }
    }
  }

  //val weatherResponse = queryWeather("5", "10")



}