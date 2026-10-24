import re
base='src/main/java/dev/war/openfight/'
def edit(p,f):
    raw=open(base+p,'rb').read().decode('utf-8');crlf='\r\n' in raw;s=raw.replace('\r\n','\n')
    s=f(s)
    if crlf:s=s.replace('\n','\r\n')
    open(base+p,'wb').write(s.encode('utf-8'))
def winnate(s):
    a=s.index('		RenderSystem.enableBlend();\n		RenderSystem.blendFunc(SourceFactor')
    b=s.index('        if (container.isActivated() && (container.getSkill().getActivateType() == ActivateType.DURATION')
    new='''		int gaugeColor;

		if (canUse) {
			gaugeColor = container.getStack() > 0 ? ARGB.colorFromFloat(0.8F, 0.0F, 0.64F, 0.72F) : ARGB.colorFromFloat(0.6F, 0.0F, 0.5F, 0.5F);
		} else {
			gaugeColor = ARGB.colorFromFloat(0.6F, 0.5F, 0.5F, 0.5F);
		}

		// Remaining (cooling down) part of the icon
		java.util.List<float[]> fan = new java.util.ArrayList<> ();

		for (int j = 0; j < vertexNum; j++) {
			fan.add(new float[] {x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, CLOCK_POS[j].x, CLOCK_POS[j].y});
		}

		fan.add(new float[] {lastVertexX, lastVertexY, lastTexX, lastTexY});
		drawClockFan(guiGraphics, container.getSkill().getSkillTexture(), fan, gaugeColor);

		// Already charged part of the icon (the reverse fan)
		fan = new java.util.ArrayList<> ();

		for (int j = 0; j < 2; j++) {
			fan.add(new float[] {x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, CLOCK_POS[j].x, CLOCK_POS[j].y});
		}

		for (int j = CLOCK_POS.length - 1; j >= vertexNum; j--) {
			fan.add(new float[] {x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, CLOCK_POS[j].x, CLOCK_POS[j].y});
		}

		fan.add(new float[] {lastVertexX, lastVertexY, lastTexX, lastTexY});
		drawClockFan(guiGraphics, container.getSkill().getSkillTexture(), fan, canUse ? ARGB.colorFromFloat(1.0F, 0.08F, 0.79F, 0.95F) : -1);

'''
    s=s[:a]+new+s[b:]
    s=s.replace('		RenderSystem.disableBlend();\n	}\n}','''	}

	private static void drawClockFan(GuiGraphicsExtractor guiGraphics, Identifier texture, java.util.List<float[]> vertices, int color) {
		float[] xy = new float[vertices.size() * 2];
		float[] uv = new float[vertices.size() * 2];

		for (int i = 0; i < vertices.size(); i++) {
			xy[i * 2] = vertices.get(i)[0];
			xy[i * 2 + 1] = vertices.get(i)[1];
			uv[i * 2] = vertices.get(i)[2];
			uv[i * 2 + 1] = vertices.get(i)[3];
		}

		dev.war.openfight.client.gui.GuiShapes.texturedFan(guiGraphics, texture, xy, uv, color);
	}
}''')
    s=re.sub(r'import com\.mojang\.blaze3d\.platform\.GlStateManager\.\w+;\n','',s)
    for imp in ['net.minecraft.util.ARGB','net.minecraft.resources.Identifier']:
        if 'import '+imp+';' not in s: s=s.replace('\nimport ','\nimport '+imp+';\nimport ',1)
    return s
edit('skill/weaponinnate/WeaponInnateSkill.java',winnate)
def fan(s):
    s=re.sub(r'(\t\t|        )VertexConsumer vertexconsumer = guiGraphics\.bufferSource\(\)\.getBuffer\(EpicFightRenderTypes\.guiTriangle\(\)\);\n(?:.*\n)*?\1guiGraphics\.flush\(\);',
      lambda m: m.group(1)+'dev.war.openfight.client.gui.GuiShapes.triangles(guiGraphics, new float[] {\n'+m.group(1)+'	centerX, minY, minX, centerY, centerX, maxY,\n'+m.group(1)+'	centerX, maxY, maxX, centerY, centerX, minY\n'+m.group(1)+'}, ARGB.colorFromFloat(a, r, g, b));',s)
    s=re.sub(r'(?:\t\t|        )Matrix4f matrix4f = guiGraphics\.pose\(\)\.last\(\)\.pose\(\);\n','',s)
    return s
edit('client/gui/widgets/ColorSlider.java',fan)
edit('client/gui/widgets/ColorDeterminator.java',fan)
print('ok')
