/*
 * Project: jgeom
 * Created: Jun 2, 2007
 * Changed: -
 * Author: sg
 */
package net.jgeom.mesh;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;

public class SDSVertex extends Point3f{
    private static final long serialVersionUID = 1L;
    
    private int valence = 0;
    public int crease = 0;
    private List valenceEdges = new ArrayList(4);
    
    public SDSVertex(){};
    
    public SDSVertex(SDSVertex copy){
        valence = copy.valence;
        crease = copy.crease;
        valenceEdges.addAll(copy.valenceEdges);
    }
    
    /**
     * Return how many edges contain this MeshPoint.
     * 
     * @return valence
     */
    public int getValence() {
        return valence;
    }

    /**
     * Increment the valence of this point.
     */
    public void incrementValence() {
        valence++;
    }

    /**
     * Set the valence of the MeshPoin t to the given value.
     * 
     * @param v
     *            Valence to set this MeshPoint to.
     */
    public void setValence(int v) {
        valence = v;
    }

    /**
     * Decrement the valence of thie MeshPoint.
     */
    public void decrementValence() {
        valence--;
    }

    /**
     * Add a valence edge to this MeshPoint. The valence is automatically
     * incremented.
     * 
     * @param e
     *            Edge to add as valence Edge.
     */
    public void addValenceEdge(Edge e) {
        valenceEdges.add(e);
        valence++;
    }

    /**
     * Get all edges which contain this point.
     * 
     * @return Array of vlanece Edges
     */
    public Edge[] getValenceEdges() {
        Edge res[] = new Edge[valenceEdges.size()];
        valenceEdges.toArray(res);
        return res;
    }
}
