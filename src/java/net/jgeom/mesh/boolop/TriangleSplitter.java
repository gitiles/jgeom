/*
 * Project: jgeom
 * Created: Jul 21, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;

import net.jgeom.mesh.Edge;

public class TriangleSplitter {
    private Map<Point3f, Node> vertices = new HashMap<Point3f, Node>();

    private boolean addVertex(Point3f p, int location) {
        if (p == null || vertices.containsKey(p)) {
            return false;
        } else {
            vertices.put(p, new Node(p, location));
            return true;
        }
    }

    private void removeVertex(Point3f p) {
        Node n = vertices.remove(p);
        for (Node m : n.adj.keySet()) {
            removeEdge(n, m);
        }
    }

    private void addEdge(Point3f v, Point3f w, LinkedList<Point3f> trail) {
        Node nv = vertices.get(v);
        Node nw = vertices.get(w);
        if (nv != null && nw != null) {
            nw.addEdgeTo(nv, trail);
            nv.addEdgeTo(nw, trail);
        }
    }

    private void removeEdge(Node v, Node w) {
        if (v == null || w == null) {
            System.out.println("invalid edge removal");
        }
        w.removeEdgeTo(v);
        v.removeEdgeTo(w);
    }

    private List<Point3f> findPolygon(Node from, Node to) {
        resetVertices();
        if (!bfs(from, to)) {
            return null;
        }

        LinkedList<Point3f> poly = new LinkedList<Point3f>();

        Node current = to;
        while (current.path != null) {
            LinkedList<Point3f> trail = current.getTrailTo(current.path);
            if (trail == null) {
                poly.addFirst(current.point);
            } else if (trail.getFirst().equals(current.point)) {
                for (int i = 0; i < trail.size() - 1; i++) {
                    poly.addFirst(trail.get(i));
                }
            } else {
                for (int i = trail.size() - 1; i > 0; i--) {
                    poly.addFirst(trail.get(i));
                }
            }
            current = current.path;
        }
        poly.addFirst(current.point);
        return poly;
    }

    private boolean bfs(Node from, Node to) {
        from.visited = true;
        LinkedList<Node> fifo = new LinkedList<Node>();
        fifo.addFirst(from);

        while (!fifo.isEmpty()) {
            Node n1 = fifo.removeLast();
            for (Node adj : n1.adj.keySet()) {
                if (!adj.visited) {
                    adj.visited = true;
                    fifo.addFirst(adj);
                    adj.path = n1;
                    if (adj == to) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private void resetVertices() {
        for (Node n : vertices.values()) {
            n.visited = false;
            n.path = null;
        }
    }

    public List<Polygon> findPolygons(BoolopFace<Point3f> face) {
        vertices.clear();

        // First sort cutlines by edges
        LinkedList<CutEnd> cutends[] = new LinkedList[4];
        cutends[0] = new LinkedList<CutEnd>();
        cutends[1] = new LinkedList<CutEnd>();
        cutends[2] = new LinkedList<CutEnd>();
        cutends[3] = new LinkedList<CutEnd>();

        for (CutLine<Point3f> cut : face.cutlines) {

            // cut inside triangle
            if (cut.start == null && cut.end == null) {
                cutends[3].add(new CutEnd(null, cut, null, LocatedFace.UNKNOWN));
                continue;
            }
            // not a complete cut
            if (cut.start == null || cut.end == null) {
                continue;
            }

            for (int i = 0; i < 3; i++) {
                if (cut.start == face.edge[i]) {
                    Point3f tmp = null;
                    if (face.edge[i].ccw == face) {
                        tmp = face.edge[i].start;
                    } else {
                        tmp = face.edge[i].end;
                    }
                    cutends[i].add(new CutEnd(cut.points.getFirst(), cut, tmp, cut.startEdgeStartLocation));
                    break;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (cut.end == face.edge[i]) {
                    Point3f tmp = null;
                    if (face.edge[i].ccw == face) {
                        tmp = face.edge[i].start;
                    } else {
                        tmp = face.edge[i].end;
                    }
                    cutends[i].add(new CutEnd(cut.points.getLast(), cut, tmp, cut.endEdgeStartLocation));
                    break;
                }
            }
        }

        // Build Graph

        // add triangle edge vertices with location
        int singleLocation = LocatedFace.UNKNOWN;
        for (int i = 0; i < 3; i++) {

            if (cutends[i].isEmpty()) {
                continue;
            }
            Collections.sort(cutends[i]);

            CutEnd first = cutends[i].getFirst();
            CutEnd last = cutends[i].getLast();
            if (last.cutline.start == last.cutline.end) {       
                if(face.edge[i].ccw == face){
                    singleLocation = first.startLocation;
                }
                else{
                    singleLocation = last.startLocation;
                }
                addVertex(face.edge[i].start, singleLocation);
                addVertex(face.edge[i].end, singleLocation);
                

            } else {
                if(face.edge[i].ccw == face){
                    addVertex(face.edge[i].start, first.startLocation);
                    addVertex(face.edge[i].end, invertLocation(last.startLocation));
                }
                else{
                    addVertex(face.edge[i].start, last.startLocation);
                    addVertex(face.edge[i].end, invertLocation(first.startLocation));
                }
            }

        }

        if (vertices.size() != 3) {
            System.out.println("vertices: " + vertices.size());
            for (Edge<Point3f> e : face.edge) {
                addVertex(e.start, singleLocation);
                addVertex(e.end, singleLocation);
            }
        }

        // add all other vertices
        for (CutLine<Point3f> cl : face.cutlines) {
            addVertex(cl.points.getFirst(), LocatedFace.UNKNOWN);
            addVertex(cl.points.getLast(), LocatedFace.UNKNOWN);
        }

        // add edges
        for (int i = 0; i < 3; i++) {

            Point3f previous = null;
            if (face.edge[i].ccw == face) {
                previous = face.edge[i].start;
            } else {
                previous = face.edge[i].end;
            }

            for (int j = 0; j < cutends[i].size(); j++) {
                CutEnd ce = cutends[i].get(j);

                addEdge(previous, ce.ip, null);
                addEdge(ce.cutline.points.getFirst(), ce.cutline.points.getLast(), ce.cutline.points);

                if (ce.cutline.start == ce.cutline.end) {
                    Point3f stop = null;
                    if (ce.cutline.points.getFirst().equals(ce.ip)) {
                        stop = ce.cutline.points.getLast();
                        previous = ce.cutline.points.getFirst();
                    } else {
                        stop = ce.cutline.points.getFirst();
                        previous = ce.cutline.points.getLast();
                    }
                    j++;
                    for (; j < cutends[i].size(); j++) {
                        ce = cutends[i].get(j);
                        if (ce.ip.equals(stop)) {
                            previous = cutends[i].get(j).ip;
                            break;
                        }
                    }
                } else {
                    previous = ce.ip;
                }
            }
            if (face.edge[i].ccw == face) {
                addEdge(previous, face.edge[i].end, null);
            } else {
                addEdge(previous, face.edge[i].start, null);
            }
        }

        //TODO debug plotgraph("graphStart.txt");
        // split triangle
        List<Polygon> polys = new LinkedList<Polygon>();

        for (int i = 0; i < 3; i++) {

            Point3f previous = null;
            if (face.edge[i].ccw == face) {
                previous = face.edge[i].start;
            } else {
                previous = face.edge[i].end;
            }

            Node begin = vertices.get(previous);

            int location = begin.location;
            for (int j = 0; j < cutends[i].size(); j++) {
                CutEnd ce = cutends[i].get(j);

                Node end = vertices.get(previous);
                Node start = vertices.get(ce.ip);
                removeEdge(start, end);
                List<Point3f> polyline = findPolygon(start, end);
                if(polyline == null){
                    break;
                }
                polys.add(new Polygon(polyline, location));
                removeVertex(previous);
                
                
                if (ce.cutline.start == ce.cutline.end) {
                    int startIndex = j;
                    Point3f stop = null;
                   //TODO debug plotgraph("graph.txt");
                    if (ce.cutline.points.getFirst().equals(ce.ip)) {
                        stop = ce.cutline.points.getLast();
                        previous = ce.cutline.points.getFirst();
                    } else {
                        stop = ce.cutline.points.getFirst();
                        previous = ce.cutline.points.getLast();
                    }
                    j++;
                    for (; j < cutends[i].size(); j++) {
                        ce = cutends[i].get(j);
                        if (ce.ip.equals(stop)) {
                            buildEdgePolygons(startIndex, j, cutends[i], location, polys);
                            if (j < cutends[i].size() - 1) {
                                j++;
                                previous = cutends[i].get(j).ip;
                                break;
                            }
                        }
                    }
                } else {
                    previous = ce.ip;
                    location = invertLocation(location);
                }

            }

        }

        return polys;
    }

    private void buildEdgePolygons(int start, int end, LinkedList<CutEnd> cuts, int location, Collection<Polygon> polys) {
        int ncuts = (end - start + 1) / 2;
        System.out.println("Edge cuts: " + ncuts);
        for (int i = 1; i < ncuts; i++) {
            location = invertLocation(location);
            CutEnd outerEnd = cuts.get(start + i - 1);
            CutEnd innerEnd = cuts.get(start + i);

            LinkedList<Point3f> outer = outerEnd.cutline.points;
            LinkedList<Point3f> inner = innerEnd.cutline.points;

            if (outerEnd.ip == outer.getFirst()) {
                Collections.reverse(outer);
            }

            if (innerEnd.ip == inner.getLast()) {
                Collections.reverse(inner);
            }
            for (Point3f p : inner) {
                outer.addLast(p);
            }
            polys.add(new Polygon(outer, location));
        }

        // Add last one
        location = invertLocation(location);
        CutEnd innermost = cuts.get(start + ncuts - 1);
        LinkedList<Point3f> points = innermost.cutline.points;
        if (innermost.ip == points.getFirst()) {
            Collections.reverse(points);
        }
        polys.add(new Polygon(points, location));

    }

    private int invertLocation(int location) {
        if (location == LocatedFace.INSIDE) {
            return LocatedFace.OUTSIDE;
        } else {
            return LocatedFace.INSIDE;
        }
    }

    private void plotgraph(String fname) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fname));
            pw.print("polygonplot3d([");

            for (Node n1 : vertices.values()) {
                for (Node n2 : n1.adj.keySet()) {
                    pw.print("[[" + n1.point.x + "," + n1.point.y + "," + n1.point.z + "],");
                    pw.print("[" + n2.point.x + "," + n2.point.y + "," + n2.point.z + "]],");
                }
            }

            pw.print("]);");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Node {
        public Point3f point;
        public Node path;
        private Map<Node, LinkedList<Point3f>> adj = new HashMap<Node, LinkedList<Point3f>>();
        public int location;
        public boolean visited = false;

        public Node(Point3f p, int location) {
            this.point = p;
            this.location = location;
        }

        public void addEdgeTo(Node to, LinkedList<Point3f> trail) {
            adj.put(to, trail);
        }

        public void removeEdgeTo(Node to) {
            adj.remove(to);
        }

        public LinkedList<Point3f> getTrailTo(Node to) {
            return adj.get(to);
        }

        public Collection<Node> getAdjacent() {
            return adj.keySet();
        }
    }

    private class CutEnd implements Comparable {
        public Point3f ip;
        public CutLine<Point3f> cutline;
        public Point3f compare;
        public int startLocation = LocatedFace.UNKNOWN;

        public CutEnd(Point3f ip, CutLine<Point3f> polygon, Point3f compare, int startLocation) {
            this.ip = ip;
            this.cutline = polygon;
            this.compare = compare;
            this.startLocation = startLocation;
        }

        public int compareTo(Object o) {
            CutEnd ce = (CutEnd) o;
            float d1 = ce.ip.distanceSquared(compare);
            float d2 = ip.distanceSquared(compare);
            float d = d1 - d2;
            if (d < 0)
                return 1;
            if (d > 0)
                return -1;
            return 0;
        }

    }

}
