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

import net.jgeom.j3d.evaluators.BasicNurbsCurveEvaluator;
import net.jgeom.j3d.evaluators.NurbsCurveEvaluator;
import net.jgeom.nurbs.NurbsCurve;


/**
 * A nurbs-curve shape.<br>
 * If nothing specified the evaluator defaults to a BasicNurbsCurveEvaluator and the number of segments is 20.
 * @author sg
 */
public  class NurbsCurveShape extends Shape3D{
    private static final  int DEFAULT_SEG=20;
    
    private NurbsCurve nurbs;
    private NurbsCurveEvaluator evaluator;
    private int segU;
    
    /**
     * Creates a new nurbs-curve withe the given nurbs-curve.
     * @param nurbs NurbsCurve
     */
    public NurbsCurveShape(NurbsCurve nurbs){
        this(nurbs, new BasicNurbsCurveEvaluator());
    }
    
    /**
     * Creates a new nurbs-curve withe the given nurbs-curve and  number of segments
     * @param nurbs NurbsCurve
     * @param segU Number of curve segments
     */
    public NurbsCurveShape(NurbsCurve nurbs, int segU){
        this(nurbs, new BasicNurbsCurveEvaluator(), segU);
       
    }
    
    /**
     * Creates a new nurbs-curve withe the given nurbs-curve and evaluator
     * @param nurbs NurbsCurve
     * @param evaluator Evaluator to use
     */
    public NurbsCurveShape(NurbsCurve nurbs, BasicNurbsCurveEvaluator evaluator){
        this(nurbs, evaluator, DEFAULT_SEG);
    }
    
    /**
     * Creates a new nurbs-curve withe the given nurbs-curve, evaluator and number of segments 
     * @param nurbs NurbsCurve
     * @param evaluator Evaluator to use
     * @param segU Number of curve segments
     */
    public NurbsCurveShape(NurbsCurve nurbs, BasicNurbsCurveEvaluator evaluator, int segU){
        this.nurbs=nurbs;
        this.evaluator=evaluator;
        this.segU=segU;
        setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        recalculate();
    }
    
    /**
     * @return the NurbsCurve used
     */
    public NurbsCurve getNurbs(){
        return nurbs;
    }
    
    /**
     * Recalculates the geometry of this shape
     */
    public void recalculate(){
        setGeometry(evaluator.evaluateCurve(nurbs, segU));
    }

    /**
     * @return Returns the number of segment used when caclulating this nurbssurface.
     */
    public int getSegU() {
        return segU;
    }
    
    /**
     * @param segU Sets the no of segments used and perform a recalculation of the surface.
     */
    public void setSegU(int segU) {
        this.segU = segU;
        recalculate();
    }
}
