/*
 * jgeom: Geometry Library fo Java
 * 
 * Copyright (C) 2005  Samuel Gerber
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.jgeom.j3d.evaluators;

import javax.media.j3d.GeometryArray;
import javax.vecmath.Point3f;

import net.jgeom.mesh.Mesh;


/**
 * Interface for evaluating Subdividable Surfaces into a Java3D representation.
 * @author sg
 * @version 1.0
 */
public interface MeshEvaluator {
	
    public final static int ANY = 0;
    public final static int QUAD = 1;
    public final static int TRI = 2;
    
  /**
   * Generate a GeometryArray of the given SubdividableSurface after nSubD subdivsions.
   * @param m Mesh to evaluate.
   * @return A GeomtryArray representing the surface after nSubD subdivsions.
   */
  GeometryArray evaluateSurface(Mesh<Point3f> m, int meshtype);
  
}
