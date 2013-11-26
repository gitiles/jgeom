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
 * Edge of a Polygon. Has connections to the faces building this edges, and its start and end points.
 * An edge is equal to another if the edges the two points are equal with no respect to start or end.
 * 
 * @author sg
 * @version 1.0
 */
public class Edge<T> {
  public static float TOLERANCE = 10E-6f;
  /**
   * Start point of the edge.
   */
  public T start = null;

  /**
   * End point of the edge.
   */
  public T end = null;

  /**
   * Face connection in clockwise order to this edge.
   */
  public Face<T> cw = null;
  
  /**
   * Face connection in counterclockwise order to this edge.
   */
  public Face<T> ccw = null;

  public boolean equals(Object o) {
    if (o instanceof Edge) {
      Edge e = (Edge) o;
      return e.start.equals(start) && e.end.equals(end) || e.start.equals(end) && e.end.equals(start);
    }
    return false;
  }

  public int hashCode() {
    return end.hashCode() & start.hashCode();
  }
}
