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
 * Face of the polygon. Has connections to all its edges.
 * @author sg
 * @version 1.0
 */
public class Face<T> {
	/**
	 * Edges describing this face.
	 */
	public Edge<T> edge[];
    
    
    public String toString(){
        String s = "";
        for(Edge<T> e:edge){
            if(e.ccw == this){
                s+= e.start.toString();
            }
            else{
                s+= e.end.toString();
            }
            s+= ", ";
        }
        return s;
    }
}
