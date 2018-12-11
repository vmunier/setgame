package com.setgame
import enumeratum._

sealed trait CardShape extends EnumEntry
object CardShape extends Enum[CardShape] {
  val values = findValues
  case object Oval     extends CardShape
  case object Squiggle extends CardShape
  case object Diamond  extends CardShape
}

sealed trait CardColour extends EnumEntry
object CardColour extends Enum[CardColour] {
  val values = findValues
  case object Red    extends CardColour
  case object Purple extends CardColour
  case object Green  extends CardColour
}

sealed trait CardCount extends EnumEntry
object CardCount extends Enum[CardCount] {
  val values = findValues
  case object One   extends CardCount
  case object Two   extends CardCount
  case object Three extends CardCount
}

sealed trait CardShading extends EnumEntry
object CardShading extends Enum[CardShading] {
  val values = findValues
  case object Solid    extends CardShading
  case object Striped  extends CardShading
  case object Outlined extends CardShading
}
