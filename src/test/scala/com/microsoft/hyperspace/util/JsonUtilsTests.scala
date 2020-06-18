/*
 * Copyright (2020) The Hyperspace Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoft.hyperspace.util

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.scalatest.FunSuite

import com.microsoft.hyperspace.actions.Constants
import com.microsoft.hyperspace.index._

class JsonUtilsTests extends FunSuite {
  test("Test for JsonUtils.") {
    val schema = StructType(
      Seq(
        StructField("id", IntegerType),
        StructField("name", StringType),
        StructField("school", StringType)))

    val sourcePlanProperties = SparkPlan.Properties(
      "plan",
      LogicalPlanFingerprint(
        LogicalPlanFingerprint.Properties(Seq(Signature("signatureProvider", "dfSignature")))))
    val sourceDataProperties =
      Hdfs.Properties(Content("", Seq(Content.Directory("", Seq(), NoOpFingerprint()))))

    val index = IndexLogEntry(
      "myIndex",
      CoveringIndex(
        CoveringIndex.Properties(
          CoveringIndex.Properties.Columns(Seq("id"), Seq("name", "school")),
          IndexLogEntry.schemaString(schema),
          10)),
      Content("path", Seq()),
      Source(SparkPlan(sourcePlanProperties), Seq(Hdfs(sourceDataProperties))),
      Map())
    index.state = Constants.States.ACTIVE

    val deserializedIndex =
      JsonUtils.fromJson[IndexLogEntry](JsonUtils.toJson[IndexLogEntry](index))
    assert(deserializedIndex.equals(index))
  }
}