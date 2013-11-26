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

import net.jgeom.j3d.evaluators.BasicNurbsSurfaceEvaluator;
import net.jgeom.j3d.evaluators.NurbsSurfaceEvaluator;
import net.jgeom.nurbs.NurbsCurve;
import net.jgeom.nurbs.util.NurbsCreator;
import net.jgeom.util.Axis3D;

/**
 * A revolved nurbs-surface shape.
 * @author sg
 */
public class RevolvedNurbsShape extends NurbsSurfaceShape{
    private NurbsCurve curve;
    private Axis3D axis;
    private double angle;
    
  
    /**
     * @param axis
     * @param curve
     * @param angle
     */
    public RevolvedNurbsShape(Axis3D axis, NurbsCurve curve, double angle) {
        this(axis, curve, angle, new BasicNurbsSurfaceEvaluator());
    }

    /**
     * @param axis
     * @param curve
     * @param angle
     * @param segU
     * @param segV
     */
    public RevolvedNurbsShape(Axis3D axis, NurbsCurve curve, double angle, int segU, int segV) {
        this(axis, curve, angle, new BasicNurbsSurfaceEvaluator(), segU, segV);
    }

    /**
     * @param axis
     * @param curve
     * @param angle
     * @param evaluator
     */
    public RevolvedNurbsShape(Axis3D axis, NurbsCurve curve, double angle, NurbsSurfaceEvaluator evaluator) {
        this(axis, curve, angle, evaluator, DEFAULT_SEGU, DEFAULT_SEGV);
    }
    
    /**
     * @param axis
     * @param curve
     * @param angle
     * @param evaluator
     * @param segU
     * @param segV
     */
    public RevolvedNurbsShape(Axis3D axis, NurbsCurve curve, double angle, NurbsSurfaceEvaluator evaluator, int segU, int segV) {
        super(NurbsCreator.createRevolvedSurface(axis, curve, angle), evaluator, segU, segV);
        this.curve=curve;
        this.angle=angle;
        this.axis=axis;
    }
    
    /**
     * @return Returns the angle.
     */
    public double getAngle() {
        return angle;
        
    }
    
    /**
     * @param angle sets the angle of the revolution and performs a recalculation.
     */
    public void setAngle(double angle) {
        this.angle = angle;
        nurbs=NurbsCreator.createRevolvedSurface(axis, curve, angle);
        recalculate();
    }
    
    /**
     * @return Returns the axis.
     */
    public Axis3D getAxis() {
        return axis;
    }
    
    /**
     * @param axis sets the axis of the revolution and performs a recalculation.
     */
    public void setAxis(Axis3D axis) {
        this.axis = axis;
        nurbs=NurbsCreator.createRevolvedSurface(axis, curve, angle);
        recalculate();
    }
    
    /**
     * @return Returns the curve.
     */
    public NurbsCurve getCurve() {
        return curve;
    }
    
    /**
     * @param curve sets the curve to revolve and performs a recalculation.
     */
    public void setCurve(NurbsCurve curve) {
        this.curve = curve;
        nurbs=NurbsCreator.createRevolvedSurface(axis, curve, angle);
        recalculate();
    }
}
