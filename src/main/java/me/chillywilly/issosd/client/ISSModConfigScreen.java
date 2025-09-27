package me.chillywilly.issosd.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
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
    EditBoxWidget widget_upsound;
    EditBoxWidget widget_downsound;
    EditBoxWidget widget_upsound_pitch;
    EditBoxWidget widget_downsound_pitch;

    private boolean closing = false;

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

        widget_upsound = new EditBoxWidget.Builder()
                .x(centerX - 100)
                .y(baseY + 80)
                .placeholder(Text.translatable("issosd.config.upsound"))
                .textShadow(true)
                .build(MinecraftClient.getInstance().textRenderer, 180, 20, Text.of(""));
        widget_upsound.setText(config.getUpSoundRaw());

        widget_upsound_pitch = new EditBoxWidget.Builder()
                .x(centerX + 85)
                .y(baseY + 80)
                .placeholder(Text.translatable("issosd.config.upsound_pitch"))
                .build(MinecraftClient.getInstance().textRenderer, 30, 20, Text.of(""));
        widget_upsound_pitch.setText(String.valueOf(config.getUpSoundPitch()));

        TextWidget label_upsound = new TextWidget(
                centerX - 60,
                baseY + 60,
                120,
                20,
                Text.translatable("issosd.config.upsound"),
                MinecraftClient.getInstance().textRenderer
        );

        TextWidget label_upsound_pitch = new TextWidget(
                centerX + 40,
                baseY + 60,
                120,
                20,
                Text.translatable("issosd.config.upsound_pitch"),
                MinecraftClient.getInstance().textRenderer
        );

        ButtonWidget widget_toggleUpSoundButton = new ButtonWidget.Builder(
                Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getUpSoundEnabled())),
                btn -> {
                    config.setUpSoundEnabled(!IssosdClient.config.getUpSoundEnabled());
                    btn.setMessage(Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getUpSoundEnabled())));
                })
                .position(centerX - 190, baseY + 80)
                .width(70)
                .build();

        ButtonWidget widget_testUpSoundButton = new ButtonWidget.Builder(Text.translatable("issosd.config.sound_test_button"), btn -> {
            playTestSoundToPlayer(widget_upsound, widget_upsound_pitch);
        }).position(centerX + 130, baseY + 80).width(50).build();




        widget_downsound = new EditBoxWidget.Builder()
                .x(centerX - 100)
                .y(baseY + 120)
                .placeholder(Text.translatable("issosd.config.downsound"))
                .textShadow(true)
                .build(MinecraftClient.getInstance().textRenderer, 180, 20, Text.of(""));
        widget_downsound.setText(config.getDownSoundRaw());

        widget_downsound_pitch = new EditBoxWidget.Builder()
                .x(centerX + 85)
                .y(baseY + 120)
                .placeholder(Text.translatable("issosd.config.downsound_pitch"))
                .build(MinecraftClient.getInstance().textRenderer, 30, 20, Text.of(""));
        widget_downsound_pitch.setText(String.valueOf(config.getDownSoundPitch()));

        TextWidget label_downsound = new TextWidget(
                centerX - 60,
                baseY + 100,
                120,
                20,
                Text.translatable("issosd.config.downsound"),
                MinecraftClient.getInstance().textRenderer
        );

        TextWidget label_downsound_pitch = new TextWidget(
                centerX + 40,
                baseY + 100,
                120,
                20,
                Text.translatable("issosd.config.downsound_pitch"),
                MinecraftClient.getInstance().textRenderer
        );

        ButtonWidget widget_toggleDownSoundButton = new ButtonWidget.Builder(
                Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getDownSoundEnabled())),
                btn -> {
                    config.setDownSoundEnabled(!IssosdClient.config.getDownSoundEnabled());
                    btn.setMessage(Text.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getDownSoundEnabled())));
                })
                .position(centerX - 190, baseY + 120)
                .width(70)
                .build();

        ButtonWidget widget_testDownSoundButton = new ButtonWidget.Builder(Text.translatable("issosd.config.sound_test_button"), btn -> {
            playTestSoundToPlayer(widget_downsound, widget_downsound_pitch);
        }).position(centerX + 130, baseY + 120).width(50).build();

        ButtonWidget widget_toggleModButton = new ButtonWidget.Builder(
                Text.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())),
                btn -> {
                    config.setEnabled(!IssosdClient.config.getEnabled());
                    btn.setMessage(Text.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())));
                })
                .position(centerX - 100, baseY + 150)
                .width(70)
                .build();

        ButtonWidget resetButton = new ButtonWidget.Builder(Text.translatable("issosd.config.reset_button"), btn -> {
            config.reset();
            closing = true;
            MinecraftClient.getInstance().setScreen(new ISSModConfigScreen());
        }).position(centerX - 20, baseY + 150).width(50).tooltip(Tooltip.of(Text.translatable("issosd.config.reset_button.tooltip"))).build();

        this.addDrawableChild(widget_x);
        this.addDrawableChild(widget_y);

        this.addDrawableChild(widget_toggleUpSoundButton);
        this.addDrawableChild(widget_upsound);
        this.addDrawableChild(widget_upsound_pitch);
        this.addDrawableChild(label_upsound);
        this.addDrawableChild(label_upsound_pitch);
        this.addDrawableChild(widget_testUpSoundButton);

        this.addDrawableChild(widget_toggleDownSoundButton);
        this.addDrawableChild(widget_downsound);
        this.addDrawableChild(widget_downsound_pitch);
        this.addDrawableChild(label_downsound);
        this.addDrawableChild(label_downsound_pitch);
        this.addDrawableChild(widget_testDownSoundButton);


        this.addDrawableChild(resetButton);
        this.addDrawableChild(widget_toggleModButton);
    }

    private void playTestSoundToPlayer(EditBoxWidget sound_widget, EditBoxWidget pitch_widget) {
        String[] split = sound_widget.getText().split(":");
        if (split.length != 2) {
            IssosdClient.LOGGER.warn("Sound ID is incorrect! {}", sound_widget.getText());
            return;
        }

        Identifier soundID = Identifier.of(split[0], split[1]);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            IssosdClient.LOGGER.warn("Player is null, something is broken");
            return;
        }

        try {
            float pitch = Float.parseFloat(pitch_widget.getText());
            player.playSoundToPlayer(SoundEvent.of(soundID), SoundCategory.UI, 1.0F, pitch);
        } catch(NumberFormatException e) {
            IssosdClient.LOGGER.warn("Provided value is not a number: {}", pitch_widget.getText(), e);
        }
    }

    @Override
    public void removed() {
        if (!closing) {
            config.setUpSound(widget_upsound.getText());
            config.setDownSound(widget_downsound.getText());

            try {
                Float upsound_pitch = Float.parseFloat(widget_upsound_pitch.getText());
                config.setUpSoundPitch(upsound_pitch);
            } catch (NumberFormatException e) {
                IssosdClient.LOGGER.warn("Increasing Sound Pitch is not a number", e);
            }
            try {
                Float downsound_pitch = Float.parseFloat(widget_downsound_pitch.getText());
                config.setDownSoundPitch(downsound_pitch);
            } catch (NumberFormatException e) {
                IssosdClient.LOGGER.warn("Decreasing Sound Pitch is not a number!", e);
            }
        }
        config.save();
    }
}