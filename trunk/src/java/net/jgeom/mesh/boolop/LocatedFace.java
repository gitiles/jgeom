/*
 * Project: jgeom
 * Created: Jul 12, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import net.jgeom.mesh.Face;

public class LocatedFace<T> extends Face<T> {
    public final static int UNKNOWN = 0, INSIDE = 1, OUTSIDE = 2;
    public int location = UNKNOWN;
    boolean visited = false;
}
