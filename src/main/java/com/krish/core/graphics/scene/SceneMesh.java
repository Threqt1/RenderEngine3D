package com.krish.core.graphics.scene;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneMesh {
    private final int vertices;
    //The vertex array ID
    private final int vaoId;
    //The list containing all the vertex buffer IDs
    private final List<Integer> vboIdList;

    /**
     * Create a new Mesh
     * @param positions The positions of the vertices
     * @param textureCoordinates The colors at each vertex
     * @param indices The indices for the triangles
     */
    public SceneMesh(float[] positions, float[] textureCoordinates, int[] indices) {
        //Allocate a new stack in memory
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.vertices = indices.length;
            vboIdList = new ArrayList<>();

            //Create VAO and set it as the current active one
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //Create a positions buffer
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            //Allocate a float buffer and store the current data in it
            FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
            positionsBuffer.put(0, positions);
            //Select the current VBO as an array buffer
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            //Set the VBO's data to the buffer
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
            //Enable the VBO's index on the attribute array
            glEnableVertexAttribArray(0);
            //Set the information about the VBO
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //Deselect the VBO at the end
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            //Create a textures buffer
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            //Allocate a float buffer and store the current data in it
            FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
            textureBuffer.put(0, textureCoordinates);
            //Select the current VBO as an array buffer
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            //Set the VBO's data to the buffer
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            //Enable the VBO's index on the attribute array
            glEnableVertexAttribArray(1);
            //Set the information about the VBO
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //Deselect the VBO at the end
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            //Create indices buffer
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            //Allocate the indices to an integer buffer
            IntBuffer buffer = stack.callocInt(indices.length);
            buffer.put(0, indices);
            //Select the current VBO as an element array buffer
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            //Write the indice buffer data to the element array buffer
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

            //Deselect VBO
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            //Deselect the VAO
            glBindVertexArray(0);
        }
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
