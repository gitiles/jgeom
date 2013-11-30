jgeom source code

version 1.0.0 : 6 August 2012

Initial Installation of existing jgeom code into SourceForge project.

In order compile the jgeom-core.jar library or the Javadoc documentation
you will need to obtain the vtk.jar library for the Visualisation Toolkit --
see http://www.vtk.org to download this open-source code. 
The build.xml file is configured to load the jar file vtk.jar library from the
,.lib subfolder; or you can place the vtk.jar elsewhere in the CLASSPATH
for the java compiler for compilation.

VTK is licensed under BSD License: see
http://www.vtk.org/VTK/project/license.html

The vtk.jar library is not needed to use the jgeom library unless the
calling java application creates an instance of the
net.jgeom.nurbs.evaluators.TrimSurfaceEvaluatorDelaunay class.
