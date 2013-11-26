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
package net.jgeom.j3d.objects;

import javax.media.j3d.Shape3D;

import net.jgeom.j3d.evaluators.BasicSDSEvaluator;
import net.jgeom.j3d.evaluators.SDSurfaceEvaluator;
import net.jgeom.sds.SubdividableSurface;
import net.jgeom.sds.SubdivisionSurface;


/**
 * A Subdivision-Shape
 * If nothing specified the evaluator defaults to a BasicSDSEvaluator and the number of subdivisions is 3.
 * @author sg
 */
public class SubdivisionShape extends Shape3D{
    protected static int DEFAULT_SUBS=3;
    private SubdividableSurface sds;
    private SDSurfaceEvaluator evaluator;
    private int nSubs;
    
    /**
     * @param sds
     */
    public SubdivisionShape(SubdivisionSurface sds){
        this(sds, new BasicSDSEvaluator());
    }
    
    /**
     * @param sds
     * @param nSubs
     */
    public SubdivisionShape(SubdivisionSurface sds, int nSubs){
        this(sds, new BasicSDSEvaluator(), nSubs);
    }
    

    
    /**
     * @param sds
     * @param evaluator
     */
    public SubdivisionShape(SubdivisionSurface sds, SDSurfaceEvaluator evaluator){
        this(sds, evaluator, DEFAULT_SUBS);
       
    }
    
    /**
     * @param sds
     * @param evaluator
     * @param nSubs
     */
    public SubdivisionShape(SubdivisionSurface sds, SDSurfaceEvaluator evaluator, int nSubs){
        this.sds=sds;
        this.evaluator=evaluator;
        this.nSubs=nSubs;
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        recalculate();
    }
    
    /**
     * Recalcultes the geometry of this shape
     */
    public void recalculate(){
        setGeometry(evaluator.evaluateSurface(sds, nSubs));
    }    
  
    /**
     * @return Returns the evaluator.
     */
    public SDSurfaceEvaluator getEvaluator() {
        return evaluator;
    }
    
    /**
     * @param evaluator The evaluator to set.
     */
    public void setEvaluator(SDSurfaceEvaluator evaluator) {
        this.evaluator = evaluator;
        recalculate();
    }
    
    /**
     * @return Returns the no of subdivision steps.
     */
    public int getNoSubs() {
        return nSubs;
    }
    
    /**$Set no of subdivsion steps and performs a recalculation.
     * @param subs The no of subdivision steps to calculate the geometry .
     */
    public void setNoSubs(int subs) {
        nSubs = subs;
        recalculate();
    }
    
    /**
     * @return Returns the SubdivisionSurface.
     */
    public SubdividableSurface getSds() {
        return sds;
    }
    
    /**
     * @param sds Sets the SubdividableSurface and performs a recalculation.
     */
    public void setSds(SubdividableSurface sds) {
        this.sds = sds;
        recalculate();
    }
}
