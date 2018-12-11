package com.setgame

case class CardSet(cards: Set[Card])

object CardSet {

  /**
    * Build a SET from a set of cards
    * @param cards set of cards
    * @return Some(new CardSet(cards)) if there are three cards that represent a valid SET,
    *         otherwise return None
    */
  def build(cards: Set[Card]): Option[CardSet] = {
    val colours  = cards.map(_.colour)
    val counts   = cards.map(_.count)
    val shadings = cards.map(_.shading)
    val shapes   = cards.map(_.shape)
    val features = Seq(colours, counts, shadings, shapes)

    // a feature must be either the same on all 3 cards, or different on each of the 3 cards
    if (cards.size == 3 && features.forall(f => f.size == 1 || f.size == 3)) {
      Some(new CardSet(cards))
    } else {
      None
    }
  }
}
