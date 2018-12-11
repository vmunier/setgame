package com.setgame

import java.util.UUID

case class PlayerId(id: UUID) {
  override def toString: String = id.toString
}

object PlayerId {
  def random(): PlayerId = PlayerId(UUID.randomUUID())
}
