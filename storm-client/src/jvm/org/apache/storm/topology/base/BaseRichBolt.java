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

package org.apache.storm.topology.base;

import org.apache.storm.topology.IRichBolt;
import org.apache.storm.tuple.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

public abstract class BaseRichBolt extends BaseComponent implements IRichBolt{
    @Override
    public void cleanup() {
    }

    Tuple input;
    private String stateFileName;
    public void saveState(Tuple input) throws FileNotFoundException {
        System.out.println("Bolt function: State save function is called.");

        Date date= new Date();
        long time = date.getTime();
        System.out.println("Time in Milliseconds: " + time);
        Timestamp ts = new Timestamp(time);
        System.out.println("Current Time Stamp: " + ts);

        String sentence = input.getString(0);
        //[PL20190911] Save the state into a file.
        System.out.println("input: " + sentence);
        System.out.println("Saving state...");
        setStateFileName("state.txt_" + time);
        try (PrintWriter out = new PrintWriter(getStateFileName())) {
            out.println(sentence);
        }
    }

    public String getStateFileName() {
        return stateFileName;
    }

    public void setStateFileName(String stateFileName) {
        this.stateFileName = stateFileName;
    }
}
