package uk.co.catlord.mods.nabsack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.gson.Gson;

import net.minecraft.entity.EntityType;

public class Config {
  public boolean development = false;
  public List<String> entityIdWhitelist;

  public static Config loadConfig() throws IOException {
    Gson gson = new Gson();

    File configFile = new File("nabsack.json");

    if (!configFile.exists()) {
      InputStream stream = Config.class.getResourceAsStream("/nabsack.json");

      if (stream == null) {
        throw new RuntimeException("Failed to load default config");
      }

      boolean created = configFile.createNewFile();

      if (!created) {
        throw new RuntimeException("Failed to create default config file");
      }

      FileOutputStream writer = new FileOutputStream(configFile);
      stream.transferTo(writer);
      writer.close();
    }

    FileReader reader = new FileReader("nabsack.json");
    Config config = gson.fromJson(reader, Config.class);
    config.validate();

    return config;
  }

  public void validate() {
    if (entityIdWhitelist == null) {
      throw new RuntimeException("entityIdWhitelist is null");
    }
  }

  public boolean isEntityTypeWhitelisted(EntityType<?> entityType) {
    return entityIdWhitelist.contains(EntityType.getId(entityType).toString());
  }
}
