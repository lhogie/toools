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

package toools.text.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import toools.text.TextUtilities;

public class DNode {
	private String name;
	private DNode parent;
	private final List<DNode> children = new ArrayList<>();
	private final Map<String, String> attributes;

	public DNode(String title) {
		this(title, true);
	}

	public DNode(String title, boolean sortAttributes) {
		setName(title);

		if (sortAttributes) {
			attributes = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
		} else {
			attributes = new HashMap<>();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException();

		this.name = name;
	}

	public List<DNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public String toString() {
		return toXML(0);
	}

	public String toXML(int tab) {
		StringBuilder b = new StringBuilder();
		toXML(b, tab);
		return b.toString();
	}

	public void toXML(StringBuilder b, int tab) {
		if (this instanceof TextNode) {
			b.append(this);
		} else {
			b.append(TextUtilities.repeat('\t', tab));
			b.append('<');
			b.append(name);

			for (String k : getAttributes().keySet()) {
				b.append(' ');
				b.append(k);
				b.append('=');
				b.append('"');
				b.append(getAttributes().get(k));
				b.append('"');
			}

			if (getChildren().isEmpty()) {
				b.append(" />");
			} else {
				b.append('>');
				int numberOfTextNodes = 0;

				for (DNode c : getChildren()) {
					if (c.getClass() == TextNode.class) {
						++numberOfTextNodes;
						c.toXML(b, 0);
					} else {
						b.append('\n');
						c.toXML(b, tab + 1);
					}
				}

				// if there are no text children
				if (numberOfTextNodes == 0) {
					b.append('\n');
					b.append(TextUtilities.repeat('\t', tab));
				}

				b.append('<');
				b.append('/');
				b.append(name);
				b.append('>');
			}
		}
	}
	
	public void toJSON(StringBuilder b, int tab) {
		if (this instanceof TextNode) {
			b.append(this);
		} else {
			b.append(TextUtilities.repeat('\t', tab));
			b.append('{');

			for (String k : getAttributes().keySet()) {
				b.append(' ');
				b.append(k);
				b.append('=');
				b.append('"');
				b.append(getAttributes().get(k));
				b.append('"');
			}

			if (getChildren().isEmpty()) {
				b.append(" />");
			} else {
				b.append('>');
				int numberOfTextNodes = 0;

				for (DNode c : getChildren()) {
					if (c.getClass() == TextNode.class) {
						++numberOfTextNodes;
						c.toXML(b, 0);
					} else {
						b.append('\n');
						c.toXML(b, tab + 1);
					}
				}

				// if there are no text children
				if (numberOfTextNodes == 0) {
					b.append('\n');
					b.append(TextUtilities.repeat('\t', tab));
				}

				b.append('<');
				b.append('/');
				b.append(name);
				b.append('>');
			}
		}
	}

	public byte[] toBytes() {
		try {
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			toBytes(o);
			return o.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public void toBytes(OutputStream os) throws IOException {

		DataOutputStream b = new DataOutputStream(os);
		boolean compact = isCompactOk();
		b.writeBoolean(compact);

		if (this instanceof TextNode) {
			b.writeBoolean(true);
			writeString(b, this.toString(), compact);
		} else {
			b.writeBoolean(false);
			writeString(b, getName(), compact);
			b.writeInt(getAttributes().keySet().size());

			for (String k : getAttributes().keySet()) {
				writeString(b, k, compact);
				writeString(b, getAttributes().get(k), compact);
			}

			b.writeInt(getChildren().size());

			for (DNode c : getChildren()) {
				c.toBytes(b);
			}
		}
	}

	private static void writeString(DataOutputStream os, String s, boolean compact) throws IOException {
		if (compact) {
			os.writeInt(s.length());
			os.write(s.getBytes());
		} else {
			os.writeUTF(s);
		}
	}

	private static String readString(DataInputStream os, boolean compact) throws IOException {
		if (compact) {
			int sz = os.readInt();
			byte[] b = new byte[sz];
			os.readFully(b);
			return new String(b);
		} else {
			return os.readUTF();
		}
	}

	private boolean isCompactOk() {
		if (name.length() > 255)
			return false;

		for (String k : attributes.keySet()) {
			if (k.length() > 255)
				return false;

			if (attributes.get(k).length() > 255)
				return false;
		}

		return true;
	}

	public static DNode fromBytes(byte[] bytes) {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);

		try {
			return fromBytes(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}

	public static DNode fromBytes(InputStream is) throws IOException {
		DataInputStream b = new DataInputStream(is);
		boolean compact = b.readBoolean();
		boolean textNode = b.readBoolean();

		if (textNode) {
			TextNode tn = new TextNode();
			tn.setText(readString(b, compact));
			return tn;
		} else {
			DNode n = new DNode(readString(b, compact));
			int numberOfAttributes = b.readInt();

			for (int i = 0; i < numberOfAttributes; ++i) {
				n.getAttributes().put(readString(b, compact), readString(b, compact));
			}

			int numberOfChildren = b.readInt();

			for (int i = 0; i < numberOfChildren; ++i) {
				DNode c = fromBytes(is);
				c.setParent(n);
			}

			return n;
		}
	}

	public List<DNode> findChildren(String regex, boolean recursive) {
		List<DNode> c = new ArrayList<DNode>();

		for (DNode n : getChildren()) {
			if (n.getName().matches(regex)) {
				c.add(n);
			}

			if (recursive) {
				c.addAll(n.findChildren(regex, recursive));
			}
		}

		return c;
	}

	public List<DNode> findDescendents(String regex) {
		List<DNode> c = new ArrayList<DNode>();
		DNode n = this;

		while ((n = n.getParent()) != null) {
			if (n.getName().matches(regex)) {
				c.add(n);
			}
		}

		return c;
	}

	public void setParent(DNode newParent) {
		setParent(newParent, newParent.getChildren().size());
	}

	public void setParent(DNode newParent, int childIndex) {
		if (newParent != this.parent) {
			// detach from old parent
			if (this.parent != null) {
				this.parent.children.remove(this);
			}

			// attach to new parent
			if (newParent != null) {
				newParent.children.add(childIndex, this);
			}

			this.parent = newParent;
		}
	}

	public DNode getParent() {
		return parent;
	}

	public List<DNode> dfs() {
		List<DNode> r = new ArrayList<DNode>();

		Stack<DNode> stack = new Stack<DNode>();
		stack.push(this);

		while (!stack.isEmpty()) {
			DNode n = stack.pop();
			r.add(n);

			for (DNode c : n.getChildren()) {
				stack.push(c);
			}
		}

		return r;
	}

	public DNode getRoot() {
		return parent == null ? this : parent.getRoot();
	}

	public static void main(String[] args) {
		DNode n = new DNode("test");
		DNode c = new DNode("child");
		c.setParent(n);
		c.getAttributes().put("name", "fubar");
		TextNode tn = new TextNode();
		tn.setText("coucou");
		tn.setParent(c);
		System.out.println(n);

		System.out.println(fromBytes(n.toBytes()));
	}

	public String getText(boolean recursive) {
		String s = "";

		for (DNode n : findChildren("#text", recursive)) {
			s += ((TextNode) n).getText();
		}

		return s;
	}

}
