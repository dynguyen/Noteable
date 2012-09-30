/***************************************************************************
 *   Copyright (C) 2006 by Arnaud Desaedeleer                              *
 *   arnaud@desaedeleer.com                                                *
 *                                                                         *
 *   This file is part of OpenOMR                                          *                                                      
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/


package openomr.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DoBlackandWhite
{
	private Bitmap buffImage;
	
	public DoBlackandWhite(Bitmap buffImage)
	{
		int scaledWidth = buffImage.getWidth() / 2;
		int scaledHeight = buffImage.getHeight() / 2;
		Bitmap scaledDownImage = Bitmap.createScaledBitmap(buffImage, scaledWidth, scaledHeight, false);
		this.buffImage = scaledDownImage.copy(Bitmap.Config.ARGB_8888, true);
	}
	
	public Bitmap doBW()
	{
		float colorTotal = 0;
		float[] hsbVals = new float[3];
		//determine color average based on HSB brightness
		for (int i=0; i<buffImage.getHeight(); i+=1)
			for (int j=0; j<buffImage.getWidth(); j+=1)
			{
				int pix = buffImage.getPixel(j, i);
				Color.RGBToHSV(Color.red(pix), Color.green(pix), Color.blue(pix), hsbVals);
				colorTotal += hsbVals[2];
			}
		
		float colorAverage = colorTotal / (buffImage.getHeight() * buffImage.getWidth());
		
		//converts pixels to black or white depending on comparison to color average
		for (int i=0; i<buffImage.getHeight(); i+=1)
			for (int j=0; j<buffImage.getWidth(); j+=1)
			{
				int pix = buffImage.getPixel(j, i);
				//if it's not a black or white pixel, set it to white
				Color.RGBToHSV(Color.red(pix), Color.green(pix), Color.blue(pix), hsbVals);
				if(hsbVals[2] < colorAverage) {
					buffImage.setPixel(j, i, Color.BLACK);
				} else {
					buffImage.setPixel(j, i, Color.WHITE);
				}
			}
		
		for (int i=1; i<buffImage.getWidth()-1; i+=1)
			for (int j=1; j<buffImage.getHeight()-1; j+=1)
			{
				if (buffImage.getPixel(i, j) == 0)
				{
					int p1 = buffImage.getPixel(i-1, j-1);
					int p2 = buffImage.getPixel(i-1, j);
					int p3 = buffImage.getPixel(i-1, j+1);
					int p4 = buffImage.getPixel(i, j-1);
					int p5 = buffImage.getPixel(i, j+1);
					int p6 = buffImage.getPixel(i-1, j-1);
					int p7 = buffImage.getPixel(i-1, j);
					int p8 = buffImage.getPixel(i-1, j+1);
				
					if (p1==-1 && p2==-1 && p3==-1 && p4==-1 && p5==-1 && p6==-1 && p7==-1 && p8==-1)
					{
						buffImage.setPixel(i, j, -1);
					}
				}
			}
		return buffImage;
	}
}
