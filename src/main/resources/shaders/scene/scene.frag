#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;
const float SPECULAR_POWER = 10;

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextureCoordinate;

out vec4 fragmentColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
};

struct AmbientLight {
    vec3 color;
    float factor;
};

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation att;
};

struct DirectionLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct SpotLight {
    PointLight pl;
    vec3 conedirection;
    float cutoff;
};

struct Fog {
    int active;
    vec3 color;
    float density;
};

uniform sampler2D textureSampler;
uniform Material material;
uniform AmbientLight ambientLight;
uniform DirectionLight directionLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform Fog fog;

vec4 calculateAmbient(AmbientLight ambientLight, vec4 ambient) {
    return vec4(ambientLight.factor * ambientLight.color, 1) * ambient;
}

vec4 calculateLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) {
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    vec4 diffuseColor = diffuse * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));

    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    vec4 specularColor = specular * vec4(lightColor, 1.0) * lightIntensity * specularFactor * material.reflectance;

    return (specularColor + diffuseColor);
}

vec4 calculatePointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calculateLightColor(diffuse, specular, light.color, light.intensity, position, toLightDirection, normal);

    float distance = length(lightDirection);
    float attentuationInverse = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return lightColor / attentuationInverse;
}

vec4 calculateDirectionLight(vec4 diffuse, vec4 specular, DirectionLight light, vec3 position, vec3 normal) {
    return calculateLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calculateSpotLight(vec4 diffuse, vec4 specular, SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.pl.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec3 fromLightDirection = -toLightDirection;
    float spot_alfa = dot(fromLightDirection, normalize(light.conedirection));

    vec4 color = vec4(0, 0, 0, 0);

    if (spot_alfa > light.cutoff) {
        color = calculatePointLight(diffuse, specular, light.pl, position, normal);
        color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }

    return color;
}

vec4 calculateFog(vec3 position, vec4 color, Fog inputFog, vec3 ambient, DirectionLight direction) {
    vec3 fogColor = inputFog.color * (ambient + direction.color * direction.intensity);
    float distance = length(position);
    float fogFactor = 1.0 / exp((distance * inputFog.density) * (distance * inputFog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

void main()
{
    vec4 textureColor = texture(textureSampler, outTextureCoordinate) + material.diffuse;
    vec4 ambientColor = calculateAmbient(ambientLight, textureColor + material.ambient);
    vec4 diffuseColor = textureColor + material.diffuse;
    vec4 specularColor = textureColor + material.specular;

    vec4 diffuseSpecularComposition = calculateDirectionLight(diffuseColor, specularColor, directionLight, outPosition, outNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComposition += calculatePointLight(diffuseColor, specularColor, pointLights[i], outPosition, outNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pl.intensity > 0) {
            diffuseSpecularComposition += calculateSpotLight(diffuseColor, specularColor, spotLights[i], outPosition, outNormal);
        }
    }

    fragmentColor = ambientColor + diffuseSpecularComposition;

    if (fog.active == 1) {
        fragmentColor = calculateFog(outPosition, fragmentColor, fog, ambientLight.color, directionLight);
    }
}