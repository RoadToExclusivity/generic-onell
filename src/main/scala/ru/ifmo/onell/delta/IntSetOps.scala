package ru.ifmo.onell.delta

import java.util.concurrent.{ThreadLocalRandom => Random}

import ru.ifmo.onell.HasDeltaOperations
import ru.ifmo.onell.util.{BinomialScanner, HierarchicIntSet, IntSet}

object IntSetOps extends HasDeltaOperations[IntSet] {
  override def createStorage(nChanges: Long): IntSet = {
    new HierarchicIntSet(nChanges)
  }

  override def initializeDeltaWithDefaultSize(delta: IntSet, nChanges: Long, expectedSize: Double, rng: Random): Int = {
    delta.clear()

    val scanner = BinomialScanner(expectedSize / nChanges)
    var index = scanner.offset(rng) - 1L
    while (index < nChanges) {
      delta.add(index)
      index += scanner.offset(rng)
    }

    delta.shuffleOrder(rng)
    delta.size
  }

  override def initializeDeltaWithGivenSize(delta: IntSet, nChanges: Long, size: Int, rng: Random): Unit = {
    delta.clear()
    while (delta.size < size) {
      delta.add(rng.nextLong(nChanges))
    }
  }

  override def initializeDeltaFromExisting(delta: IntSet, source: IntSet, expectedSize: Double, rng: Random): Int = {
    delta.clear()

    val sourceSize = source.size
    val scanner = BinomialScanner(expectedSize / sourceSize)
    var index = scanner.offset(rng) - 1L
    while (index < sourceSize) {
      delta.add(source(index.toInt))
      index += scanner.offset(rng)
    }

    delta.size
  }

  override def copyDelta(source: IntSet, target: IntSet): Unit = {
    target.clear()
    val size = source.size
    var i = 0
    while (i < size) {
      target.add(source(i))
      i += 1
    }
  }
}
