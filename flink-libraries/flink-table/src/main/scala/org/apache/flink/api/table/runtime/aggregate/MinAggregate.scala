/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.table.runtime.aggregate

import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.table.Row

abstract  class MinAggregate[T](implicit ord: Ordering[T]) extends Aggregate[T]{

  protected var minIndex: Int = _

  /**
   * Accessed in MapFunction, prepare the input of partial aggregate.
   *
   * @param value
   * @param partial
   */
  override def prepare(value: Any, partial: Row): Unit = {
    if (value == null) {
      initiate(partial)
    } else {
      partial.setField(minIndex, value)
    }
  }

  /**
   * Accessed in CombineFunction and GroupReduceFunction, merge partial
   * aggregate result into aggregate buffer.
   *
   * @param partial
   * @param buffer
   */
  override def merge(partial: Row, buffer: Row): Unit = {
    val partialValue = partial.productElement(minIndex).asInstanceOf[T]
    val bufferValue = buffer.productElement(minIndex).asInstanceOf[T]
    val min: T = if (ord.compare(partialValue, bufferValue) < 0) partialValue else bufferValue
    buffer.setField(minIndex, min)
  }

  /**
   * Return the final aggregated result based on aggregate buffer.
   *
   * @param buffer
   * @return
   */
  override def evaluate(buffer: Row): T = {
    buffer.productElement(minIndex).asInstanceOf[T]
  }

  override def supportPartial: Boolean = true

  override def setAggOffsetInRow(aggOffset: Int): Unit = {
    minIndex = aggOffset
  }
}

class ByteMinAggregate extends MinAggregate[Byte] {

  override def intermediateDataType = Array(BasicTypeInfo.BYTE_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Byte.MaxValue)
  }
}

class ShortMinAggregate extends MinAggregate[Short] {

  override def intermediateDataType = Array(BasicTypeInfo.SHORT_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Short.MaxValue)
  }
}

class IntMinAggregate extends MinAggregate[Int] {

  override def intermediateDataType = Array(BasicTypeInfo.INT_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Int.MaxValue)
  }
}

class LongMinAggregate extends MinAggregate[Long] {

  override def intermediateDataType = Array(BasicTypeInfo.LONG_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Long.MaxValue)
  }
}

class FloatMinAggregate extends MinAggregate[Float] {

  override def intermediateDataType = Array(BasicTypeInfo.FLOAT_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Float.MaxValue)
  }
}

class DoubleMinAggregate extends MinAggregate[Double] {

  override def intermediateDataType = Array(BasicTypeInfo.DOUBLE_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, Double.MaxValue)
  }
}

class BooleanMinAggregate extends MinAggregate[Boolean] {

  override def intermediateDataType = Array(BasicTypeInfo.BOOLEAN_TYPE_INFO)

  override def initiate(intermediate: Row): Unit = {
    intermediate.setField(minIndex, true)
  }
}
