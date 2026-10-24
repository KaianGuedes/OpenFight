package dev.war.openfight.client.renderer;

/**
 * 26.3 replaced ShaderInstance with RenderPearl pipelines (see {@link EpicFightRenderTypes}); Epic Fight's own
 * "solid_model" position/color/normal program is now covered by the vanilla {@code RenderTypes.lines()} /
 * debug pipelines, so no custom shader needs registering.
 */
public final class EpicFightShaders {
	private EpicFightShaders() {}
}
