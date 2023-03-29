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
    private int vaoId;
    private List<Integer> vboIdList;

    public Mesh(float[] positions, float[] colors, int[] indices) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.vertices = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            createVBO(0, 3, positions, stack);
            createVBO(1, 3, colors, stack);

            createIndicesBuffer(indices, stack);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void createVBO(int index, int vertices, float[] data, MemoryStack stack) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer buffer = stack.callocFloat(data.length);
        buffer.put(0, data);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, vertices, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void createIndicesBuffer(int[] indices, MemoryStack stack) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        IntBuffer buffer = stack.callocInt(indices.length);
        buffer.put(0, indices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);
    }

    public int getVertices() {
        return vertices;
    }

    public int getVaoId() {
        return vaoId;
    }
}
