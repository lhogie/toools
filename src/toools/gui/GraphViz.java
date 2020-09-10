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

import toools.extern.Proces;

public class GraphViz {

	public enum COMMAND {
		dot, neato, fdp, twopi, circo
	}

	public enum OUTPUT_FORMAT {
		bmp, canon, dot, gv, xdot, xdot1_2, xdot1_4, cgimage, cmap, eps, exr, fig, gd, gd2, gif, gtk, ico, imap, cmapx, imap_np, cmapx_np, ismap, jp2, jpg, jpeg, jpe, pct, pict, pdf, pic, plain_ext, png, pov, ps, ps2, psd, sgi, svg, svgz, tga, tif, tiff, tk, vml, vmlz, vrml, wbmp, webp, xlib, x11
	}

	public static byte[] toBytes(COMMAND cmd, String dotText, OUTPUT_FORMAT of) {
		return Proces.exec(cmd.name(), dotText.getBytes(), "-T" + of.name());
	}

	public static String exampleDotText = "graph {		a -- b; 		b -- c; 		a -- c; 		d -- c; 		e -- c; 		e -- a;	} ";
}
