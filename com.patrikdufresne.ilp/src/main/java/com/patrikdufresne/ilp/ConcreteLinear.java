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
package com.patrikdufresne.ilp;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is a complete implementation of the {@link Linear} interface. This
 * implementation store all the data in memory.
 * 
 * @author Patrik Dufresne
 * 
 */
public class ConcreteLinear implements Linear {

    @Override
    public String toString() {
        if (this.terms == null || this.terms.size() == 0) {
            return "[]"; //$NON-NLS-1$
        }

        Iterator<Term> i = this.terms.iterator();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            Term e = i.next();
            sb.append(e);
            if (!i.hasNext()) return sb.append(']').toString();
            sb.append(" + "); //$NON-NLS-1$
        }
    }

    private Set<Term> terms;

    @Override
    public Iterator<Term> iterator() {
        if (this.terms == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return this.terms.iterator();
    }

    @Override
    public void add(Term term) {
        if (this.terms == null) {
            this.terms = new LinkedHashSet<Term>();
        }
        this.terms.add(term);
    }

    @Override
    public void remove(Term term) {
        if (this.terms == null) {
            return;
        }
        this.terms.remove(term);
    }

    @Override
    public void clear() {
        if (this.terms == null) {
            return;
        }
        this.terms.clear();
    }

    @Override
    public int size() {
        if (this.terms == null) {
            return 0;
        }
        return this.terms.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.terms == null) ? 0 : this.terms.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ConcreteLinear other = (ConcreteLinear) obj;
        if (this.terms == null) {
            if (other.terms != null) return false;
        } else if (!this.terms.equals(other.terms)) return false;
        return true;
    }

}
