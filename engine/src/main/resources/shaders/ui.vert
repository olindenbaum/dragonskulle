#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inNormal;
layout(location = 2) in vec3 inColor;
layout(location = 3) in vec2 inUV;

layout(location = 4) in mat4 model;
layout(location = 8) in vec4 instColor; 

layout(location = 0) out vec4 fragColor;
layout(location = 1) out vec2 fragUV;

void main() {
	gl_Position = model * vec4(inPosition, 1.0);
	fragColor = vec4(inColor, 1.0) * instColor;
	fragUV = inUV;
}
