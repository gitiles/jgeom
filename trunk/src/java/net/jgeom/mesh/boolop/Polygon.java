/*
 * Project: jgeom
 * Created: Jul 23, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;

public class Polygon {
    private Point3f outerContour[] = null;
    private List<Point3f[]> innerContours = new LinkedList<Point3f[]>();
    private int location;

    
    Polygon(List<Point3f> outer, int location) {
        outerContour = new Point3f[outer.size()];
        outer.toArray(outerContour);
        this.location = location;
        
    }
    
    Polygon(List<Point3f> outer, List<List<Point3f>> inner, int location) {
        outerContour = new Point3f[outer.size()];
        outer.toArray(outerContour);
        for (List<Point3f> in : inner) {
            addInner(in);
        }
        this.location = location;
    }

    public List<Point3f[]> getInnerContours() {
        return innerContours;
    }

    public Point3f[] getOuterContour() {
        return outerContour;
    }
    
    public void addInner(List<Point3f> inner){
        Point3f tmp[] = new Point3f[inner.size()];
        inner.toArray(tmp);
        innerContours.add(tmp);  
    }

    public int getLocation() {
        return location;
    }
    
    public void removeDuplicatePoints(float eps){
        LinkedList<Point3f> tmp = new LinkedList<Point3f>();
        Point3f previous = outerContour[outerContour.length-1];
        for(int i=0; i < outerContour.length; i++){
            Point3f current = outerContour[i];
            if( !current.epsilonEquals(previous, eps)){
                tmp.addLast(current);
            }
            else{
                System.out.println("duplicate point");
            }
            previous = current;
        }
        outerContour = new Point3f[tmp.size()];
        tmp.toArray(outerContour);
        
        List<Point3f[]> newInner = new LinkedList<Point3f[]>();
        for(Point3f pnts[]:innerContours){
            tmp.clear();
            previous = pnts[pnts.length-1];
            for(int i=0; i < pnts.length; i++){
                Point3f current = pnts[i];
                if( !current.epsilonEquals(previous, eps)){
                    tmp.addLast(current);
                }
                
                previous = current;
            }
            Point3f newPnts[] = new Point3f[tmp.size()];
            tmp.toArray(newPnts);
            newInner.add(newPnts);
        }
        
        innerContours = newInner;
    }
}

