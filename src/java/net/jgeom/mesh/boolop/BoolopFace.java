/*
 * Project: jgeom
 * Created: Jul 11, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import java.util.List;

import net.jgeom.mesh.Face;

public class BoolopFace<T> extends Face<T> {
    public List< CutLine<T> > cutlines = null;
}
