# SET game

## Rules

The rules of the game can be found here: https://www.setgame.com/sites/default/files/instructions/SET%20INSTRUCTIONS%20-%20ENGLISH.pdf

## Game functions

* `def pickCardSet(game: Game, playerId: PlayerId, cards: Set[Card]): Either[GameError, Game]`: a player picks up a set of cards. If correct, the SET is kept by the player for one point and 3 cards from the deck are added to the board. If the player picks up cards that is not a SET, he/she loses one point, and the 3 cards are returned to the board.

* `def getPlayerScore(game: Game, playerId: PlayerId): Either[GameError, Int]`: return the player's score, which is the number of valid SETs collected - lost points (1 lost point for each invalid SET picked)

* `def dealThreeCards(game: Game): Either[GameError, Game]`: if all players agree that the board contains no SETs, 3 more cards are added to the board.

* `def deal(game: Game): Game`: add additional cards as needed to reach 'BoardSize' cards.

* `def solve(cards: Seq[Card]): Seq[CardSet]`: return all possible SETs that can be created with the cards

* `def getWinner(game: Game): Either[GameError, Player]`: return the winner of the game
