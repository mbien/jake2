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

// Created on 31.10.2003 by RST.
// $Id: trace_t.java,v 1.3.2.1 2004-09-06 19:39:15 hzi Exp $

package jake2.game;

import jake2.util.Math3D;

//a trace is returned when a box is swept through the world
public class trace_t implements Cloneable {
	public boolean allsolid; // if true, plane is not valid
	public boolean startsolid; // if true, the initial point was in a solid area
	public float fraction; // time completed, 1.0 = didn't hit anything
	public float[] endpos = { 0, 0, 0 }; // final position
	// memory
	public cplane_t plane = new cplane_t(); // surface normal at impact
	// pointer
	public csurface_t surface; // surface hit
	public int contents; // contents on other side of surface hit
	// pointer
	public edict_t ent; // not set by CM_*() functions

	public void set(trace_t from)
	{
		allsolid = from.allsolid;
		startsolid = from.allsolid;
		fraction = from.fraction;
		Math3D.VectorCopy(from.endpos, endpos);
		plane.set(from.plane);
		surface = from.surface;
		contents = from.contents;
		ent = from.ent;
	}
}
