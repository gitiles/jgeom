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
import javax.vecmath.Point3f;

import net.jgeom.mesh.Edge;
import net.jgeom.mesh.Face;
import net.jgeom.mesh.Mesh;


/**
 * A basic implementation of SubdivsionSurface evaluator for Java3D.
 * Evaluates the mesh into TriangleArray if type of mesh is ANY or TRI, and into a QuadArray if type is QUAD.
 * @author sg
 * @version 1.0
 */
public class BasicMeshEvaluator implements MeshEvaluator {
  

    
  public GeometryArray evaluateSurface(Mesh<Point3f> m, int meshtype) {
    //MeshPoint pnts[] = m.getPoints();
    Face<Point3f> faces[] = m.getFaces();
    GeometryArray result = null;
    int index = 0;
    switch (meshtype) {
      case ANY :
        int nTri = 0;

        for (int i = 0; i < faces.length; i++) {
          nTri += faces[i].edge.length - 2;
        }
        TriangleArray ta = new TriangleArray(3 * nTri, TriangleArray.COORDINATES); 
        for (int i = 0; i < faces.length; i++) {
          Edge<Point3f> edges[] = faces[i].edge;
          Point3f vertices[] = new Point3f[edges.length];
          for (int j = 0; j < edges.length; j++) {
            if (edges[j].ccw == faces[i]) {
              vertices[j] = edges[j].start;
            }
            else {
              vertices[j] = edges[j].end;
            }
          }
          int low = 0;
          int high = vertices.length - 1;
          boolean incLow = true;
          while (low < high - 1) {
            ta.setCoordinate(index++, vertices[low]);
            int tmp = high;
            if (incLow) {
              ta.setCoordinate(index++, vertices[++low]);
              incLow = false;
            }
            else {
              ta.setCoordinate(index++, vertices[--high]);
              incLow = true;
            }
            ta.setCoordinate(index++, vertices[tmp]);
          }
        }
        result = ta;
        break;
      case QUAD :
        QuadArray qa = new QuadArray(4 * faces.length, QuadArray.COORDINATES);
        for (int i = 0; i < faces.length; i++) {
          Edge<Point3f> edges[] = faces[i].edge;
          for (int j = 0; j < edges.length; j++) {
            if (edges[j].ccw == faces[i]) {
              qa.setCoordinate(index++, edges[j].start);
            }
            else {
              qa.setCoordinate(index++, edges[j].end);
            }
          }
        }
        result = qa;
        break;
      case TRI :
        TriangleArray tar = new TriangleArray(3 * faces.length, TriangleArray.COORDINATES);
        for (int i = 0; i < faces.length; i++) {
          Edge<Point3f> edges[] = faces[i].edge;
          for (int j = 0; j < edges.length; j++) {
            if (edges[j].ccw == faces[i]) {
              tar.setCoordinate(index++, edges[j].start);
            }
            else {
              tar.setCoordinate(index++, edges[j].end);
            }
          }
        }
        result = tar;
        break;
    }

    return result;

  }
}
