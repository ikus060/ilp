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
package com.patrikdufresne;

import com.patrikdufresne.ilp.Constraint;
import com.patrikdufresne.ilp.Linear;
import com.patrikdufresne.ilp.LinearProblem;
import com.patrikdufresne.ilp.Solver;
import com.patrikdufresne.ilp.SolverOption;
import com.patrikdufresne.ilp.Variable;
import com.patrikdufresne.ilp.cbc.CbcSolverFactory;

public class Main {

    public static void main(String[] args) {
        // Create the solver.
        Solver solver = CbcSolverFactory.instance().createSolver();
        LinearProblem lp = solver.createLinearProblem();

        // Create the model
        Variable x = lp.addIntegerVariable("x", 0, null);
        Variable y = lp.addIntegerVariable("y", 0, null);
        Linear linear = lp.createLinear();
        linear.add(lp.createTerm(1, y));
        lp.setObjectiveLinear(linear);
        lp.setObjectiveDirection(LinearProblem.MAXIMIZE);
        Constraint constraint1 = lp.addConstraint("constraint1", new int[] { -2, 6 }, new Variable[] { x, y }, null, 6);
        Constraint constraint2 = lp.addConstraint("constraint2", new int[] { 2, 6 }, new Variable[] { x, y }, null, 12);

        // Solve the problem.
        SolverOption option = solver.createSolverOption();
        solver.solve(lp, option);

        // Out the data.
        System.out.println("Status: " + lp.getStatus());
        System.out.println("Objective value:" + lp.getObjectiveValue().doubleValue());
        System.out.println("Solution:" + lp.toString());

    }

}
