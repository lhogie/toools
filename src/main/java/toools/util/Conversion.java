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

package toools.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import toools.io.ser.JavaSerializer;
import toools.reflect.Clazz;
import toools.text.TextUtilities;

public class Conversion {
	public static int long2int(long n) {
		int intValue = (int) n;

		if (n != intValue)
			throw new Error("too big to be converted to int: " + n + " => " + intValue);

		return intValue;
	}

	public static int[] toIntArray(long... a) {
		int[] r = new int[a.length];

		for (int i = 0; i < a.length; ++i) {
			r[i] = long2int(a[i]);
		}

		return r;
	}

	public static <E> E clone(E o) {
		if (o instanceof Cloneable) {
			try {
				Method m = o.getClass().getMethod("clone");
				m.setAccessible(true);
				return (E) m.invoke(o);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			// immutable types
		} else if (o instanceof Number || o instanceof String) {
			return (E) o;
		} else if (o instanceof Collection) {
			Collection l = (Collection) o;
			Collection r = allocateContainer(l, l.size());
			r.addAll(l);
			return (E) r;
		} else if (o instanceof Map) {
			Map m = (Map) o;
			Map r = allocateContainer(m, m.size());
			r.putAll(m);
			return (E) r;
		} else if (o instanceof Serializable) {
			return (E) new JavaSerializer().clone(o);
		} else {
			throw new IllegalArgumentException("unable to clone instances of " + o.getClass());
		}
	}

	private static <E> E allocateContainer(E o, int size) {
		try {
			E r = (E) o.getClass().getConstructor(int.class).newInstance(size);
			return (E) r;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		Map o = new HashMap<>();
		o.put("he", "fd");
		System.out.println(clone(o));
	}

	public static boolean text2boolean(String s) {
		if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true")) {
			return true;
		} else if (s.equalsIgnoreCase("no") || s.equalsIgnoreCase("false")) {
			return false;
		}

		throw new NumberFormatException("invalid boolean: " + s);
	}

	private static final char[] hexAlphabet = "0123456789ABCDEF".toCharArray();

	public static char[] bytesToHex(byte[] b) {
		final char[] r = new char[hexAlphabet.length * 3 - 1];
		int i = 0;

		for (byte c : b) {
			r[i++] = hexAlphabet[(c >> 4) & 0x0f];
			r[i++] = hexAlphabet[c & 0x0f];

			if (i < r.length - 1) {
				r[i++] = ' ';
			}
		}

		return r;
	}

	public static interface Converter<FROM, TO> extends Function<FROM, TO> {
		boolean support(FROM from, Class<?> to);
	}

//	public static <TO> TO convert(Object initialObject, Class<TO> destinationClass, Iterable<Converter<?, ?>> converters) throws IllegalArgumentException {
	public static <TO> TO convert(Object initialObject, Class<TO> destinationClass) {
		if (destinationClass.isAssignableFrom(initialObject.getClass()))
			return (TO) initialObject;

		if (initialObject.getClass() == String.class && destinationClass == Class.class)
			try {
				return (TO) Class.forName(initialObject.toString());
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException(e1);
			}

		if (destinationClass == double.class || destinationClass == Double.class)
			return (TO) Double.valueOf(initialObject.toString());

		if (destinationClass == int.class || destinationClass == Integer.class)
			return (TO) (Integer) Integer.parseInt(initialObject.toString());

		if (destinationClass == long.class || destinationClass == Long.class)
			return (TO) (Long) Long.parseLong(initialObject.toString());

		if (Collection.class.isAssignableFrom(destinationClass)) {
			if (destinationClass.isAssignableFrom(LongArrayList.class))
				destinationClass = (Class<TO>) LongArrayList.class;

			if (initialObject instanceof String) {
				return (TO) string2collectionOfLongs((Class<Collection<Long>>) destinationClass,
						(String) initialObject);
			}
		}

		if (byte[].class.isAssignableFrom(destinationClass)) {
			if (initialObject instanceof InputStream) {
				try {
					return (TO) ((InputStream) initialObject).readAllBytes();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			return (TO) TextUtilities.toString(initialObject).getBytes();
		}
		/*
		 * if (converters != null) { for (var c : converters) { if
		 * (c.support(initialObject, destinationClass.getClass())) { return (TO)
		 * c.apply(initialObject); } } }
		 */
		throw new IllegalArgumentException(initialObject.getClass() + " cannot be converted to " + destinationClass);
	}

	public static Collection<Long> string2collectionOfLongs(Class<? extends Collection<Long>> c, String from) {
		var cc = Clazz.makeInstance(c);

		if (cc == null)
			throw new IllegalArgumentException("cannot instanciate " + c);

		for (String i : from.split(",")) {
			cc.add(Long.parseLong(i));
		}

		return cc;
	}
}
