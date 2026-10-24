package dev.war.openfight.client.renderer.patched.entity;

import net.minecraft.client.model.monster.zombie.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.client.mesh.VillagerMesh;
import dev.war.openfight.client.renderer.patched.layer.PatchedVillagerProfessionLayer;
import dev.war.openfight.world.capabilities.entitypatch.MobPatch;

public class PZombieVillagerRenderer extends PHumanoidRenderer<ZombieVillager, MobPatch<ZombieVillager>, ZombieVillagerModel<?>, ZombieVillagerRenderer, VillagerMesh> {
	public PZombieVillagerRenderer(EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(Meshes.VILLAGER_ZOMBIE, context, entityType);
		
		this.addPatchedLayer(VillagerProfessionLayer.class, new PatchedVillagerProfessionLayer());
	}
}