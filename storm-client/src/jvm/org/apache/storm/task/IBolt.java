/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package org.apache.storm.task;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.apache.storm.Config;
import org.apache.storm.tuple.Tuple;

/**
 * An IBolt represents a component that takes tuples as input and produces tuples as output. An IBolt can do everything from filtering to
 * joining to functions to aggregations. It does not have to process a tuple immediately and may hold onto tuples to process later.
 *
 * <p>A bolt's lifecycle is as follows:
 *
 * <p>IBolt object created on client machine. The IBolt is serialized into the topology (using Java serialization) and
 * submitted to the master machine of the cluster (Nimbus). Nimbus then launches workers which deserialize the object,
 * call prepare on it, and then start processing tuples.
 *
 * <p>If you want to parameterize an IBolt, you should set the parameters through its constructor and save the parameterization state as
 * instance variables (which will then get serialized and shipped to every task executing this bolt across the cluster).
 *
 * <p>When defining bolts in Java, you should use the IRichBolt interface which adds necessary methods for using the
 * Java TopologyBuilder API.
 */
public interface IBolt extends Serializable {
    /**
     * Called when a task for this component is initialized within a worker on the cluster. It provides the bolt with the environment in
     * which the bolt executes.
     *
     * <p>This includes the:
     *
     * @param topoConf  The Storm configuration for this bolt. This is the configuration provided to the topology merged in with cluster
     *                  configuration on this machine.
     * @param context   This object can be used to get information about this task's place within the topology, including the task id and
     *                  component id of this task, input and output information, etc.
     * @param collector The collector is used to emit tuples from this bolt. Tuples can be emitted at any time, including the prepare and
     *                  cleanup methods. The collector is thread-safe and should be saved as an instance variable of this bolt object.
     */
    void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector);

    /**
     * Process a single tuple of input. The Tuple object contains metadata on it about which component/stream/task it came from. The values
     * of the Tuple can be accessed using Tuple#getValue. The IBolt does not have to process the Tuple immediately. It is perfectly fine to
     * hang onto a tuple and process it later (for instance, to do an aggregation or join).
     *
     * <p>Tuples should be emitted using the OutputCollector provided through the prepare method. It is required that all input tuples are
     * acked or failed at some point using the OutputCollector. Otherwise, Storm will be unable to determine when tuples coming off the
     * spouts have been completed.
     *
     * <p>For the common case of acking an input tuple at the end of the execute method, see IBasicBolt which automates this.
     *
     * @param input The input tuple to be processed.
     */
    void execute(Tuple input);

    /**
     * Called when an IBolt is going to be shutdown. Storm will make a best-effort attempt to call this if the worker shutdown is orderly.
     * The {@link Config#SUPERVISOR_WORKER_SHUTDOWN_SLEEP_SECS} setting controls how long orderly shutdown is allowed to take.
     * There is no guarantee that cleanup will be called if shutdown is not orderly, or if the shutdown exceeds the time limit.
     *
     * <p>The one context where cleanup is guaranteed to be called is when a topology is killed when running Storm in local mode.
     */
    void cleanup();


}
