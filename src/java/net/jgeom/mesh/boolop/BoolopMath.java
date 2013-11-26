/*
 * Project: jgeom
 * Created: Jul 13, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import net.jgeom.mesh.Edge;
import net.jgeom.mesh.Face;

public final class BoolopMath {
    private BoolopMath(){}
    
    //Face 1 
    private static Point3f f1_p1 = new Point3f();
    private static Point3f f1_p2 = new Point3f();
    private static Point3f f1_p3 = new Point3f();
    private static Vector3f f1_v1 = new Vector3f();
    private static Vector3f f1_v2 = new Vector3f();
    private static Vector3f f1_v3 = new Vector3f();
    private static Vector3f f1_normal = new Vector3f();
    private static Vector3f f1_dir1 = new Vector3f();
    private static Vector3f f1_dir2 = new Vector3f();
    
    //Face 2
//    private static Point3f f2_p1 = new Point3f();
//    private static Point3f f2_p2 = new Point3f();
//    private static Point3f f2_p3 = new Point3f();
//    private static Vector3f f2_v1 = new Vector3f();
//    private static Vector3f f2_v2 = new Vector3f();
//    private static Vector3f f2_v3 = new Vector3f();
//    private static Vector3f f2_normal = new Vector3f();
    //private static Vector3f f2_dir1 = new Vector3f();
    //private static Vector3f f2_dir2 = new Vector3f();
    
    //Edge
    private static Vector3f edge_dir = new Vector3f();
    private static Point3f edge_dstart = new Point3f();
    private static Point3f edge_dend = new Point3f();
    
    
   public static void setPoints(Face<Point3f> face, Point3f points[]){
       Point3f tmp = null;
       for (int i = 0; i < 3; i++) {

           if (face == face.edge[i].cw)
               tmp = face.edge[i].start;
           else
               tmp = face.edge[i].end;

           points[i] = tmp;

       }
       
   }
    
    private static void setFace1(Face<Point3f> face){
        if(face.edge[0].ccw == face){
            f1_p1 = face.edge[0].start;
            f1_p2 = face.edge[0].end; 
        }
        else{
            f1_p1 = face.edge[0].end;
            f1_p2 = face.edge[0].start;  
        }
        Edge<Point3f> tmp = face.edge[1];
        if(tmp.start.equals(f1_p1) || tmp.start.equals(f1_p2)){
            f1_p3 = tmp.end;
        }
        else{
            f1_p3 = tmp.start;   
        }


        //Triangle "lines"
        f1_v1.set(f1_p2);
        f1_v1.sub(f1_p1);
        f1_v2.set(f1_p3); 
        f1_v2.sub(f1_p2);
        f1_v3.set(f1_p3);
        f1_v3.sub(f1_p1);
        
    }
    
//    private static void setFace2(Face<Point3f> face){
//        if(face.edge[0].ccw == face){
//            f2_p1 = face.edge[0].start;
//            f2_p2 = face.edge[0].end; 
//        }
//        else{
//            f2_p1 = face.edge[0].end;
//            f2_p2 = face.edge[0].start;  
//        }
//        Edge<Point3f> tmp = face.edge[1];
//        if(tmp.start.equals(f2_p1) || tmp.start.equals(f2_p2)){
//            f2_p3 = tmp.end;
//        }
//        else{
//            f2_p3 = tmp.start;   
//        }
//
//
//        //Triangle "lines"
//        f2_v1.set(f2_p2);
//        f2_v1.sub(f2_p1);
//        f2_v2.set(f2_p3);
//        f2_v2.sub(f2_p2);
//        f2_v3.set(f2_p3);
//        f2_v3.sub(f2_p1);
//        
//    }
    
    
    
    private static void setEdge(Edge<Point3f> edge){
        //Intersect line
        edge_dir.set(edge.end);
        edge_dir.sub(edge.start);
        edge_dstart.set(edge.start);
        edge_dend.set(edge.end);
    }
    
    
    private static float dot(Tuple3f t1, Tuple3f t2) {
        return t1.x * t2.x + t1.y * t2.y + t1.z * t2.z;
    }
    
//    public static double signedDistance(Face<Point3f> face1, Set<Face<Point3f>> faces, Vector3f normal1, Vector3f normal2){
//        setFace1(face1);
//        f1_normal.cross(f1_v1, f1_v2);
//        f1_normal.normalize();
//        normal1.set(f1_normal);
//       
//        
//        
//        
//        double distanceMin = Double.MAX_VALUE;
//        double distanceMax = 0;
//        double distance; 
//        
//        Point3f tmp = new Point3f();
//        for(Face<Point3f> f:faces){
//            distanceMax = 0;
//            
//            setFace2(f);
//            f2_normal.cross(f2_v1, f2_v2);
//            f2_normal.normalize();
//            
//            tmp.set(f1_p1);
//            tmp.sub(f2_p1);
//            distance = dot(f2_normal, tmp);
//            if(Math.abs(distance) > Math.abs(distanceMax)){
//                distanceMax = distance;
//            }
//            
//            tmp.set(f1_p2);
//            tmp.sub(f2_p1);
//            distance = dot(f2_normal, tmp);
//            if(Math.abs(distance) > Math.abs(distanceMax)){
//                distanceMax = distance;
//            }
//            
//            tmp.set(f1_p3);
//            tmp.sub(f2_p1);
//            distance = dot(f2_normal, tmp);
//            if(Math.abs(distance) > Math.abs(distanceMax)){
//                distanceMax = distance;
//            }
//            
//            if(Math.abs(distanceMax) < Math.abs(distanceMin)){
//                distanceMin = distanceMax;
//                normal2.set(f2_normal);
//            }   
//        }
//        return distanceMin;  
//    }
    

    
    private static int startLocation = LocatedFace.UNKNOWN;
    private static double TOLERANCE = 10E-10;
    public static Point3f intersect(Face<Point3f> face, Edge<Point3f> edge) {

        //Set Temporary variables
        setFace1(face);
        setEdge(edge);

        f1_normal.cross(f1_v1, f1_v2);
        f1_normal.normalize();

        edge_dstart.sub(f1_p1);
        edge_dend.sub(f1_p1);
        
        float distance1 = dot(f1_normal, edge_dstart);
        float distance2 = dot(f1_normal, edge_dend);
        
        float lambda = -2.0f;
        //check if line intersects
        if( distance1 <= TOLERANCE && distance2 >= -TOLERANCE ){
            startLocation = LocatedFace.INSIDE;
           lambda = -distance1 / (distance2 - distance1);
        }
        else if( distance1 >= -TOLERANCE && distance2 <= TOLERANCE ){
            startLocation = LocatedFace.OUTSIDE;
            lambda = distance1 / (distance1 - distance2);
        }
        
        if(lambda != -2.0f){
            Point3f ip = new Point3f(edge.start);
            edge_dir.scale(lambda);
            ip.add(edge_dir);

            if(face == face.edge[0].ccw){
                if (liesInside(ip, face)) {
                    return ip;
                }
            }else{
                if (liesInside(ip, face)) {
                    return ip;
                }
                
            }
            
        }

        return null;
    }
    
    public static int startLocation(){
        return startLocation;
    }
    
    
    /*old version
    public static Point3f intersect(Face<Point3f> face, Edge<Point3f> edge) {

        //Set Temporary variables
        setFace1(face);
        setEdge(edge);

        f1_normal.cross(f1_v1, f1_v2);
        f1_normal.normalize();

        

        float D = dot(f1_normal, f1_p1);
        // l.start + lambda *l.dir = point on plane
        float lambda = (D - dot(f1_normal, edge.start)) / edge_dir.dot(f1_normal);

        // Is point on plane is between start and endpoint of line?
        // start + lambda * length = Point on top of triangle
        // --> lambda > 1 end point of line before intersection point
        // --> lambda < 0 intersection before start point
        float intersectTol = 0; // TODO
        if (lambda >= 0 - intersectTol && lambda <= 1 + intersectTol) {
            Point3f ip = new Point3f(edge.start);
            edge_dir.scale(lambda);
            ip.add(edge_dir);

            if (!liesInside(ip)) {
                return null;
            }

            return ip;
        }

        return null;
    }*/

    
    private static boolean liesInside(Point3f ip, Face<Point3f> f){
        Point3f start = null;
        Point3f end = null;
        Vector3f normal = f1_normal;
        double det[] = new double[3];
        for(int i=0; i<3; i++){
            if(f == f.edge[i].ccw){
                start = f.edge[i].start;
                end = f.edge[i].end;
            }
            else{
                start = f.edge[i].end;
                end = f.edge[i].start;
                
            }
            double a11 = start.x - ip.x;
            double a12 = start.y - ip.y;
            double a13 = start.z - ip.z;
            double a21 = end.x - ip.x;
            double a22 = end.y - ip.y;
            double a23 = end.z - ip.z;
            double a31 = end.x + normal.x - ip.x;
            double a32 = end.y + normal.y - ip.y;
            double a33 = end.z + normal.z - ip.z;
            det[i] = a11*a22*a33 + a12*a23*a31 + a13*a21*a32 - a13*a22*a31 - a11*a23*a32 - a12*a21*a33;
        }
        if(det[0] < 0){
            for(int i=1; i < 3; i++){
                if(det[i] > 0){
                    return false;
                }
            }
        }
        else{
            for(int i=1; i < 3; i++){
                if(det[i] < 0){
                    return false;
                }
            }
        }
        return true;
    }
    
    /*
    private static boolean liesInside(Point3f ip) {
        f1_v1.normalize();
        f1_v2.normalize();
        f1_v3.normalize();
        float cosA = f1_v1.dot(f1_v3);
        f1_v1.negate();
        float cosB = f1_v2.dot(f1_v1);

        // Vector from p1 to p
        f1_dir1.set(ip);
        f1_dir1.sub(f1_p1);
        // Vector from p2 to p
        f1_dir2.set(ip);
        f1_dir2.sub(f1_p2);

        // Cosinus from lines[0] and lines[2] to dir1P
        double cosA1, cosA2;

        // Cosinus from lines[1] and lines[2] to dir2P
        double cosB1, cosB2;

        // Calculation of Cosinuses
        float l = f1_dir2.length();
        if (l == 0) {
            return true;
        } else {
            f1_dir2.scale(1 / l);
            cosB1 = f1_v1.dot(f1_dir2);
            cosB2 = f1_v2.dot(f1_dir2);
        }

        l = f1_dir1.length();
        if (l == 0) {
            return true;
        } else {
            f1_dir1.scale(1 / l);
            f1_v1.negate();
            cosA1 = f1_v1.dot(f1_dir1);
            cosA2 = f1_v3.dot(f1_dir1);
        }

        // TODO check for consistency
        float tolAngle = 0;
        // Check that all calculated cosinus are smaller than these of the
        // triangles
        return (cosA - tolAngle <= cosA1 && cosA - tolAngle <= cosA2 && cosB - tolAngle <= cosB1 && cosB - tolAngle <= cosB2);
        // TODO || isEdgepoint(p, tolAngle) != null;
    }*/
    
}
