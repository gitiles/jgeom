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
package net.jgeom.nurbs.util;

import vtk.vtkActor;
import vtk.vtkCellArray;
import vtk.vtkCleanPolyData;
import vtk.vtkConeSource;
import vtk.vtkDelaunay2D;
import vtk.vtkMergePoints;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkQuantizePolyDataPoints;
import vtk.vtkRenderWindow;
import vtk.vtkRenderer;


public class vtkDelaunay {
	
	static boolean useConstraints=false;
	
	 // In the static contructor we load in the native code.
	  // The libraries must be in your path to work.
	  static { 
	    System.loadLibrary("vtkCommonJava"); 
	    System.loadLibrary("vtkFilteringJava"); 
	    System.loadLibrary("vtkIOJava"); 
	    System.loadLibrary("vtkImagingJava"); 
	    System.loadLibrary("vtkGraphicsJava"); 
	    System.loadLibrary("vtkRenderingJava"); 
	  }
	  
	synchronized  public static int[] getTriangulation(float[]points, int[]constraintEdges)
	  {
		  vtkPoints pts=new vtkPoints();
//		  pts.DebugOn();
		  int k=0;
		  for(int i=0;i<points.length/3;i++)
		  {
			  pts.InsertNextPoint(new double[]{points[k++],points[k++],points[k++]});
		  }

		  vtkPolyData pointCloud=new vtkPolyData();
		  pointCloud.SetPoints(pts);
//		  pointCloud.DebugOn();
		  
		  if (useConstraints) {
			vtkCellArray polys = new vtkCellArray();
			polys.InsertNextCell(constraintEdges.length);
			if (constraintEdges != null) {
				for (int index : constraintEdges)
					polys.InsertCellPoint(index);
				pointCloud.SetPolys(polys);
				pointCloud.BuildCells();
				// pointCloud.BuildLinks(0);
			}
		}
//		  
		  pts.Delete();
		  
		  vtkDelaunay2D del=new vtkDelaunay2D();
//		  del.DebugOn();

//		  vtkCleanPolyData clean=new vtkCleanPolyData();
//		  clean.SetInput(pointCloud);
//		  clean.PointMergingOff();
//		  clean.Update();

//		  del.SetInput(clean.GetOutput());
//		  del.SetSource(clean.GetOutput());
		  
		  del.SetInput(pointCloud);
		  del.SetSource(pointCloud);
		  
//		  del.SetOffset(0);
//		  del.SetTolerance(0.6);
		  pointCloud.Delete();
		  del.BreakOnError();
//		  del.SetTolerance(0.25);
//		  del.SetAlpha(1);
		  del.Update();
		 
		  vtkPolyData output=del.GetOutput();

//		  int outNumPts = output.GetNumberOfPoints();
//		  int outNumCells = output.GetNumberOfCells();
//		  int outNumPolys = output.GetNumberOfPolys();
//		  int outNumLines = output.GetNumberOfLines();
//		  int outNumVerts = output.GetNumberOfVerts();
		  
//		  System.out.println("outNumPts"+outNumPts);
//		  System.out.println("outNumCells"+outNumCells);
//		  System.out.println("outNumPolys"+outNumPolys);
//		  System.out.println("outNumLines"+outNumLines);
//		  System.out.println("outNumVerts"+outNumVerts);
		  
		  int[] indices=new int[output.GetPolys().GetData().GetSize()-(output.GetPolys().GetData().GetSize()/4)];

//		  System.out.println("nb pts : "+clean.GetOutput().GetPolys().GetData().GetSize()*3/4);
//		  System.out.println("taille mesh : "+indices.length);
		  int j=0;
		  for (int i=0;i<output.GetPolys().GetData().GetSize();i++)
		  {
//			  System.out.println(output.GetPolys().GetData().GetValue(i));
//			  if (i%3==0)i++;
//			  if(i>=indices.length)break;
			  if(i%4!=0&&i!=0)
				  indices[j++]=output.GetPolys().GetData().GetValue(i);
		  }
		  
		  return indices;
	  }

