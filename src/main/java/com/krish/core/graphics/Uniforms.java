package com.krish.core.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.*;

import static org.lwjgl.opengl.GL30.*;

public class Uniforms {
    private int programId;
    private Map<String, Integer> uniforms;

    /**
     * Create a new Uniforms manager
     * @param programId The program ID for the shaders
     */
    public Uniforms(int programId) {
        this.programId = programId;
        this.uniforms = new HashMap<>();
    }

    /**
     * Create a new uniform and register it
     * @param uniformName The name of the uniform (case sensitive)
     */
    public void createUniform(String uniformName) {
        //Get the uniform location according to LWJGL
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform [" + uniformName + "] in shader program [" + programId + "]");
        }

        //Store it in a map according to its name
        uniforms.put(uniformName, uniformLocation);
    }

    /**
     * Set the uniform's value
     * @param uniformName The name of the uniform
     * @param value The matrix to set it as
     */
    public void setUniform(String uniformName, Matrix4f value) {
        //Allocate a new memory stack
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //Get location of the uniform
            Integer location = this.uniforms.get(uniformName);
            if (location == null) {
                throw new RuntimeException("Could not find uniform [" + uniformName + "]");
            }

            //Use LWJGL to set the matrix as the uniform's value
            glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }
}
