/*
 * Project: jgeom
 * Created: Jul 11, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh.boolop;

import net.jgeom.mesh.Face;
import net.jgeom.mesh.FaceFactory;

public class BoolopFaceFactory<T> implements FaceFactory<T> {

    public Face<T> createFace() {
        return new BoolopFace<T>();
    }

}
