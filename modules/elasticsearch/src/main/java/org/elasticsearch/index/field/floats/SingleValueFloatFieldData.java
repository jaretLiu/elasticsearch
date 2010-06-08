/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.field.floats;

import org.elasticsearch.index.field.FieldDataOptions;
import org.elasticsearch.index.field.doubles.DoubleFieldData;

/**
 * @author kimchy (shay.banon)
 */
public class SingleValueFloatFieldData extends FloatFieldData {

    private ThreadLocal<double[]> doublesValuesCache = new ThreadLocal<double[]>() {
        @Override protected double[] initialValue() {
            return new double[1];
        }
    };

    private ThreadLocal<float[]> valuesCache = new ThreadLocal<float[]>() {
        @Override protected float[] initialValue() {
            return new float[1];
        }
    };

    // order with value 0 indicates no value
    private final int[] order;

    public SingleValueFloatFieldData(String fieldName, FieldDataOptions options, int[] order, float[] values, int[] freqs) {
        super(fieldName, options, values, freqs);
        this.order = order;
    }

    @Override public boolean multiValued() {
        return false;
    }

    @Override public boolean hasValue(int docId) {
        return order[docId] != 0;
    }

    @Override public void forEachValueInDoc(int docId, StringValueInDocProc proc) {
        int loc = order[docId];
        if (loc == 0) {
            return;
        }
        proc.onValue(docId, Float.toString(values[loc]));
    }

    @Override public void forEachValueInDoc(int docId, DoubleValueInDocProc proc) {
        int loc = order[docId];
        if (loc == 0) {
            return;
        }
        proc.onValue(docId, values[loc]);
    }

    @Override public double[] doubleValues(int docId) {
        int loc = order[docId];
        if (loc == 0) {
            return DoubleFieldData.EMPTY_DOUBLE_ARRAY;
        }
        double[] ret = doublesValuesCache.get();
        ret[0] = values[loc];
        return ret;
    }

    @Override public float value(int docId) {
        return values[order[docId]];
    }

    @Override public float[] values(int docId) {
        int loc = order[docId];
        if (loc == 0) {
            return EMPTY_FLOAT_ARRAY;
        }
        float[] ret = valuesCache.get();
        ret[0] = values[loc];
        return ret;
    }
}