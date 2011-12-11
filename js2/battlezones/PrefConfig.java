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
 * This class handles config file configuration.
 * 
 * @TODO Add detailed preferences for high flexibility.
 * 
 * @author Jacob Tyo
 * @version 12/11/2011
 */
public class PrefConfig {
    public static final String PREF_ENABLED = "enabled";
    public static final String PREF_RELEASE_MEM = "release-mem";
    public static final String PREF_DEBUG = "debug";
    
    private BattleZones plugin;
    private File configFile;
    private FileConfiguration config;
    
    /**
     * Create a new instance of {@code PrefConfig}.
     * @param plugin Parent {@link BattleZones} instance.
     */
    public PrefConfig(BattleZones plugin)
    {
        init(plugin);
        defaults();
    }

    /**
     * Initialize all variables.
     */
    private void init(BattleZones plugin)
    {
        this.plugin                 = plugin;
        configFile                  = new File(plugin.getDataFolder(), "config.yml");
        config                      = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * Set default values if no configuration exists.
     */
    private void defaults()
    {
        boolean hasChanged = false;
        if (!getConfig().contains(PREF_ENABLED)) { getConfig().createSection(PREF_ENABLED); getConfig().set(PREF_ENABLED, true); hasChanged = true; }
        if (!getConfig().contains(PREF_RELEASE_MEM)) { getConfig().createSection(PREF_RELEASE_MEM); getConfig().set(PREF_RELEASE_MEM, true); hasChanged = true; }
        if (!getConfig().contains(PREF_DEBUG)) { getConfig().createSection(PREF_DEBUG); getConfig().set(PREF_DEBUG, false); hasChanged = true; }
        if (hasChanged) {
            try {
                savePrefs();
            } catch (IOException ex) {
                BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
            }
        }
    }
    
    /**
     * Save the configuration file.
     * @throws IOException Throws if there is a problem saving the file.
     */
    public void savePrefs() throws IOException
    {
        getConfig().options().header("Modify settings to the " + plugin.getDescription().getName() + " plugin.\n"
                + "\n"
                + PREF_ENABLED + " - Enabled or disable the plugin.\n"
                + "\n"
                + PREF_RELEASE_MEM + " - Release player data from memory on logout.\n"
                + "\n"
                + PREF_DEBUG + " - Enable or disable debug mode. This provides more detailed printouts.");
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
