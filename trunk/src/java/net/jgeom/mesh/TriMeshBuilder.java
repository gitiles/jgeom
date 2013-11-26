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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;




/**
 * Allows to build a mesh containig triangles.
 * @author sg
 * @version 1.0
 */
public class TriMeshBuilder<T> {
    
  private Map<Edge<T>, Edge<T>> edges = new HashMap<Edge<T>, Edge<T> >();
  private List<Face<T>> faces = new LinkedList<Face<T>>();
  
  private Set<T> pnts = new HashSet<T>();
  
  private FaceFactory<T> faceFactory = null;
  private EdgeFactory<T> edgeFactory = null;
  
  public TriMeshBuilder(FaceFactory<T> facefactory, EdgeFactory<T> edgeFactory){
      this.faceFactory = facefactory;
      this.edgeFactory = edgeFactory;
  }

  private Edge<T> addEdge(T p1, T p2, Face<T> f) {
      
    pnts.add(p1);
    pnts.add(p2);
      
    Edge<T> e = edgeFactory.createEdge();
    e.start = p1;
    e.end = p2;
    Edge<T> tmp = edges.put(e, e);
    if (tmp == null) {
        if(e.ccw != null){
            System.out.println("ccw not null");
        }
      e.ccw = f;
      return e;
    }
    edges.put(tmp, tmp);
    if(tmp.cw != null){
        System.out.println("cw not null:");
    }
    tmp.cw = f;
    return tmp;
  }

  /**
   * Add a Face to this TriMesh builder
   * @param p1 Edgepoint one
   * @param p2 Edgepoint two
   * @param p3 Edgepoint three
   */
  public Face<T> addFace(T p1, T p2, T p3) {
    Face<T> f = faceFactory.createFace();
    f.edge = new Edge[3];
    f.edge[0] = addEdge(p1, p2, f);
    f.edge[1] = addEdge(p2, p3, f);
    f.edge[2] = addEdge(p3, p1, f);
    faces.add(f);
    return f;
  }

  /**
   * get the mesh builded by this TriMeshBuilder
   * @return GenericMesh builded.
   */
  public Mesh<T> getMesh() {
      
    Mesh<T> gm = new Mesh<T>( edges.size(), faces.size());
    int i = 0;

    i = 0;
    Iterator<Face<T>> fit = faces.iterator();
    while (fit.hasNext()) {
      gm.setFace(i++, fit.next());
    }

    Iterator<Edge<T>> eit = edges.values().iterator();
    i = 0;
    while (eit.hasNext()) {
      gm.setEdge(i++,  eit.next());
    }
    return gm;
  }

}
