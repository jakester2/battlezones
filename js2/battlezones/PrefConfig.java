/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * 
 * @author Jacob Tyo
 * @version 12/10/2011
 */
public class PrefConfig {
    private BattleZones plugin;
    private File configFile;
    private FileConfiguration config;
    
    public PrefConfig(BattleZones plugin)
    {
        init(plugin);
        defaults();
    }

    private void init(BattleZones plugin)
    {
        this.plugin                 = plugin;
        configFile                  = new File(plugin.getDataFolder(), "config.yml");
        config                      = YamlConfiguration.loadConfiguration(configFile);
    }
    
    private void defaults()
    {
        boolean hasChanged = false;
        if (!getConfig().contains("enabled")) { getConfig().createSection("enabled"); getConfig().set("enabled", true); hasChanged = true; }
        if (!getConfig().contains("debug")) { getConfig().createSection("debug"); getConfig().set("debug", true); hasChanged = true; }
        if (hasChanged) {
            try {
                savePrefs();
            } catch (IOException ex) {
                BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
            }
        }
    }
    
    public void savePrefs() throws IOException
    {
        getConfig().options().header("Modify settings to the " + plugin.getDescription().getName() + " plugin.\n"
                + "\n"
                + "enabled - Enabled or disable the plugin.\n"
                + "\n"
                + "debug - Enable or disable debug mode. This provides more detailed printouts.");
        getConfig().save(configFile);
    }

    /**
     * Returns the FileConfiguration Object.
     * @return FileConfiguration Object.
     */
    public FileConfiguration getConfig() {
        return config;
    }
    
}
