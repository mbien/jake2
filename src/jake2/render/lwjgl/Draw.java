/*
 * Draw.java
 * Copyright (C) 2003
 *
 * $Id: Draw.java,v 1.2.8.1 2005-07-10 17:55:50 cawe Exp $
 */ 
 /*
Copyright (C) 1997-2001 Id Software, Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package jake2.render.lwjgl;

import jake2.Defines;
import jake2.client.VID;
import jake2.qcommon.Com;
import jake2.render.image_t;
import jake2.util.Lib;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Draw
 * (gl_draw.c)
 * 
 * @author cwei
 */
public abstract class Draw extends Image {

	/*
	===============
	Draw_InitLocal
	===============
	*/
	void Draw_InitLocal() {
		// load console characters (don't bilerp characters)
		draw_chars = GL_FindImage("pics/conchars.pcx", it_pic);
		GL_Bind(draw_chars.texnum);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	/*
	================
	Draw_Char

	Draws one 8*8 graphics character with 0 being transparent.
	It can be clipped to the top of the screen to allow the console to be
	smoothly scrolled off.
	================
	*/
	protected void Draw_Char(int x, int y, int num) {

		num &= 255;
	
		if ( (num&127) == 32 ) return; // space

		if (y <= -8) return; // totally off screen

		int row = num>>4;
		int col = num&15;

		float frow = row*0.0625f;
		float fcol = col*0.0625f;
		float size = 0.0625f;

		GL_Bind(draw_chars.texnum);

		glBegin (GL_QUADS);
		glTexCoord2f (fcol, frow);
		glVertex2f (x, y);
		glTexCoord2f (fcol + size, frow);
		glVertex2f (x+8, y);
		glTexCoord2f (fcol + size, frow + size);
		glVertex2f (x+8, y+8);
		glTexCoord2f (fcol, frow + size);
		glVertex2f (x, y+8);
		glEnd ();
	}


	/*
	=============
	Draw_FindPic
	=============
	*/
	protected image_t Draw_FindPic(String name) {
		image_t image = null;
		String fullname;

		if (!name.startsWith("/") && !name.startsWith("\\"))
		{
			fullname = "pics/" + name + ".pcx";
			image = GL_FindImage(fullname, it_pic);
		} else {
			image = GL_FindImage(name.substring(1), it_pic);
		}
		return image;
	}


	/*
	=============
	Draw_GetPicSize
	=============
	*/
	protected void Draw_GetPicSize(Dimension dim, String pic)	{

		image_t image = Draw_FindPic(pic);
		dim.width = (image != null) ? image.width : -1;
		dim.height = (image != null) ? image.height : -1;
	}

	/*
	=============
	Draw_StretchPic
	=============
	*/
	protected void Draw_StretchPic (int x, int y, int w, int h, String pic) {
		
		image_t image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			VID.Printf (Defines.PRINT_ALL, "Can't find pic: " + pic +'\n');
			return;
		}

		if (scrap_dirty)
			Scrap_Upload();

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0) ) && !image.has_alpha)
			glDisable(GL_ALPHA_TEST);

		GL_Bind(image.texnum);
		glBegin (GL_QUADS);
		glTexCoord2f (image.sl, image.tl);
		glVertex2f (x, y);
		glTexCoord2f (image.sh, image.tl);
		glVertex2f (x+w, y);
		glTexCoord2f (image.sh, image.th);
		glVertex2f (x+w, y+h);
		glTexCoord2f (image.sl, image.th);
		glVertex2f (x, y+h);
		glEnd ();

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) !=0 ) ) && !image.has_alpha)
			glEnable(GL_ALPHA_TEST);
	}


	/*
	=============
	Draw_Pic
	=============
	*/
	protected void Draw_Pic(int x, int y, String pic)
	{
		image_t image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			VID.Printf(Defines.PRINT_ALL, "Can't find pic: " +pic + '\n');
			return;
		}
		if (scrap_dirty)
			Scrap_Upload();

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) && !image.has_alpha)
			glDisable (GL_ALPHA_TEST);

		GL_Bind(image.texnum);

		glBegin (GL_QUADS);
		glTexCoord2f (image.sl, image.tl);
		glVertex2f (x, y);
		glTexCoord2f (image.sh, image.tl);
		glVertex2f (x+image.width, y);
		glTexCoord2f (image.sh, image.th);
		glVertex2f (x+image.width, y+image.height);
		glTexCoord2f (image.sl, image.th);
		glVertex2f (x, y+image.height);
		glEnd ();

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
			glEnable (GL_ALPHA_TEST);
	}

	/*
	=============
	Draw_TileClear

	This repeats a 64*64 tile graphic to fill the screen around a sized down
	refresh window.
	=============
	*/
	protected void Draw_TileClear(int x, int y, int w, int h, String pic) {
		image_t	image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			VID.Printf(Defines.PRINT_ALL, "Can't find pic: " + pic + '\n');
			return;
		}

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
			glDisable(GL_ALPHA_TEST);

		GL_Bind(image.texnum);
		glBegin (GL_QUADS);
		glTexCoord2f(x/64.0f, y/64.0f);
		glVertex2f (x, y);
		glTexCoord2f( (x+w)/64.0f, y/64.0f);
		glVertex2f(x+w, y);
		glTexCoord2f( (x+w)/64.0f, (y+h)/64.0f);
		glVertex2f(x+w, y+h);
		glTexCoord2f( x/64.0f, (y+h)/64.0f );
		glVertex2f (x, y+h);
		glEnd ();

		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
			glEnable(GL_ALPHA_TEST);
	}


	/*
	=============
	Draw_Fill

	Fills a box of pixels with a single color
	=============
	*/
	protected void Draw_Fill(int x, int y, int w, int h, int colorIndex)	{

		if ( colorIndex > 255)
			Com.Error(Defines.ERR_FATAL, "Draw_Fill: bad color");

		glDisable(GL_TEXTURE_2D);

		int color = d_8to24table[colorIndex]; 

		glColor3ub(
			(byte)((color >> 0) & 0xff), // r
			(byte)((color >> 8) & 0xff), // g
			(byte)((color >> 16) & 0xff) // b
		);

		glBegin (GL_QUADS);

		glVertex2f(x,y);
		glVertex2f(x+w, y);
		glVertex2f(x+w, y+h);
		glVertex2f(x, y+h);

		glEnd();
		glColor3f(1,1,1);
		glEnable(GL_TEXTURE_2D);
	}

	//=============================================================================

	/*
	================
	Draw_FadeScreen
	================
	*/
	protected void Draw_FadeScreen()	{
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glColor4f(0, 0, 0, 0.8f);
		glBegin(GL_QUADS);

		glVertex2f(0,0);
		glVertex2f(vid.width, 0);
		glVertex2f(vid.width, vid.height);
		glVertex2f(0, vid.height);

		glEnd();
		glColor4f(1,1,1,1);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

// ====================================================================

	IntBuffer image32=Lib.newIntBuffer(256*256);
	ByteBuffer image8=Lib.newByteBuffer(256*256);
	

	/*
	=============
	Draw_StretchRaw
	=============
	*/
	protected void Draw_StretchRaw (int x, int y, int w, int h, int cols, int rows, byte[] data)
	{
		int i, j, trows;
		int sourceIndex;
		int frac, fracstep;
		float hscale;
		int row;
		float t;

		GL_Bind(0);

		if (rows<=256)
		{
			hscale = 1;
			trows = rows;
		}
		else
		{
			hscale = rows/256.0f;
			trows = 256;
		}
		t = rows*hscale / 256;

		if ( !qglColorTableEXT )
		{
			//int[] image32 = new int[256*256];
			image32.clear();
			int destIndex = 0;

			for (i=0 ; i<trows ; i++)
			{
				row = (int)(i*hscale);
				if (row > rows)
					break;
				sourceIndex = cols*row;
				destIndex = i*256;
				fracstep = cols*0x10000/256;
				frac = fracstep >> 1;
				for (j=0 ; j<256 ; j++)
				{
					image32.put(destIndex + j, r_rawpalette[data[sourceIndex + (frac>>16)] & 0xff]);
					frac += fracstep;
				}
			}
			glTexImage2D (GL_TEXTURE_2D, 0, gl_tex_solid_format, 256, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, image32);
		}
		else
		{
			//byte[] image8 = new byte[256*256];
			image8.clear();
			int destIndex = 0;;

			for (i=0 ; i<trows ; i++)
			{
				row = (int)(i*hscale);
				if (row > rows)
					break;
				sourceIndex = cols*row;
				destIndex = i*256;
				fracstep = cols*0x10000/256;
				frac = fracstep >> 1;
				for (j=0 ; j<256 ; j++)
				{
					image8.put(destIndex  + j, data[sourceIndex + (frac>>16)]);
					frac += fracstep;
				}
			}

			glTexImage2D( GL_TEXTURE_2D, 
						   0, 
						   GL_COLOR_INDEX8_EXT, 
						   256, 256, 
						   0, 
						   GL_COLOR_INDEX, 
						   GL_UNSIGNED_BYTE, 
						   image8 );
		}
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
			glDisable (GL_ALPHA_TEST);

		glBegin (GL_QUADS);
		glTexCoord2f (0, 0);
		glVertex2f (x, y);
		glTexCoord2f (1, 0);
		glVertex2f (x+w, y);
		glTexCoord2f (1, t);
		glVertex2f (x+w, y+h);
		glTexCoord2f (0, t);
		glVertex2f (x, y+h);
		glEnd ();

		if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
			glEnable (GL_ALPHA_TEST);
	}

}
