/**
  * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
  *
  * Licensed under the Apache License, Version 2.0 (the "License"). You may not
  * use this file except in compliance with the License. A copy of the License
  * is located at
  *
  *     http://aws.amazon.com/apache2.0/
  *
  * or in the "license" file accompanying this file. This file is distributed on
  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
  * express or implied. See the License for the specific language governing
  * permissions and limitations under the License.
  *
  */

package com.amazon.deequ.runtime.spark.executor

import com.amazon.deequ.metrics.Metric
import com.amazon.deequ.runtime.spark.operators.Operator
import com.amazon.deequ.runtime.spark.{SparkStateLoader, SparkStatePersister}
import org.apache.spark.sql.DataFrame
import org.apache.spark.storage.StorageLevel

/**
  * Defines a set of analyzers to run on data.
  *
  * @param analyzers
  */
case class OperatorList(analyzers: Seq[Operator[_, Metric[_]]] = Seq.empty) {

  def addAnalyzer(analyzer: Operator[_, Metric[_]]): OperatorList = {
    OperatorList(analyzers :+ analyzer)
  }

  def addAnalyzers(otherAnalyzers: Seq[Operator[_, Metric[_]]]): OperatorList = {
    OperatorList(analyzers ++ otherAnalyzers)
  }

  /**
    * Compute the metrics from the analyzers configured in the analyis
    *
    * @param data data on which to operate
    * @param aggregateWith load existing states for the configured analyzers and aggregate them
    *                      (optional)
    * @param saveStatesWith persist resulting states for the configured analyzers (optional)
    * @param storageLevelOfGroupedDataForMultiplePasses caching level for grouped data that must
    *                                                   be accessed multiple times (use
    *                                                   StorageLevel.NONE to completely disable
    *                                                   caching)
    * @return
    */
  @deprecated("Use the AnalysisRunner instead (the onData method there)", "24-09-2019")
  def run(
           data: DataFrame,
           aggregateWith: Option[SparkStateLoader] = None,
           saveStatesWith: Option[SparkStatePersister] = None,
           storageLevelOfGroupedDataForMultiplePasses: StorageLevel = StorageLevel.MEMORY_AND_DISK)
  : OperatorResults = {

    SparkExecutor.doAnalysisRun(data, analyzers, aggregateWith = aggregateWith,
      saveStatesWith = saveStatesWith)
  }
}