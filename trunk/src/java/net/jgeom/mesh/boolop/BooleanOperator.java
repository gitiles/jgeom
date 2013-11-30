/*
 * jgeom: Geometry Library fo Java
 * 
 * Copyright (C) 2007  Samuel Gerber
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
package net.jgeom.mesh.boolop;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;

import net.jgeom.mesh.DefaultEdgeFactory;
import net.jgeom.mesh.Edge;
import net.jgeom.mesh.Face;
import net.jgeom.mesh.Mesh;
import net.jgeom.mesh.TriMeshBuilder;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Class that calculates from two Java3D TriangleArrays representing
 * surfaces another TriangleArray using a boolean operator (difference, union,
 * intersect).
 * 
 * @author sg
 * @version 1.0
 */
public class BooleanOperator {
    
  

    /**
    * Operator definition
    */
  public static final int INTERSECTION = 0;
  public static final int INTERSECTIONLEFT = 1;
  public static final int INTERSECTIONRIGHT = 2;
  public static final int UNION = 3;
  public static final int UNIONLEFT = 4;
  public static final int UNIONRIGHT = 5;
  public static final int DIFFERENCE = 6;
  public static final int DIFFERENCELEFT = 7;
  public static final int DIFFERENCERIGHT= 8;
  
  //DEbug only
  public static final int ALLRIGHT= 9;
  public static final int ALLLEFT= 10;

    public float TOLERANCE = 10E-10f;

    private Mesh<Point3f> newLeft;
    private Mesh<Point3f> newRight;

    private Mesh<Point3f> intoMesh(TriangleArray ta) {
        long time = System.currentTimeMillis();
        TriMeshBuilder<Point3f> mb = new TriMeshBuilder<Point3f>(new BoolopFaceFactory<Point3f>(),
                new DefaultEdgeFactory<Point3f>());
        int n = ta.getVertexCount();
        int s = ta.getInitialCoordIndex();

        for (int i = s + 2; i < s + n; i += 3) {
            Point3f p1 = new Point3f();
            Point3f p2 = new Point3f();
            Point3f p3 = new Point3f();
            
            ta.getCoordinate(i - 2, p1);
            ta.getCoordinate(i - 1, p2);
            ta.getCoordinate(i, p3);
            if(p1.equals(p2) || p1.equals(p3) || p2.equals(p3)) continue;
            
            mb.addFace(p1, p2, p3);

        }

        Mesh<Point3f> mesh = mb.getMesh();

        // TODO debug only - Check mesh
        for (Face f : mesh.getFaces()) {
            for (int i = 0; i < 3; i++) {
                if (f.edge[i] == null) {
                    System.out.println("Face has a null edge");
                }
            }
        }

        for (Edge e : mesh.getEdges()) {
            if (e.ccw == null) {
                System.out.println("ccw is null");
                break;
            }
            if (e.cw == null) {
                System.out.println("cw is null");
                break;
            }
        }
        //end check

        System.out.println("Into mesh time: " + (System.currentTimeMillis() - time));
        return mesh;
    }

    //debug only
    private List<List<IntersectInfo>> cuts = new LinkedList<List<IntersectInfo>>();
    private LinkedList<IntersectInfo> cutTmp;

