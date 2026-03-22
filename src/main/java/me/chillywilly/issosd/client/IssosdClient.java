package me.chillywilly.issosd.client;

import com.lightstreamer.client.LightstreamerClient;
import com.lightstreamer.client.Subscription;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssosdClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("issosd");
    public static final ISSModConfig config = new ISSModConfig();
    LightstreamerClient client;
    public float value = 0;
    public static IssosdClient instance;
    public static Identifier normal_texture = Identifier.fromNamespaceAndPath("issosd", "textures/gui/piss_icon.png");
    public static Identifier notif_texture = Identifier.fromNamespaceAndPath("issosd", "textures/gui/piss_icon_notif.png");

    public SpacemanHudElement hudElement;

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

        sub.addListener(new ISSSubListener());
        hudElement = new SpacemanHudElement(normal_texture);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("issosd", "pissdisplay"), hudElement);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("issosd")
                    .then(Commands.argument("display_num", IntegerArgumentType.integer())
                            .executes(IssosdClient::executeCommandWithArg)));
        });
    }

    private static int executeCommandWithArg(CommandContext<CommandSourceStack> context) {
        int value = IntegerArgumentType.getInteger(context, "display_num");
        IssosdClient.instance.update(String.valueOf(value));
        return 1;
    }

    public void update(String newValue) {
        try {
            float val = Float.parseFloat(newValue);
            float old_value = value;
            value = val;
            LOGGER.info("Received New Value: {}", newValue);
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000); //wait 2 seconds and check again
                        if (val == old_value) return; //if value hasn't changed, skip all
                        if (val > old_value && checkUpSound()) {
                            playSoundToPlayer(config.getUpSound(), config.getUpSoundPitch());
                        }
                        if (val < old_value && checkDownSound()) {
                            playSoundToPlayer(config.getDownSound(), config.getDownSoundPitch());
                        }
                        hudElement.texture = notif_texture; //Display notification texture for 5 seconds
                        Thread.sleep(5000);
                        hudElement.texture = normal_texture;
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }.start();
        } catch(NumberFormatException e) {
            LOGGER.warn("Number provided is not a number: {}", newValue);
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

    public boolean checkUpSound() {
        return checkMod() && config.getUpSoundEnabled();
    }

    public boolean checkDownSound() {
        return checkMod() && config.getDownSoundEnabled();
    }

    public boolean checkMod() {
        return config.getEnabled();
    }
}
