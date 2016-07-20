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
                    latitude_deg: String,
                    longitude_deg: String,
                    elevation_ft: String,
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
                   length_ft: String,
                   width_ft: String,
                   surface: String,
                   lighted: String,
                   closed: String,
                   le_ident: String,
                   le_latitude_deg: Option[String],
                   le_longitude_deg: Option[String],
                   le_elevation_ft: Option[String],
                   le_heading_degT: Option[String],
                   le_displaced_threshold_ft: Option[String],
                   he_ident: Option[String],
                   he_latitude_deg: Option[String],
                   he_longitude_deg: Option[String],
                   he_elevation_ft: Option[String],
                   he_heading_degT: Option[String],
                   he_displaced_threshold_ft: Option[String]
                 )
