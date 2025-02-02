/*
 * jgeom: Geometry Library for Java
 * 
 * Copyright (C) 2005  Samuel Gerber
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.jgeom.nurbs.util;

 
/**
 * Quad-Edge data structure
 *
 * @author Code by X.Philippeau - Structure by Guibas and Stolfi
 *
 * @see "Primitives for the Manipulation of General Subdivisions
 *      and the Computation of Voronoi Diagrams (Leonidas Guibas,Jorge Stolfi)"
 */
public class QuadEdge {
 
	// pointer to the next (direct order) QuadEdge
	private QuadEdge onext;
 
	// pointer to the dual QuadEdge (faces graph <-> edges graph)
	private QuadEdge rot;
 
	// origin point of the edge/face
	private Point orig;
 
	// marker for triangle generation
	public boolean mark=false;
 
	/**
	 * (private) constructor. Use makeEdge() to create a new QuadEdge
	 *
	 * @param onext pointer to the next QuadEdge on the ring
	 * @param rot   pointer to the next (direct order) crossing edge
	 * @param orig  Origin point
	 */
	private QuadEdge(QuadEdge Onext, QuadEdge rot, Point orig) {
		this.onext = Onext;
		this.rot = rot;
		this.orig = orig;
	}
 
	// ----------------------------------------------------------------
	//                             Getter/Setter
	// ----------------------------------------------------------------
 
	public QuadEdge onext() {
		return onext;
	}
 
	public QuadEdge rot() {
		return rot;
	}
 
	public Point orig() {
		return orig;
	}
 
	public void setOnext(QuadEdge next) {
		onext = next;
	}
 
	public void setRot(QuadEdge rot) {
		this.rot = rot;
	}
 
	public void setOrig(Point p) {
		this.orig = p;
	}
 
	// ----------------------------------------------------------------
	//                      QuadEdge Navigation
	// ----------------------------------------------------------------
 
	/**
	 * @return the symetric (reverse) QuadEdge
	 */
	public QuadEdge sym() {
		return rot.rot();
	}
 
	/**
	 * @return the other extremity point
	 */
	public Point dest() {
		return sym().orig();
	}
 
	/**
	 * @return the symetric dual QuadEdge
	 */
	public QuadEdge rotSym() {
		return rot.sym();
	}
 
	/**
	 * @return the previous QuadEdge (pointing to this.orig)
	 */
	public QuadEdge oprev() {
		return rot.onext().rot();
	}
 
	/**
	 * @return the previous QuadEdge starting from dest()
	 */
	public QuadEdge dprev() {
		return rotSym().onext().rotSym();
	}
 
	/**
	 * @return the next QuadEdge on left Face
	 */
	public QuadEdge lnext() {
		return rotSym().onext().rot();
	}
 
	/**
	 * @return the previous QuadEdge on left Face
	 */
	public QuadEdge lprev() {
		return onext().sym();
	}
 
 
	// ************************** STATIC ******************************
 
 
	/**
	 * Create a new edge (i.e. a segment)
	 *
	 * @param  orig origin of the segment
	 * @param  dest end of the segment
	 * @return the QuadEdge of the origin point
	 */
	public static QuadEdge makeEdge(Point orig, Point dest) {
		QuadEdge q0 = new QuadEdge(null, null, orig);
		QuadEdge q1 = new QuadEdge(null, null, null);
		QuadEdge q2 = new QuadEdge(null, null, dest);
		QuadEdge q3 = new QuadEdge(null, null, null);
 
		// create the segment
		q0.onext = q0; q2.onext = q2; // lonely segment: no "next" quadedge
		q1.onext = q3; q3.onext = q1; // in the dual: 2 communicating facets
 
		// dual switch
		q0.rot = q1; q1.rot = q2;
		q2.rot = q3; q3.rot = q0;
 
		return q0;
	}
 
	/**
	 * attach/detach the two edges = combine/split the two rings in the dual space
	 *
	 * @param a first  of the 2 QuadEdges to attach/detach
	 * @param b second of the 2 QuadEdges to attach/detach
	 */
	public static void splice(QuadEdge a, QuadEdge b) {
		QuadEdge alpha = a.onext().rot();
		QuadEdge beta  = b.onext().rot();
 
		QuadEdge t1 = b.onext();
		QuadEdge t2 = a.onext();
		QuadEdge t3 = beta.onext();
		QuadEdge t4 = alpha.onext();
 
		a.setOnext(t1);
		b.setOnext(t2);
		alpha.setOnext(t3);
		beta.setOnext(t4);
	}
 
	/**
	 * Create a new QuadEdge by connecting 2 QuadEdges
     *
	 * @param e1 first  of the 2 QuadEdges to connect
	 * @param e2 second of the 2 QuadEdges to connect
	 * @return the new QuadEdge
	 */
	public static QuadEdge connect(QuadEdge e1, QuadEdge e2) {
		QuadEdge q = makeEdge(e1.dest(), e2.orig());
		splice(q, e1.lnext());
		splice(q.sym(), e2);
		return q;
	}
 
