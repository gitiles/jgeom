/*
 * jgeom: Geometry Library for Java
 * 
 * Copyright (C) 2005  Samuel Gerber
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.jgeom.nurbs;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import net.jgeom.MeshedSurface;
import net.jgeom.nurbs.evaluators.BasicNurbsSurfaceEvaluator;
import net.jgeom.nurbs.evaluators.TrimEvaluator;
import net.jgeom.nurbs.evaluators.TrimSurfaceEvaluator;
import net.jgeom.nurbs.evaluators.TrimSurfaceEvaluatorDelaunay;
import net.jgeom.nurbs.evaluators.TrimSurfaceEvaluatorPointsCloud;
import net.jgeom.nurbs.geomContainers.GeometryArray;

/**
 * @author alex
 *
 */
public class MeshedNurbsSurface extends BasicNurbsSurface implements MeshedSurface {

	public enum TesselationType {
		DELAUNAY_TRIM, QUADS_TRIM, POINT_CLOUD_TRIM, NO_TRIM, NO_TRIANGULATION
	}

	private boolean isMeshed = false;

	private BasicNurbsSurfaceEvaluator evaluator = null;

	private int segU = 50;

	private int segV = 50;


	@SuppressWarnings("unchecked")
	public MeshedNurbsSurface(NurbsSurface surface,
			TesselationType tesselationType) {
		super(surface.getControlNet(), surface.getUKnots(),
				surface.getVKnots(), surface.getUDegree(), surface.getVDegree());
		this.innerTrimms = surface.getInnerTrimCurves();
		this.outerTrimms=surface.getOuterTrimCurves();
		this.setEvaluator(tesselationType);
	}

	public void setEvaluator(TesselationType tesselationType) {
		switch (tesselationType) {
		case NO_TRIM:
			evaluator = new BasicNurbsSurfaceEvaluator();
			break;
		case DELAUNAY_TRIM:
			evaluator = new TrimSurfaceEvaluatorDelaunay();
			break;
		case QUADS_TRIM:
			evaluator = new TrimSurfaceEvaluator();
			break;
		case POINT_CLOUD_TRIM:
			evaluator = new TrimSurfaceEvaluatorPointsCloud();
			break;
		case NO_TRIANGULATION:
			evaluator = null;
		}
	}

	public MeshedNurbsSurface(ControlNet cps, float[] uK, float[] vK, int p,
			int q) throws IllegalArgumentException {
		super(cps, uK, vK, p, q);
	}

	public MeshedNurbsSurface(ControlNet cps, float[] uK, float[] vK, int p,
			int q, TesselationType tesselationType)
			throws IllegalArgumentException {
		super(cps, uK, vK, p, q);
		this.setEvaluator(tesselationType);
	}

	public void setPrecision(int segU, int segV) {
		this.segU = segU;
		this.segV = segV;
	}

	public GeometryArray getMeshedSurface() {
		isMeshed = true;
		return evaluator.evaluateSurface(this, segU, segV);
	}

	
	/*
	 * (non-Javadoc)
	 * @see net.jgeom.nurbs.BasicNurbsSurface#addTrimCurve(net.jgeom.nurbs.TrimCurve)
	 * note : flag surface to be remeshed if a trimming curve has been added
	 */
	@Override
	public void addInnerTrimCurve(TrimCurve tc) {
		isMeshed=false;
		super.addInnerTrimCurve(tc);
	}
	
	@Override
	public void addOuterTrimCurve(TrimCurve tc) {
		isMeshed=false;
		super.addOuterTrimCurve(tc);
	}

	/*
	 * Point projection and inversion
	 * Get the 4 closest  points on the mesh to the given point (p).
	 * Compute bilinear interpolation in parameter space
	 * We assume that f^1(p)  is within the surface domain range
	 */
	public UVCoord2f getUVParameterInvertion(Point3f p) {
		if (!isMeshed)
			this.getMeshedSurface();

		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int index4 = 0;
		float distMin1 = Integer.MAX_VALUE;
		float distMin2 = Integer.MAX_VALUE;
		float distMin3 = Integer.MAX_VALUE;
		float distMin4 = Integer.MAX_VALUE;

		//get the 4 closest points
		for (int i = 0; i < evaluator.vertexList.size(); i++) {
			float distTmp = p.distance(evaluator.vertexList.get(i));
			if (distTmp < distMin1) {
				index1 = i;
				distMin1 = distTmp;
			}
			if (distTmp != distMin1) {
				if (distTmp < distMin2) {
					index2 = i;
					distMin2 = distTmp;
				}
				if (distTmp != distMin2) {
					if (distTmp < distMin3) {
						index3 = i;
						distMin3 = distTmp;
					}
					if (distTmp != distMin3 && distTmp < distMin4) {
						index4 = i;
						distMin4 = distTmp;
					}
				}
			}
			
		}
		UVCoord2f uv1 = evaluator.uvCoords.get(index1);
		UVCoord2f uv2 = evaluator.uvCoords.get(index2);
		UVCoord2f uv3 = evaluator.uvCoords.get(index3);
		UVCoord2f uv4 = evaluator.uvCoords.get(index4);
		
		//compute bilinear interpolation
		float sumDist=distMin1+distMin2+distMin3+distMin4;
		
		UVCoord2f uv=new UVCoord2f(0,0);
		uv.x+=(distMin1/sumDist)*uv1.x;
		uv.y+=(distMin1/sumDist)*uv1.y;
		uv.x+=(distMin2/sumDist)*uv2.x;
		uv.y+=(distMin2/sumDist)*uv2.y;
		uv.x+=(distMin3/sumDist)*uv3.x;
		uv.y+=(distMin3/sumDist)*uv3.y;
		uv.x+=(distMin4/sumDist)*uv4.x;
		uv.y+=(distMin4/sumDist)*uv2.y;
		
		System.out.println(uv1 +" "+uv2+" "+uv3+" "+uv3+" "+uv4+" ->"+uv);
		return uv;
	}

}
