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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import toools.io.file.RegularFile;

public class FIGRenderingAWTComponent extends PagePanel
{
	public void setPDFData(byte[] bytes, int pageNumber)
	{
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		
		try
		{
			PDFFile pdffile = new PDFFile(buf);
			System.out.println(pdffile.getNumPages());
			PDFPage page = pdffile.getPage(pageNumber);
			showPage(page);
			doLayout();
			repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void displayInJFrame(String title)
	{
		final JFrame f = new JFrame( title);
		f.setContentPane(this);
		f.setSize(600, 450);
		f.setVisible(true);
		f.doLayout();
		f.repaint();

		f.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e)
			{
				f.dispose();
			}
		});

	}
	
	public static void main(String[] args) throws IOException
	{
		FIGRenderingAWTComponent p = new FIGRenderingAWTComponent();
		p.displayInJFrame("test");
		p.setPDFData(new RegularFile("/Users/lhogie/Downloads/grph-user-manual.pdf").getContent(), 0);
	}
}
