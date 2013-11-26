/*
 * Project: jgeom
 * Created: Jul 11, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh;

public class DefaultEdgeFactory<T> implements EdgeFactory<T> {

    public Edge<T> createEdge() {
        return new Edge<T>();
    }

}
