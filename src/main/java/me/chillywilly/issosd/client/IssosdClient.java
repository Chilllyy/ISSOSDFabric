package me.chillywilly.issosd.client;

import com.lightstreamer.client.LightstreamerClient;
import com.lightstreamer.client.Subscription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class IssosdClient implements ClientModInitializer {
    LightstreamerClient client;
    private float value = 0;
    public static IssosdClient instance;

    @Override
    public void onInitializeClient() {
        System.out.println("Initializing mod");
        IssosdClient.instance = this;
        client = new LightstreamerClient("https://push.lightstreamer.com", "ISSLIVE");
        client.connect();

        String[] items = {"NODE3000005"};
        String[] fields = {"Value"};
        Subscription sub = new Subscription("MERGE", items, fields);
        sub.setRequestedSnapshot("yes");
        client.subscribe(sub);

        sub.addListener(new ISSSubListener());

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("issosd", "pissdisplay"), IssosdClient::render);
    }

    private static void render(DrawContext context, RenderTickCounter counter) {
        int color = 0xFFA87132;
        Identifier texture = Identifier.of("issosd", "textures/gui/piss_icon.png");
        int height = context.getScaledWindowHeight();

        context.drawText(MinecraftClient.getInstance().textRenderer, IssosdClient.instance.value + "%", 24, height - 16, color, true);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, 4, height - 20, 0, 0, 16, 16, 16, 16);
    }

    public void update(String newValue) {
        try {
            float val = Float.parseFloat(newValue);
            value = val;
        } catch(NumberFormatException e) {
            System.out.println("Number provided is not a number: " + newValue);
        }
        System.out.println("Received New value: " + newValue);
    }
}
