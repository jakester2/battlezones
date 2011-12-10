/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify
 * and use the following code however you wish, as long as you give credit to its
 * original author.
 */
package js2.battlezones;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 * @author Jacob Tyo
 * @version 12/08/2011
 */
public class BattleZonesCommandExecutor implements CommandExecutor {
    public static final String CMD_USAGE_HELP           = "/bz help";
    public static final String CMD_USAGE_ADD            = "/bz add [worldName] [zoneName]";
    public static final String CMD_USAGE_ADD_CONSOLE    = "/bz add [worldName] [zoneName] [x1, y1, z1, x2, y2, z2]";
    public static final String CMD_USAGE_REMOVE         = "/bz remove [worldName] [zoneName]";
    public static final String CMD_USAGE_ENABLE         = "/bz enable [worldName] [zoneName]";
    public static final String CMD_USAGE_DISABLE        = "/bz disable [worldName] [zoneName]";
    public static final String CMD_USAGE_LIST           = "/bz list";
    public static final String CMD_DESC_HELP            = "Prints this menu.";
    public static final String CMD_DESC_ADD             = "Creates a new PvP zone.";
    public static final String CMD_DESC_REMOVE          = "Removes a PvP zone.";
    public static final String CMD_DESC_ENABLE          = "Enables the target PvP zone.";
    public static final String CMD_DESC_DISABLE         = "Disables the target PvP zone.";
    public static final String CMD_DESC_LIST            = "Lists all PvP zones and their active states.";
    
    private BattleZones plugin;
    private Player player;

    public BattleZonesCommandExecutor(BattleZones plugin) {
        init(plugin);
    }
    
