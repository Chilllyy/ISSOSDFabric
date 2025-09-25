package me.chillywilly.issosd.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

public class ISSModConfigScreen extends Screen {
    private final ISSModConfig config;
    EditBoxWidget widget_namespace;
    EditBoxWidget widget_soundID;

    public ISSModConfigScreen() {
        super(Text.translatable("issosd.menu.title"));
        this.config = IssosdClient.config;
    }

    @Override
    public void init() {
        config.load();
        this.clearChildren();
        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;

        SliderWidget widget_x = new SliderWidget(centerX - 100, baseY, 200, 20, Text.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"), config.getX()) {
            @Override
            protected void updateMessage() {
            }

            @Override
            protected void applyValue() {
                config.setX(this.value);
                this.setMessage(Text.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"));
            }
        };

        SliderWidget widget_y = new SliderWidget(centerX - 100, baseY + 40, 200, 20, Text.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"), config.getY()) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                config.setY(this.value);
                this.setMessage(Text.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"));
            }
        };

        widget_namespace = new EditBoxWidget.Builder().x(centerX - 120).y(baseY + 80).placeholder(Text.translatable("issosd.config.namespace")).textShadow(true).build(MinecraftClient.getInstance().textRenderer, 70, 20, Text.of(""));
        widget_namespace.setText(config.getSoundNamespace());
        widget_soundID = new EditBoxWidget.Builder().x(centerX - 120 + 80).y(baseY + 80).placeholder(Text.translatable("issosd.config.sound_id")).textShadow(true).build(MinecraftClient.getInstance().textRenderer, 120, 20, Text.of(""));
        widget_soundID.setText(config.getSoundID());


        ButtonWidget widget_toggleSoundButton = new ButtonWidget.Builder(
                Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getSoundEnabled())),
                btn -> {
                    config.setSoundEnabled(!IssosdClient.config.getSoundEnabled());
                    btn.setMessage(Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getSoundEnabled())));
                })
                .position(centerX - 200, baseY + 80)
                .width(70)
                .build();

        ButtonWidget widget_testSoundButton = new ButtonWidget.Builder(Text.translatable("issosd.config.sound_test_button"), btn -> {
            try {
                Identifier sound_identifier = Identifier.of(widget_namespace.getText(), widget_soundID.getText());
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null && IssosdClient.instance.checkSound()) {
                    player.playSoundToPlayer(SoundEvent.of(sound_identifier), SoundCategory.UI, 1.0F, 1.0F);
                }
            } catch(InvalidIdentifierException e) {
                IssosdClient.LOGGER.warn("Invalid identifier {}", e.getMessage(), e);
            }
        }).position(centerX - 120 + 80 + 130, baseY + 80).width(50).build();

        ButtonWidget widget_toggleModButton = new ButtonWidget.Builder(
                Text.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())),
                btn -> {
                    config.setEnabled(!IssosdClient.config.getEnabled());
                    btn.setMessage(Text.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())));
                })
                .position(centerX - 100, baseY + 120)
                .width(70)
                .build();

        ButtonWidget resetButton = new ButtonWidget.Builder(Text.translatable("issosd.config.reset_button"), btn -> {
            config.reset();
            MinecraftClient.getInstance().setScreen(new ISSModConfigScreen());
        }).position(centerX - 20, baseY + 120).width(50).tooltip(Tooltip.of(Text.translatable("issosd.config.reset_button.tooltip"))).build();

        this.addDrawableChild(widget_x);
        this.addDrawableChild(widget_y);
        this.addDrawableChild(widget_namespace);
        this.addDrawableChild(widget_soundID);
        this.addDrawableChild(widget_testSoundButton);
        this.addDrawableChild(resetButton);
        this.addDrawableChild(widget_toggleSoundButton);
        this.addDrawableChild(widget_toggleModButton);
    }

    @Override
    public void removed() {
        config.setSoundID(widget_soundID.getText());
        config.setSoundNamespace(widget_namespace.getText());
        config.save();
    }
}
