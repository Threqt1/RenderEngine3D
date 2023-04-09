#version 330

layout (location = 0) in vec2 inPosition;
layout (location = 1) in vec2 inTextureCoordinates;
layout (location = 2) in vec4 inColor;

out vec2 fragmentTextureCoordinates;
out vec4 fragmentColor;

uniform vec2 scale;

void main() {
    fragmentTextureCoordinates = inTextureCoordinates;
    fragmentColor = inColor;
    gl_Position = vec4(inPosition * scale + vec2(-1.0, 1.0), 0.0, 1.0);
}