	public static void swapEdge(QuadEdge e) {
		QuadEdge a = e.oprev();
		QuadEdge b = e.sym().oprev();
		splice(e, a);
		splice(e.sym(), b);
		splice(e, a.lnext());
		splice(e.sym(), b.lnext());
		e.orig = a.dest();
		e.sym().orig = b.dest();
	}
 
	/**
	 * Delete a QuadEdge
	 *
	 * @param q the QuadEdge to delete
	 */
	public static void deleteEdge(QuadEdge q) {
		splice(q, q.oprev());
		splice(q.sym(), q.sym().oprev());
	}
 
	// ----------------------------------------------------------------
	//                      Geometric computation
	// ----------------------------------------------------------------
 
	/**
	 * Test if the Point p is on the edge
	 *
	 * @param e QuadEdge
	 * @param p Point to test
	 * @return true/false
	 */
	public static boolean isOnEdge(QuadEdge e, Point p) {
		double d = distanceToSegment(e.orig(),e.dest(),p);
		if (d>0.00001) return false;
		return true;
	}
 
	/**
	 * Return the distance from a segment to a point
	 *
	 * @param ps,pe the start/end points of the segment
	 * @param p the point
	 * @return distance value
	 */
	private static double distanceToSegment(Point ps, Point pe, Point p) {
		if (ps.x==pe.x && ps.y==pe.y) return squaredistance(ps,p);
 
		int sx=pe.x-ps.x;
		int sy=pe.y-ps.y;
 
		int ux=p.x-ps.x;
		int uy=p.y-ps.y;
 
		int dp=sx*ux+sy*uy;
		if (dp<0) return squaredistance(ps,p);
 
		int sn = sx*sx+sy*sy;
		if (dp>sn) return squaredistance(pe,p);
 
		double ratio = (double)dp/sn;
		int projx = (int)(ps.x + ratio*sx);
		int projy = (int)(ps.y + ratio*sy);
 
		return squaredistance(new Point(projx,projy),p);
	}
 
	/**
	 * return the square of the distance between two points
	 *
	 * @param p1,p2 the two points
	 * @return dist the square of the distance
	 */
	private static double squaredistance(Point p1, Point p2) {
		int d2 = (p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y);
		return d2;
	}
 
	/**
	 * Test if the Point p is at the right of the QuadEdge q.
	 *
	 * @param q QuadEdge reference
	 * @param p Point to test
	 * @return true/false
	 */
	public static boolean isAtRightOf(QuadEdge q, Point p) {
		return isCounterClockwise(p, q.dest(), q.orig());
	}
 
	/** return true if a, b and c turn in Counter Clockwise direction
	 *
	 * @param a first  of the 3 points to test
	 * @param b second of the 3 points to test
	 * @param c third  of the 3 points to test
	 * @return true if a, b and c turn in Counter Clockwise direction
	 */
	public static boolean isCounterClockwise(Point a, Point b, Point c) {
		// test the sign of the determinant of ab x cb
		if ( (a.x - b.x)*(b.y - c.y) > (a.y - b.y)*(b.x - c.x) ) return true;
		return false;
	}
 
	/**
	 * The Delaunay criteria:
	 *
	 *   test if the point d is inside the circumscribed circle of triangle a,b,c
	 *
	 * @param a triangle Point
	 * @param b triangle Point
	 * @param c triangle Point
	 * @param d point to test
	 * @return  true/false
	 */
	public static boolean inCircle(Point a, Point b, Point c, Point d) {
		/*
		 if "d" is strictly INSIDE the circle, then
 
		     |d² dx dy 1|
             |a² ax ay 1|
		 det |b² bx by 1| < 0
		     |c² cx cy 1|
 
		*/
		long a2 = a.x*a.x + a.y*a.y;
		long b2 = b.x*b.x + b.y*b.y;
		long c2 = c.x*c.x + c.y*c.y;
		long d2 = d.x*d.x + d.y*d.y;
 
		long det44 = 0;
		det44 += d2  * det33( a.x,a.y, 1,  b.x,b.y, 1,  c.x,c.y, 1 );
		det44 -= d.x * det33( a2 ,a.y, 1,  b2 ,b.y, 1,  c2 ,c.y, 1 );
		det44 += d.y * det33( a2 ,a.x, 1,  b2 ,b.x, 1,  c2 ,c.x, 1 );
		det44 -= 1   * det33( a2,a.x,a.y,  b2,b.x,b.y,  c2,c.x,c.y );
 
		if (det44<0) return true;
		return false;
	}
 
	private static long det33( long... m ) {
		long det33=0;
		det33 += m[0] * (m[4]*m[8] - m[5]*m[7]);
		det33 -= m[1] * (m[3]*m[8] - m[5]*m[6]);
		det33 += m[2] * (m[3]*m[7] - m[4]*m[6]);
		return det33;
	}
}