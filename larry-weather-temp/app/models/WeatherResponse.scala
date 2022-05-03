package models

import play.api.libs.json.{Format, Json, OFormat}

case class WeatherResponse(status: Int, message: String = "Fail", condition: String = "", temperature: Double = 0, Temptype: String = "")

object WeatherResponse  {
  implicit val format: Format[WeatherResponse] = Json.format[WeatherResponse]
}

case class Coord( lon: Double, lat: Double)

object Coord {
  implicit val format: Format[Coord] = Json.format[Coord]
}

case class Weather( id: Double, main: String, description: String, icon: String)

object Weather  {
  implicit val format: Format[Weather] = Json.format[Weather]
}

case class Main( temp: Double, feels_like: Double, humidity: Double)

object Main  {
  implicit val format: Format[Main] = Json.format[Main]
}

case class Wind( speed: Double, deg: Double, gust: Double)

object Wind  {
  implicit val format: Format[Wind] = Json.format[Wind]
}

case class Rain(lh: Double)

object Rain  {
  implicit val format: Format[Rain] = Json.format[Rain]
}

case class Clouds(all: Double)

object Clouds  {
  implicit val format: Format[Clouds] = Json.format[Clouds]
}

case class ApiResponse(coord: Coord, weather: Seq[Weather], main: Main, wind: Wind, rain: Rain, clouds: Clouds)
object ApiResponse  {
  implicit val format: Format[ApiResponse] = Json.format[ApiResponse]
}