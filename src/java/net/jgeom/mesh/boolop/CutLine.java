/*
 * Project: jgeom
 * Created: Jul 12, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import java.util.LinkedList;

import net.jgeom.mesh.Edge;

public class CutLine<T> {
    
    public LinkedList<T> points;
    //public Set<Face<T>> faces = new HashSet<Face<T>>();
    public int startEdgeStartLocation = LocatedFace.UNKNOWN;
    public int endEdgeStartLocation = LocatedFace.UNKNOWN;
    public Edge<T> start;
    public Edge<T> end;
    
    public boolean used = false;
    
    public CutLine(LinkedList<T> points){
        this.points = points;
    }
    
    public CutLine(){
        points = new LinkedList<T>();
    }
    
}
