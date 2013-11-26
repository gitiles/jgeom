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

import java.util.*;

import javax.vecmath.Point3f;


import net.jgeom.mesh.Edge;
import net.jgeom.mesh.Face;
import net.jgeom.mesh.Mesh;
import net.jgeom.mesh.MeshBuilder;
import net.jgeom.mesh.SDSEdge;
import net.jgeom.mesh.SDSVertex;


/**
 * An implementation of the Catmull-Clark Subdivsion Scheme for arbitrary meshes.
 * 
 * @author sg
 * @version 1.0
 */
public class ArbitraryCatmullClark implements SubdivisionScheme {

  public Mesh subdivide(Mesh mesh) {
  /*  List pntsList = (List) mesh.data;
    SDSVertex pnts[] = (SDSVertex []) pntsList.toArray();
    Face faces[] = mesh.getFaces();
    Edge edges[] = mesh.getEdges();

    Map<SDSEdge, Integer> calculatedEdges = new HashMap<SDSEdge, Integer>(edges.length);
    Map<Face, Integer> calculatedFaces = new HashMap<Face, Integer>(faces.length);
    Map<SDSVertex, Integer> calculatedVertices = new HashMap<SDSVertex, Integer>(pnts.length);

    MeshBuilder meshBuilder = new MeshBuilder();

    for (int i = 0; i < faces.length; i++) {
      Face face = faces[i];
      //Calculate new Points
      Integer facePoint = calculatedFaces.get(face);
      //FacePoint (odd)
      if (facePoint == null) {
        facePoint = calculateFacePoint(face, pnts, calculatedFaces, meshBuilder);
      }

      int vertexPoints[] = new int[face.edge.length];
      int edgePoints[] = new int[vertexPoints.length];
      for(int j=0; j < edgePoints.length; j++){
          edgePoints[j] = -1;
          vertexPoints[j] = -1;
      }

      for (int j = 0; j < face.edge.length; j++) {
        SDSEdge edge = (SDSEdge) faces[i].edge[j];
        SDSVertex vertex = pnts[edge.end];
        if (edge.ccw == face) {
          vertex = pnts[edge.start];
        }

        //VertexPoints (even)
        Integer o = calculatedVertices.get(vertex);
        if (o != null) {
          vertexPoints[j] =  o;
        }
        else if (vertex.crease > 0) {
          SDSVertex insert = new SDSVertex(vertex);
          insert.crease = vertex.crease - 1;
          int id = meshBuilder.addVertex(insert);
          calculatedVertices.put(vertex, id);
          vertexPoints[j] = id;
          
        }
        else {

          SDSVertex insert = new SDSVertex(vertex);
          calculatedVertices.put(vertex, insert);
          vertexPoints[j] = insert;
          switch (vertex.getValence()) {
            case 2 : //Edge of surface point						
              meshBuilder.addVertex(insert);
              break;
            case 3 : //check for  boundary pnt
              Edge ve[] = vertex.getValenceEdges();
              Set boundaryPnt = new HashSet(3);
              //Check if it is a boundary vertex
              for (int y = 0; y < ve.length; y++) {
                if (ve[y].ccw == null || ve[y].cw == null) {
                  if (!boundaryPnt.add(pnts[ve[y].start])) {
                    boundaryPnt.remove(pnts[ve[y].start]);
                  }
                  if (!boundaryPnt.add(pnts[ve[y].end])) {
                    boundaryPnt.remove(pnts[ve[y].end]);
                  }
                }
              }
              if (boundaryPnt.size() != 0) { //Use boundary mask
                if (boundaryPnt.size() != 2)
                  throw new RuntimeException(); //TODO:throw appropriate exception
                
                insert.scale(3 / 4f);
                Iterator it = boundaryPnt.iterator();
                while (it.hasNext()) {
                  Point3f tmp =  (Point3f) it.next();
                  tmp.scale(1 / 8f);
                  insert.add(tmp);
                }
                meshBuilder.addVertex(insert);
                break;
              }
              //else use interior mask
            default : //interior pnt
              Set outerPoints = new HashSet();
              Edge vE[] = vertex.getValenceEdges();
              for (int y = 0; y < vE.length; y++) {
                outerPoints.add(pnts[vE[y].start]);
                outerPoints.add(pnts[vE[y].end]);
                Integer f =  calculatedFaces.get(vE[y].ccw);
                if (f == null) {
                  f = calculateFacePoint(vE[y].ccw, pnts, calculatedFaces, meshBuilder);
                }
                outerPoints.add(f);

                f =  calculatedFaces.get(vE[y].cw);
                if (f == null) {
                  f = calculateFacePoint(vE[y].cw, pnts, calculatedFaces, meshBuilder);
                }
                outerPoints.add(f);
              }

              outerPoints.remove(vertex);

              int k = vertex.getValence();
              insert.scale((k - 2) / (float) k);

              Iterator it = outerPoints.iterator();
              float scale = 1.0f / (k * k);
              while (it.hasNext()) {
                Point3f tmp = (Point3f) it.next();
                tmp.scale(scale);
                insert.add(tmp);
              }
              meshBuilder.addVertex(insert);
              break;
          }
        }
        //EdgePoints (odd)
        if (edge.ccw == null || edge.cw == null) { //boundary edge
          SDSVertex edgePoint = new SDSVertex( pnts[edge.start]);
          edgePoint.add( pnts[edge.end] );
          edgePoint.scale(0.5f);
          //calculatedEdges.put(edge, edgePoint);//TODO: unecessary (a boundary edge should  never gets computed twice)
          edgePoints[j] = meshBuilder.addVertex(edgePoint);
        }
        else { //interior edge
          edgePoints[j] = calculatedEdges.get(edge);
          if (edgePoints[j] == -1) {
            if (edge.crease > 0) {
              SDSVertex edgePoint = new SDSVertex( pnts[edge.start]);
              edgePoint.add( pnts[edge.end]);
              edgePoint.scale(0.5f);
			  edgePoint.crease=edge.crease-1;
              edgePoints[j] = meshBuilder.addVertex(edgePoint);
              calculatedEdges.put(edge, edgePoints[j]);
              
            }
            else {

              Integer pnt1 =  calculatedFaces.get(edge.ccw);
              if (pnt1 == null) {
                pnt1 = calculateFacePoint(edge.ccw, pnts, calculatedFaces, meshBuilder);
              }
              Integer pnt2 = calculatedFaces.get(edge.cw);
              if (pnt2 == null) {
                pnt2 = calculateFacePoint(edge.cw, pnts, calculatedFaces, meshBuilder);
              }
              SDSVertex edgePoint = new SDSVertex( pnts[edge.start]);
              edgePoint.add( pnts[edge.end]);
              edgePoint.add( (SDSVertex)meshBuilder.getVertex(pnt1) );
              edgePoint.add( (SDSVertex)meshBuilder.getVertex(pnt2) );
              edgePoint.scale(0.25f);
              edgePoints[j] = meshBuilder.addVertex(edgePoint);;
              calculatedEdges.put(edge, edgePoints[j]);
              
            }
          }
        }
      }

      //Build up new Edges and Faces with calculated Points
      SDSEdge last = new SDSEdge();
      last.start = edgePoints[0];
      last.end = facePoint;
      SDSEdge first = last;
      meshBuilder.addEdge(last);
      for (int j = 0; j < edgePoints.length - 1; j++) {
        Face newFace = new Face();

        SDSEdge e1 = new SDSEdge();
        e1.crease= edgePoints[j].crease;
        e1.start = edgePoints[j];
        e1.end = vertexPoints[j + 1];
        Object o = meshBuilder.addEdge(e1);
        if (o == null) {
          e1.ccw = newFace;
        }
        else {
          e1 = (Edge) o;
          e1.cw = newFace;
        }

        Edge e2 = new Edge();
        e2.crease=edgePoints[j+1].crease;
        e2.start = vertexPoints[j + 1];
        e2.end = edgePoints[j + 1];
        o = meshBuilder.addEdge(e2);
        if (o == null) {
          e2.ccw = newFace;
        }
        else {
          e2 = (Edge) o;
          e2.cw = newFace;
        }

        Edge e3 = new Edge();
        e3.start = edgePoints[j + 1];
        e3.end = facePoint;
        meshBuilder.addEdge(e3);
        e3.ccw = newFace;

        last.cw = newFace;

        newFace.edge = new Edge[] { e1, e2, e3, last };
        last = e3;
        meshBuilder.addFace(newFace);
      }

      Face newFace = new Face();

      Edge e1 = new Edge();
      e1.crease=edgePoints[edgePoints.length - 1].crease;
      e1.start = edgePoints[edgePoints.length - 1];
      e1.end = vertexPoints[0];
      SDSEdge o = (SDSEdge) meshBuilder.addEdge(e1);
      if (o == null) {
        e1.ccw = newFace;
      }
      else {
        e1 = (Edge) o;
        e1.cw = newFace;
      }

      SDSEdge e2 = new SDSEdge();
      e2.crease=edgePoints[0].crease;
      e2.start = vertexPoints[0];
      e2.end = edgePoints[0];
      o = meshBuilder.addEdge(e2);
      if (o == null) {
        e2.ccw = newFace;
      }
      else {
        e2 = o;
        e2.cw = newFace;
      }
      first.ccw = newFace;
      last.cw = newFace;
      newFace.edge = new Edge[] { e1, e2, first, last };
      meshBuilder.addFace(newFace);
    }

    Mesh gm = meshBuilder.getMesh();
    return gm;
    */
      return null;
  }

  /*
  private int calculateFacePoint(Face f, SDSVertex pnts[], Map<Face, Integer> calculatedFaces, MeshBuilder builder) {
    SDSVertex facePoint = new SDSVertex();
    for (int j = 0; j < f.edge.length; j++) {
      Edge edge = f.edge[j];
      int p =  edge.end;
      if (edge.ccw == f) {
        p = edge.start;
      }
      facePoint.add(pnts[p]);
    }
    facePoint.scale(1.0f / f.edge.length);
    int findex = builder.addVertex(facePoint);
    calculatedFaces.put(f, findex);
    return findex;
  }
  */

}
