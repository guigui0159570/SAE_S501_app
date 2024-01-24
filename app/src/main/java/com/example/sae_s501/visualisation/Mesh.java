package com.example.sae_s501.visualisation;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

public class Mesh {
    private float[] vertices;
    private int[] faces;
    private float[] normals;

    public Mesh(float[] vertices, int[] faces, float[] normals) {
        this.vertices = vertices;
        this.faces = faces;
        this.normals = normals;
    }

    public int getNumVertices() {
        return vertices.length / 3; // Assuming each vertex has x, y, z coordinates
    }

    public int getNumFaces() {
        return faces.length / 3; // Assuming each vertex has x, y, z coordinates
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getFaces() {
        return faces;
    }

    public boolean containsNullInFaces() {
        int index = 0;
        for (int face : faces) {
            index++;
            if (face <= 0) { // Ajoutez d'autres conditions si nécessaire
                System.out.println(face);
                System.out.println(index);
                return true;
            }
        }
        return false;
    }

    public boolean indexOutOfRangeFaceToVertex(){
        if (getNumVertices() < getNumFaces()){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices: ").append(Arrays.toString(vertices)).append("\n");
        sb.append("Faces: ").append(Arrays.toString(faces)).append("\n");
        sb.append("Normals: ").append(Arrays.toString(normals)).append("\n");
        return sb.toString();
    }
}
