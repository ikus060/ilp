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
 * Immutable class storing a snapshot.
 * 
 * @author Patrik Dufresne
 * 
 */
public class BoundSnapshot extends HashMap<Variable, Bound> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Store the bound of the variable in a snapshot.
     * 
     * @param vars
     *            the input variable
     * @return the snapshot value
     */
    public static BoundSnapshot create(Collection<? extends Variable> vars) {
        Map<Variable, Bound> snapshot = new HashMap<Variable, Bound>(vars.size());
        for (Variable var : vars) {
            snapshot.put(var, new Bound(var.getLowerBound(), var.getUpperBound()));
        }
        return new BoundSnapshot(snapshot);
    }

    /**
     * Create a new snapshot with the given map.
     * 
     * @param snapshot
     */
    protected BoundSnapshot(Map<Variable, Bound> snapshot) {
        super(snapshot);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Variable, Bound>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    /**
     * Return the lower bound value stored in the snapshot for the given
     * variable.
     * 
     * @param var
     *            the variable
     * @return the lower bound value for this variable, also return null if this
     *         snapshot doesn't contain the variable specified.
     */
    public Number getLower(Variable var) {
        Bound bound = get(var);
        if (bound == null) {
            return null;
        }
        return bound.getLower();
    }

    /**
     * Return the lower bound value stored in the snapshot for the given
     * variable.
     * 
     * @param var
     *            the variable
     * @return the value for this variable.
     */
    public Number getUpper(Variable var) {
        Bound bound = get(var);
        if (bound == null) {
            return null;
        }
        return bound.getUpper();
    }

    @Override
    public Set<Variable> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    @Override
    public Bound put(Variable key, Bound value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Variable, ? extends Bound> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bound remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Bound> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    /**
     * Restore the variables bound according to this snapshot.
     */
    public void restore() {
        // Loop on each entry
        for (Entry<Variable, Bound> e : this.entrySet()) {
            e.getKey().setLowerBound(e.getValue().getLower());
            e.getKey().setUpperBound(e.getValue().getUpper());
        }
    }

}
