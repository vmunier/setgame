package com.setgame

case class Player(id: PlayerId, cardSets: Seq[CardSet] = Seq.empty, lostPoints: Int = 0) {
  lazy val score: Int = cardSets.size - lostPoints

  def addCardSet(cardSet: CardSet): Player = copy(cardSets = cardSet +: cardSets)

  def losePoint: Player = copy(lostPoints = lostPoints + 1)
}
