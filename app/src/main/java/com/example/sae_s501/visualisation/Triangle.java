package com.example.sae_s501.visualisation;

import android.opengl.GLES20;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int vPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    private short drawOrderShort[] /*= {
    *        0, 1, 2
    }*/;

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public Triangle(Mesh file) throws IOException {
        triangleCoords = file.getVertices();

        int[] drawOrder = file.getFaces();
        Log.d("VERTEXTRIANGLES", String.valueOf(file.getNumVertices()));Log.d("FACESTRIANGLES", String.valueOf(file.getNumFaces()));
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        drawOrderShort = new short[drawOrder.length];
        for (int i = 0; i < drawOrder.length; i++) {
            drawOrderShort[i] = (short) ((int) drawOrder[i]);
        }

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrderShort.length * 2);
        dlb.order(ByteOrder.nativeOrder());

        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrderShort);
        drawListBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        GLES20.glLinkProgram(mProgram);
        // creates OpenGL ES program executables
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("OpenGL Error", "Program link failed: " + GLES20.glGetProgramInfoLog(mProgram));
        }
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Use drawListBuffer for indices
        if (drawListBuffer != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrderShort.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        } else {
            Log.e("Triangle", "drawListBuffer is null");
        }
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL Error", "Error: " + error);
        }
    }

}
