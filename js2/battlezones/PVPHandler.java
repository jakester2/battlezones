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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 
 * 
 * @author Jacob Tyo
 * @version 12/10/2011
 */
public class PVPHandler extends PlayerListener {
    public BattleZones plugin;
    public HashMap<String, Boolean> playerPvPMap;
    public HashMap<String, String> playerZoneMap;
    
    public PVPHandler(BattleZones plugin)
    {
        init(plugin);
    }
    
    private void init(BattleZones plugin) {
        this.plugin                     = plugin;
        playerPvPMap                    = new HashMap<String, Boolean>();
        playerZoneMap                   = new HashMap<String, String>();
    }
    
    public boolean isGlobalPVPEnabled(World world)
    {
        return world.getPVP();
    }
    
    public void setGlobalPVP(World world, boolean enabled)
    {
        world.setPVP(enabled);
    }
    
    public boolean isPlayerPVPEnabled(Player player)
    {
        return playerPvPMap.get(player.getName());
    }
    
    public boolean playerHasPVPPermissions(Player player)
    {
        return player.hasPermission("battlezones.pvp");
    }
    
    public void setPlayerPVP(Player player, boolean enabled)
    {
        if (!playerHasPVPPermissions(player)) return;
        playerPvPMap.put(player.getName(), enabled);
    }
    
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

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        super.onPlayerQuit(event);
        playerPvPMap.remove(event.getPlayer().getName());
        playerZoneMap.remove(event.getPlayer().getName());
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
    }
    
    

}
