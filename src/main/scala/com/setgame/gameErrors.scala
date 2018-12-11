package com.setgame

sealed abstract class GameError(message: String)

case object CardsNotFound extends GameError("At least one of the cards is not on the board")

case object WrongNumberOfCards extends GameError("Three cards must be selected")

case class PlayerNotFound(playerId: PlayerId) extends GameError(s"Player $playerId not found")

case class BoardContainsSets(numberOfSets: Int)
    extends GameError(s"The board contains $numberOfSets SETs")

case object EmptyDeck extends GameError(s"The deck is empty")

case class GameContainsSets(numberOfSets: Int)
    extends GameError(s"The game still contains $numberOfSets SETs")

case class MultipleWinners(players: Seq[Player], score: Int)
    extends GameError(
      s"Players ${players.map(_.id).mkString(",")} are all winners with a score of $score")

case object NoPlayers extends GameError(s"There are no players in the game")
