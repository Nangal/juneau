// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.utils;

import java.util.*;

/**
 * An extension of {@link LinkedList} with a convenience {@link #append(Object)} method.
 *
 * <p>
 * Primarily used for testing purposes for quickly creating populated lists.
 * <p class='bcode'>
 * 	<jc>// Example:</jc>
 * 	List&lt;String&gt; l = <jk>new</jk> AList&lt;String&gt;().append(<js>"foo"</js>).append(<js>"bar"</js>);
 * </p>
 *
 * @param <T> The entry type.
 */
@SuppressWarnings({"serial","unchecked"})
public final class AList<T> extends LinkedList<T> {

	/**
	 * Adds an entry to this list.
	 *
	 * @param t The entry to add to this list.
	 * @return This object (for method chaining).
	 */
	public AList<T> append(T t) {
		add(t);
		return this;
	}

	/**
	 * Adds multiple entries to this list.
	 *
	 * @param t The entries to add to this list.
	 * @return This object (for method chaining).
	 */
	public AList<T> appendAll(T...t) {
		addAll(Arrays.asList(t));
		return this;
	}
}
