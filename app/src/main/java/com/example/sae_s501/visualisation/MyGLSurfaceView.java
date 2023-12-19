package com.example.sae_s501.visualisation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.InputStream;

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context, InputStream inputStream){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(inputStream);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    public void setZoomFactor(float zoomFactor) {
        renderer.setZoomFactor(zoomFactor);
        Log.d("ZOOM", String.valueOf(zoomFactor));
        requestRender(); // Redessiner avec les nouveaux paramètres de zoom
    }


    public void updateModel(InputStream inputStream) {

    }
}
