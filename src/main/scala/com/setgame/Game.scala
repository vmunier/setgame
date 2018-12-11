package com.setgame

import scala.util.Random

/**
  * Game is the current state of a SET game
  *
  * @param deck    the remaining cards in the deck
  * @param board   the cards placed face up on the board
  * @param players players in the game
  */
case class Game(deck: Seq[Card], board: Set[Card], players: Map[PlayerId, Player])

case object Game {
  val BoardSize = 12

  lazy val allCards: Seq[Card] = for {
    shape   <- CardShape.values
    colour  <- CardColour.values
    count   <- CardCount.values
    shading <- CardShading.values
  } yield Card(shape, colour, count, shading)

  /**
    * A player picks up a set of cards.
    * If correct, the SET is kept by the player for one point and 3 cards from the deck are added to the board.
    * If the player picks up cards that is not a SET, he/she loses one point, and the 3 cards are returned to the board.
    *
    * @param game     the current game state
    * @param playerId ID of player
    * @param cards    the cards being picked up the player
    * @return the updated game if it was a SET, otherwise return a game error
    */
  def pickCardSet(game: Game, playerId: PlayerId, cards: Set[Card]): Either[GameError, Game] = {
    for {
      player <- game.players.get(playerId).toRight(PlayerNotFound(playerId))
      _      <- validateCards(cards, game.board)
    } yield {
      CardSet.build(cards) match {
        case Some(cardSet) =>
          val updatedBoard   = game.board -- cardSet.cards
          val updatedPlayers = game.players.updated(playerId, player.addCardSet(cardSet))
          val updatedGame    = game.copy(board = updatedBoard, players = updatedPlayers)
          deal(updatedGame)
        case None =>
          game.copy(players = game.players.updated(playerId, player.losePoint))
      }
    }
  }

  /**
    * Return the player's score,
    * which is the number of valid SETs collected - lost points (1 lost point for each invalid SET picked)
    *
    * @param game     the current game state
    * @param playerId ID of player
    * @return the player's score
    */
  def getPlayerScore(game: Game, playerId: PlayerId): Either[GameError, Int] = {
    game.players.get(playerId).map(_.score).toRight(PlayerNotFound(playerId))
  }

  /**
    * If all players agree that the board contains no SETs, 3 more cards are added to the board.
    *
    * @param game the current game state
    * @return the updated game or a game error if the board contains SETs or if the deck is empty
    */
  def dealThreeCards(game: Game): Either[GameError, Game] = {
    val cardSets = solve(game.board.toSeq)
    if (!cardSets.isEmpty) {
      Left(BoardContainsSets(cardSets.size))
    } else if (game.deck.isEmpty) {
      Left(EmptyDeck)
    } else {
      val (newCards, newDeck) = game.deck.splitAt(3)
      Right(
        game.copy(
          deck = newDeck,
          board = game.board ++ newCards
        ))
    }
  }

  /**
    * Adds additional cards as needed to reach 'BoardSize' cards.
    *
    * @param game the current game state
    * @return the updated game
    */
  def deal(game: Game): Game = addCards(game, BoardSize - game.board.size)

  /**
    * Move 'numberOfCards' cards from the deck to the board
    *
    * @param game          the current game state
    * @param numberOfCards number of cards to add
    * @return the updated game
    */
  def addCards(game: Game, numberOfCards: Int): Game = {
    val (newCards, newDeck) = game.deck.splitAt(numberOfCards)
    game.copy(
      deck = newDeck,
      board = game.board ++ newCards
    )
  }

  /**
    * Return all possible SETs that can be created with the cards
    *
    * @param cards cards
    * @return all possible SETs
    */
  def solve(cards: Seq[Card]): Seq[CardSet] = {
    cards.combinations(3).flatMap(threeCards => CardSet.build(threeCards.toSet)).toList
  }

  /**
    * Get the winner of the game. Return a game error if the game still contains SETs or if there are multiple winners.
    *
    * @param game the current game state
    * @return the player who won the game
    */
  def getWinner(game: Game): Either[GameError, Player] = {
    val cardSets = solve(game.board.toSeq ++ game.deck)
    if (!cardSets.isEmpty) {
      Left(GameContainsSets(cardSets.size))
    } else if (game.players.isEmpty) {
      Left(NoPlayers)
    } else {
      val players  = game.players.values.toSeq
      val maxScore = players.map(_.score).max
      val winners  = players.filter(_.score == maxScore)
      winners match {
        case Seq(winner) => Right(winner)
        case _           => Left(MultipleWinners(winners, maxScore))
      }
    }
  }

  private def validateCards(cards: Set[Card], board: Set[Card]): Either[GameError, Set[Card]] = {
    if (!cards.subsetOf(board)) {
      Left(CardsNotFound)
    } else if (cards.size != 3) {
      Left(WrongNumberOfCards)
    } else {
      Right(cards)
    }
  }

  def apply(numberOfPlayers: Int): Game = {
    val players =
      Seq.fill(numberOfPlayers)(PlayerId.random).map(playerId => playerId -> Player(playerId)).toMap
    Game(deck = Random.shuffle(allCards), board = Set.empty, players = players)
  }
}
