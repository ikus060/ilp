/*
 * Copyright (c) 2011, Patrik Dufresne. All rights reserved.
 * Patrik Dufresne PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
		Map<Variable, Number> snapshot = new HashMap<Variable, Number>(
				vars.size());
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
