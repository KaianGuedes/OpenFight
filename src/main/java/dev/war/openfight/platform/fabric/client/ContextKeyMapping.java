package dev.war.openfight.platform.fabric.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

/** Preserves the GUI/in-game applicability of the original NeoForge key bindings. */
@Environment(EnvType.CLIENT)
public class ContextKeyMapping extends KeyMapping {
    public enum Context { GUI, IN_GAME }
    private final Context context;
    public ContextKeyMapping(String name, Context context, InputConstants.Type type, int code, Category category) {
        super(name,type,code,category);
        this.context=context;
    }
    protected boolean active() {
        Minecraft minecraft=Minecraft.getInstance();
        return context==Context.GUI ? minecraft.gui.screen()!=null : minecraft.gui.screen()==null;
    }
    @Override public boolean isDown() { return active() && super.isDown(); }
    @Override public boolean consumeClick() {
        boolean clicked=super.consumeClick();
        return active() && clicked;
    }
    @Override public boolean matches(InputConstants.Key key) { return active() && super.matches(key); }
    @Override public boolean matches(KeyEvent event) { return active() && super.matches(event); }
    @Override public boolean matchesMouse(MouseButtonEvent event) { return active() && super.matchesMouse(event); }
}
