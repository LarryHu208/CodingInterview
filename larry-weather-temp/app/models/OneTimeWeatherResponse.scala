package models

import play.api.libs.json.{Format, Json, OFormat}

case class OneTimeWeatherResponse(status: Int, condition: String, temperature: Double = 0, tempType: String = "", alert: String)

object OneTimeWeatherResponse  {
  implicit val format: Format[OneTimeWeatherResponse] = Json.format[OneTimeWeatherResponse]
}

case class Current(temp: Double, feels_like: Double, humidity: Double, clouds: Double, weather: Seq[Weather], rain: Option[Rain] = None)

object Current  {
  implicit val format: Format[Current] = Json.format[Current]
}

case class Alerts(sender_name: String, event: String, start: Double, end: Double, description: String, tags: String)

object Alerts  {
  implicit val format: Format[Alerts] = Json.format[Alerts]
}

// Minutely, Hourly, Daily are disabled so case classes are not here

case class OneTimeApiResponse(
                      lat: Double,
                      lon: Double,
                      timezone: String,
                      current: Current,
                      alerts: Option[Seq[Alerts]] = None
                      )

object OneTimeApiResponse  {
  implicit val format: Format[OneTimeApiResponse] = Json.format[OneTimeApiResponse]
}