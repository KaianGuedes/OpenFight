package dev.war.openfight.client.renderer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.renderer.texture.TextureAtlas;
import dev.war.openfight.EpicFight;

/**
 * Epic Fight's custom render types for the 26.3 RenderPearl pipeline. The old RenderStateShard/CompositeState
 * machinery no longer exists: a render type is now a {@link RenderPipeline} plus a {@link RenderSetup}.
 * <p>
 * Epic Fight meshes are triangle lists, but they are emitted as degenerate quads (last vertex repeated) so that
 * vanilla QUADS entity render types, and shader loaders such as Iris, handle them natively.
 */
public final class EpicFightRenderTypes {
	private static final Identifier WHITE_TEXTURE = EpicFight.identifier("textures/common/white.png");

	/***********************************************************************************************
	 * Pipeline helpers                                                                            *
	 ***********************************************************************************************/

	private static RenderPipeline.Builder builderFrom(RenderPipeline base) {
		ColorTargetState[] colorTargets = base.getColorTargetStates().toArray(new ColorTargetState[0]);
		com.mojang.renderpearl.api.vertex.VertexFormat[] formats = new com.mojang.renderpearl.api.vertex.VertexFormat[16];

		for (int i = 0; i < base.getVertexFormatBindings().size() && i < formats.length; i++) {
			formats[i] = base.getVertexFormatBindings().get(i);
		}

		ColorTargetState[] padded = Arrays.copyOf(colorTargets, 8);

		RenderPipeline.Snippet snippet = new RenderPipeline.Snippet(
			base.getShaders(),
			Optional.of(base.getShaderDefines()),
			Optional.of(base.getBindGroupLayouts()),
			padded,
			colorTargets.length,
			Optional.ofNullable(base.getDepthStencilState()),
			Optional.of(base.getPolygonMode()),
			Optional.of(base.isCull()),
			formats,
			Optional.of(base.getPrimitiveTopology()),
			base.pushConstantSize()
		);

		return RenderPipeline.builder(snippet);
	}

	/** Creates and registers a pipeline derived from {@code base}. */
	public static RenderPipeline derive(RenderPipeline base, String name, Consumer<RenderPipeline.Builder> modifier) {
		RenderPipeline.Builder builder = builderFrom(base).withLocation(EpicFight.identifier("pipeline/" + name));
		modifier.accept(builder);
		return RenderPipelines.register(builder.build());
	}

	/** Meshes are emitted as degenerate quads (see Mesh), so vanilla pipelines are used unchanged. */
	public static RenderPipeline getTriangulated(RenderPipeline pipeline) {
		return pipeline;
	}

	/***********************************************************************************************
	 * Render type transformation                                                                  *
	 ***********************************************************************************************/

	/** Meshes are emitted as degenerate quads, so the vanilla render type (and Iris' handling of it) is kept. */
	public static RenderType getTriangulated(RenderType renderType) {
		return renderType;
	}

	public static RenderType makeTriangulated(RenderType renderType) {
		return getTriangulated(renderType);
	}

	private static final Map<RenderType, Map<Identifier, RenderType>> TEXTURE_REPLACED_RENDER_TYPES = new ConcurrentHashMap<> ();

	/** Returns the given render type with its main (Sampler0) texture replaced. */
	public static RenderType replaceTexture(Identifier texLocation, RenderType renderType) {
		Map<Identifier, RenderType> byTexture = TEXTURE_REPLACED_RENDER_TYPES.computeIfAbsent(renderType, k -> new ConcurrentHashMap<> ());

		return byTexture.computeIfAbsent(texLocation, tex -> {
			RenderSetup source = renderType.state;

			if (!source.textures.containsKey("Sampler0")) {
				return renderType;
			}

			Map<String, RenderSetup.TextureBinding> textures = new HashMap<> (source.textures);
			textures.put("Sampler0", new RenderSetup.TextureBinding(tex, () -> null));

			return RenderType.create(renderType.name, new RenderSetup(
				source.pipeline,
				source.oitPipelineSet,
				textures,
				source.useLightmap,
				source.useOverlay,
				source.layeringTransform,
				source.textureTransform,
				source.outlineProperty,
				source.outlineTextureName,
				source.affectsCrumbling,
				source.sortOnUpload,
				source.forceSolidModelPhase
			));
		});
	}

	/***********************************************************************************************
	 * Custom pipelines                                                                            *
	 ***********************************************************************************************/

	private static final ColorTargetState WRITE_NONE = new ColorTargetState(Optional.empty(), ColorTargetState.DEFAULT.format(), ColorTargetState.WRITE_NONE);
	private static final DepthStencilState EQUAL_NO_WRITE = new DepthStencilState(CompareOp.EQUAL, false);

