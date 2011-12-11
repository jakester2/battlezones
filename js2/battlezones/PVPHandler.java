/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.HashMap;
import java.util.Iterator;
import javax.vecmath.Point3i;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This class handles all PvP related control for the plugin.
 * 
 * @author Jacob Tyo
 * @version 12/11/2011
 */
public class PVPHandler extends PlayerListener {
    public BattleZones plugin;
    public HashMap<String, Boolean> playerPvPMap;
    public HashMap<String, String> playerZoneMap;
    
    /**
     * Create a new instance of {@code PVPHandler}.
     * @param plugin Parent {@link BattleZones} instance.
     */
    public PVPHandler(BattleZones plugin)
    {
        init(plugin);
    }
    
    /**
     * Initialize all variables.
     */
    private void init(BattleZones plugin) {
        this.plugin                     = plugin;
        playerPvPMap                    = new HashMap<String, Boolean>();
        playerZoneMap                   = new HashMap<String, String>();
    }
    
    /**
     * Returns whether PvP is enabled for a specified {@link World}.
     * @param world World to check PvP status.
     * @return {@code true} if the world is set to PvP, else {@code false}
     */
    public boolean isGlobalPVPEnabled(World world)
    {
        return world.getPVP();
    }
    
    /**
     * Set the PvP status for a specified world.
     * @param world World to set PvP status
     * @param enabled PvP status to set
     */
    public void setGlobalPVP(World world, boolean enabled)
    {
        world.setPVP(enabled);
    }
    
    /**
     * Returns whether PvP is enabled for a specified player.
     * @param player Player to check PvP status
     * @return {@code true} if the player can PvP, else {@code false}
     */
    public boolean isPlayerPVPEnabled(Player player)
    {
        return playerPvPMap.get(player.getName());
    }
    
    /**
     * Returns whether the specified player has permissions to PVP.
     * @param player Player to check permissions
     * @return {@code true} if the player can PvP, else {@code false}
     */
    public boolean playerHasPVPPermissions(Player player)
    {
        return player.hasPermission("battlezones.pvp");
    }
    
    /**
     * Set the specified players PvP status.
     * @param player Player to set PvP status
     * @param enabled PvP status to set
     */
    public void setPlayerPVP(Player player, boolean enabled)
    {
        if (!playerHasPVPPermissions(player)) return;
        playerPvPMap.put(player.getName(), enabled);
    }
    
    /**
     * This method returns the amount of players inside a specific zone.
     * @param world World that the zone is in
     * @param zoneName Zone to get the number of players
     * @return Number of players in the zone specified
     */
    public int getNumPlayersInZone(World world, String zoneName)
    {
        int numberOfPlayers = 0;
        for (Iterator<String> it = playerZoneMap.values().iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (!world.getName().equals(string[0])) continue;
            if (string[1].equals(zoneName)) numberOfPlayers++;
        }
        return numberOfPlayers;
    }

    /**
     * This method fires when a player logs in. If they log in inside a PvP zone,
     * they will be set to PvP enabled.
     * @param event Relevant event details
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        super.onPlayerJoin(event);
        if (!plugin.isZonesSet) return;
        boolean isInsideZone = false;
        for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
            String[] string = it.next().split("\\.");
            if (string[0].equals(event.getPlayer().getWorld().getName()) && plugin.zoneConfig.getConfig().getBoolean("zones." + string[0] + "." + string[1] + ".enabled")) {
                String root = "zones." + string[0] + "." + string[1] + ".";
                FileConfiguration config = plugin.zoneConfig.getConfig();
                // If logging in inside a zone.
                if (plugin.pvpHandler.playerHasPVPPermissions(event.getPlayer()) &&
                    plugin.movementListener.isIntersecting(event.getPlayer().getLocation(), 
                        new Point3i(config.getInt(root + "x1"), config.getInt(root + "y1"), config.getInt(root + "z1")), 
                        new Point3i(config.getInt(root + "x2"), config.getInt(root + "y2"), config.getInt(root + "z2"))))
                {
                    setPlayerPVP(event.getPlayer(), true);
                    playerZoneMap.put(event.getPlayer().getName(), string[0] + "." + string[1]);
                    isInsideZone = true;
                    Message.send(event.getPlayer(), "Entered: " + string[1] + ". PvP " + ChatColor.GREEN + "ON!");
                }
            }
        }
        if (!isInsideZone) {
            setPlayerPVP(event.getPlayer(), false);
            playerZoneMap.put(event.getPlayer().getName(), "");
        }
    }

    /**
     * This method removes player zone data from memory when a player logs out.
     * This is ran only if the "RELEASE_MEM_ON_LOGOUT" preference is set to true.
     * @param event Relevant event details
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        super.onPlayerQuit(event);
        if (plugin.prefConfig.getConfig().getBoolean(PrefConfig.PREF_RELEASE_MEM))
        {
            playerPvPMap.remove(event.getPlayer().getName());
            playerZoneMap.remove(event.getPlayer().getName());
        }
    }

}
