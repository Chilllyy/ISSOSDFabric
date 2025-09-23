package me.chillywilly.issosd.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ISSModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private double pos_x;
    private double pos_y;
    private String sound_namespace;
    private String sound_id;
    public boolean mod_enabled;
    public boolean sound_enabled;

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
            setSoundNamespace(obj.getSoundNamespace());
            setSoundID(obj.getSoundID());
            setEnabled(obj.getEnabled());
            setSoundEnabled(obj.getSoundEnabled());
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
        sound_namespace = "minecraft";
        sound_id = "block.note_block.harp";
        mod_enabled = true;
        sound_enabled = true;
    }

    public void check() {
        boolean save = false;
        if (sound_namespace == null) {
            sound_namespace = "minecraft";
            save = true;
        }
        if (sound_id == null) {
            sound_id = "block.note_block.harp";
            save = true;
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

    public String getSoundNamespace() {
        if (this.sound_namespace != null) {
            this.sound_namespace = "minecraft";
            return this.sound_namespace;
        }
        return this.sound_namespace;
    }

    public String getSoundID() {
        if (this.sound_id != null) {
            this.sound_id = "block.note_block.harp";
            return this.sound_id;
        }
        return this.sound_id;
    }

    public void setSoundNamespace(String namespace) {
        this.sound_namespace = namespace;
    }

    public void setSoundID(String ID) {
        this.sound_id = ID;
    }

    public boolean getEnabled() {
        return mod_enabled;
    }

    public boolean getSoundEnabled() {
        return sound_enabled;
    }

    public void setEnabled(boolean enabled) {
        this.mod_enabled = enabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.sound_enabled = enabled;
    }
}
