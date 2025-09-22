package me.chillywilly.issosd.client;

import com.lightstreamer.client.LightstreamerClient;
import com.lightstreamer.client.Subscription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class IssosdClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("issosd");
    public static final ISSModConfig config = new ISSModConfig();
    LightstreamerClient client;
    private float value = 0;
    public static IssosdClient instance;
    public boolean animate = false;
    public static Identifier texture;
    public static Identifier normal_texture = Identifier.of("issosd", "textures/gui/piss_icon.png");
    public static Identifier notif_texture = Identifier.of("issosd", "textures/gui/piss_icon_notif.png");

    @Override
    public void onInitializeClient() {
        config.load();
        IssosdClient.instance = this;

        client = new LightstreamerClient("https://push.lightstreamer.com", "ISSLIVE");
        client.connect();

        String[] items = {"NODE3000005"};
        String[] fields = {"Value"};
        Subscription sub = new Subscription("MERGE", items, fields);
        sub.setRequestedSnapshot("yes");
        client.subscribe(sub);
        animate = FabricLoader.getInstance().isModLoaded("animatica");
        LOGGER.info("Animate loaded {}", animate);

        sub.addListener(new ISSSubListener());
        texture = IssosdClient.normal_texture;
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("issosd", "pissdisplay"), IssosdClient::render);
    }

    private static void render(DrawContext context, RenderTickCounter counter) {
        int color = 0xFFA87132;
        int x_start = (int) (context.getScaledWindowWidth() * config.getX());
        int y_start = (int) (context.getScaledWindowHeight() * config.getY());
        context.drawTexture(RenderPipelines.GUI_TEXTURED, IssosdClient.texture, x_start, y_start, 0, 0, 16, 16, 16, 16);
        context.drawText(MinecraftClient.getInstance().textRenderer, IssosdClient.instance.value + "%", x_start + 20, y_start + 4, color, true);
    }

    public void update(String newValue) {
        try {
            float val = Float.parseFloat(newValue);
            value = val;
            LOGGER.info("Received New Value: {}", newValue);
            new Thread() {
                @Override
                public void run() {
                    try {
                        IssosdClient.texture = IssosdClient.notif_texture; //Display notification texture for 5 seconds
                        Thread.sleep(5000);
                        IssosdClient.texture = IssosdClient.normal_texture;
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }.start();
        } catch(NumberFormatException e) {
            LOGGER.warn("Number provided is not a number: {}", newValue);
        }
    }
}
