package dev.war.openfight.client.gui.datapack.screen;

import dev.war.openfight.platform.fabric.compat.TagCompat;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.RenderPipelines;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import dev.war.openfight.api.animation.types.AttackAnimation;
import dev.war.openfight.api.animation.types.StaticAnimation;
import dev.war.openfight.api.asset.AssetAccessor;
import dev.war.openfight.api.client.model.Meshes;
import dev.war.openfight.api.collider.Collider;
import dev.war.openfight.api.utils.ParseUtil;
import dev.war.openfight.client.gui.datapack.widgets.Grid;
import dev.war.openfight.client.gui.datapack.widgets.Grid.GridBuilder.RowEditButton;
import dev.war.openfight.client.gui.datapack.widgets.InputComponentList;
import dev.war.openfight.client.gui.datapack.widgets.ModelPreviewer;
import dev.war.openfight.client.gui.datapack.widgets.PopupBox;
import dev.war.openfight.client.gui.datapack.widgets.ResizableComponent.HorizontalSizing;
import dev.war.openfight.client.gui.datapack.widgets.ResizableComponent.VerticalSizing;
import dev.war.openfight.client.gui.datapack.widgets.Static;
import dev.war.openfight.gameasset.Armatures;
import dev.war.openfight.gameasset.ColliderPreset;
import dev.war.openfight.world.capabilities.item.CapabilityItem.Styles;
import dev.war.openfight.world.capabilities.item.Style;

public class WeaponComboScreen extends Screen {
	private final Screen parentScreen;
	private Grid stylesGrid;
	private Grid comboGrid;
	private PopupBox.AnimationPopupBox dashAttackPopupbox;
	private PopupBox.AnimationPopupBox airSlashPopupbox;
	private final InputComponentList<ListTag> inputComponentsList;
	private final ModelPreviewer modelPreviewer;
	private final List<PackEntry<String, ListTag>> styles = Lists.newArrayList();
	private final CompoundTag rootTag;
	
