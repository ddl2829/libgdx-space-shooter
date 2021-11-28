#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;
uniform float iTime;

void main()
{
	vec2 uv = v_texCoords;

	float w = (0.5 - (uv.x)) * (v_texCoords.x / v_texCoords.y);
    float h = 0.5 - uv.y;
	float distanceFromCenter = sqrt(w * w + h * h);

	float sinArg = distanceFromCenter * 10.0 - iTime * 10.0;
	float slope = cos(sinArg) ;
	vec4 color = texture2D(u_texture, uv + normalize(vec2(w, h)) * slope * 0.05);

	gl_FragColor = color;
}