    //Calculates two new meshes that which are labeled into in and outside triangles
    //by cutting the mesh along the interesction of the two meshes
    private void calculateNewSurface(Mesh<Point3f> left, Mesh<Point3f> right) {

        cuts.clear();

        Face<Point3f> lFace[] = left.getFaces();
        Edge<Point3f> rEdge[] = right.getEdges();

        //Set of already found intersection points
        Set<Point3f> pips = new HashSet<Point3f>();

        long time1 = System.currentTimeMillis();
        
        // --- 1. Find cutlines
        // Finds the cutlines where the two surfaces interescts for each
        // and adds the to the correspond faces
        
        //Find an starting point of an intersection curve
        IntersectInfo start = null;
        for (int i = 0; i < lFace.length; i++) {
            Face<Point3f> f = lFace[i];
            for (int j = 0; j < rEdge.length; j++) {
                Point3f ip = BoolopMath.intersect(f, rEdge[j]);
                if (ip != null && !pips.contains(ip)) {
                    pips.add(ip);
                    start = new IntersectInfo((BoolopFace<Point3f>) f, rEdge[j], BoolopMath.startLocation(), ip);
                    cutTmp = new LinkedList<IntersectInfo>();
                    cuts.add(cutTmp);
                    //follow the found staring point and create a cutline
                    stitch(start, pips);
                }
            }
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Find cutline time: " + (time2 - time1));

        System.out.println("# Cuts: " + cuts.size());

        // --- 2. Build new meshes
        // Splits faces that have cutlines (the ones along the seam = intersection curves of the two surface) 
        // into new triangle faces 
        // that adhere to the cutline and also mark the spliited faces
        // as in or outside.
        newLeft = newMesh(left);
        newRight = newMesh(right);
        
        // --- 3. traveres the mesh and decide which faces belong in and outside
        // This can be done fastly since we along the seam the faces are already decided 
        // to be in and outside. We traverse the mesh recursively (starting from a face that is
        // along the mesh) and mark each face accordingly. When we cross the intersection seam 
        // we switch the location.
        traverseMesh(newLeft);
        traverseMesh(newRight);
        time1 = System.currentTimeMillis();
        System.out.println("New mesh time: " + (time1 - time2));
        
        //TODO debug only
        for(Face<Point3f> face:newLeft.getFaces()){
            LocatedFace<Point3f> lf = (LocatedFace<Point3f>) face;
            if(lf.location == LocatedFace.UNKNOWN){
                System.out.println("UNKNOWN Location lef");
            }
        }
        
        for(Face<Point3f> face:newRight.getFaces()){
            LocatedFace<Point3f> lf = (LocatedFace<Point3f>) face;
            if(lf.location == LocatedFace.UNKNOWN){
                System.out.println("UNKNOWN Location right");
            }
        }

    }

    // Once an interestcion has found this methods follow along the cut
    // and adds cutlines to the corrsponding faces
    //
    //It's called stitch because it resembles travelling aling a seam.
    //Once an intersection is found the dajacent faces of the intersecting edge are checked
    //for more interesction with the intersected face and if there are none the edges of
    //the interesected edge are tested for intersection of the adjacent face of the intersecting
    //edge. In this way we travel along the interesction seam of the two surfaces and
    //add cutlines to the corresponding triangles which will e split into smaller triangles
    //according to the cutlines in a later stage. During the intersection the start and end points 
    //are also marked as inside or outside the other surfaces. This allows for fast location 
    //decicion in a later stage.
    private void stitch(IntersectInfo startIntersection, Set<Point3f> pips) {

        IntersectInfo current = startIntersection;
        cutTmp.add(current);

        //special treatment for start face
        CutLine<Point3f> fStart = new CutLine<Point3f>();
        CutLine<Point3f> fPoly = fStart;

        boolean done = false;
        CutLine<Point3f> ePoly = instanciateCutLine(current.edge, current.ccw);
        if (ePoly == null) {
            done = true;
            current.ccw = !current.ccw;
            ePoly = instanciateCutLine(current.edge, current.ccw);
        }

        fPoly.points.addLast(current.ip);
        ePoly.points.addLast(current.ip);
        ePoly.start = current.edge;
        ePoly.startEdgeStartLocation = current.startLocation;

        
        IntersectInfo next = null;
        for (;;) {

            int cutcase = -1;
            //see if edges of current face cut adjacent face of current cutting edge
            if (current.ccw) {
                next = intersect(current.edge.ccw, current.face.edge, null, current.ip);
            } else {
                next = intersect(current.edge.cw, current.face.edge, null, current.ip);
            }

            if (next != null) {
                next.ccw = current.face != next.edge.ccw;
                cutcase = 0;
            } else {
                //see if adjacent face of current cutting edge cuts current face
                if (current.ccw) {
                    next = intersect(current.face, current.edge.ccw.edge, current.edge, current.ip);
                    if (next != null) {
                        next.ccw = next.edge.ccw != current.edge.ccw;
                    }
                } else {
                    next = intersect(current.face, current.edge.cw.edge, current.edge, current.ip);
                    if (next != null) {
                        next.ccw = next.edge.ccw != current.edge.cw;
                    }
                }
                if (next != null) {
                    cutcase = 1;
                }
            }
            if (cutcase == -1) {
                //DebugUtils.printFaceAndNeighbors(current.face, (current.ccw ? current.edge.ccw: current.edge.cw), "nextnull.txt");
                System.err.println("next == null");
                break;
            }
            pips.add(next.ip);
            //add points depending on cutcase to the current face cutline
            //and the current edge cutline
            if (cutcase == 0) {
                //The intersecting face switches
                
                ePoly.points.addLast(next.ip);

                fPoly.points.addLast(next.ip);
                fPoly.end = next.edge;
                fPoly.endEdgeStartLocation = next.startLocation;

                fPoly = ePoly;

            } else {
                //we are still intersectin the same face.
                fPoly.points.addLast(next.ip);

                ePoly.points.addLast(next.ip);
                ePoly.end = next.edge;
                ePoly.endEdgeStartLocation = next.startLocation;
            }

            //check if we looped around
            if (startIntersection.edge == next.edge && startIntersection.face == next.face) {
                //finish up last cutline and end
                //Collections.reverse(fStart.points);
                fStart.points.removeFirst();
                while(!fStart.points.isEmpty()){
                    fPoly.points.addLast(fStart.points.removeFirst()); 
                }
                fPoly.end = fStart.end;
                fPoly.endEdgeStartLocation = fStart.endEdgeStartLocation;
                System.out.println("closed cut");
                break;
            }

            ePoly = instanciateCutLine(next.edge, next.ccw);

            //did we hit an end?
            if (ePoly == null) {
                if (done) {
                    break;
                }
                done = true;
                System.out.println("inverted direction");
                //invert direction
                next = startIntersection;
                fPoly = fStart;
                fPoly.start = fPoly.end;
                fPoly.startEdgeStartLocation = fPoly.endEdgeStartLocation;
                Collections.reverse(fPoly.points);
                next.ccw = !next.ccw;
                ePoly = instanciateCutLine(next.edge, next.ccw);
                continue;
            } else {
                ePoly.points.addLast(next.ip);
                ePoly.start = next.edge;
                ePoly.startEdgeStartLocation = next.startLocation;
            }

            current = next;
        }
    }

    //Helper methods for stitch
    private CutLine<Point3f> instanciateCutLine(Face<Point3f> face) {
        if (face == null) {
            return null;
        }
        BoolopFace<Point3f> bf = (BoolopFace<Point3f>) face;
        if (bf.cutlines == null) {
            bf.cutlines = new LinkedList<CutLine<Point3f>>();
        }
        CutLine<Point3f> bp = new CutLine<Point3f>();
        bf.cutlines.add(bp);
        return bp;
    }

    private CutLine<Point3f> instanciateCutLine(Edge<Point3f> edge, boolean ccw) {
        if (ccw) {
            return instanciateCutLine(edge.ccw);
        } else {
            return instanciateCutLine(edge.cw);
        }
    }

    //traverse the mesh and decide location (in or outside for each face
    private void traverseMesh(Mesh<Point3f> mesh) {
        for (Face<Point3f> f : mesh.getFaces()) {
            LocatedFace<Point3f> lf = (LocatedFace<Point3f>) f;
            if (lf.location != LocatedFace.UNKNOWN) {
                /*int location = lf.location;
                 if(location == LocatedFace.SAME_DIFF){
                 location = LocatedFace.OUTSIDE;
                 }
                 else if(location == LocatedFace.SAME_UNION){
                 location = LocatedFace.INSIDE;
                 }*/
                visitNode(lf, 0);
                break;
            }
        }

    }

    //helper method for traverse mesh (recursive traversing)
    private void visitNode(LocatedFace<Point3f> f, int location) {
        if (f != null && !f.visited) {
            f.visited = true;
            if (f.location == LocatedFace.UNKNOWN) {
                f.location = location;
            } else {
                location = f.location;
                /*    if(location == LocatedFace.SAME_DIFF){
                 location = LocatedFace.OUTSIDE;
                 }
                 else if(location == LocatedFace.SAME_UNION){
                 location = LocatedFace.INSIDE;
                 }*/
            }
            for (Edge<Point3f> e : f.edge) {
                if (e.ccw == f) {
                    visitNode((LocatedFace<Point3f>) e.cw, location);
                } else {
                    visitNode((LocatedFace<Point3f>) e.ccw, location);
                }
            }
        }
    }

    
    //Build the new mesh by splitting triangles with cutlines and keeping the regular ones
    private Mesh<Point3f> newMesh(Mesh<Point3f> mesh) {
        TriMeshBuilder<Point3f> mb = new TriMeshBuilder<Point3f>(new LocatedFaceFactory<Point3f>(),
                new DefaultEdgeFactory<Point3f>());

        TriangleSplitter splitter = new TriangleSplitter();
        Point3f fpoints[] = new Point3f[3];
        for (Face<Point3f> f : mesh.getFaces()) {
            BoolopFace<Point3f> bf = (BoolopFace<Point3f>) f;
            if (bf.cutlines == null) {
                BoolopMath.setPoints(bf, fpoints);
                mb.addFace(fpoints[0], fpoints[1], fpoints[2]);
            } else {
                
                List<Polygon> polygons = splitter.findPolygons(bf);
                for (Polygon poly : polygons) {
                    GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);

                    Point3f[] outer = poly.getOuterContour();
                    List<Point3f[]> inner = poly.getInnerContours();
                    gi.setContourCounts(new int[] { 1 + inner.size() });

                    int stripCounts[] = new int[1 + inner.size()];
                    stripCounts[0] = outer.length;
                    for (int i = 0; i < inner.size(); i++) {
                        stripCounts[i + 1] = inner.get(i).length;
                    }
                    gi.setStripCounts(stripCounts);
                    gi.setCoordinates(outer);
                    for (Point3f[] innerPoints : inner) {
                        gi.setCoordinates(innerPoints);
                    }

                    NormalGenerator ng = new NormalGenerator();
                    ng.generateNormals(gi);

                    GeometryArray ga = gi.getGeometryArray();

                    int n = ga.getVertexCount();
                    int s = ga.getInitialCoordIndex();

                    for (int i = s + 2; i < s + n; i += 3) {
                        Point3f p1 = new Point3f();
                        Point3f p2 = new Point3f();
                        Point3f p3 = new Point3f();
                        ga.getCoordinate(i - 2, p1);
                        ga.getCoordinate(i - 1, p2);
                        ga.getCoordinate(i, p3);

                        LocatedFace<Point3f> face = (LocatedFace<Point3f>) mb.addFace(p1, p2, p3);
                        face.location = poly.getLocation();
                    }
                }
            }
        }

        Mesh<Point3f> newMesh = mb.getMesh();

        return newMesh;
    }

