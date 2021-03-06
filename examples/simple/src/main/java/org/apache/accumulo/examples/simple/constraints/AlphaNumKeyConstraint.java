/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.accumulo.examples.simple.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.accumulo.core.constraints.Constraint;
import org.apache.accumulo.core.data.ColumnUpdate;
import org.apache.accumulo.core.data.Mutation;

/**
 * This class is an accumulo constraint that ensures all fields of a key are alpha numeric.
 *
 * See docs/examples/README.constraint for instructions.
 *
 */

public class AlphaNumKeyConstraint implements Constraint {

  private static final short NON_ALPHA_NUM_ROW = 1;
  private static final short NON_ALPHA_NUM_COLF = 2;
  private static final short NON_ALPHA_NUM_COLQ = 3;

  private boolean isAlphaNum(byte bytes[]) {
    for (byte b : bytes) {
      boolean ok = ((b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || (b >= '0' && b <= '9'));
      if (!ok)
        return false;
    }

    return true;
  }

  private List<Short> addViolation(List<Short> violations, short violation) {
    if (violations == null) {
      violations = new ArrayList<Short>();
      violations.add(violation);
    } else if (!violations.contains(violation)) {
      violations.add(violation);
    }
    return violations;
  }

  @Override
  public List<Short> check(Environment env, Mutation mutation) {
    List<Short> violations = null;

    if (!isAlphaNum(mutation.getRow()))
      violations = addViolation(violations, NON_ALPHA_NUM_ROW);

    Collection<ColumnUpdate> updates = mutation.getUpdates();
    for (ColumnUpdate columnUpdate : updates) {
      if (!isAlphaNum(columnUpdate.getColumnFamily()))
        violations = addViolation(violations, NON_ALPHA_NUM_COLF);

      if (!isAlphaNum(columnUpdate.getColumnQualifier()))
        violations = addViolation(violations, NON_ALPHA_NUM_COLQ);
    }

    return violations;
  }

  @Override
  public String getViolationDescription(short violationCode) {

    switch (violationCode) {
      case NON_ALPHA_NUM_ROW:
        return "Row was not alpha numeric";
      case NON_ALPHA_NUM_COLF:
        return "Column family was not alpha numeric";
      case NON_ALPHA_NUM_COLQ:
        return "Column qualifier was not alpha numeric";
    }

    return null;
  }

}
