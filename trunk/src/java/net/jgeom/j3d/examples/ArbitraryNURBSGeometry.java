/*
 * jgeom: Geometry Library for Java
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
package net.jgeom.j3d.examples;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3d;

import net.jgeom.j3d.objects.NurbsSurfaceShape;
import net.jgeom.nurbs.BasicNurbsSurface;
import net.jgeom.nurbs.ControlNet;
import net.jgeom.nurbs.ControlPoint4f;


import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * Shows how to create an arbitrary NURBS Surface
 * @author sg
 */
public class ArbitraryNURBSGeometry extends Applet {
    private static final long serialVersionUID = 1L;

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph() {
        BranchGroup objRoot = new BranchGroup();
        TransformGroup objTrans = new TransformGroup();
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRoot.addChild(objTrans);

        
        objTrans.addChild(createArbitraryNURBS());

        Transform3D yAxis = new Transform3D();
        Alpha rotationAlpha = new Alpha(-1, 4000);

        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis, 0.0f,
                (float) Math.PI * 2.0f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        rotator.setSchedulingBounds(bounds);
        objRoot.addChild(rotator);
        objRoot.compile();
        return objRoot;
    }

    public ArbitraryNURBSGeometry() {
    }

    public void init() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        BranchGroup scene = createSceneGraph();
        u = new SimpleUniverse(c);
        u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);
        
        BranchGroup lights=new BranchGroup();
        AmbientLight al=new AmbientLight();
        al.setEnable(true);
        al.setInfluencingBounds(new BoundingSphere(new Point3d(), 10));
        lights.addChild(al);
        DirectionalLight dl=new DirectionalLight();
        dl.setInfluencingBounds(new BoundingSphere(new Point3d(), 10));
        dl.setDirection(1,1,1);
        dl.setEnable(true);
        lights.addChild(dl);
        u.addBranchGraph(lights);
    }

    private Shape3D createArbitraryNURBS() {
        //Geometry
        
        //Controlpoints
        //Each array is on "spine" in the u-direction
        ControlPoint4f cp1[] = { new ControlPoint4f(-0.7f, 0, 0, 1), new ControlPoint4f(-0.3f, 0.4f, 0, 1),
                                 new ControlPoint4f(0.3f, 0.6f, 0, 1), new ControlPoint4f(0.5f, -0.4f, 0, 1),
                                 new ControlPoint4f(0.7f, 0.2f, 0, 1) };
        ControlPoint4f cp2[] = { new ControlPoint4f(-0.7f, 0, 0.2f, 1), new ControlPoint4f(-0.3f, 0.4f, 0.2f, 1),
                                 new ControlPoint4f(0.3f, 0.6f, 0.2f, 1), new ControlPoint4f(0.5f, -0.4f, 0.2f, 1),
                                 new ControlPoint4f(0.7f, 0.1f, 0.2f, 1) };
        ControlPoint4f cp3[] = { new ControlPoint4f(-0.7f, -0.4f, 0.4f, 1), new ControlPoint4f(-0.3f, 0.2f, 0.4f, 1),
                                 new ControlPoint4f(0.3f, 0.6f, 0.4f, 1), new ControlPoint4f(0.5f, -0.4f, 0.4f, 1),
                                 new ControlPoint4f(0.7f, 0.7f, 0.4f, 1) };
        ControlPoint4f cp4[] = { new ControlPoint4f(-0.7f, 0.2f, 0.6f, 1), new ControlPoint4f(-0.3f, 0.9f, 0.6f, 1),
                                 new ControlPoint4f(0.3f, 0.6f, 0.6f, 1), new ControlPoint4f(0.5f, -0.4f, 0.6f, 1),
                                 new ControlPoint4f(0.7f, 0.4f, 0.6f, 1) };
        ControlPoint4f cp5[] = { new ControlPoint4f(-0.7f, 0, 0.8f, 1), new ControlPoint4f(-0.3f, 0.7f, 0.8f, 1),
                                 new ControlPoint4f(0.3f, 0.2f, 0.8f, 1), new ControlPoint4f(0.3f, -0.4f, 0.8f, 1),
                                 new ControlPoint4f(0.7f, 0, 0.8f, 1) };

        ControlPoint4f cps[][] = { cp1, cp2, cp3, cp4, cp5 };
        
        //Control Net
        ControlNet cnet = new ControlNet(cps);
        
        //Knot vectors
        float u[] = { 0.5f, 0.5f, 0.5f,0.5f, 0.75f, 1, 1, 1, 1 };
        float v[] = { 0.5f, 0.5f, 0.5f, 0.5f, 0.75f, 1, 1, 1, 1 };
        
        
        //Build the Nurbssurface
        BasicNurbsSurface ns = new BasicNurbsSurface(cnet, u, v, 3, 3);

        //Appearance
        Appearance surfaceApp = new Appearance();
        Material surfMat = new Material();
        surfMat.setAmbientColor(1, 0, 0);
        surfaceApp.setMaterial(surfMat);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        surfaceApp.setPolygonAttributes(pa);
        TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0.4f);
        ta.setDstBlendFunction(TransparencyAttributes.BLEND_ONE);
        ta.setSrcBlendFunction(TransparencyAttributes.BLEND_ONE_MINUS_SRC_ALPHA);
        surfaceApp.setTransparencyAttributes(ta);

        NurbsSurfaceShape nss = new NurbsSurfaceShape(ns);
        nss.setAppearance(surfaceApp);
        return nss;
    }

    public void destroy() {
        u.cleanup();
    }

    public static void main(String[] args) {
        new MainFrame(new ArbitraryNURBSGeometry(), 256, 256);
    }
}
