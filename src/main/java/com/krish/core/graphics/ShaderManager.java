package com.krish.core.graphics;

import com.krish.core.utils.Utils;
import org.lwjgl.opengl.GL30;

import java.util.*;
import static org.lwjgl.opengl.GL30.*;

public class ShaderManager {
    private final int programID;

    /**
     * Create a new shader manager
     * @param shaderModuleDataList List of raw shader file data
     */
    public ShaderManager(List<ShaderModuleData> shaderModuleDataList) {
        //Create a new shader program
        programID = glCreateProgram();
        if (programID == 0) {
            throw new RuntimeException("Could not create Shader");
        }

        //Parse all the shader modules
        List<Integer> shaderModules = new ArrayList<>();
        shaderModuleDataList.forEach(s -> shaderModules.add(createShader(Utils.readFile(s.shaderFile), s.shaderType)));

        //Link all the shader modules to the program
        link(shaderModules);
    }

    protected int createShader(String shaderCode, int shaderType) {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new RuntimeException("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programID, shaderId);

        return shaderId;
    }

    public void bind() { glUseProgram(programID); }

    /**
     * Link all the parsed shader modules to the program
     * @param shaderModules All the shader modules
     */
    private void link(List<Integer> shaderModules) {
        //Link to the program
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programID, 1024));
        }

        shaderModules.forEach(s -> glDetachShader(programID, s));
        shaderModules.forEach(GL30::glDeleteShader);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            glDeleteProgram(programID);
        }
    }

    public void unbind() {
        glUseProgram(0);
    }

    public record ShaderModuleData(String shaderFile, int shaderType) {

    }

    public int getProgramID() {
        return programID;
    }
}