	private static RenderPipeline uiColorPipeline;
	private static RenderPipeline uiTexturePipeline;
	private static RenderPipeline guiTrianglePipeline;
	private static RenderPipeline guiTexturedTrianglePipeline;
	private static RenderPipeline overlayModelPipeline;
	private static RenderPipeline entityParticlePipeline;
	private static RenderPipeline itemParticlePipeline;
	private static RenderPipeline afterimageEntityPipeline;
	private static RenderPipeline afterimageItemPipeline;
	private static RenderPipeline stencilEntityPipeline;
	private static RenderPipeline stencilItemPipeline;
	private static RenderPipeline blockHighlightPipeline;

	private static synchronized void ensurePipelines() {
		if (uiColorPipeline != null) {
			return;
		}

		// These pipelines only renamed vanilla pipelines and were invisible to Iris' program map.
		// Keep their vanilla identities so shader loaders can classify them normally.
		uiColorPipeline = RenderPipelines.DEBUG_QUADS;
		uiTexturePipeline = RenderPipelines.EYES;
		guiTrianglePipeline = derive(RenderPipelines.GUI, "gui_triangle", builder -> builder.withPrimitiveTopology(PrimitiveTopology.TRIANGLES));
		guiTexturedTrianglePipeline = derive(RenderPipelines.GUI_TEXTURED, "gui_textured_triangle", builder -> builder.withPrimitiveTopology(PrimitiveTopology.TRIANGLES));
		overlayModelPipeline = derive(RenderPipelines.ENTITY_TRANSLUCENT, "overlay_model", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
		entityParticlePipeline = derive(RenderPipelines.ENTITY_TRANSLUCENT, "entity_particle", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
		itemParticlePipeline = derive(RenderPipelines.ENTITY_TRANSLUCENT, "item_particle", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
		afterimageEntityPipeline = derive(RenderPipelines.TRANSLUCENT_PARTICLE, "entity_afterimage", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
		afterimageItemPipeline = derive(RenderPipelines.TRANSLUCENT_PARTICLE, "item_afterimage", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
		stencilEntityPipeline = derive(RenderPipelines.GUI_TEXTURED, "entity_particle_stencil", builder -> builder.withCull(false).withColorTargetState(WRITE_NONE).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true)));
		stencilItemPipeline = derive(RenderPipelines.GUI_TEXTURED, "item_particle_stencil", builder -> builder.withCull(false).withColorTargetState(WRITE_NONE).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true)));
		blockHighlightPipeline = derive(RenderPipelines.TRANSLUCENT_BLOCK, "block_highlight", builder -> builder.withCull(false).withDepthStencilState(EQUAL_NO_WRITE));
	}

	public static RenderPipeline guiColoredTrianglePipeline() {
		ensurePipelines();
		return guiTrianglePipeline;
	}

	public static RenderPipeline guiTexturedTrianglePipeline() {
		ensurePipelines();
		return guiTexturedTrianglePipeline;
	}

	/** Forces creation of every custom pipeline, which must happen before the pipelines are precompiled. */
	public static void bootstrap() {
		ensurePipelines();
	}

	/***********************************************************************************************
	 * Custom render types                                                                         *
	 ***********************************************************************************************/

	private static RenderType uiColored;
	private static final Function<Identifier, RenderType> ENTITY_UI_TEXTURE = Util.memoize(tex -> {
		ensurePipelines();
		return RenderType.create("epicfight_ui_texture", RenderSetup.builder(uiTexturePipeline).withTexture("Sampler0", tex).createRenderSetup());
	});
	private static final Function<Identifier, RenderType> OVERLAY_MODEL = Util.memoize(tex -> {
		ensurePipelines();
		return RenderType.create("epicfight_overlay_model", RenderSetup.builder(overlayModelPipeline).withTexture("Sampler0", tex).useLightmap().useOverlay().createRenderSetup());
	});
	private static final Function<Identifier, RenderType> ENTITY_PARTICLE = Util.memoize(tex -> {
		ensurePipelines();
		return RenderType.create("epicfight_entity_particle", RenderSetup.builder(entityParticlePipeline).withTexture("Sampler0", tex).useLightmap().useOverlay().createRenderSetup());
	});
	private static final Function<Identifier, RenderType> ENTITY_PARTICLE_STENCIL = Util.memoize(tex -> {
		ensurePipelines();
		return RenderType.create("epicfight_entity_particle_stencil", RenderSetup.builder(stencilEntityPipeline).withTexture("Sampler0", tex).createRenderSetup());
	});
	private static RenderType itemParticle;
	private static RenderType itemParticleStencil;
	private static RenderType entityAfterimageWhite;
	private static RenderType itemAfterimageWhite;
	private static RenderType blockHighlight;
	private static RenderType guiTriangle;

	public static synchronized RenderType entityUIColor() {
		ensurePipelines();

		if (uiColored == null) {
			uiColored = RenderType.create("epicfight_ui_color", RenderSetup.builder(uiColorPipeline).createRenderSetup());
		}

		return uiColored;
	}

	public static RenderType entityUITexture(Identifier texture) {
		return ENTITY_UI_TEXTURE.apply(texture);
	}

	public static RenderType debugCollider() {
		return RenderTypes.lines();
	}

	public static RenderType debugQuads() {
		return RenderTypes.debugQuads();
	}

	public static synchronized RenderType guiTriangle() {
		ensurePipelines();

		if (guiTriangle == null) {
			guiTriangle = RenderType.create("epicfight_gui_triangle", RenderSetup.builder(guiTrianglePipeline).createRenderSetup());
		}

		return guiTriangle;
	}

	public static RenderType overlayModel(Identifier textureLocation) {
		return OVERLAY_MODEL.apply(textureLocation);
	}

	public static RenderType entityAfterimageStencil(Identifier textureLocation) {
		return ENTITY_PARTICLE_STENCIL.apply(textureLocation);
	}

	public static synchronized RenderType itemAfterimageStencil() {
		ensurePipelines();

		if (itemParticleStencil == null) {
			itemParticleStencil = RenderType.create("epicfight_item_particle_stencil", RenderSetup.builder(stencilItemPipeline).withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS).createRenderSetup());
		}

		return itemParticleStencil;
	}

