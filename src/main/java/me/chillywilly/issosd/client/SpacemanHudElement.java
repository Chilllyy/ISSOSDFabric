package me.chillywilly.issosd.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SpacemanHudElement implements HudElement {
    private ISSModConfig config;
    public Identifier texture;
    public SpacemanHudElement(Identifier texture) {
        config = IssosdClient.config;
        this.texture = texture;
    }
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
        if (!config.getEnabled()) return;
        int color = 0xFFA87132;
        int x_start = (int) (guiGraphicsExtractor.guiWidth() * config.getX());
        int y_start = (int) (guiGraphicsExtractor.guiHeight() * config.getY());

        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, texture, x_start, y_start, 0, 0, 16, 16, 16, 16, 16, 16, -1);
        guiGraphicsExtractor.text(Minecraft.getInstance().font, IssosdClient.instance.value + "%", x_start + 20, y_start + 4, color, true);
    }
}
