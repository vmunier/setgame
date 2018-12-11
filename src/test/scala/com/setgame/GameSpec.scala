package com.setgame

import org.scalatest.Matchers._
import org.scalatest._

class GameSpec extends FlatSpec {
  "A Game" should "have 81 cards" in {
    // 4 card features (shape, colour, count, shading) and each feature has 3 variants (e.g. Red, Purple, Green)
    // So, there are 3^4 combinations or 81 cards.
    Game.allCards.size shouldBe (81)
  }

  it should "find 1080 unique sets when using solve on all the cards" in {
    // Given any two cards, there is exactly one card that forms a set with those two cards
    // any two cards: 81 * 80 / 2
    // 3 ways to add the last card: x _ _, _ x _, _ _ x
    // So there are 81 * 80 / 2 / 3 = 1080 valid card sets
    Game.solve(Game.allCards).size shouldBe (1080)
  }

  it should "increase score when a player picks a SET" in new TwoPlayersGame {
    val gameWithBoard = game.copy(board = validSet)
    val newGame       = Game.pickCardSet(gameWithBoard, firstPlayerId, validSet).right.get
    val player        = newGame.players.get(firstPlayerId).get
    player.score shouldBe 1
    player.cardSets.toSet shouldBe Set(CardSet.build(validSet).get)
  }

  it should "decrease score when a player picks an invalid SET" in new TwoPlayersGame {
    val gameWithBoard = game.copy(board = invalidSet)
    val newGame       = Game.pickCardSet(gameWithBoard, firstPlayerId, invalidSet).right.get
    val player        = newGame.players.get(firstPlayerId).get
    player.score shouldBe -1
    player.cardSets shouldBe empty
  }

  it should "deal cards: add 'BoardSize' cards" in {
    val updatedGame = Game.deal(Game(2))
    updatedGame.board.size shouldBe (Game.BoardSize)
    updatedGame.deck.size shouldBe (Game.allCards.size - Game.BoardSize)
  }

  it should "deal cards: keep the board unchanged when it already has 'BoardSize' cards" in {
    val updatedGame = Game.deal(Game.deal(Game(2)))
    updatedGame.board.size shouldBe (Game.BoardSize)
    updatedGame.deck.size shouldBe (Game.allCards.size - Game.BoardSize)
  }

  it should "deal cards: keep the board unchanged when it has more than 'BoardSize' cards" in {
    val boardSize   = Game.BoardSize + 2
    val updatedGame = Game.deal(Game.addCards(Game(2), boardSize))
    updatedGame.board.size shouldBe (boardSize)
    updatedGame.deck.size shouldBe (Game.allCards.size - boardSize)
  }

  it should "deal three cards: empty deck" in {
    Game.dealThreeCards(Game(2).copy(deck = Seq.empty)) shouldBe Left(EmptyDeck)
  }

  it should "deal three cards: board contains SETs" in new TwoPlayersGame {
    Game.dealThreeCards(game.copy(board = validSet)) shouldBe Left(BoardContainsSets(1))
  }

  it should "deal three cards: board contains 3 more cards" in new TwoPlayersGame {
    val deck        = (Game.allCards.toSet -- invalidSet).toSeq
    val updatedGame = Game.dealThreeCards(game.copy(deck = deck, board = invalidSet)).right.get
    updatedGame.board.size shouldBe (invalidSet.size + 3)
  }

  it should "detect non existing players" in {
    val playerId = PlayerId.random()
    Game.pickCardSet(Game(2), playerId, Set.empty) shouldBe Left(PlayerNotFound(playerId))
  }

  it should "detect when a user picks up cards that are not on the board" in new TwoPlayersGame {
    val cardsNotOnTheBoard = game.deck.take(3).toSet
    Game.pickCardSet(game, firstPlayerId, cardsNotOnTheBoard) shouldBe Left(CardsNotFound)
  }

  it should "detect invalid card set, not enough cards" in new TwoPlayersGame {
    val cards = game.board.take(2)
    Game.pickCardSet(game, firstPlayerId, cards) shouldBe Left(WrongNumberOfCards)
  }

  it should "detect invalid card set, too many cards" in new TwoPlayersGame {
    val cards = game.board.take(4)
    Game.pickCardSet(game, firstPlayerId, cards) shouldBe Left(WrongNumberOfCards)
  }

  it should "get winner" in new TwoPlayersGame {
    val lastSetGame = game.copy(deck = Seq.empty, board = validSet)
    val endGame     = Game.pickCardSet(lastSetGame, firstPlayerId, validSet).right.get
    val winner      = Game.getWinner(endGame).right.get
    winner.id shouldBe (firstPlayerId)
    winner.score shouldBe (1)
  }

  it should "get winner: game still contains sets" in new TwoPlayersGame {
    val lastSetGame = game.copy(deck = Seq.empty, board = validSet)
    Game.getWinner(lastSetGame) shouldBe (Left(GameContainsSets(1)))
  }

  it should "get winner: multiple winners" in new TwoPlayersGame {
    val endGame = game.copy(deck = Seq.empty, board = Set.empty)
    Game.getWinner(endGame) shouldBe (Left(MultipleWinners(game.players.values.toSeq, 0)))
  }

  it should "get winner: no players error" in {
    val endGame = Game(deck = Seq.empty, board = Set.empty, players = Map.empty)
    Game.getWinner(endGame) shouldBe (Left(NoPlayers))
  }
}

trait TwoPlayersGame {
  val game                               = Game(2)
  val Seq(firstPlayerId, secondPlayerId) = game.players.keys.toSeq

  val validSet: Set[Card] = {
    val card = Game.allCards.head
    CardShape.values.map(shape => card.copy(shape = shape)).toSet
  }

  val invalidSet: Set[Card] = {
    val sameColour = Game.allCards.filter(_.colour == CardColour.Purple).take(3)
    sameColour.updated(0, sameColour.head.copy(colour = CardColour.Red)).toSet
  }
}
