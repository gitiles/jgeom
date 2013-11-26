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

import net.jgeom.j3d.evaluators.BasicNurbsSurfaceEvaluator;
import net.jgeom.j3d.evaluators.NurbsSurfaceEvaluator;
import net.jgeom.nurbs.NurbsSurface;


/**
 * A Nurbs-Surface Shape
 * If nothing specified the evaluator defaults to a BasicNurbsSurfaceEvaluator and the number of segments in u and v is 20.
 * @author sg
 */
public  class NurbsSurfaceShape extends Shape3D{
    protected static int DEFAULT_SEGU=20, DEFAULT_SEGV=20;
    protected NurbsSurface nurbs;
    private NurbsSurfaceEvaluator evaluator;
    private int segU, segV;
    
    /**
     * @param nurbs
     */
    public NurbsSurfaceShape(NurbsSurface nurbs){
        this(nurbs, new BasicNurbsSurfaceEvaluator());
    }
    
    /**
     * @param nurbs
     * @param segU
     * @param segV
     */
    public NurbsSurfaceShape(NurbsSurface nurbs, int segU, int segV){
        this(nurbs, new BasicNurbsSurfaceEvaluator(), segU, segV);
       
    }
    
    /**
     * @param nurbs
     * @param evaluator
     */
    public NurbsSurfaceShape(NurbsSurface nurbs, NurbsSurfaceEvaluator evaluator){
        this(nurbs, evaluator, DEFAULT_SEGU, DEFAULT_SEGV);
    }
    
    /**
     * @param nurbs
     * @param evaluator
     * @param segU
     * @param segV
     */
    public NurbsSurfaceShape(NurbsSurface nurbs, NurbsSurfaceEvaluator evaluator, int segU, int segV){
        this.nurbs=nurbs;
        this.evaluator=evaluator;
        this.segU=segU;
        this.segV=segV;
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        recalculate();
    }
    
    /**
     * @return
     */
    public NurbsSurface getNurbs(){
        return nurbs;
    }
    
    /**
     * Recalculates the geometry of this shape
     */
    public void recalculate(){
        setGeometry(evaluator.evaluateSurface(nurbs, segU, segV));
    }

    /**
     * @return Returns the no of segments used in u direction.
     */
    public int getSegU() {
        return segU;
    }
    
    /**
     * @param segU sets the segment in u direction and performs a recalculation.
     */
    public void setSegU(int segU) {
        this.segU = segU;
        recalculate();
    }
    
    /**
     * @return Returns the no of segments used invu direction.
     */
    public int getSegV() {
        return segV;
    }
    
    /**
     * @param segV sets the segment in v direction and performs a recalculation.
     */
    public void setSegV(int segV) {
        this.segV = segV;
        recalculate();
    }
    
    /**
     * @param segV sets the segment in u and v direction and performs a recalculation.
     */
    public void setSegments(int u, int v) {
        this.segU = u;
        this.segV = v;
        recalculate();
    }
    
    /**
     * @return Returns the evaluator.
     */
    public NurbsSurfaceEvaluator getEvaluator() {
        return evaluator;
    }
    
    /**
     * @param evaluator Sets the evaluator and performs a recalculation.
     */
    public void setEvaluator(NurbsSurfaceEvaluator evaluator) {
        this.evaluator = evaluator;
        recalculate();
    }
    
    /**
     * @param nurbs set the NurbsSurface and performs a recalculation of the geometry.
     */
    public void setNurbs(NurbsSurface nurbs) {
        this.nurbs = nurbs;
        recalculate();
    }
}
