package dev.war.openfight.client.renderer.patched.item;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.item.properties.numeric.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import dev.war.openfight.world.item.SkillBookItem;
public final class EpicFightItemProperties {
 public static void registerItemProperties(){ RangeSelectItemModelProperties.ID_MAPPER.put(dev.war.openfight.main.EpicFightMod.identifier("skill"),SkillCategoryProperty.CODEC); }
 public record SkillCategoryProperty() implements RangeSelectItemModelProperty {
  public static final MapCodec<SkillCategoryProperty> CODEC=MapCodec.unit(new SkillCategoryProperty());
  public float get(ItemStack stack,ClientLevel level,ItemOwner owner,int seed){return SkillBookItem.getContainSkill(stack).map(s->(float)s.value().getCategory().universalOrdinal()).orElse(-1F);}
  public MapCodec<SkillCategoryProperty> type(){return CODEC;}
 }
}
