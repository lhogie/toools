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

package toools.img;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;

public class Utilities
{
	public static BufferedImage toBufferedImage(Image image, ColorModel cm)
	{
		if (image instanceof BufferedImage
				&& ((BufferedImage) image).getColorModel().equals(cm))
		{
			return (BufferedImage) image;
		}
		else
		{
			int w = image.getWidth(null);
			int h = image.getHeight(null);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
			BufferedImage result = new BufferedImage(cm, raster, alphaPremultiplied,
					null);
			Graphics2D g = result.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return result;
		}
	}

	public static Image scale(ImageIcon imageIcon, int width, int height)
	{
		Image image = imageIcon.getImage();
		return image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
	}

	/**
	 * Posted by alpha02 at http://www.dreamincode.net/code/snippet1076.htm
	 */
	public static BufferedImage toBufferedImage(Image image)
	{
		if (image instanceof BufferedImage)
			return (BufferedImage) image;

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		try
		{
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;

			if (hasAlpha == true)
				transparency = Transparency.BITMASK;

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();

			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null),
					transparency);
		}
		catch (HeadlessException e)
		{
		} // No screen

		if (bimage == null)
		{
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;

			if (hasAlpha == true)
			{
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public static boolean hasAlpha(Image image)
	{
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage)
			return ((BufferedImage) image).getColorModel().hasAlpha();

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try
		{
			pg.grabPixels();
		}
		catch (InterruptedException e)
		{
		}

		// Get the image's color model
		return pg.getColorModel().hasAlpha();
	}
}
