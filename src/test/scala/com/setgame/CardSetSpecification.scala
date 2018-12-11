package com.setgame

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll

object CardSetSpecification extends Properties("CardSet") {

  val genShape: Gen[CardShape]     = Gen.oneOf(CardShape.values)
  val genColour: Gen[CardColour]   = Gen.oneOf(CardColour.values)
  val genCount: Gen[CardCount]     = Gen.oneOf(CardCount.values)
  val genShading: Gen[CardShading] = Gen.oneOf(CardShading.values)

  val genInvalidShapes =
    genInvalidFeature(CardShape.values)((card, shape) => card.copy(shape = shape))
  val genInvalidColours =
    genInvalidFeature(CardColour.values)((card, colour) => card.copy(colour = colour))
  val genInvalidCounts =
    genInvalidFeature(CardCount.values)((card, count) => card.copy(count = count))
  val genInvalidShadings =
    genInvalidFeature(CardShading.values)((card, shading) => card.copy(shading = shading))

  /**
    * update firstCard and secondCard with the same feature and
    * thirdCard with another feature to create an invalid SET
    */
  def genInvalidFeature[A](features: Seq[A])(update: (Card, A) => Card): Gen[Set[Card]] = {
    for {
      Seq(firstCard, secondCard, thirdCard) <- Gen.pick(3, Game.allCards)
      Seq(sharedFeature, otherFeature)      <- Gen.pick(2, features)
    } yield {
      val sharedFeatureCards = Set(firstCard, secondCard).map(card => update(card, sharedFeature))
      val otherFeatureCard   = update(thirdCard, otherFeature)
      sharedFeatureCards + otherFeatureCard
    }
  }

  val genValidSetOfCards: Gen[Set[Card]] = for {
    shapes   <- genSameOrAllDifferentFeatures(genShape, CardShape.values)
    colours  <- genSameOrAllDifferentFeatures(genColour, CardColour.values)
    counts   <- genSameOrAllDifferentFeatures(genCount, CardCount.values)
    shadings <- genSameOrAllDifferentFeatures(genShading, CardShading.values)
    setOfCards = getSetOfCards(shapes, colours, counts, shadings)
    if setOfCards.size == 3
  } yield setOfCards

  private def getSetOfCards(shapes: Seq[CardShape],
                            colours: Seq[CardColour],
                            counts: Seq[CardCount],
                            shadings: Seq[CardShading]): Set[Card] = {
    val cards =
      for ((((shape, colour), count), shading) <- shapes zip colours zip counts zip shadings)
        yield Card(shape, colour, count, shading)
    cards.toSet
  }

  def genSameOrAllDifferentFeatures[A](genFeature: Gen[A], values: Seq[A]): Gen[Seq[A]] = {
    for {
      feature  <- genFeature
      features <- Gen.oneOf(Seq.fill(3)(feature), values)
    } yield features
  }

  property("valid") = forAll(genValidSetOfCards) { cards =>
    val cardSet = CardSet.build(cards)
    cardSet.isDefined && cardSet.map(_.cards) == Some(cards)
  }

  property("invalid shapes") = forAll(genInvalidShapes) { cards =>
    CardSet.build(cards) == None
  }

  property("invalid colours") = forAll(genInvalidColours) { cards =>
    CardSet.build(cards) == None
  }

  property("invalid counts") = forAll(genInvalidCounts) { cards =>
    CardSet.build(cards) == None
  }

  property("invalid shadings") = forAll(genInvalidShadings) { cards =>
    CardSet.build(cards) == None
  }
}
