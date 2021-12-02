#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 texSize;

void main()
{
    	float width = 2.0;
        float w = width / texSize.x;
        float h = width / texSize.y;
        vec4 color = texture2D(u_texture, v_texCoords);

        float alpha = 4.0 * color.a;
        alpha -= texture2D(u_texture, v_texCoords + vec2( w, 0.0)).a;
        alpha -= texture2D(u_texture, v_texCoords + vec2(-w, 0.0)).a;
        alpha -= texture2D(u_texture, v_texCoords + vec2( 0.0, h)).a;
        alpha -= texture2D(u_texture, v_texCoords + vec2( 0.0,-h)).a;

        gl_FragColor = vec4(1,1,1,alpha);
        //vec4 final_color = mix(color, vec4(0,0,0,0), clamp(alpha, 0.0, 1.0));
        //gl_FragColor = vec4(final_color.rgb, clamp(abs(alpha) + color.a, 0.0, 1.0) );

}