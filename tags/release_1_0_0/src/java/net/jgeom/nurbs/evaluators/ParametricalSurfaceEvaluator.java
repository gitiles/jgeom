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
package net.jgeom.nurbs.evaluators;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import net.jgeom.nurbs.*;
import net.jgeom.nurbs.geomContainers.GeometryArray;
import net.jgeom.nurbs.geomContainers.QuadArray;

public class ParametricalSurfaceEvaluator {

	public ArrayList<Integer> indexes = new ArrayList<Integer>();

	public ArrayList<Point3f> vertexList = new ArrayList<Point3f>();

	public ArrayList<UVCoord2f> uvCoords = new ArrayList<UVCoord2f>();

	public ParametricalSurfaceEvaluator() {
	}

	public GeometryArray evaluateSurface(Surface surface, int segU,
			int segV) {
//		float uKnots[] = surface.getUKnots();
		float u = 0; //TODO: always 0?
		float maxU = 1;
		float uStep = (maxU - u) / segU;

//		float vKnots[] = surface.getVKnots();
		float v = 0; //TODO: always 0?
		float maxV = 1;
		float vStep = (maxV - v) / segV;

		//	    System.out.println("surface UV domain : U["+u+" .. "+maxU+"] , V["+v+" .. "+maxV+"]");

		QuadArray qa = new QuadArray(segU * segV * 4, QuadArray.COORDINATES);
		int step = segV * 4;
		int low = -step + 1;
		int high = 0;

		for (int i = 0; i <= segU; i++) {
			v = 0;
			for (int j = 0; j <= segV; j++) {
				Point3f point = surface.pointOnSurface(u, v);
				v += vStep;
				if (i == 0) {
					if (j == 0) {
						qa.setCoordinate(0, point);
						System.out.println(0);
					} else if (j == segV) {
						qa.setCoordinate(step - 1, point);
						System.out.println(step - 1);
					} else {
						int tmp = j * 4;
						qa.setCoordinate(tmp, point);
						//	            System.out.println(tmp);
						qa.setCoordinate(tmp - 1, point);
						//	            System.out.println(tmp-1);
					}
				} else if (i == segU) {
					if (j == 0) {
						qa.setCoordinate(low, point);
						//	            System.out.println(low);
					} else if (j == segV) {
						qa.setCoordinate(low + (j - 1) * 4 + 1, point);
						//	            System.out.println(low + (j - 1) * 4 + 1);
					} else {
						int tmp = low + (j - 1) * 4;
						qa.setCoordinate(tmp + 1, point);
						//	            System.out.println(tmp+1);
						qa.setCoordinate(tmp + 4, point);
						//	            System.out.println(tmp+4);
					}
				} else {
					if (j == 0) {
						qa.setCoordinate(low, point);
						//	            System.out.println(low);
						qa.setCoordinate(high, point);
						//	            System.out.println(high);
					} else if (j == segV) {
						qa.setCoordinate(low + step - 3, point);
						//	            System.out.println(low+step-3);
						qa.setCoordinate(high + step - 1, point);
						//	            System.out.println(high+step-1);
					} else {
						int tmp = low + (j - 1) * 4 + 1;
						qa.setCoordinate(tmp, point);
						//	            System.out.println(tmp);
						qa.setCoordinate(tmp + 3, point);
						//	            System.out.println(tmp+3);
						tmp = high + j * 4;
						qa.setCoordinate(tmp, point);
						//	            System.out.println(tmp);
						qa.setCoordinate(tmp - 1, point);
						//	            System.out.println(tmp-1);
					}
				}
			}
			low += step;
			high += step;
			u += uStep;
		}

		return qa;
	}


}
