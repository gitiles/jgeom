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

import javax.media.j3d.*;

import net.jgeom.sds.SubdividableSurface;


/**
 * A basic implementation of SubdivsionSurface evaluator for Java3D.
 * Evaluates the mesh into TriangleArray if type of mesh is ANY or TRI, and into a QuadArray if type is QUAD.
 * @author sg
 * @version 1.0
 */
public class BasicSDSEvaluator implements SDSurfaceEvaluator {
  private BasicMeshEvaluator eval=new BasicMeshEvaluator();
    
  public GeometryArray evaluateSurface(SubdividableSurface sds, int nSubD) {
    return eval.evaluateSurface(sds.subdivide(nSubD), BasicMeshEvaluator.ANY);
  }

  public GeometryArray controlSurface(SubdividableSurface sds) {
    return eval.evaluateSurface(sds.getControlMesh(), BasicMeshEvaluator.ANY);
  }
}
