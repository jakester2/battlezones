/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.util.logging.Level;
import javax.vecmath.Point3i;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * This class handles the creation of zones. It will ask the user to select two
 * blocks and use their locations to create a cuboid then register it as a PvP zone.
 * 
 * @author Jacob Tyo
 * @version 12/10/2011
 */
public class CuboidHandler extends PlayerListener {
    public BattleZones plugin;
    public boolean isWaiting;
    public CommandSender sender;
    public int requesting;
    public Point3i pos1;
    public Point3i pos2;
    public String zoneName;
    public String worldName;
    
    /**
     * Create a new instance of {@code CuboidHandler}.
     * @param plugin Parent {@link BattleZones} instance.
     * @param sender Source of the command
     * @param zoneName Name of new zone to create
     * @param worldName Name of world the zone will reside in
     */
    public CuboidHandler(BattleZones plugin, CommandSender sender, String zoneName, String worldName)
    {
        init(plugin, sender, zoneName, worldName);
        make();
    }
    
    /**
     * Initialize all variables.
     */
    private void init(BattleZones plugin, CommandSender sender, String zoneName, String worldName) {
        this.plugin                     = plugin;
        this.sender                     = sender;
        this.zoneName                   = zoneName;
        this.worldName                  = worldName;
    }
    
    /**
     * Provide initial setup for CuboidHandler. Apply class settings here.
     */
    private void make() {
        plugin.manager.registerEvent(Event.Type.PLAYER_INTERACT, this, Event.Priority.Low, plugin);
    }
    
    /**
     * This method creates a {@code Thread} that requests the player to select
     * two blocks that will ultimately become position1 and position2 of the new
     * cuboid.
     */
    public void requestCuboid()
    {
        if (sender.getServer().getWorld(worldName) == null) {
            Message.send(sender, Message.LEVEL_ERROR, "The world '" + worldName + "' does not exist...");
            return;
        }
        Thread requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message.send(sender, Message.LEVEL_SPECIAL, "Selecting the bounds for " + zoneName);
                requestBlockPos(1);
                requestBlockPos(2);
                if (pos1.x > pos2.x) {
                    int buffer = pos2.x;
                    pos2.x = pos1.x;
                    pos1.x = buffer;
                }
                if (pos1.y < pos2.y) {
                    int buffer = pos2.y;
                    pos2.y = pos1.y;
                    pos1.y = buffer;
                }
                if (pos1.z > pos2.z) {
                    int buffer = pos2.z;
                    pos2.z = pos1.z;
                    pos1.z = buffer;
                }
                plugin.zoneConfig.addZone(sender, zoneName, worldName, true, pos1, pos2);
            }
        });
        requestThread.start();
    }
    
    /**
     * Request a specific block for the formation of the cuboid.
     * @param requesting Which block to request. Between 1 and 2.
     */
    public void requestBlockPos(int requesting)
    {
        if (requesting < 1 || requesting > 2) return;
        isWaiting = true;
        this.requesting = requesting;
        Message.send(sender, Message.LEVEL_SPECIAL, "Punch a block to set position " + this.requesting + "...");
        while (isWaiting) {
            switch (requesting) {
                case 1: if (pos1 != null) isWaiting = false; break;
                case 2: if (pos2 != null) isWaiting = false; break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                BattleZones.LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Called when a player clicks an entity.
     * @param event Relevant event details
     */
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
        if (!sender.getName().equals(event.getPlayer().getName())) return;
        if (isWaiting) {
            Block block = event.getClickedBlock();
            switch (requesting) {
                case 1:
                    pos1 = new Point3i(block.getX(), block.getY(), block.getZ());
                    Message.send(event.getPlayer(), Message.LEVEL_SUCCESS, "Position 1 set!");
                    break;
                case 2:
                    pos2 = new Point3i(block.getX(), block.getY(), block.getZ());
                    Message.send(event.getPlayer(), Message.LEVEL_SUCCESS, "Position 2 set!");
                    break;
            }
        }
    }

}
