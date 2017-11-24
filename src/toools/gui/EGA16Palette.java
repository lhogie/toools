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
import java.util.ArrayList;

/*
 * Created on May 11, 2005
 */

/**
 * @author luc.hogie
 * 
 * http://en.wikipedia.org/wiki/Enhanced_Graphics_Adapter
 */
public class EGA16Palette extends ArrayList<Color> implements ColorPalette
{
	public EGA16Palette()
	{
		add(new Color(0x000000));
		add(new Color(0x0000AA));
		add(new Color(0x00AA00));
		add(new Color(0x00AAAA));
		add(new Color(0xAA0000));
		add(new Color(0xAA00AA));
		add(new Color(0xAA5500));
		add(new Color(0xAAAAAA));
		add(new Color(0x555555));
		add(new Color(0x5555FF));
		add(new Color(0x55FF55));
		add(new Color(0x55FFFF));
		add(new Color(0xFF5555));
		add(new Color(0xFF55FF));
		add(new Color(0xFFFF55));
		add(new Color(0xFFFFFF));
	}

	@Override
	public Color getColor(int index)
	{
		return get(index);
	}

	@Override
	public int getNumberOfColors()
	{
		return size();
	}

}
