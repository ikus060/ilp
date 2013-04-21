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

/**
 * The variable type.
 * 
 * @author Patrik Dufresne
 * 
 */
public class VarType {

    /**
     * Used for binary variable.
     */
    public static final VarType BOOL = new VarType("BOOL"); //$NON-NLS-1$
    /**
     * Used for integer variable.
     */
    public static final VarType INTEGER = new VarType("INT"); //$NON-NLS-1$
    /**
     * Used for continuous variable.
     */
    public static final VarType REAL = new VarType("REAL"); //$NON-NLS-1$

    /**
     * The internal variable type.
     */
    private String type;

    /**
     * Private constructor.
     */
    private VarType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VarType [" + this.type + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VarType other = (VarType) obj;
        if (!this.type.equals(other.type)) return false;
        return true;
    }

}
