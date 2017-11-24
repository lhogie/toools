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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import toools.util.assertion.Assertions;

/**
 * @author luc.hogie Created on Jun 18, 2004
 */
public class Utilities
{
	public static Color getRandomColor(Random random)
	{
		return getRandomColor(false, random);
	}

	public static String toRGBHexa(Color c)
	{
		return Integer.toHexString(c.getRGB()).substring(2);
	}

	public static Color getColor(Color a, Color b, double r)
	{
		Assertions.ensure(0 <= r && r <= 1, "must be between 0 and 1");
		return new Color((int) (a.getRed() * (1 - r) + b.getRed() * r),
				(int) (a.getGreen() * (1 - r) + b.getGreen() * r),
				(int) (a.getBlue() * (1 - r) + b.getBlue() * r));
	}

	public static Dimension scale(Dimension d, double ratio)
	{
		return new Dimension((int) (d.getWidth() * ratio), (int) (d.getHeight() * ratio));
	}

	public static Dimension limit(Dimension d, Dimension bounds, double ratio)
	{
		ratio = ratio * Math.min(bounds.getWidth() / (double) d.getWidth(),
				bounds.getHeight() / (double) d.getHeight());

		return scale(d, ratio);
	}

	public static Color getRandomColor(boolean alpha, Random random)
	{
		int r = (int) toools.math.MathsUtilities.pickRandomBetween(0, 255, random);
		int g = (int) toools.math.MathsUtilities.pickRandomBetween(0, 255, random);
		int b = (int) toools.math.MathsUtilities.pickRandomBetween(0, 255, random);
		int a = alpha ? 255
				: (int) toools.math.MathsUtilities.pickRandomBetween(0, 255, random);
		return new Color(r, g, b, a);
	}

	public static Color setAlpha(Color color, int alpha)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public static String getCurrentLine(JTextArea ta)
	{
		try
		{
			int lineNumber = ta.getLineOfOffset(ta.getCaretPosition());
			int a = ta.getLineStartOffset(lineNumber);
			int b = ta.getLineEndOffset(lineNumber);
			String line = ta.getText(a, b - a - 1);
			return line;
		}
		catch (BadLocationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static JFrame displayInJFrame(Component c, String title)
	{
		final JFrame f = new JFrame(title);

		f.addWindowListener(new WindowAdapter()
		{

			public void windowClosing(WindowEvent e)
			{
				f.dispose();
			}
		});

		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1, 1));
		p.add(c);
		f.setContentPane(p);
		f.setSize(c.getPreferredSize());
		Utilities.centerOnScreen(f);
		f.setSize(f.getMaximumSize());
		f.setVisible(true);
		return f;
	}

	private static void centerOnScreen(JFrame f)
	{
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = f.getSize();
		f.setLocation((sd.width - frameSize.width) / 2,
				(sd.height - frameSize.height) / 2);
	}
}
