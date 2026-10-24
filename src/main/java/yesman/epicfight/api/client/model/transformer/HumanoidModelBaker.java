package yesman.epicfight.api.client.model.transformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.SharedConstants;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.main.EpicFightMod;

public class HumanoidModelBaker {
	static final Map<Identifier, SkinnedMesh> BAKED_MODELS = new HashMap<> ();
	static final List<HumanoidModelTransformer> MODEL_TRANSFORMERS = new ArrayList<> ();
	
	static final Set<net.minecraft.world.item.Item> EXCEPTIONAL_MODELS = new HashSet<> ();
	static final Set<ModelPart> MODEL_PARTS = new HashSet<> ();
	
	public static final HumanoidModelTransformer VANILLA_TRANSFORMER = new VanillaModelTransformer();
	
	public interface ModelProvider {
		public Model get(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default);
	}
	
	public static void registerNewTransformer(HumanoidModelTransformer transformer) {
		MODEL_TRANSFORMERS.add(transformer);
	}
	
	public static void exportModels(File resourcePackDirectory) throws IOException {
		File zipFile = new File(resourcePackDirectory, "epicfight_custom_armors.zip");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
		for (Map.Entry<Identifier, SkinnedMesh> entry : BAKED_MODELS.entrySet()) {
			ZipEntry zipEntry = new ZipEntry(String.format("assets/%s/animmodels/armor/%s.json", entry.getKey().getNamespace(), entry.getKey().getPath()));
			Gson gson = new GsonBuilder().create();
			out.putNextEntry(zipEntry);
			out.write(gson.toJson(entry.getValue().toJsonObject()).getBytes());
			out.closeEntry();
			EpicFightMod.LOGGER.info("Exported custom armor model : " + entry.getKey());
		}
		
		ZipEntry zipEntry = new ZipEntry("pack.mcmeta");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject root = new JsonObject();
		JsonObject pack = new JsonObject();
		pack.addProperty("description", "epicfight_custom_armor_models");
		pack.addProperty("pack_format", SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).major());
		root.add("pack", pack);
		out.putNextEntry(zipEntry);
		out.write(gson.toJson(root).getBytes());
		out.closeEntry();
		out.close();
	}
	
	/**
	 * Returns the armor mesh for an item without a bundled model. 26.3/Fabric has no replaced client armor models
	 * (NeoForge's IClientItemExtensions#getHumanoidArmorModel), so the entity mesh's own armor model is always used.
	 */
	public static SkinnedMesh bakeArmor(LivingEntity entityLiving, ItemStack itemstack, EquipmentSlot slot, HumanoidMesh entityMesh) {
		SkinnedMesh skinnedArmorModel = entityMesh.getHumanoidArmorModel(slot).get();
		BAKED_MODELS.put(BuiltInRegistries.ITEM.getKey(itemstack.getItem()), skinnedArmorModel);
		return skinnedArmorModel;
	}
}
