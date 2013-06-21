/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.patrikdufresne.ilp.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.patrikdufresne.ilp.Variable;

/**
 * Class storing a snapshot of the variable's value.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ValueSnapshot extends HashMap<Variable, Number> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new value snapshot for the variables specified.
     * 
     * @param vars
     *            the input variable
     * @return the snapshot value
     */
    public static ValueSnapshot create(Collection<? extends Variable> vars) {
        Map<Variable, Number> snapshot = new HashMap<Variable, Number>(vars.size());
        for (Variable var : vars) {
            snapshot.put(var, var.getValue());
        }
        return new ValueSnapshot(snapshot);
    }

    /**
     * Create a new snapshot with the given map.
     * 
     * @param snapshot
     */
    protected ValueSnapshot(Map<Variable, Number> snapshot) {
        super(snapshot);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Variable, Number>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public Set<Variable> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    @Override
    public Number put(Variable key, Number value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Variable, ? extends Number> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Number remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Number> values() {
        return Collections.unmodifiableCollection(super.values());
    }

}
