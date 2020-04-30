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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

//import com.apple.eawt.Application;

import toools.os.MacOSX;
import toools.os.OperatingSystem;

public class JFrame2 extends JFrame
{
	public void setSizeToMaximum()
	{
		setSize(getMaximumSize());
	}

	public void setDockIcon(ImageIcon dockIcon)
	{
		if (OperatingSystem.getLocalOperatingSystem() instanceof MacOSX)
		{
//			Application.getApplication().setDockIconImage(dockIcon.getImage());
		}
	}

	public void fullScreen()
	{
		GraphicsDevice d = getGraphicDevice();

		if (d.isFullScreenSupported())
		{
			setUndecorated(true);
			setSize(Toolkit.getDefaultToolkit().getScreenSize());
			d.setFullScreenWindow(this);
		}
	}

	public static GraphicsDevice getGraphicDevice()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		if (gs.length == 1)
		{
			return gs[0];
		}
		else
		{
			throw new IllegalStateException();
		}
	}
}
