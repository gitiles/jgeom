/*
 * Project: jgeom
 * Created: Jul 12, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import net.jgeom.mesh.Face;
import net.jgeom.mesh.FaceFactory;

public class LocatedFaceFactory<T> implements FaceFactory<T> {

    public Face<T> createFace() {
        return new LocatedFace<T>();
    }

}
