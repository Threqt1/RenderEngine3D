#version 330

in vec2 fragmentTextureCoordinates;
in vec4 fragmentColor;

uniform sampler2D textureSampler;

out vec4 outputColor;

void main() {
    outputColor = fragmentColor * texture(textureSampler, fragmentTextureCoordinates);
}