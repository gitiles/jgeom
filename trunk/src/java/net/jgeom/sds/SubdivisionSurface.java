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
package net.jgeom.sds;

import net.jgeom.mesh.Mesh;

/**
 * Implementation of configurable Subdividable Surface.
 * 
 * @author sg
 * @version 1.0
 */
public class SubdivisionSurface implements SubdividableSurface {

  private Mesh controlMesh;
  private SubdivisionScheme scheme=new ArbitraryCatmullClark();
  
  /**
   * Create a subdividable surface with the given mesh. 
   * The subdivision scheme is by default the ArbitraryCatmullClark schem.
   * @see ArbitraryCatmullClark
   * @param mesh Original mesh of the SubdivisionSurface.
   */
  public SubdivisionSurface(Mesh mesh){
  	this.controlMesh=mesh;
  }
  
  public Mesh subdivide(int n) {
	Mesh subMesh=controlMesh;
    for(int i=0; i<n; i++){
    	subMesh=scheme.subdivide(subMesh);
    }
    return subMesh;
  }

  public Mesh getControlMesh() {
    return controlMesh;
  }
  
  /**
   * Set the Subdivision Scheme to use with this SubdivsionSurface
   * @param scheme Scheme to set
   */
  public void setSubdivisionScheme(SubdivisionScheme scheme){
  	this.scheme=scheme;
  }
  

}
