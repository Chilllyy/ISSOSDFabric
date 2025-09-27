package me.chillywilly.issosd.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ISSModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private double pos_x;
    private double pos_y;
    private String up_sound;
    private String down_sound;
    private float up_sound_pitch;
    private float down_sound_pitch;
    public boolean mod_enabled;
    public boolean up_sound_enabled;
    public boolean down_sound_enabled;

    public void load() {
        File folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        File file = new File(folder, "issPiss.json");
        if (!file.exists()) {
            reset();
            return;
        }
        try (var fr = new FileReader(file)) {
            ISSModConfig obj = GSON.fromJson(fr, ISSModConfig.class);
            setX(obj.getX());
            setY(obj.getY());
            setUpSound(obj.getUpSoundRaw());
            setDownSound(obj.getDownSoundRaw());
            setUpSoundPitch(obj.getUpSoundPitch());
            setDownSoundPitch(obj.getDownSoundPitch());
            setEnabled(obj.getEnabled());
            setUpSoundEnabled(obj.getUpSoundEnabled());
            setDownSoundEnabled(obj.getDownSoundEnabled());
            check();
        } catch (Exception e) {
            IssosdClient.LOGGER.error("Failed to read file {}", file.getName(), e);
        }
    }

    public void save() {
        File folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            IssosdClient.LOGGER.error("Failed to create missing config folder");
            return;
        }
        File file = new File(folder, "issPiss.json");
        try (var fw = new FileWriter(file)) {
            GSON.toJson(this, ISSModConfig.class, fw);
            IssosdClient.LOGGER.info("Saved Config");
        } catch (Exception e) {
            IssosdClient.LOGGER.error("Failed to write file {}", file.getName(), e);
        }
    }

    public void reset() {
        pos_x = 0.5;
        pos_y = 0.0;
        up_sound_pitch = 1.0F;
        down_sound_pitch = 1.0F;
        up_sound = "minecraft:block.note_block.harp";
        down_sound = "minecraft:block.note_block.banjo";
        mod_enabled = true;
        up_sound_enabled = true;
        down_sound_enabled = true;

        save();
    }

    public void check() {
        boolean save = false;
        if (up_sound == null) {
            up_sound = "minecraft:block.note_block.harp";
            save = true;
        }
        if (down_sound == null) {
            down_sound = "minecraft:block.note_block.banjo";
        }

        if (save) save();
    }

    public double getX() {
        return pos_x;
    }

    public double getY() {
        return pos_y;
    }

    public void setX(double pos_x) {
        this.pos_x = pos_x;
    }

    public void setY(double pos_y) {
        this.pos_y = pos_y;
    }

    public Identifier getUpSound() {
        check();
        String[] up_sound_split = up_sound.split(":");
        if (up_sound_split.length == 2) {
            try {
                return Identifier.of(up_sound_split[0], up_sound_split[1]);
            } catch(Exception e) {
                IssosdClient.LOGGER.warn("Exception occured during sound ID initialization: {}", e.getMessage(), e);
            }
        }
        return Identifier.of("unset", "unset");
    }

    public Identifier getDownSound() {
        check();
        String[] down_sound_split = down_sound.split(":");
        if (down_sound_split.length == 2) {
            try {
                return Identifier.of(down_sound_split[0], down_sound_split[1]);
            } catch(Exception e) {
                IssosdClient.LOGGER.warn("Exception occured during sound ID initialization: {}", e.getMessage(), e);
            }
        }
        return Identifier.of("unset", "unset");
    }

    public String getUpSoundRaw() {
        check();
        return this.up_sound;
    }

    public String getDownSoundRaw() {
        check();
        return this.down_sound;
    }

    public float getUpSoundPitch() {
        return this.up_sound_pitch;
    }

    public float getDownSoundPitch() {
        return this.down_sound_pitch;
    }

    public void setUpSound(String sound) {
        this.up_sound = sound;
    }

    public void setDownSound(String sound) {
        this.down_sound = sound;
    }

    public void setUpSoundPitch(float pitch) {
        this.up_sound_pitch = pitch;
    }

    public void setDownSoundPitch(float pitch) {
        this.down_sound_pitch = pitch;
    }

    public boolean getEnabled() {
        return mod_enabled;
    }

    public boolean getUpSoundEnabled() {
        return this.up_sound_enabled;
    }

    public boolean getDownSoundEnabled() {
        return this.down_sound_enabled;
    }

    public void setEnabled(boolean enabled) {
        this.mod_enabled = enabled;
    }

    public void setUpSoundEnabled(boolean enabled) {
        this.up_sound_enabled = enabled;
    }

    public void setDownSoundEnabled(boolean enabled) {
        this.down_sound_enabled = enabled;
    }
}