    public void combineSurface(TriangleArray lSurface, TriangleArray rSurface) {
        combineSurface(intoMesh(lSurface), intoMesh(rSurface));
    }

    /**
     * Combines the two given surfaces. To obtained a result use getSurface after calling this method.
     * 
     * @param lSurface
     * @param rSurface
     */
    public void combineSurface(Mesh<Point3f> lSurface, Mesh<Point3f> rSurface) {
        calculateNewSurface(lSurface, rSurface);
    }

    /**
     * Creates a resulting surface according to the given operator. 
     * combineSurface has to be called once before this method is used. After
     * combineSurface is called multiple calls to this method can be used with
     * different operators and the boolean operation does not needed to e done
     * again.
     * 
     * @param operator Boolean operator that indicates how to combine the surfaces
     * @return the resulting GeometryArray surface
     */
    public GeometryArray getSurface(int operator) {
        List<Face<Point3f>> regular = new LinkedList<Face<Point3f>>();
        List<Face<Point3f>> inverted = new LinkedList<Face<Point3f>>();
        switch (operator) {
        case UNION:
            addFaces(regular, LocatedFace.OUTSIDE, newLeft);
            addFaces(regular, LocatedFace.OUTSIDE, newRight);
            break;
        case INTERSECTION:
            addFaces(regular, LocatedFace.INSIDE, newLeft);
            addFaces(regular, LocatedFace.INSIDE, newRight);
            break;
        case DIFFERENCE:
            addFaces(regular, LocatedFace.OUTSIDE, newLeft);
            addFaces(inverted, LocatedFace.INSIDE, newRight);
            break;
        case DIFFERENCELEFT:
            addFaces(regular, LocatedFace.OUTSIDE, newLeft);
            break;
        case DIFFERENCERIGHT:
            addFaces(inverted, LocatedFace.INSIDE, newRight);
            break;
        case INTERSECTIONLEFT:
            addFaces(regular, LocatedFace.INSIDE, newLeft);
            break;
        case INTERSECTIONRIGHT:
            addFaces(regular, LocatedFace.INSIDE, newRight);
            break;
        case UNIONLEFT:
            addFaces(regular, LocatedFace.OUTSIDE, newLeft);
            break;
        case UNIONRIGHT:
            addFaces(regular, LocatedFace.OUTSIDE, newRight);
            break;
        case ALLLEFT:
            addFaces(regular, LocatedFace.OUTSIDE, newLeft);
            addFaces(regular, LocatedFace.INSIDE, newLeft);
            break;
        case ALLRIGHT:
            addFaces(regular, LocatedFace.OUTSIDE, newRight);
            addFaces(regular, LocatedFace.INSIDE, newRight);
            break;
        
        }

        //System.out.println("Regular size " + regular.size());
        //System.out.println("Inverted size " + inverted.size());
        Point3f points[] = new Point3f[(regular.size() + inverted.size()) * 3];
        int index = 0;
        Point3f fpoints[] = new Point3f[3];
        for (Face<Point3f> f : regular) {
            BoolopMath.setPoints(f, fpoints);
            points[index++] = fpoints[0];
            points[index++] = fpoints[1];
            points[index++] = fpoints[2];
        }

        for (Face<Point3f> f : inverted) {
            BoolopMath.setPoints(f, fpoints);
            points[index++] = fpoints[1];
            points[index++] = fpoints[0];
            points[index++] = fpoints[2];
        }

        
        GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
        gi.setCoordinates(points);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(gi);
        return gi.getIndexedGeometryArray();

    }

