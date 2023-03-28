package com.krish.core;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;
    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programID = glCreateProgram();
        if (programID == 0)
            throw new Exception("Couldn't create shader");
        this.uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Uniform " + uniformName + " not found");
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String name, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(name), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = glCreateShader(shaderType);
        if (shaderID == 0)
            throw new Exception("Error creating shader");

        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
            throw new Exception("Error creating shader");

        glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception {
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0)
            throw new Exception("Error creating shader");

        if (vertexShaderID != 0)
            glDetachShader(programID, vertexShaderID);

        if (fragmentShaderID != 0)
            glDetachShader(programID, fragmentShaderID);

        glValidateProgram(programID);

        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            throw new Exception("Couldn't validate shader");
        }
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }
}
