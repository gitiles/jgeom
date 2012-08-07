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
package net.jgeom.nurbs.evaluators;

import java.util.*;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

//import org.j3d.geom.TriangulationUtils;


import net.jgeom.nurbs.evaluators.TrimSurfaceEvaluator.Coordinate;
import net.jgeom.nurbs.geomContainers.GeometryArray;
import net.jgeom.nurbs.geomContainers.IndexedTriangleArray;
import net.jgeom.nurbs.geomContainers.TriangleArray;
import net.jgeom.nurbs.util.IncrementalDelaunay;
import net.jgeom.nurbs.util.Triangulate;
import net.jgeom.nurbs.util.vtkDelaunay;
import net.jgeom.nurbs.NurbsSurface;
import net.jgeom.nurbs.TrimCurve;
import net.jgeom.nurbs.UVCoord2f;

/**
 * Evaluates a NurbsSurface with TrimCurves into a PointArray. 
 * All points that are trimmed are not int the PoinArray and therefore not drawn.
 * 
 * @author sg
 * @version 1.0
 */
public class TrimSurfaceEvaluatorDelaunay extends TrimEvaluator {

	public GeometryArray evaluateSurface(NurbsSurface surface, int segU,
			int segV) {
		float uKnots[] = surface.getUKnots();
		float u = uKnots[0]; //TODO: always 0?
		float maxU = uKnots[uKnots.length - 1];
		float uStep = (maxU - u) / segU;

		float vKnots[] = surface.getVKnots();
		float v = vKnots[0]; //TODO: always 0?
		float maxV = vKnots[vKnots.length - 1];
		float vStep = (maxV - v) / segV;

		List trim = surface.getOuterTrimCurves();
		
//		 add the trimCurve polyline representation to the set of vertices
		for (Object tc : surface.getInnerTrimCurves()) 	
		{
			TrimCurve tcCast = (TrimCurve)tc;
			for (UVCoord2f coordUV : tcCast.getCoordinates()) {
//				if(!uvCoords.contains(coordUV))
				{
					if(coordUV.x!=Float.NaN && coordUV.y!=Float.NaN)
					{
						vertexList.add(surface.pointOnSurface(coordUV.x, coordUV.y));
						uvCoords.add(coordUV);
					}
				}
			}
		}
		
//		ArrayList<UVCoord2f> outerBounds=new ArrayList<UVCoord2f>(); 
////		 add the trimCurve polyline representation to the set of vertices
//		for (Object tc : surface.getOuterTrimCurves()) 	
//		{
//			TrimCurve tcCast = (TrimCurve)tc;
//			for (UVCoord2f coordUV : tcCast.getCoordinates()) {
////				if(!uvCoords.contains(coordUV))
//				{
//					if(coordUV.x!=Float.NaN && coordUV.y!=Float.NaN)
//					{
////						vertexList.add(surface.pointOnSurface(coordUV.x, coordUV.y));
////						uvCoords.add(new UVCoord2f(coordUV.x,coordUV.y));
//						outerBounds.add(coordUV);
//					}
//				}
//			}
//		}
		
		Random r=new Random();
		ArrayList<Integer> constraints=new ArrayList<Integer>();
		for (u = uKnots[0]; u <= maxU+uStep; ) {
//			this.setUforAllCurves(surface, u);
			
			for (v=vKnots[0]; v <= maxV+vStep; ) {
				Iterator it = trim.iterator();
				boolean trimmed = false;
//				trimmed = isTrimmedForSetU(surface, v);
				trimmed = !this.isVertexInBoundary(surface, u, v);
				
				if (!trimmed) 
				{
					vertexList.add(surface.pointOnSurface(u, v));
					uvCoords.add(new UVCoord2f(u, v));
				}
//				else
//				{
//					UVCoord2f p=this.getClosestPoint(new UVCoord2f(u,v), outerBounds);
//					if(!uvCoords.contains(p))
//					{
//						uvCoords.add(p);
//						vertexList.add(surface.pointOnSurface(p.x,p.y));
//					}
//				}
				else
				{
					Point2f p=this.closestIntersection(surface,u, v);
					if(!uvCoords.contains(p))
					{
						vertexList.add(surface.pointOnSurface(p.x,p.y));
						uvCoords.add(new UVCoord2f(p.x, p.y));
						constraints.add(uvCoords.size()-1);
					}
				}
//				v += vStep;
				v+=vStep*(r.nextFloat()/2+0.5);//anisotrop meshing
			}
			u += uStep*(r.nextFloat()/2+0.5);//anisotrop meshing
//			u += uStep;
		}
		
		int factorForTriangulation=1;
		
		float[] UVcoords3d=new float[uvCoords.size()*3];
		{int i=0;
			for(UVCoord2f coord : uvCoords)
			{
				UVcoords3d[i++]=coord.x*factorForTriangulation;
				UVcoords3d[i++]=coord.y*factorForTriangulation;
				UVcoords3d[i++]=0;
			}
		}
			
		int[] constraints_tab=new int[constraints.size()];
		
//		for (int i=0;i<constraints.size();i++)
//			constraints_tab[i]=constraints.get(i);
//		
		for (int i=constraints.size()-1;i>=0;i--)
			constraints_tab[i]=constraints.get(i);
		
		int[] j3dOutputTriangles=vtkDelaunay.getTriangulation(UVcoords3d,constraints_tab);
		
		//java based triangulator
//		int[] j3dOutputTriangles=(new IncrementalDelaunay()).getTriangulation(UVcoords3d,constraints_tab);
		
		Point3f[] trianglesList_Array = new Point3f[vertexList.size()];
		vertexList.toArray(trianglesList_Array);
		
		ArrayList<Point3f>flatTriangles=new ArrayList<Point3f>(); 
		
		int j=0,i=0;
		while(i<=j3dOutputTriangles.length-3)
		{
			boolean trimmed = false;
			trimmed = !this.isVertexInBoundary(surface 
					,(uvCoords.get(j3dOutputTriangles[i]).x + uvCoords.get(j3dOutputTriangles[i+1]).x + uvCoords.get(j3dOutputTriangles[i+2]).x )/3
					,(uvCoords.get(j3dOutputTriangles[i]).y + uvCoords.get(j3dOutputTriangles[i+1]).y + uvCoords.get(j3dOutputTriangles[i+2]).y )/3);
			
			
			if(!trimmed )
			{
				flatTriangles.add(trianglesList_Array[j3dOutputTriangles[i]]);
				flatTriangles.add(trianglesList_Array[j3dOutputTriangles[i+1]]);
				flatTriangles.add(trianglesList_Array[j3dOutputTriangles[i+2]]);
			}
			i+=3;
		}
		Point3f[] flatTrianglesArray=new Point3f[flatTriangles.size()];
		flatTriangles.toArray(flatTrianglesArray);
		
		TriangleArray tra=new TriangleArray(j,TriangleArray.COORDINATES);
		tra.setCoordinates(0, flatTrianglesArray);
		
//		IndexedTriangleArray tra = new IndexedTriangleArray(j3dUVcoords.length/3,
//		IndexedTriangleArray.COORDINATES, j3dUVcoords.length/3);
		
//		tra.setCoordinates(0, trianglesList_Array);
//		tra.setCoordinateIndices(0, j3dOutputTriangles);
		
		/*Code for Calling external Delaunay Triangulation*/
		/*
		float[][] samples = new float[2][vertexList.size()];
		for (int i = 0; i < uvCoords.size(); i++) {
			samples[0][i] = ((UVCoord2f) uvCoords.get(i)).x;
			samples[1][i] = ((UVCoord2f) uvCoords.get(i)).y;
		}
		
		Triangulate t = new Triangulate();
		Triangulate.XYZ[] pointsT = new Triangulate.XYZ[uvCoords.size()];
		for (int i = 0; i < pointsT.length; i++)
			pointsT[i] = t.new XYZ(uvCoords.get(i).x, uvCoords.get(i).y, 0);

		Triangulate.ITRIANGLE[] triangles = new Triangulate.ITRIANGLE[vertexList
				.size() * 3];

		for (int i = 0; i < triangles.length; i++)
			triangles[i] = t.new ITRIANGLE();

		int ntri = t.Triangulate(uvCoords.size()-3 , pointsT, triangles);

		Point3f[] trianglesList_Array = new Point3f[vertexList.size()];
		vertexList.toArray(trianglesList_Array);

		int[] indexesArray = new int[triangles.length * 3];
		int j = 0;
		for (int i = 0; i < triangles.length * 3; i += 3) {
			indexesArray[i] = triangles[j].p1;
			indexesArray[i + 1] = triangles[j].p2;
			indexesArray[i + 2] = triangles[j].p3;
			j++;
		}
		
		
		/*
		// code for non indexed triangle set
		j = 0;
		List<Point3f> trianglesRaw=new LinkedList<Point3f>();
		for (int i = 0; i < ntri; i++) {

			UVCoord2f p1 = uvCoords.get(triangles[j].p1);
			UVCoord2f p2 = uvCoords.get(triangles[j].p2);
			UVCoord2f p3 = uvCoords.get(triangles[j].p3);
			if (!this.isVertexUVTrimmed(surface, p1.x, p1.y)
					&& !this.isVertexUVTrimmed(surface, p2.x, p2.y)
					&& !this.isVertexUVTrimmed(surface, p3.x, p3.y)) 
			{
				// don't draw triangle if a part of it is trimmed
				Point2f barycentre = new Point2f((p1.x + p2.x + p3.x) / 3,
						(p1.y + p2.y + p3.y) / 3);
				if (!this.isVertexUVTrimmed(surface, barycentre.x, barycentre.y)) 
				{
					trianglesRaw.add(vertexList.get(triangles[j].p1));
					trianglesRaw.add(vertexList.get(triangles[j].p2));
					trianglesRaw.add(vertexList.get(triangles[j].p3));
				}
			}
				j++;

		}
		Point3f[] triangesRaw_Array = new Point3f[trianglesRaw.size()];
		trianglesRaw.toArray(triangesRaw_Array);
		
		TriangleArray tra = new TriangleArray(triangesRaw_Array.length,TriangleArray.COORDINATES);
		tra.setCoordinates(0, triangesRaw_Array);
		
		*/
		
		

//		IndexedTriangleArray ta = new IndexedTriangleArray(points.size(),
//				IndexedTriangleArray.COORDINATES, triangles.length * 3);
//		ta.setCoordinates(0, trianglesList_Array);
//		ta.setCoordinateIndices(0, indexesArray);

//		PointArray pa = new PointArray(points.size(), PointArray.COORDINATES);
//		Point3f pnts[] = new Point3f[points.size()];
//		points.toArray(pnts);
//		pa.setCoordinates(0, pnts);
		//    for(int i=0;i<pa.getVertexCount();i++)
		//    {
		//    	pa.setColor(i, new float[]{derivates.get(i),derivates.get(i),0.5f});
		//    	System.out.println(derivates.get(i));
		//    }
		return tra;
	}
	
	UVCoord2f getClosestPoint(UVCoord2f pt, ArrayList<UVCoord2f>list )
	{
		Iterator it=list.iterator();
		float minDist=Float.MAX_VALUE;
		UVCoord2f result=null;
		while(it.hasNext())
		{
			UVCoord2f pt2=(UVCoord2f)it.next();
			float distTmp=pt.distanceTo(pt2);
			if(distTmp<minDist)
			{
				minDist=distTmp;
				result=pt2;
			}
		}
		return result;
	}

}
