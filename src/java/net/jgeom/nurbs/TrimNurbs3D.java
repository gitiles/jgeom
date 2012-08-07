/*
 * jgeom: Geometry Library for Java
 * 
 * Copyright (C) 2005  Samuel Gerber
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.jgeom.nurbs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

/**
 * @author alex
 * This class define a 3D Trimming curve. The curve is supposed to lie on a given 3d surface.
 * It calls a point projection and invertion routine in order to find the corresponding surface parametrization.
 */
public class TrimNurbs3D extends TrimCurve {
	NurbsCurve curve=null;//keep a reference to the original NurbsCurve
	MeshedNurbsSurface surface=null;

	public TrimNurbs3D(NurbsCurve curve, MeshedNurbsSurface surface, int segnum) {
		super();
		this.curve=curve;
		this.surface=surface;
		float uKnots[] = curve.getKnots();
		float u = uKnots[0];
		float max = uKnots[uKnots.length - 1];
		float uStep = (max - u) / segnum;
		
		UVCoord2f linesLocal[] = new UVCoord2f[segnum + 1];

		linesLocal = new UVCoord2f[segnum+1];
		u = uKnots[0];
		for (int i = 0; i <= segnum; i++) {
			Point3f point = curve.pointOnCurve(u);
			//convert 3d point to 2d UV.
			UVCoord2f coord = surface.getUVParameterInvertion(point);
			linesLocal[i] = coord;
			u += uStep;
		}
		
		this.lines=linesLocal;
	}
	
	public NurbsCurve getNurbsCurve()
	{
		return this.curve;
	}

}
