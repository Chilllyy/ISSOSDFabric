package me.chillywilly.issosd.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IssosdClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("issosd");
    public static final ISSModConfig config = new ISSModConfig();
    public int value = 0;
    public static IssosdClient instance;
    public static Identifier normal_texture = Identifier.fromNamespaceAndPath("issosd", "textures/gui/piss_icon.png");
    public static Identifier notif_texture = Identifier.fromNamespaceAndPath("issosd", "textures/gui/piss_icon_notif.png");

    public SpacemanHudElement hudElement;

    private Thread thread;

    @Override
    public void onInitializeClient() {
        config.load();
        IssosdClient.instance = this;

        hudElement = new SpacemanHudElement(normal_texture);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("issosd", "pissdisplay"), hudElement);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("issosd")
                    .then(Commands.argument("display_num", IntegerArgumentType.integer())
                            .executes(IssosdClient::executeCommandWithArg)));
        });

        ServerFetcher fetcher = new ServerFetcher();
        thread = new Thread(fetcher);
        thread.start();

        ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
            thread.interrupt();
        });
    }

    private static int executeCommandWithArg(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "display_num");
        IssosdClient.instance.update(String.valueOf(value));
        return 1;
    }

    public void update(String newValue) {
        try {
            int val = Integer.parseInt(newValue);
            update(val);
        } catch(NumberFormatException e) {
            LOGGER.warn("Number provided is not a number: {}", newValue);
        }
    }

    public void update(int newValue) {
        if (value == newValue) return;
        Identifier sound = null;
        float pitch = 1.0F;
        if (newValue > value) {
            sound = config.getUpSound();
            pitch = config.getUpSoundPitch();
        } else {
            sound = config.getDownSound();
            pitch = config.getDownSoundPitch();
        }
        value = newValue;
        LOGGER.debug("Received New Value: " + value);
        playSoundToPlayer(sound, pitch);
        new Thread(() -> {
            try {
                hudElement.texture = notif_texture;
                Thread.sleep(5000);
                hudElement.texture = normal_texture;
            } catch (InterruptedException e) {
                LOGGER.error("Thread interrupted during hud element change", e);
            }
        }).start();
    }

    public void update(JsonObject jsonObject) {
        try {
            float newValue = jsonObject.get("value").getAsFloat();
            update((int)newValue);
        } catch (NumberFormatException e) {
            LOGGER.error("Value provided from webserver is not a number", e);
        }

    }

    private void playSoundToPlayer(Identifier sound, float pitch) {

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            LOGGER.warn("Player is null, you may not be on a world");
            return;
        }

        player.playSound(SoundEvent.createVariableRangeEvent(sound), 1.0F, pitch);
    }

    public boolean checkMod() {
        return config.getEnabled();
    }

    private static class ServerFetcher implements Runnable {
        private volatile boolean running = true;
        private volatile int errorcount = 0;

        @Override
        public void run() {
            LOGGER.info("Starting ISS Value Fetcher");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(config.server_url)
                    .build();
            while (running && errorcount < 5) {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                        IssosdClient.instance.update(jsonObject);
                    } else {
                        LOGGER.error(String.format("Webserver provided response code %s", response.code()));
                        errorcount++;
                    }
                    Thread.sleep(30000); //Grab every 30 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                } catch (IOException e) {
                    LOGGER.error("Ran into issue while grabbing data from the web endpoint\n", e);
                    errorcount++;
                }
            }
            LOGGER.info("Stopping ISS Value Fetcher");
        }
    }
}
