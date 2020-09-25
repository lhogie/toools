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
 
 package toools.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import toools.extern.Proces;
import toools.gui.GraphViz.COMMAND;
import toools.gui.GraphViz.OUTPUT_FORMAT;
import toools.io.file.Directory;

public class Painter
{
	public static void paint(byte[] png, ImageShowerComponent c)
	{
		c.image = new ImageIcon(png).getImage();
	}

	public static void main(String[] args)
	{
		System.out.println("starging");
		Proces.path.add(new Directory("/opt/local/bin/"));
		byte[] bytes = GraphViz.toBytes(COMMAND.dot, GraphViz.exampleDotText,
				OUTPUT_FORMAT.png);
		JFrame f = new JFrame();
		f.setSize(300, 300);

		ImageShowerComponent c = new ImageShowerComponent();
		f.getContentPane().add(c);
		f.getContentPane().setLayout(new GridLayout(1, 1));

		paint(bytes, c);
		f.setVisible(true);

	}

	public static class ImageShowerComponent extends Component
	{
		Image image;

		@Override
		public void paint(Graphics g)
		{
			g.drawImage(image, 0, 0, this);
		}
	}
}
