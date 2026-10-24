package dev.war.openfight.skill.passive;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import dev.war.openfight.api.utils.side.ClientOnly;
import dev.war.openfight.client.gui.BattleModeGui;
import dev.war.openfight.skill.Skill;
import dev.war.openfight.skill.SkillBuilder;
import dev.war.openfight.skill.SkillCategories;
import dev.war.openfight.skill.SkillContainer;
import dev.war.openfight.world.capabilities.entitypatch.player.PlayerPatch;
import dev.war.openfight.world.capabilities.item.CapabilityItem;

import java.util.Collections;
import java.util.function.Function;

public abstract class PassiveSkill extends Skill {
	public static SkillBuilder<?> createPassiveBuilder(Function<SkillBuilder<?>, ? extends PassiveSkill> constructor) {
		return new SkillBuilder<>(constructor).setCategory(SkillCategories.PASSIVE).setResource(Resource.NONE);
	}
	
	@SuppressWarnings("rawtypes")
	public PassiveSkill(SkillBuilder<? extends SkillBuilder> builder) {
		super(builder);
	}
	
	@Override @ClientOnly
	public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphicsExtractor guiGraphics, float x, float y, float partialTick) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.getSkillTexture(), (int)x, (int)y, (float)(0), (float)(0), 24, 24, 1, 1, 1, 1);
		String remainTime = String.format("%.0f", container.getMaxResource() - container.getResource());
		guiGraphics.text(gui.getFont(), remainTime, (int)((int)(x + 12 - 4 * remainTime.length())), (int)((int)((y+6))), 16777215, true);
	}

	@Override
	public Component getTranslatedTooltip(ItemStack itemStack, CapabilityItem itemCap, PlayerPatch<?> playerPatch) {
		return Component.translatable(this.getTranslationKey() + ".tooltip", getTooltipArgsOfScreen(Lists.newArrayList()).toArray(new Object[0]));
	}
}