/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/

package toools.collections.relation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface Relation<A, B> extends Cloneable, Serializable
{
	String getDescription();

	void setDescription(String s);

	/**
	 * @return the keys of the table.
	 */
	Collection<A> keySet();

	/**
	 * @return the values that are associated to the given key. An empty set may
	 *         be returned.
	 */
	Collection<B> getValues(A key);

	Collection<Collection<B>> getValues();

	B getValue(A key);

	/**
	 * @return if there is any association to the given object.
	 */
	boolean isDefined(A key);

	/**
	 * @return if there is an association between the given key and the given
	 *         value.
	 */
	boolean isDefined(A key, B value);

	/**
	 * Adds the key/value relation in the relation.
	 */
	void add(A key);

	void add(A key, B value);

	void addAll(A key, Collection<B> collection);

	/**
	 * Removes the key/value relation from the table.
	 */
	void remove(A key, B value);

	/**
	 * Removes all the values for the given key.
	 */
	void remove(A key);

	boolean isInjective();

	boolean isFunction();

	/**
	 * Clears the relation.
	 */
	void clear();

	public Object clone();

	/**
	 * @return the inverse of the table. Be careful. Changing the content of the
	 *         inverse table will affect the content of this table.
	 */
	Relation<B, A> getInverseRelation();

	/**
	 * The value container is user-definable because there are different needs:
	 * - if you need very fast access that do not depend on the amount of stored
	 * objets, you may not override this method and so use a java.util.HashSet
	 * container. - if you need a relation order (on the add() invocations), you
	 * will have to use a container that handle order, such as java.util.Vector
	 * or java.util.LinkedList.
	 *
	 * Generics cannot be used here because one container will used for storing
	 * the keys, and another one will be used to store the values.
	 * 
	 * @return a collection that will be used to store the values for a given
	 *         key.
	 */
	public Collection createContainer();

	Collection<A> getKeysReferingTheEmptySet();

	Map<A, B> toMap(Map<A, B> mapClass);

	void addAll(Relation<A, B> r);

}
