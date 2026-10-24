package yesman.epicfight.client.renderer.patched.layer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.monster.zombie.ZombieVillagerModel;
import yesman.epicfight.client.renderer.compat.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.VillagerMesh;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class PatchedVillagerProfessionLayer extends ModelRenderLayer<ZombieVillager, MobPatch<ZombieVillager>, ZombieVillagerModel<?>, VillagerProfessionLayer<?, ?>, VillagerMesh> {
	public PatchedVillagerProfessionLayer() {
		super(Meshes.VILLAGER_ZOMBIE);
	}
	
	@Override
	protected void renderLayer(MobPatch<ZombieVillager> entitypatch, ZombieVillager entityliving, VillagerProfessionLayer<?, ?> vanillaLayer, PoseStack postStack, MultiBufferSource buffer, int packedLight,
			OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
		
		if (!entityliving.isInvisible()) {
			VillagerData villagerdata = ((VillagerDataHolder)entitypatch.getOriginal()).getVillagerData();

			// 26.3 moved hat visibility into the villager model/state; the vanilla "hat" metadata is not consulted any more.
			if (!entityliving.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
				this.mesh.get().head.setHidden(true);
				this.mesh.get().hat.setHidden(true);
			}

			if (!entitypatch.getOriginal().getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
				this.mesh.get().jacket.setHidden(true);
			}

			Identifier typeKey = BuiltInRegistries.VILLAGER_TYPE.getKey(villagerdata.type().value());
			this.mesh.get().draw(postStack, buffer, RenderTypes.entityCutout(typeKey.withPath(path -> "textures/entity/zombie_villager/type/" + path + ".png")),
					packedLight, 1.0F, 1.0F, 1.0F, 1.0F, LivingEntityRenderer.getOverlayCoords(yesman.epicfight.client.renderer.compat.EpicRenderContext.state() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState lstate ? lstate : null, 0.0F), entitypatch.getArmature(), poses);

			if (!villagerdata.profession().is(VillagerProfession.NONE)) {
				Identifier professionKey = villagerdata.profession().unwrapKey().orElseThrow().identifier();
				this.mesh.get().draw(postStack, buffer, RenderTypes.entityCutout(professionKey.withPath(path -> "textures/entity/zombie_villager/profession/" + path + ".png")),
						packedLight, 1.0F, 1.0F, 1.0F, 1.0F, LivingEntityRenderer.getOverlayCoords(yesman.epicfight.client.renderer.compat.EpicRenderContext.state() instanceof net.minecraft.client.renderer.entity.state.LivingEntityRenderState lstate ? lstate : null, 0.0F), entitypatch.getArmature(), poses);
			}
		}
	}
}