    private void init(BattleZones plugin) {
        this.plugin                     = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] strings) {
        if (cmd.getName().equalsIgnoreCase("bz")) { // If the player typed /bz then do the following...
            if (sender instanceof Player) {
                player = (Player) sender;
            }
            if (strings.length == 0) parseCommand(sender, cmd, "help", new String[]{"help"});
            else                     parseCommand(sender, cmd, string, strings);
            return true;
        }
        return false;
    }

    private void printHelp(CommandSender sender, Command cmd, String string, String[] strings) {
        Message.sendRaw(sender, "------------ " + plugin.getDescription().getFullName() + " ------------");
        Message.sendRaw(sender, "- " + CMD_USAGE_HELP + " - " + CMD_DESC_HELP + "");
        Message.sendRaw(sender, "- " + (player == null ? CMD_USAGE_ADD_CONSOLE : CMD_USAGE_ADD) + " - " + CMD_DESC_ADD + "");
        Message.sendRaw(sender, "- " + CMD_USAGE_REMOVE + " - " + CMD_DESC_REMOVE + "");
        Message.sendRaw(sender, "- " + CMD_USAGE_ENABLE + " - " + CMD_DESC_ENABLE + "");
        Message.sendRaw(sender, "- " + CMD_USAGE_DISABLE + " - " + CMD_DESC_DISABLE + "");
        Message.sendRaw(sender, "- " + CMD_USAGE_LIST + " - " + CMD_DESC_LIST + "");
    }

    private void parseCommand(CommandSender sender, Command cmd, String string, String[] strings) {
        if (!sender.hasPermission("battlezones." + strings[0])) {
            Message.sendRaw(sender, Message.LEVEL_ERROR, "You do not have permissions for that.");
            return;
        }
        if (strings[0].equalsIgnoreCase("help")) // Print the help screen.
        {
            printHelp(sender, cmd, string, strings);
        }
        else if (strings[0].equalsIgnoreCase("add")) // Add a new zone.
        {
            if (player == null) {
                Message.send(sender, Message.LEVEL_ERROR, "This command can only be ran by a player.");
            } else {
                if (strings.length != 3) Message.sendRaw(sender, "- " + CMD_USAGE_ADD + " - " + CMD_DESC_ADD + "");
                else {
                    CuboidHandler cuboidHandler = new CuboidHandler(plugin, sender, strings[2], strings[1]);
                    cuboidHandler.requestCuboid();
                }
            }
        }
        else if (strings[0].equalsIgnoreCase("remove")) // Remove an existing zone.
        {
            if (strings.length != 3)    Message.sendRaw(sender, "- " + CMD_USAGE_REMOVE + " - " + CMD_DESC_REMOVE + "");
            else                        plugin.zoneConfig.removeZone(sender, strings[2], strings[1]);
        }
        else if (strings[0].equalsIgnoreCase("enable")) // Enable target zone.
        {
            if (strings.length != 3) Message.sendRaw(sender, "- " + CMD_USAGE_ENABLE + " - " + CMD_DESC_ENABLE + "");
            else {
                if (sender.getServer().getWorld(strings[1]) == null) {
                    Message.send(sender, Message.LEVEL_ERROR, "The world '" + strings[1] + "' does not exist...");
                } else {
                    if (plugin.zoneConfig.getConfig().contains("zones." + strings[1] + "." + strings[2])) {
                        plugin.zoneConfig.getConfig().set("zones." + strings[1] + "." + strings[2] + ".enabled", true);
                        try {
                            plugin.zoneConfig.saveZones();
                        } catch (IOException ex) {
                            BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
                            if (sender != null) Message.send(sender, Message.LEVEL_ERROR, plugin.getDescription().getName() + " encountered an error. Please check the log.");
                        }
                        Message.send(sender, Message.LEVEL_SUCCESS, "[" + strings[1] + "] " + strings[2] + ": PvP zone enabled!");
                    } else {
                        Message.send(sender, Message.LEVEL_ERROR, "The zone '" + strings[2] + "' does not exist...");
                    }
                }
                
            }                    
        }
        else if (strings[0].equalsIgnoreCase("disable")) // Disable target zone.
        {
            if (strings.length != 3) Message.sendRaw(sender, "- " + CMD_USAGE_DISABLE + " - " + CMD_DESC_DISABLE + "");
            else {
                if (sender.getServer().getWorld(strings[1]) == null) {
                    Message.send(sender, Message.LEVEL_ERROR, "The world '" + strings[1] + "' does not exist...");
                } else {
                    if (plugin.zoneConfig.getConfig().contains("zones." + strings[1] + "." + strings[2])) {
                        plugin.zoneConfig.getConfig().set("zones." + strings[1] + "." + strings[2] + ".enabled", false);
                        try {
                            plugin.zoneConfig.saveZones();
                        } catch (IOException ex) {
                            BattleZones.LOG.log(Level.INFO, (Message.getPrefix() + " Error: Cannot save zone config. Exception: " + ex));
                            if (sender != null) Message.send(sender, Message.LEVEL_ERROR, plugin.getDescription().getName() + " encountered an error. Please check the log.");
                        }
                        Message.send(sender, Message.LEVEL_SUCCESS, "[" + strings[1] + "] " + strings[2] + ": PvP zone " + ChatColor.RED + "disabled.");
                    } else {
                        Message.send(sender, Message.LEVEL_ERROR, "The zone '" + strings[2] + "' does not exist...");
                    }
                }
                
            }
        }
        else if (strings[0].equalsIgnoreCase("list")) // List all available zones.
        {
            if (plugin.zoneConfig.isEmpty()) {
                Message.send(sender, Message.LEVEL_ERROR, "No zones loaded...");
            } else {
                Message.sendRaw(sender, "------------ Available PvP Zones ------------");
                for (Iterator<String> it = plugin.nestedZones.iterator(); it.hasNext();) {
                    String[] zoneData = it.next().split("\\.");
                    boolean isEnabled = plugin.zoneConfig.getConfig().getBoolean("zones." + zoneData[0] + "." + zoneData[1] + ".enabled");
                    ChatColor prefix = (isEnabled) ? ChatColor.GREEN : ChatColor.RED;
                    Message.sendRaw(sender, prefix + "- [" + zoneData[0] + "] " + zoneData[1] + " - " + ((isEnabled) ? "Enabled" : "Disabled"));
                }
            }
        }
        else // If all else fails...
        {
            Message.send(sender, "Cannot understand: " + strings[0]);
        }
    }

}
