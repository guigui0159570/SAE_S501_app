package com.example.sae_s501.visualisation;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {
    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle;
    private int colorHandle;

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
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final float[] vertices;
    private final int[] faces;

    private final float[] normales;

    private final int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Mesh(float[] vertices, int[] faces, float[] normales) {
        this.vertices = vertices;
        this.faces = faces;
        this.normales = normales;

        mProgram = GLES20.glCreateProgram();

        vertexCount = vertices.length / COORDS_PER_VERTEX;

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4); // 4 bytes per float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        linkProgram();
    }

    public void draw(float[] mvpMatrix){
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Set the projection and view transformation
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void linkProgram() {
        GLES20.glLinkProgram(mProgram);

        int[] status = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == GLES20.GL_FALSE) {
            Log.e("Mesh", "Error linking program: " + GLES20.glGetProgramInfoLog(mProgram));
        } else {
            Log.d("Mesh", "Program linked successfully");
        }
    }

    public float[] getVertices() {
        return vertices;
    }

    public int getNumVertices() {
        return vertices.length / 3;
    }

    public int[] getFaces() {
        return faces;
    }

    public int getNumFaces() {
        return faces.length;
    }

    public float[] getNormales() {
        return normales;
    }

    public int getNumNormales(){
        return normales.length;
    }
}
