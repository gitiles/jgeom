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
package net.jgeom.mesh;


/**
 * An implementation of a mesh, used for the Catmull-Clark scheme.
 * @author sg
 * @version 1.0
 */
public class Mesh<T>  {
  
  private Face<T> faces[];
  private Edge<T> edges[];

  /**
   * Create a new GenericMesh with space for the given number of vertices, edges and faces.
   * // @param nVertices Number of vertices for this mesh.
   * @param nEdges Number of edges for this mesh.
   * @param nFaces Number of faces for this mesh.
   */
  public Mesh(int nEdges, int nFaces) {
    faces = new Face[nFaces];
    edges = new Edge[nEdges];
  }

  public Face<T>[] getFaces() {
    return faces;
  }

  public Edge<T>[] getEdges() {
    return edges;
  }

  /**
   * Set the edge at the given index.
   * @param i Index to set edge
   * @param start Index of the startpoint of the edge to set
   * @param end Index of the endpoint of the edge to set.
   */
  public void setEdge(int i, T start, T end) {
    edges[i] = new Edge<T>();
    edges[i].start = start;
    edges[i].end = end;
  }

  
  /**
   * Set edge at the given index to the one given.
   * @param i Index to set the edge
   * @param e Edge to set.
   */
  public void setEdge(int i, Edge<T> e){
  	edges[i]=e; 	
  }

  /**
   * Set the face at the given index in the face list
   * @param i Index to set Face
   * @param edgs Indices of edges
   * @param ccw  boolean indicating for each edge if it is ccw (true) or cw (false)
   */
  public void setFace(int i, int[] edgs, boolean ccw[]) {
  	Face<T> f=new Face<T>();
    faces[i] = f;
	f.edge = new Edge[edgs.length];
    for (int j = 0; j < edgs.length; j++) {
      f.edge[j]=edges[edgs[j]];
      if (ccw[j]) {
        edges[edgs[j]].ccw = f;
      }
      else {
		 edges[edgs[j]].cw = f;
      }
    }
  }
  
  /**
   * Set the face at index i.
   * @param i Index to set face.
   * @param f Face to set.
   */
  public void setFace(int i, Face<T> f){
  	faces[i]=f;
    //TODO check if edges point to that face
  }
  
};
