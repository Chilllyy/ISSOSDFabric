package me.chillywilly.issosd.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ISSModConfigScreen extends Screen {
    private final ISSModConfig config;
    MultiLineEditBox widget_upsound;
    MultiLineEditBox widget_downsound;
    MultiLineEditBox widget_upsound_pitch;
    MultiLineEditBox widget_downsound_pitch;

    private boolean closing = false;

    public ISSModConfigScreen() {
        super(Component.translatable("issosd.menu.title"));
        this.config = IssosdClient.config;
    }

    @Override
    public void init() {
        config.load();
        this.clearWidgets();
        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;

        AbstractSliderButton widget_x = new AbstractSliderButton(centerX - 100, baseY, 200, 20, Component.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"), config.getX()) {
            @Override
            protected void updateMessage() {
            }

            @Override
            protected void applyValue() {
                config.setX(this.value);
                this.setMessage(Component.translatable("issosd.config.x").append(" " + (int) (config.getX() * 100) + "%"));
            }
        };

        AbstractSliderButton widget_y = new AbstractSliderButton(centerX - 100, baseY + 40, 200, 20, Component.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"), config.getY()) {
            @Override
            protected void updateMessage() {}

            @Override
            protected void applyValue() {
                config.setY(this.value);
                this.setMessage(Component.translatable("issosd.config.y").append(" " + (int) (config.getY() * 100) + "%"));
            }
        };

        widget_upsound = new MultiLineEditBox.Builder()
                .setX(centerX - 100)
                .setY(baseY + 80)
                .setPlaceholder(Component.translatable("issosd.config.upsound"))
                .setTextShadow(true)
                .build(Minecraft.getInstance().font, 180, 20, Component.nullToEmpty(""));
        widget_upsound.setValue(config.getUpSoundRaw());

        widget_upsound_pitch = new MultiLineEditBox.Builder()
                .setX(centerX + 85)
                .setY(baseY + 80)
                .setPlaceholder(Component.translatable("issosd.config.upsound_pitch"))
                .build(Minecraft.getInstance().font, 30, 20, Component.nullToEmpty(""));
        widget_upsound_pitch.setValue(String.valueOf(config.getUpSoundPitch()));

        StringWidget label_upsound = new StringWidget(
                centerX - 60,
                baseY + 60,
                120,
                20,
                Component.translatable("issosd.config.upsound"),
                Minecraft.getInstance().font
        );

        StringWidget label_upsound_pitch = new StringWidget(
                centerX + 40,
                baseY + 60,
                120,
                20,
                Component.translatable("issosd.config.upsound_pitch"),
                Minecraft.getInstance().font
        );

        Button widget_toggleUpSoundButton = new Button.Builder(
                Component.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getUpSoundEnabled())),
                btn -> {
                    config.setUpSoundEnabled(!IssosdClient.config.getUpSoundEnabled());
                    btn.setMessage(Component.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getUpSoundEnabled())));
                })
                .pos(centerX - 190, baseY + 80)
                .width(70)
                .build();

        Button widget_testUpSoundButton = new Button.Builder(Component.translatable("issosd.config.sound_test_button"), btn -> {
            playTestSoundToPlayer(widget_upsound, widget_upsound_pitch);
        }).pos(centerX + 130, baseY + 80).width(50).build();




        widget_downsound = new MultiLineEditBox.Builder()
                .setX(centerX - 100)
                .setY(baseY + 120)
                .setPlaceholder(Component.translatable("issosd.config.downsound"))
                .setTextShadow(true)
                .build(Minecraft.getInstance().font, 180, 20, Component.nullToEmpty(""));
        widget_downsound.setValue(config.getDownSoundRaw());

        widget_downsound_pitch = new MultiLineEditBox.Builder()
                .setX(centerX + 85)
                .setY(baseY + 120)
                .setPlaceholder(Component.translatable("issosd.config.downsound_pitch"))
                .build(Minecraft.getInstance().font, 30, 20, Component.nullToEmpty(""));
        widget_downsound_pitch.setValue(String.valueOf(config.getDownSoundPitch()));

        StringWidget label_downsound = new StringWidget(
                centerX - 60,
                baseY + 100,
                120,
                20,
                Component.translatable("issosd.config.downsound"),
                Minecraft.getInstance().font
        );

        StringWidget label_downsound_pitch = new StringWidget(
                centerX + 40,
                baseY + 100,
                120,
                20,
                Component.translatable("issosd.config.downsound_pitch"),
                Minecraft.getInstance().font
        );

        Button widget_toggleDownSoundButton = new Button.Builder(
                Component.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getDownSoundEnabled())),
                btn -> {
                    config.setDownSoundEnabled(!IssosdClient.config.getDownSoundEnabled());
                    btn.setMessage(Component.translatable("issosd.config.sound_enabled.text." + String.valueOf(IssosdClient.config.getDownSoundEnabled())));
                })
                .pos(centerX - 190, baseY + 120)
                .width(70)
                .build();

        Button widget_testDownSoundButton = new Button.Builder(Component.translatable("issosd.config.sound_test_button"), btn -> {
            playTestSoundToPlayer(widget_downsound, widget_downsound_pitch);
        }).pos(centerX + 130, baseY + 120).width(50).build();

        Button widget_toggleModButton = new Button.Builder(
                Component.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())),
                btn -> {
                    config.setEnabled(!IssosdClient.config.getEnabled());
                    btn.setMessage(Component.translatable("issosd.config.mod_enabled.text." + String.valueOf(IssosdClient.config.getEnabled())));
                })
                .pos(centerX - 100, baseY + 150)
                .width(70)
                .build();

        Button resetButton = new Button.Builder(Component.translatable("issosd.config.reset_button"), btn -> {
            config.reset();
            closing = true;
            Minecraft.getInstance().setScreenAndShow(new ISSModConfigScreen());
        }).pos(centerX - 20, baseY + 150).width(50).tooltip(Tooltip.create(Component.translatable("issosd.config.reset_button.tooltip"))).build();

        this.addRenderableWidget(widget_x);
        this.addRenderableWidget(widget_y);

        this.addRenderableWidget(widget_toggleUpSoundButton);
        this.addRenderableWidget(widget_upsound);
        this.addRenderableWidget(widget_upsound_pitch);
        this.addRenderableWidget(label_upsound);
        this.addRenderableWidget(label_upsound_pitch);
        this.addRenderableWidget(widget_testUpSoundButton);

        this.addRenderableWidget(widget_toggleDownSoundButton);
        this.addRenderableWidget(widget_downsound);
        this.addRenderableWidget(widget_downsound_pitch);
        this.addRenderableWidget(label_downsound);
        this.addRenderableWidget(label_downsound_pitch);
        this.addRenderableWidget(widget_testDownSoundButton);


        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(widget_toggleModButton);
    }

    private void playTestSoundToPlayer(MultiLineEditBox sound_widget, MultiLineEditBox pitch_widget) {
        String[] split = sound_widget.getValue().split(":");
        if (split.length != 2) {
            IssosdClient.LOGGER.warn("Sound ID is incorrect! {}", sound_widget.getValue());
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            IssosdClient.LOGGER.warn("Player is null, you may not be on a world");;
            return;
        }

        try {
            Identifier soundID = Identifier.fromNamespaceAndPath(split[0], split[1]);
            float pitch = Float.parseFloat(pitch_widget.getValue());
            player.playSound(SoundEvent.createVariableRangeEvent(soundID), 1.0F, pitch);
        } catch(NumberFormatException e) {
            IssosdClient.LOGGER.warn("Provided value is not a number: {}", pitch_widget.getValue(), e);
        } catch(Exception e) {
            IssosdClient.LOGGER.warn("Exception occured during sound ID initialization: {}", e.getMessage(), e);
        }
    }

    @Override
    public void removed() {
        if (!closing) {
            config.setUpSound(widget_upsound.getValue());
            config.setDownSound(widget_downsound.getValue());

            try {
                Float upsound_pitch = Float.parseFloat(widget_upsound_pitch.getValue());
                config.setUpSoundPitch(upsound_pitch);
            } catch (NumberFormatException e) {
                IssosdClient.LOGGER.warn("Increasing Sound Pitch is not a number", e);
            }
            try {
                Float downsound_pitch = Float.parseFloat(widget_downsound_pitch.getValue());
                config.setDownSoundPitch(downsound_pitch);
            } catch (NumberFormatException e) {
                IssosdClient.LOGGER.warn("Decreasing Sound Pitch is not a number!", e);
            }
        }
        config.save();
    }
}