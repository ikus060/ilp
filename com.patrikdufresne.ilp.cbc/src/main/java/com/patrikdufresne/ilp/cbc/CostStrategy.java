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
package com.patrikdufresne.ilp.cbc;

/**
 * Enumeration of cost strategy value.
 * 
 * @author Patrik Dufresne
 * 
 */
public enum CostStrategy {
    off("off"),
    priorities("priorities"),
    columnOrder("columnOrder"),
    zeroOneFirst("01first"),
    zeroOneLast("01last"),
    length("length"),
    singletons("singletons"),
    nonzero("nonzero");

    private String value;

    CostStrategy(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
