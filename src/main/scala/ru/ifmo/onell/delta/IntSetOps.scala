package ru.ifmo.onell.delta

import java.util.Random

import ru.ifmo.onell.HasDeltaOperations
import ru.ifmo.onell.util.{BinomialScanner, DenseIntSet, IntSet, SparseIntSet}

object IntSetOps extends HasDeltaOperations[IntSet] {
  override def createStorage(nChanges: Int): IntSet = {
    if (nChanges <= 1048576) {
      new DenseIntSet(nChanges)
    } else {
      new SparseIntSet
    }
  }

  override def initializeDeltaWithDefaultSize(delta: IntSet, nChanges: Int, expectedSize: Double, rng: Random): Int = {
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

  override def initializeDeltaWithGivenSize(delta: IntSet, nChanges: Int, size: Int, rng: Random): Unit = {
    delta.clear()
    while (delta.size < size) {
      delta.add(rng.nextInt(nChanges))
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
