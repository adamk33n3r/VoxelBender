/*#version 330
in vec2 texCoord;
out vec4 outColor;
 
uniform sampler2D theTexture;
 
void main()
{
  vec4 texel = texture(theTexture, texCoord);
  if(texel.a < 0.5)
    discard;
  outColor = texel;
}*/

