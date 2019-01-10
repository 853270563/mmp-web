/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.yitong.ares.jstl.lang;

import java.util.Map;

/**
 * <p>The abstract class from which all expression types
 * derive.
 *
 * @author Nathan Abramson - Art Technology Group
 * @author Shawn Bayern
 */

public abstract class Expression {
    //-------------------------------------
    // Member variables
    //-------------------------------------

    //-------------------------------------

    /**
     * Returns the expression in the expression language syntax
     */
    public abstract String getExpressionString();

    //-------------------------------------

    /**
     * Evaluates the expression in the given context
     */
    public abstract Object evaluate(Object pContext,
                                    VariableResolver pResolver,
                                    Map functions,
                                    String defaultPrefix,
                                    Logger pLogger)
            throws ELException;

    //-------------------------------------

}
