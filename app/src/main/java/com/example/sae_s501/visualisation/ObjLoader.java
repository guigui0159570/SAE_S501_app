package com.example.sae_s501.visualisation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {
    public static Mesh load(InputStream inputStream) throws IOException {
        Log.d("ObjLoader", "CREATION");
        List<float[]> vertices = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    // Parse vertex coordinates
                    String[] parts = line.split("\\s+");
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    vertices.add(new float[]{x, y, z});
                } else if (line.startsWith("vn ")) {
                    // Parse normal coordinates
                    String[] parts = line.split("\\s+");
                    float nx = Float.parseFloat(parts[1]);
                    float ny = Float.parseFloat(parts[2]);
                    float nz = Float.parseFloat(parts[3]);
                    normals.add(new float[]{nx, ny, nz});
                } else if (line.startsWith("f ")) {
                    // Parse face indices
                    String[] parts = line.split("\\s+");
                    int v1 = Integer.parseInt(parts[1].split("//")[0]) - 1;
                    int v2 = Integer.parseInt(parts[2].split("//")[0]) - 1;
                    int v3 = Integer.parseInt(parts[3].split("//")[0]) - 1;
                    int v4 = Integer.parseInt(parts[4].split("//")[0]) - 1;
                    faces.add(new int[]{v1, v2, v3});
                    faces.add(new int[]{v1, v3, v4});
                }
            }
        }

        // Convert list of vertices to a float array
        float[] verticesArray = convertListToArray(vertices);
        float[] normalsArray = convertListToArray(normals);
        int[] facesArray = convertFaceListToArray(faces);

        Log.d("VERTICES", String.valueOf(verticesArray.length));

        return new Mesh(verticesArray, facesArray, normalsArray);
    }

    private static float[] convertListToArray(List<float[]> list) {
        int size = list.size();
        float[] array = new float[size * 3];
        int index = 0;
        for (int i = 0; i < size; i++) {
            float[] element = list.get(i);
            array[index++] = element[0];
            array[index++] = element[1];
            array[index++] = element[2];
        }
        Log.d("VERTICES ARRAY", String.valueOf(array.length));
        return array;
    }

    private static int[] convertFaceListToArray(List<int[]> faceList) {
        int[] facesArray = new int[faceList.size() * 3];
        int index = 0;
        for (int[] face : faceList) {
            facesArray[index++] = face[0];
            facesArray[index++] = face[1];
            facesArray[index++] = face[2];
        }
        return facesArray;
    }

    public static void main(String[] args) {
        // Example usage:
        try (InputStream inputStream = ObjLoader.class.getClassLoader().getResourceAsStream("")) {
            if (inputStream != null) {
                Mesh mesh = load(inputStream);
                System.out.println("Number of vertices: " + mesh.getNumVertices());
            } else {
                System.err.println("Model file not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
