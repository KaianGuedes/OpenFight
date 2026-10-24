package dev.war.openfight.client.gui.compat;

public final class GuiPort {
    private GuiPort() {}
    public static net.minecraft.client.gui.components.tabs.TabNavigationBar.Builder tabs(net.minecraft.client.gui.components.tabs.TabManager manager, int width, net.minecraft.client.gui.components.tabs.Tab... tabs) {
        var builder = net.minecraft.client.gui.components.tabs.TabNavigationBar.builder(manager, 0, 0, width, 24);
        for (var tab : tabs) builder.addTab(new net.minecraft.client.gui.components.TabButton(manager, tab, Math.min(150, width / tabs.length), 24) {
            protected void extractWidgetRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int x, int y, float a) {
                graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.isSelected() ? 0xFF555555 : 0xFF222222);
                graphics.centeredText(net.minecraft.client.Minecraft.getInstance().font, this.getMessage(), this.getX()+this.getWidth()/2, this.getY()+8, 0xFFFFFFFF);
            }
            public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) { manager.setCurrentTab(tab, true); }
        }, tab);
        return builder;
    }
    public static java.util.Optional<net.minecraft.world.entity.EntityType<?>> entityType(String name) {
        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getOptional(net.minecraft.resources.Identifier.parse(name));
    }
}