    //Helper method for getSUrface
    private void addFaces(List<Face<Point3f>> faces, int filter, Mesh<Point3f> mesh) {
        for (Face<Point3f> f : mesh.getFaces()) {
            LocatedFace<Point3f> lf = (LocatedFace<Point3f>) f;
            if (lf.location == filter) { //(lf.location & filter) == 0){
                faces.add(lf);
            }
        }
    }

    //TODO: debug only
    public List<List<IntersectInfo>> getCutLine() {
        return cuts;
    }

    //Compute Intersection between face f and edges edge except for the edge exclude (can be null)
    //pip is a point if the intersection point is the same as pip null is returned
    private IntersectInfo intersect(Face<Point3f> f, Edge<Point3f> edge[], Edge<Point3f> exclude, Point3f pip) {
        IntersectInfo tmp = null;
        for (int i = 0; i < 3; i++) {
            if (edge[i] != exclude) {
                Point3f ip = BoolopMath.intersect(f, edge[i]);
                if (ip != null) {
                    tmp = new IntersectInfo((BoolopFace<Point3f>) f, edge[i], BoolopMath.startLocation(), ip);
                    if (!pip.epsilonEquals(ip, TOLERANCE)) {
                        return tmp;
                    }
                    else{
                        return null;
                    }

                }
            }
        }
        return tmp;
    }

    
    //Helper class that contains information about an Intersection between a face and am edge
    private class IntersectInfo {
        // Intersection point
        public Point3f ip;

        // Face that contains intersection point
        public BoolopFace<Point3f> face;
        //Edge that caused interesction
        public Edge<Point3f> edge;
        public int startLocation = LocatedFace.UNKNOWN;
        public boolean ccw = false;

        public IntersectInfo(BoolopFace<Point3f> face, Edge<Point3f> edge, int startLocation, Point3f ip) {
            this.edge = edge;
            this.face = face;
            this.ip = ip;
            this.startLocation = startLocation;
        }
    }

}
