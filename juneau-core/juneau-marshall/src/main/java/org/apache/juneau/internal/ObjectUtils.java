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
package org.apache.juneau.internal;

import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.transform.*;

/**
 * Utility class for efficiently converting objects between types.
 *
 * <p>
 * If the value isn't an instance of the specified type, then converts the value if possible.
 *
 * <p>
 * The following conversions are valid:
 * <table class='styled'>
 * 	<tr><th>Convert to type</th><th>Valid input value types</th><th>Notes</th></tr>
 * 	<tr>
 * 		<td>
 * 			A class that is the normal type of a registered {@link PojoSwap}.
 * 		</td>
 * 		<td>
 * 			A value whose class matches the transformed type of that registered {@link PojoSwap}.
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			A class that is the transformed type of a registered {@link PojoSwap}.
 * 		</td>
 * 		<td>
 * 			A value whose class matches the normal type of that registered {@link PojoSwap}.
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code Number} (e.g. {@code Integer}, {@code Short}, {@code Float},...)
 * 			<br><code>Number.<jsf>TYPE</jsf></code> (e.g. <code>Integer.<jsf>TYPE</jsf></code>,
 * 			<code>Short.<jsf>TYPE</jsf></code>, <code>Float.<jsf>TYPE</jsf></code>,...)
 * 		</td>
 * 		<td>
 * 			{@code Number}, {@code String}, <jk>null</jk>
 * 		</td>
 * 		<td>
 * 			For primitive {@code TYPES}, <jk>null</jk> returns the JVM default value for that type.
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code Map} (e.g. {@code Map}, {@code HashMap}, {@code TreeMap}, {@code ObjectMap})
 * 		</td>
 * 		<td>
 * 			{@code Map}
 * 		</td>
 * 		<td>
 * 			If {@code Map} is not constructible, a {@code ObjectMap} is created.
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code Collection} (e.g. {@code List}, {@code LinkedList}, {@code HashSet}, {@code ObjectList})
 * 		</td>
 * 		<td>
 * 			{@code Collection<Object>}
 * 			<br>{@code Object[]}
 * 		</td>
 * 		<td>
 * 			If {@code Collection} is not constructible, a {@code ObjectList} is created.
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code X[]} (array of any type X)
 * 		</td>
 * 		<td>
 * 			{@code List<X>}
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code X[][]} (multi-dimensional arrays)
 * 		</td>
 * 		<td>
 * 			{@code List<List<X>>}
 * 			<br>{@code List<X[]>}
 * 			<br>{@code List[]<X>}
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code Enum}
 * 		</td>
 * 		<td>
 * 			{@code String}
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			Bean
 * 		</td>
 * 		<td>
 * 			{@code Map}
 * 		</td>
 * 		<td>&nbsp;</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			{@code String}
 * 		</td>
 * 		<td>
 * 			Anything
 * 		</td>
 * 		<td>
 * 			Arrays are converted to JSON arrays
 * 		</td>
 * 	</tr>
 * 	<tr>
 * 		<td>
 * 			Anything with one of the following methods:
 * 			<br><code><jk>public static</jk> T fromString(String)</code>
 * 			<br><code><jk>public static</jk> T valueOf(String)</code>
 * 			<br><code><jk>public</jk> T(String)</code>
 * 		</td>
 * 		<td>
 * 			<code>String</code>
 * 		</td>
 * 		<td>
 * 			<br>
 * 		</td>
 * 	</tr>
 * </table>
 */
public class ObjectUtils {

	// Session objects are usually not thread safe, but we're not using any feature
	// of bean sessions that would cause thread safety issues.
	private static final BeanSession session = BeanContext.DEFAULT.createSession();

	/**
	 * Converts the specified object to the specified type.
	 *
	 * @param <T> The class type to convert the value to.
	 * @param value The value to convert.
	 * @param type The class type to convert the value to.
	 * @throws InvalidDataConversionException If the specified value cannot be converted to the specified type.
	 * @return The converted value.
	 */
	public static <T> T convertToType(Object value, Class<T> type) {
		return session.convertToType(value, type);
	}

	/**
	 * Converts the specified object to the specified type.
	 *
	 * @param <T> The class type to convert the value to.
	 * @param outer
	 * 	If class is a member class, this is the instance of the containing class.
	 * 	Should be <jk>null</jk> if not a member class.
	 * @param value The value to convert.
	 * @param type The class type to convert the value to.
	 * @throws InvalidDataConversionException If the specified value cannot be converted to the specified type.
	 * @return The converted value.
	 */
	public static <T> T convertToMemberType(Object outer, Object value, Class<T> type) {
		return session.convertToMemberType(outer, value, type);
	}

	/**
	 * Returns <jk>true</jk> if the specified objects are equal.
	 *
	 * <p>
	 * Gracefully handles <jk>null</jk>s.
	 *
	 * @param o1 Object #1
	 * @param o2 Object #2
	 * @return <jk>true</jk> if the objects are equal or both <jk>null</jk>.
	 */
	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null || o2 == null)
			return false;
		return o1.equals(o2);
	}

	/**
	 * Returns <jk>true</jk> if the specified object is empty.
	 *
	 * <p>
	 * Return <jk>true</jk> if the value is any of the following:
	 * <ul>
	 * 	<li><jk>null</jk>
	 * 	<li>An empty Collection
	 * 	<li>An empty Map
	 * 	<li>An empty array
	 * 	<li>An empty CharSequence
	 * 	<li>An empty String when serialized to a string using {@link Object#toString()}.
	 * </ul>
	 *
	 * @param o The object to test.
	 * @return <jk>true</jk> if the specified object is empty.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof Collection)
			return ((Collection)o).isEmpty();
		if (o instanceof Map)
			return ((Map)o).isEmpty();
		if (o.getClass().isArray())
			return (Array.getLength(o) == 0);
		return o.toString().isEmpty();
	}
}
