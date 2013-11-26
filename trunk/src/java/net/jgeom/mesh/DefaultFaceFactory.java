/*
 * Project: jgeom
 * Created: Jul 11, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh;

public class DefaultFaceFactory<T> implements FaceFactory<T> {

    public Face<T> createFace() {
        return new Face<T>();
    }

}