	public static RenderType entityAfterimageTranslucent(Identifier textureLocation) {
		return ENTITY_PARTICLE.apply(textureLocation);
	}

	public static synchronized RenderType itemAfterimageTranslucent() {
		ensurePipelines();

		if (itemParticle == null) {
			itemParticle = RenderType.create("epicfight_item_particle", RenderSetup.builder(itemParticlePipeline).withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS).useLightmap().useOverlay().createRenderSetup());
		}

		return itemParticle;
	}

	public static synchronized RenderType entityAfterimageWhite() {
		ensurePipelines();

		if (entityAfterimageWhite == null) {
			entityAfterimageWhite = RenderType.create("epicfight_entity_afterimage", RenderSetup.builder(afterimageEntityPipeline).withTexture("Sampler0", WHITE_TEXTURE).useLightmap().createRenderSetup());
		}

		return entityAfterimageWhite;
	}

	public static synchronized RenderType itemAfterimageWhite() {
		ensurePipelines();

		if (itemAfterimageWhite == null) {
			itemAfterimageWhite = RenderType.create("epicfight_item_afterimage", RenderSetup.builder(afterimageItemPipeline).withTexture("Sampler0", WHITE_TEXTURE).useLightmap().createRenderSetup());
		}

		return itemAfterimageWhite;
	}

	public static synchronized RenderType blockHighlight() {
		ensurePipelines();

		if (blockHighlight == null) {
			blockHighlight = RenderType.create("epicfight_block_highlight", RenderSetup.builder(blockHighlightPipeline).withTexture("Sampler0", WHITE_TEXTURE).useLightmap().createRenderSetup());
		}

		return blockHighlight;
	}

	/***********************************************************************************************
	 * Per-entity colored glint                                                                    *
	 ***********************************************************************************************/

	private static final Map<Entity, float[]> WORLD_GLINT_COLORS = new HashMap<> ();

	public static void freeUnusedWorldRenderTypes() {
		WORLD_GLINT_COLORS.entrySet().removeIf(entry -> entry.getKey().isRemoved());
	}

	public static void clearWorldRenderTypes() {
		WORLD_GLINT_COLORS.clear();
	}

	/**
	 * Returns the glint render type used for tinted glints. 26.3 has no global shader color, so the tint is
	 * remembered per owner (see {@link #getGlintColor(Entity)}) and applied by the caller as vertex color.
	 */
	public static RenderType coloredGlintWorldRendertype(Entity owner, float r, float g, float b) {
		WORLD_GLINT_COLORS.put(owner, new float[] {r, g, b});
		return getTriangulated(RenderTypes.armorCutoutNoCullGlint(EpicFight.identifier("textures/entity/overlay/glint_white.png")));
	}

	public static RenderType coloredGlintWorldRendertype(Entity owner, int r, int g, int b) {
		return coloredGlintWorldRendertype(owner, r / 255.0F, g / 255.0F, b / 255.0F);
	}

	public static float[] getGlintColor(Entity owner) {
		return WORLD_GLINT_COLORS.getOrDefault(owner, new float[] {1.0F, 1.0F, 1.0F});
	}

	//Util class
	private EpicFightRenderTypes() {
	}
}
