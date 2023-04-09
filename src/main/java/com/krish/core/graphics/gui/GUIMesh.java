package com.krish.core.graphics.gui;

import imgui.ImDrawData;
import static org.lwjgl.opengl.GL30.*;

public class GUIMesh {
    private final int indicesVBO;
    private final int vaoID;
    private final int verticesVBO;

    public GUIMesh() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        verticesVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);

        indicesVBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(indicesVBO);
        glDeleteBuffers(verticesVBO);
        glDeleteVertexArrays(vaoID);
    }

    public int getIndicesVBO() {
        return indicesVBO;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVerticesVBO() {
        return verticesVBO;
    }
}
