#version 400 core

in vec2 inTextureCoord;

out vec4 fragColor;

//uniform sampler2D textureSampler;

void main() {
    fragColor = vec4(1, 1, 1, 1);//texture(textureSampler, inTextureCoord);
}