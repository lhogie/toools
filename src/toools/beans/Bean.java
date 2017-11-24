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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Bean
{
	final private Class<?> beanClass;
	final private Map<String, BeanProperty> properties = new HashMap<String, BeanProperty>();

	public Bean(Class<?> clazz)
	{
		this.beanClass = clazz;

		try
		{
			BeanInfo i = Introspector.getBeanInfo(beanClass);

			for (PropertyDescriptor d : i.getPropertyDescriptors())
			{
				if (d.getReadMethod() != null)
				{
					BeanProperty p = new BeanProperty(d);
					this.properties.put(p.getName(), p);
				}
			}			
		}
		catch (IntrospectionException e)
		{
			throw new IllegalArgumentException(
					"cannot introspect bean " + clazz.getName());
		}
	}

	public String getName()
	{
		return getBeanClass().getName();
	}

	public Class<?> getBeanClass()
	{
		return beanClass;
	}

	public Map<String, BeanProperty> getProperties()
	{
		return properties;
	}

	@Override
	public String toString()
	{
		return getName() + ", properties: " + getProperties().keySet();
	}

	public String propertiesAsText(Object o, String regexp)
	{
		StringBuilder b = new StringBuilder();

		for (BeanProperty p : getProperties().values())
		{
			if (p.getName().matches(regexp))
			{
				b.append(p.getName() + "=" + p.getValue(o) + "\n");
			}
		}

		return b.toString();
	}

	public void setPropertyValueFromString(String propertyValuesText, Object target,
			String propertyNameFilter, Map<Class<?>, FromToString> ser)
	{
		try
		{
			Properties t = new Properties();
			t.load(new StringReader(propertyValuesText));

			for (BeanProperty p : getProperties().values())
			{
				String propName = p.getName();

				if (p.getName().matches(propertyNameFilter))
				{
					String value = t.get(propName).toString();
					Class<?> c = p.getType();

					if (c == int.class || c == Integer.class)
					{
						p.setValue(target, Integer.valueOf(value));
					}
					else if (c == long.class || c == Long.class)
					{
						p.setValue(target, Long.valueOf(value));
					}
					else if (c == boolean.class || c == Boolean.class)
					{
						p.setValue(target, Boolean.valueOf(value));
					}
					else if (c == double.class || c == Double.class)
					{
						p.setValue(target, Double.valueOf(value));
					}
					else if (c == float.class || c == Float.class)
					{
						p.setValue(target, Float.valueOf(value));
					}
					else if (c == char.class || c == Character.class)
					{
						if (value.length() == 1)
						{
							p.setValue(target, value.charAt(0));
						}
						else
						{
							throw new IllegalArgumentException(
									"invalid character: " + value);
						}
					}
					else if (c == String.class)
					{
						p.setValue(target, value);
					}
					else
					{
						FromToString sers = ser.get(c);

						if (sers == null)
						{
							throw new IllegalArgumentException("can't handle class: " + c
									+ " for property " + propName);
						}
						else
						{
							// sers.
						}
					}
				}

			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException();
		}
	}

	public static void main(String... args)
	{
		Bean b = new Bean(Bean.class);
		System.out.println(b);
		System.out.println(b.getProperties().get("name").isReadOnly());
	}
}
