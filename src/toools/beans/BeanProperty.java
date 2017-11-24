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
 
 package toools.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class BeanProperty
{
	private PropertyDescriptor descriptor;

	public BeanProperty(PropertyDescriptor d)
	{
		this.descriptor = d;
	}

	public String getName()
	{
		return this.descriptor.getName();
	}

	public Class<?> getType()
	{
		return this.descriptor.getPropertyType();
	}

	public boolean isReadOnly()
	{
		return this.descriptor.getWriteMethod() == null;
	}

	public Object getValue(Object bean)
	{
		try
		{
			return this.descriptor.getReadMethod().invoke(bean, null);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void setValue(Object bean, Object value)
	{
		if (bean == null)
			throw new IllegalArgumentException("cannot set a value to the null object");

		try
		{
			Method setter = this.descriptor.getWriteMethod();

			if (setter == null)
			{
				throw new IllegalStateException("no setter for property " + getName());
			}
			else
			{
				setter.invoke(bean, new Object[] { value });
			}
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString()
	{
		return getName() + ' ' + (isReadOnly() ? "(ro)" : "(rw)");
	}

	public void setValueAsString(Object o, String value)
	{
		Class<?> type = getType();

		if (type == int.class)
		{
			setValue(o, Integer.valueOf(value));
		}
		else if (type == double.class)
		{
			setValue(o, Double.valueOf(value));
		}
		else if (type == boolean.class)
		{
			setValue(o, Boolean.valueOf(value));
		}
		else
		{
			throw new IllegalStateException("cannot convert string to " + type.getName());
		}
	}

}