	  // now the main program
	  public static void main (String []args) {
		  
		  vtkPoints pts=new vtkPoints();
		  pts.InsertNextPoint(  1.5026018771810041,  1.5026019428618222, 2.0 );
		  pts.InsertNextPoint( -1.5026020085426373,  1.5026018115001829, 0.0 );
		  pts.InsertNextPoint( -1.5026018353814194, -1.5026019846614038, 0.0 );
		  pts.InsertNextPoint(  1.5026019189805875, -1.5026019010622396, 0.0 );
//		  pts.InsertNextPoint(  5.2149123972752491,  5.2149126252263240, 0.0 );
//		  pts.InsertNextPoint( -5.2149128531773883,  5.2149121693241645, 5.0 );
//		  pts.InsertNextPoint( -5.2149122522061022, -5.2149127702954603, 0.0 );
//		  pts.InsertNextPoint(  5.2149125423443916, -5.2149124801571842, 0.0 );
//		  pts.InsertNextPoint(  8.9272229173694946,  8.9272233075908254, 0.0 );
//		  pts.InsertNextPoint( -8.9272236978121402,  8.9272225271481460, 0.0 );
//		  pts.InsertNextPoint( -8.9272226690307868, -8.9272235559295172, -3.0 );
//		  pts.InsertNextPoint(  8.9272231657081953, -8.9272230592521282, 0.0 );
//		  pts.InsertNextPoint(  12.639533437463740,  12.639533989955329, 0.0 );
//		  pts.InsertNextPoint( -12.639534542446890,  12.639532884972127, 0.0 );
//		  pts.InsertNextPoint( -12.639533085855469, -12.639534341563573, 0.0 );
//		  pts.InsertNextPoint(  12.639533789072001, -12.639533638347073, 0.0 );
		  
		  float[] points=new float[]{ 
				   1.5026018771810041f,  1.5026019428618222f, 2.0f,
				  -1.5026020085426373f,  1.5026018115001829f, 0.0f ,
				  -1.5026018353814194f, -1.5026019846614038f, 0.0f,
				  1.5026019189805875f, -1.5026019010622396f, 0.0f ,
//				  5.2149123972752491f,  5.2149126252263240f, 0.0f ,
//				  -5.2149128531773883f,  5.2149121693241645f, 5.0f ,
//				  -5.2149122522061022f, -5.2149127702954603f, 0.0f ,
//				  5.2149125423443916f, -5.2149124801571842f, 0.0f ,
//				  8.9272229173694946f,  8.9272233075908254f, 0.0f ,
//				  -8.9272236978121402f,  8.9272225271481460f, 0.0f ,
//				  -8.9272226690307868f, -8.9272235559295172f, -3.0f ,
//				  8.9272231657081953f, -8.9272230592521282f, 0.0f ,
//				  12.639533437463740f,  12.639533989955329f, 0.0f ,
//				  -12.639534542446890f,  12.639532884972127f, 0.0f ,
//				  -12.639533085855469f, -12.639534341563573f, 0.0f ,
//				  12.639533789072001f, -12.639533638347073f, 0.0f 
				  };
		  
		  int[] indices=getTriangulation(points,null);
		  for (int i=0;i<indices.length;i++)
		  {
//			  System.out.println(indices[i]);
//			  System.out.println();
			  System.out.println(indices[i]);
		  }
		  
//		  vtkCellArray polys = new vtkCellArray();
//		  polys.InsertCellPoint(0);
//		  polys.InsertCellPoint(1);
//		  polys.InsertCellPoint(2);
//		  polys.InsertCellPoint(3);
//		  polys.InsertCellPoint(4);
//		  polys.InsertCellPoint(5);
//		  polys.InsertCellPoint(6);
		  
		  vtkPolyData pointCloud=new vtkPolyData();
		  pointCloud.SetPoints(pts);
//		  pointCloud.SetPolys(polys);
		  pts.Delete();

		  vtkDelaunay2D del=new vtkDelaunay2D();
//		  del.BoundingTriangulationOn();
		  del.SetInput(pointCloud);
		  del.SetSource(pointCloud);
		  pointCloud.Delete();
		  del.Update();
		  
		  vtkPolyData output=del.GetOutput();
		  int outNumPts = output.GetNumberOfPoints();
		  int outNumCells = output.GetNumberOfCells();
		  int outNumPolys = output.GetNumberOfPolys();
		  int outNumLines = output.GetNumberOfLines();
		  int outNumVerts = output.GetNumberOfVerts();
		  
		  System.out.println("outNumPts"+outNumPts);
		  System.out.println("outNumCells"+outNumCells);
		  System.out.println("outNumPolys"+outNumPolys);
		  System.out.println("outNumLines"+outNumLines);
		  System.out.println("outNumVerts"+outNumVerts);
		  
		  for (int i=0;i<output.GetPolys().GetData().GetSize();i++)
			  System.out.println(output.GetPolys().GetData().GetValue(i));
		  
//		  output.BuildLinks(0);

//	    // 
//	    // Next we create an instance of vtkConeSource and set some of its
//	    // properties. The instance of vtkConeSource "cone" is part of a
//	    // visualization pipeline (it is a source process object); it produces data
//	    // (output type is vtkPolyData) which other filters may process.
//	    //
//	    vtkConeSource cone = new vtkConeSource();
//	    cone.SetHeight( 3.0 );
//	    cone.SetRadius( 1.0 );
//	    cone.SetResolution( 10 );
//	  
	    // 
	    // In this example we terminate the pipeline with a mapper process object.
	    // (Intermediate filters such as vtkShrinkPolyData could be inserted in
	    // between the source and the mapper.)  We create an instance of
	    // vtkPolyDataMapper to map the polygonal data into graphics primitives. We
	    // connect the output of the cone souece to the input of this mapper.
	    //
		  
		  
	    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
	    coneMapper.SetInputConnection( del.GetOutputPort() );

	    // 
	    // Create an actor to represent the cone. The actor orchestrates rendering
	    // of the mapper's graphics primitives. An actor also refers to properties
	    // via a vtkProperty instance, and includes an internal transformation
	    // matrix. We set this actor's mapper to be coneMapper which we created
	    // above.
	    //
	    vtkActor coneActor = new vtkActor();
	    coneActor.SetMapper( coneMapper );
	    coneActor.GetProperty().SetRepresentationToWireframe();

	    //
	    // Create the Renderer and assign actors to it. A renderer is like a
	    // viewport. It is part or all of a window on the screen and it is
	    // responsible for drawing the actors it has.  We also set the background
	    // color here
	    //
	    vtkRenderer ren1 = new vtkRenderer();
	    ren1.AddActor( coneActor );
	    ren1.SetBackground( 0.1, 0.2, 0.4 );

	    //
	    // Finally we create the render window which will show up on the screen
	    // We put our renderer into the render window using AddRenderer. We also
	    // set the size to be 300 pixels by 300
	    //
	    vtkRenderWindow renWin = new vtkRenderWindow();
	    renWin.AddRenderer( ren1 );
	    renWin.SetSize( 300, 300 );
	    
	    //
	    // now we loop over 360 degreeees and render the cone each time
	    //
	    int i=0;	  
//	    for (i = 0; i < 360; ++i)
	      for(;;)
	      {
	  		i=i++%360;
	      // render the image
	      renWin.Render();
	      // rotate the active camera by one degree
	      ren1.GetActiveCamera().Azimuth( 1 );
	      try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      }
	  
	    } 
}
