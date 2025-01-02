#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 iResolution;

void main()
{
    vec2 uv = v_texCoords;
    vec3 col = texture2D(u_texture, uv).rgb;
    float dist = distance(uv, vec2(0.5)),
    falloff = .4,
    amount = .4;
    col *= smoothstep(0.8, falloff * 0.8, dist * (amount + falloff));
    gl_FragColor = vec4(col, 1.0);
}