	public WeaponComboScreen(Screen parentScreen, CompoundTag rootTag) {
		super(Component.translatable("datapack_edit.weapon_type.combos"));
		
		this.parentScreen = parentScreen;
		
		this.inputComponentsList = new InputComponentList<>(this, 0, 0, 0, 20) {
			@Override
			public void importTag(ListTag tag) {
				this.setComponentsActive(true);
				
				boolean isMount = WeaponComboScreen.this.stylesGrid.getValue(WeaponComboScreen.this.stylesGrid.getRowposition(), "style") == Styles.MOUNT;
				
				if (!isMount) {
					while (tag.size() < 2) {
						tag.add(StringTag.valueOf(""));
					}
				}
				
				Grid.PackImporter packImporter = new Grid.PackImporter();
				int tagSize = tag.size();
				
				if (isMount) {
					for (int i = 0; i < tagSize; i++) {
						packImporter.newRow().newValue("combo_animation", DatapackEditScreen.animationByKey(tag.getStringOr(i, "")));
					}
					
					this.setDataBindingComponenets(new Object[] {packImporter});
				} else {
					for (int i = 0; i < tagSize - 2; i++) {
						packImporter.newRow().newValue("combo_animation", DatapackEditScreen.animationByKey(tag.getStringOr(i, "")));
					}
					
					this.setDataBindingComponenets(new Object[] {
						packImporter,
						DatapackEditScreen.animationByKey(tag.getStringOr(tagSize - 2, "")),
						DatapackEditScreen.animationByKey(tag.getStringOr(tagSize - 1, ""))
					});
				}
			}
		};
		
		this.inputComponentsList.setX(parentScreen.width - 205);
		
		this.rootTag = ParseUtil.getOrDefaultTag(rootTag, "combos", new CompoundTag());
		
		this.stylesGrid = Grid.builder(this, Minecraft.getInstance())
								.xy1(12, 60)
								.xy2(85, 50)
								.horizontalSizing(HorizontalSizing.LEFT_WIDTH)
								.verticalSizing(VerticalSizing.TOP_BOTTOM)
								.rowHeight(21)
								.rowEditable(RowEditButton.ADD_REMOVE)
								.rowpositionChanged((rowposition, values) -> {
									this.inputComponentsList.importTag(this.styles.get(rowposition).getValue());
									this.reloadAnimationPlayer();
									
									if (values.get("style") == Styles.MOUNT) {
										this.dashAttackPopupbox._setValue(null);
										this.airSlashPopupbox._setValue(null);
										this.dashAttackPopupbox._setActive(false);
										this.airSlashPopupbox._setActive(false);
									}
								})
								.addColumn(Grid.combo("style", Style.ENUM_MANAGER.universalValues())
												.valueChanged((event) -> {
													if (event.prevValue == Styles.MOUNT) {
														this.dashAttackPopupbox._setActive(true);
														this.airSlashPopupbox._setActive(true);
														
														ListTag combosList = this.styles.get(event.rowposition).getValue();
														combosList.add(StringTag.valueOf(""));
														combosList.add(StringTag.valueOf(""));
														
													} else if (event.postValue == Styles.MOUNT) {
														this.dashAttackPopupbox._setValue(null);
														this.airSlashPopupbox._setValue(null);
														this.dashAttackPopupbox._setActive(false);
														this.airSlashPopupbox._setActive(false);
														
														ListTag combosList = this.styles.get(event.rowposition).getValue();
														combosList.remove(combosList.size() - 1);
														combosList.remove(combosList.size() - 1);
													}
													
													this.styles.get(event.rowposition).setPackKey(ParseUtil.nullParam(event.postValue).toLowerCase(Locale.ROOT));
												})
												.defaultVal(Styles.ONE_HAND))
								.pressAdd((grid, button) -> {
									this.styles.add(PackEntry.of("", ListTag::new));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "style");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.remove(removedRow));
									
									if (grid.children().size() == 0) {
										this.inputComponentsList.setComponentsActive(false);
									}
								})
								.build();
		
		this.comboGrid = Grid.builder(this, Minecraft.getInstance())
								.xy1(177, 40)
								.xy2(14, 80)
								.horizontalSizing(HorizontalSizing.WIDTH_RIGHT)
								.rowHeight(21)
								.rowEditable(RowEditButton.ADD_REMOVE)
								.addColumn(Grid.popup("combo_animation", PopupBox.AnimationPopupBox::new)
												.filter((accessor) -> accessor.checkType(AttackAnimation.class))
												.editWidgetCreated((popupBox) -> popupBox.setModel(Armatures.BIPED, Meshes.BIPED))
												.toDisplayText((animation) -> animation == null ? "" : animation.registryName().toString())
												.valueChanged((event) -> {
													ListTag animationList = this.styles.get(this.stylesGrid.getRowposition()).getValue();
													
													animationList.remove(event.rowposition);
													animationList.add(event.rowposition, StringTag.valueOf(ParseUtil.nullOrToString(event.postValue, (animation) -> animation.registryName().toString())));
													
													if (event.postValue != null) {
														this.reloadAnimationPlayer();
													}
												})
												.width(150))
								.pressAdd((grid, button) -> {
									this.styles.get(this.stylesGrid.getRowposition()).getValue().add(grid.children().size(), StringTag.valueOf(""));
									int rowposition = grid.addRow();
									grid.setGridFocus(rowposition, "combo_animation");
								})
								.pressRemove((grid, button) -> {
									grid.removeRow((removedRow) -> this.styles.get(this.stylesGrid.getRowposition()).getValue().remove(removedRow));
									this.reloadAnimationPlayer();
								})
								.build();
		
		this.modelPreviewer = new ModelPreviewer(110, 200, 45, 49, HorizontalSizing.LEFT_RIGHT, VerticalSizing.TOP_BOTTOM, Armatures.BIPED, Meshes.BIPED);
		
		CompoundTag colliderTag = rootTag.getCompoundOrEmpty("collider");
		
		try {
			Collider collider = ColliderPreset.deserializeSimpleCollider(colliderTag);
			this.modelPreviewer.setCollider(collider);
		} catch (IllegalArgumentException e) {
		}
		
		this.dashAttackPopupbox = new PopupBox.AnimationPopupBox(this, this.font, 110, 15, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.dash_attak"),
				(pair) -> {
					if (pair.getSecond() != null) {
						ListTag listTag = this.styles.get(this.stylesGrid.getRowposition()).getValue();
						listTag.remove(listTag.size() - 2);
						listTag.add(listTag.size() - 1, StringTag.valueOf(ParseUtil.nullOrToString(pair.getSecond(), (animation$2) -> animation$2.registryName().toString())));
						this.reloadAnimationPlayer();
					}
				});
		
		this.airSlashPopupbox = new PopupBox.AnimationPopupBox(this, this.font, 110, 15, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, Component.translatable("datapack_edit.weapon_type.styles.air_slash"),
				(pair) -> {
					if (pair.getSecond() != null) {
						ListTag listTag = this.styles.get(this.stylesGrid.getRowposition()).getValue();
						listTag.remove(listTag.size() - 1);
						listTag.add(listTag.size(), StringTag.valueOf(ParseUtil.nullOrToString(pair.getSecond(), (animation$2) -> animation$2.registryName().toString())));
						this.reloadAnimationPlayer();
					}
				});
		
		this.dashAttackPopupbox.setModel(Armatures.BIPED, Meshes.BIPED);
		this.airSlashPopupbox.setModel(Armatures.BIPED, Meshes.BIPED);
		
		this.dashAttackPopupbox.applyFilter(animation -> animation.checkType(AttackAnimation.class));
		this.airSlashPopupbox.applyFilter(animation -> animation.checkType(AttackAnimation.class));
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this, 80, 110, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, "datapack_edit.weapon_type.combos.combo_attacks"));
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(this.comboGrid);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this, 60, 130, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, "datapack_edit.weapon_type.combos.dash_attak"));
		this.inputComponentsList.addComponentCurrentRow(this.dashAttackPopupbox);
		
		this.inputComponentsList.newRow();
		this.inputComponentsList.addComponentCurrentRow(new Static(this, 60, 130, -1, 15, HorizontalSizing.WIDTH_RIGHT, null, "datapack_edit.weapon_type.combos.air_slash"));
		this.inputComponentsList.addComponentCurrentRow(this.airSlashPopupbox);
		
		this.inputComponentsList.setComponentsActive(false);
		
		Grid.PackImporter packImporter = new Grid.PackImporter();
		
		for (String style : this.rootTag.keySet()) {
			this.styles.add(PackEntry.of(style, () -> this.rootTag.getListOrEmpty(style)));
			
			packImporter.newRow();
			packImporter.newValue("style", Style.ENUM_MANAGER.get(style));
		}
		
		this.stylesGrid._setValue(packImporter);
	}
	
	@Override
	protected void init() {
		ScreenRectangle screenRectangle = this.getRectangle();
		
		this.stylesGrid.resize(this.getRectangle());
		
		this.inputComponentsList.updateSizeAndPosition(205, screenRectangle.height() - 79 - screenRectangle.top(), screenRectangle.top() + 34);
		this.inputComponentsList.setX(this.width - 205);
		this.modelPreviewer.resize(screenRectangle);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
			Set<String> styles = Sets.newHashSet();
			
			for (PackEntry<String, ListTag> entry : this.styles) {
				if (styles.contains(entry.getKey())) {
					this.minecraft.gui.setScreen(new MessageScreen<>("Save Failed", "Unable to save because of duplicated style: " + entry.getKey(), this, (button2) -> {
						this.minecraft.gui.setScreen(this);
					}, 180, 90));
					return;
				}
				styles.add(entry.getKey());
			}
			
			boolean allTagsNormal = true;
			String animation = null;
			String style = null;
			
			exit:
			for (PackEntry<String, ListTag> entry : this.styles) {
				for (Tag tag : entry.getValue()) {
					if (DatapackEditScreen.animationByKey(TagCompat.getAsString(tag)) == null) {
						animation = TagCompat.getAsString(tag);
						style = entry.getKey();
						allTagsNormal = false;
						break exit;
					}
				}
			}
			
			if (!allTagsNormal) {
				this.minecraft.gui.setScreen(new MessageScreen<>("Save Failed", "No animation named: " + animation + " in " + style, this, (button2) -> {
					this.minecraft.gui.setScreen(this);
				}, 180, 90));
			} else {
				this.rootTag.tags.clear();
				
				for (PackEntry<String, ListTag> entry : this.styles) {
					this.rootTag.put(entry.getKey(), entry.getValue());
				}
				
				this.onClose();
			}
		}).pos(this.width / 2 - 162, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.gui.setScreen(new MessageScreen<>("", "Do you want to quit without saving changes?", this,
														(button2) -> {
															this.onClose();
														}, (button2) -> {
															this.minecraft.gui.setScreen(this);
														}, 180, 70));
		}).pos(this.width / 2 + 2, this.height - 32).size(160, 21).build());
		
		this.addRenderableWidget(new Static(this, 12, 60, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("datapack_edit.styles"), Component.translatable("datapack_edit.styles.tooltip.mandatory")));
		this.addRenderableWidget(this.stylesGrid);
		this.addRenderableWidget(this.modelPreviewer);
		this.addRenderableWidget(this.inputComponentsList);
	}
	
	@Override
	public void onClose() {
		this.minecraft.gui.setScreen(this.parentScreen);
		this.modelPreviewer.onDestroy();
	}
	
	@Override
	public void tick() {
		this.modelPreviewer._tick();
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
double mouseX = event.x(); double mouseY = event.y(); int button = event.button();
		if (this.modelPreviewer.mouseDragged(event, dx, dy)) {
			return true;
		}
		
		if (this.inputComponentsList.mouseDragged(event, dx, dy)) {
			return true;
		}
		
		return super.mouseDragged(event, dx, dy);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		int yBegin = 32;
		int yEnd = this.height - 45;
		
		guiGraphics.text(this.font, this.title, 20, 16, 16777215);
		
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.MENU_BACKGROUND, 0, yBegin, (float)((float)this.width), (float)((float)yEnd - yBegin), this.width, yEnd, 32, 32, ARGB.colorFromFloat(1.0F, 0.125F, 0.125F, 0.125F));
		
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.MENU_BACKGROUND, 0, 0, (float)(0.0F), (float)(0.0F), this.width, yBegin, 32, 32, ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 0.25F));
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Screen.MENU_BACKGROUND, 0, yEnd, (float)(0.0F), (float)((float)yEnd - yBegin), this.width, yEnd, 32, 32, ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 0.25F));
        
        guiGraphics.fillGradient(0, yBegin, this.width, yBegin + 4, -16777216, 0);
		guiGraphics.fillGradient(0, yEnd, this.width, yEnd + 1, 0, -16777216);
		
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	private void reloadAnimationPlayer() {
		List<AssetAccessor<? extends StaticAnimation>> animations = Lists.newArrayList();
		
		this.comboGrid.visitRows((values) -> {
			@SuppressWarnings("unchecked")
			AssetAccessor<? extends StaticAnimation> animation = (AssetAccessor<? extends StaticAnimation>)values.get("combo_animation");
			
			if (animation != null) {
				animations.add(animation);
			}
		});
		
		AssetAccessor<? extends StaticAnimation> dashAttack = this.dashAttackPopupbox._getValue();
		AssetAccessor<? extends StaticAnimation> airSlash = this.airSlashPopupbox._getValue();
		
		if (dashAttack != null) {
			animations.add(dashAttack);
		}
		
		if (airSlash != null) {
			animations.add(airSlash);
		}
		
		this.modelPreviewer.clearAnimations();
		animations.forEach(this.modelPreviewer::addAnimationToPlay);
	}
}