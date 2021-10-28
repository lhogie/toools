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

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XML {
	private static Document xml2dom(String xml, boolean validating) throws SAXException

	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(validating);

		try {
			return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static <V extends DNode> DNode dom2node(Node node) {
		DNode n = new DNode(node.getNodeName());
		NamedNodeMap attributes = node.getAttributes();

		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); ++i) {
				Node a = attributes.item(i);
				String name = a.getNodeName();
				String value = a.getTextContent();
				n.getAttributes().put(name, value);
			}
		}

		NodeList domChildren = node.getChildNodes();

		for (int i = 0; i < domChildren.getLength(); ++i) {
			Node childDomNode = domChildren.item(i);

			if (childDomNode.getNodeName().equals("#text")) {
				String content = childDomNode.getTextContent().trim();

				if (!content.isEmpty()) {
					TextNode textNode = new TextNode();
					textNode.setText(content);
					textNode.setParent(n);
				}
			} else {
				DNode child = dom2node(childDomNode);
				child.setParent(n);
			}
		}

		return n;
	}

	public static DNode parseXML(String xml, boolean validating) throws SAXException {
		Document dom = xml2dom(xml, validating);
		return dom2node(dom.getDocumentElement());
	}

}
