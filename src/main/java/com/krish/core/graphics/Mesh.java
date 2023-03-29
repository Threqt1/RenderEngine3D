package com.krish.core.graphics;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private int vertices;
    //The vertex array ID
    private int vaoId;
    //The list containing all the vertex buffer IDs
    private List<Integer> vboIdList;

    /**
     * Create a new Mesh
     * @param positions The positions of the vertices
     * @param colors The colors at each vertex
     * @param indices The indices for the triangles
     */
    public Mesh(float[] positions, float[] colors, int[] indices) {
        //Allocate a new stack in memory
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.vertices = indices.length;
            vboIdList = new ArrayList<>();

            //Create VAO and set it as the current active one
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //Create the two VBOs for position and colors
            createVBO(0, 3, positions, stack);
            createVBO(1, 3, colors, stack);

            //Create indices buffer
            createIndicesBuffer(indices, stack);

            //Deselect the VAO
            glBindVertexArray(0);
        }
    }

    /**
     * Creates a new vertex (float) buffer at the specified index
     * @param index The index to create the VBO at in the VAO
     * @param size The size of the data (2 for Vector2, 3 for Vector3, etc)
     * @param data The actual data (in an array)
     * @param stack The active memory stack
     */
    public void createVBO(int index, int size, float[] data, MemoryStack stack) {
        //Create a new VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        //Allocate a float buffer and store the current data in it
        FloatBuffer buffer = stack.callocFloat(data.length);
        buffer.put(0, data);
        //Select the current VBO as an array buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        //Set the VBO's data to the buffer
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        //Enable the VBO's index on the attribute array
        glEnableVertexAttribArray(index);
        //Set the information about the VBO
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);

        //Deselect the VBO at the end
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Create the indices array
     * @param indices The array of indices
     * @param stack The active memory stack
     */
    public void createIndicesBuffer(int[] indices, MemoryStack stack) {
        //Generate a new VBO
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        //Allocate the indices to an integer buffer
        IntBuffer buffer = stack.callocInt(indices.length);
        buffer.put(0, indices);
        //Select the current VBO as an element array buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        //Write the indice buffer data to the element array buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    /**
     * Cleanup the Mesh after program is done
     */
    public void cleanup() {
        //Delete all the VBOs
        vboIdList.forEach(GL30::glDeleteBuffers);
        //Delete the VAO
        glDeleteVertexArrays(vaoId);
    }

    public int getVertices() {
        return vertices;
    }

    public int getVaoId() {
        return vaoId;
    }
}
