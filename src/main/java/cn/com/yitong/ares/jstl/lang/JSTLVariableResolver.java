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

import cn.com.yitong.ares.jstl.PageContext;

/**
 * <p>
 * This is the JSTL-specific implementation of VariableResolver. It looks up
 * variable references in the PageContext, and also recognizes references to
 * implicit objects.
 *
 * @author Nathan Abramson - Art Technology Group
 */

public class JSTLVariableResolver implements VariableResolver {
	// -------------------------------------

	/**
	 * Resolves the specified variable within the given context. Returns null if
	 * the variable is not found.
	 */
	public Object resolveVariable(String pName, Object pContext) throws ELException {
		System.setProperty("javax.servlet.jsp.functions.allowed", "true");
		PageContext ctx = (PageContext) pContext;
		if ("pageContext".equals(pName)) {
			return ctx;
		} else {
			return ctx.findAttribute(pName);
		}
	}

	// -------------------------------------
}
