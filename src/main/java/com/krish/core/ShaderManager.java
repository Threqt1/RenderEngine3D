package com.krish.core;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programID = GL20.glCreateProgram();
        if (programID == 0)
            throw new Exception("Couldn't create shader");

        uniforms = new HashMap<>();
    }

    public void createUniform(String name) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, name);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform " + name);
        }
        uniforms.put(name, uniformLocation);
    }

    public void setUniform(String name, Matrix4f value) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(name), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String name, int value) {
        GL20.glUniform1i(uniforms.get(name), value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);
        if (shaderID == 0)
            throw new Exception("Error creating shader");

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error creating shader");

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception {
        GL20.glLinkProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
            throw new Exception("Error creating shader");

        if (vertexShaderID != 0)
            GL20.glDetachShader(programID, vertexShaderID);

        if (fragmentShaderID != 0)
            GL20.glDetachShader(programID, fragmentShaderID);

        GL20.glValidateProgram(programID);

        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
            throw new Exception("Couldn't validate shader");
        }
    }

    public void bind() {
        GL20.glUseProgram(programID);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            GL20.glDeleteProgram(programID);
        }
    }
}
