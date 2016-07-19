package models

case class Country(
                    id: Long,
                    code: String,
                    name: String,
                    continent: String,
                    wikipedia_link: String,
                    keywords: Option[String]
                  )

case class Airport(
                    id: Long,
                    ident: String,
                    airport_type: String,
                    name: String,
                    latitude_deg: Float,
                    longitude_deg: Float,
                    elevation_ft: Long,
                    continent: String,
                    iso_country: String,
                    iso_region: String,
                    municipality: String,
                    scheduled_service: String,
                    gps_code: String,
                    iata_code: Option[String],
                    local_code: String,
                    home_link: Option[String],
                    wikipedia_link: Option[String],
                    keywords: Option[String]
                  )

case class Runway(
                   id: Long,
                   airport_ref: Long,
                   airport_ident: String,
                   length_ft: Long,
                   width_ft: Long,
                   surface: String,
                   lighted: Int,
                   closed: Int,
                   le_ident: String,
                   le_latitude_deg: Option[Float],
                   le_longitude_deg: Option[Float],
                   le_elevation_ft: Option[Long],
                   le_heading_degT: Option[Float],
                   le_displaced_threshold_ft: Option[Long],
                   he_ident: Option[String],
                   he_latitude_deg: Option[Float],
                   he_longitude_deg: Option[Float],
                   he_elevation_ft: Option[Float],
                   he_heading_degT: Option[Float],
                   he_displaced_threshold_ft: Option[Float]
                 )
