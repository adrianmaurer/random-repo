package models

case class QueryData(country: String)
case class QueryResult(country: String, airports: List[(Airport, List[Runway])])
