/*
 * Origonally written by Jacob Tyo for the BattleZones project. You may modify and
 * use the following code however you wish, as long as you give credit to its original
 * author.
 */
package js2.battlezones;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * This class offers a level of abstraction for sending messages to a CommandSender.
 * 
 * @author Jacob Tyo
 * @author 12/06/2011
 */
public class Message {
    public static final int LEVEL_NORMAL = 0;
    public static final int LEVEL_SUCCESS = 1;
    public static final int LEVEL_ERROR = 2;
    public static final int LEVEL_SPECIAL = 3;
    
    /**
     * Sends a new message to the sender. Will print using LEVEL_NORMAL.
     * @param sender Player to send the message.
     * @param message Message to send to the player.
     */
    public static void send(CommandSender sender, String message)
    {
        send(sender, LEVEL_NORMAL, message);
    }
    
    /**
     * Sends a new message to the sender and specify its print level. Will print 
     * using LEVEL_NORMAL.
     * @param sender Player to send the message.
     * @param level Level of importance of the message. This dictates the print 
     * color.
     * @param message Message to send to the player.
     */
    public static void send(CommandSender sender, int level, String message)
    {
        if (level < 0 || level > 3) level = LEVEL_NORMAL;
        switch (level) {
            case LEVEL_NORMAL:  sender.sendMessage(ChatColor.YELLOW + getPrefix() + message);       break;
            case LEVEL_SUCCESS: sender.sendMessage(ChatColor.GREEN + getPrefix() + message);        break;
            case LEVEL_ERROR:   sender.sendMessage(ChatColor.RED + getPrefix() + message);          break;
            case LEVEL_SPECIAL: sender.sendMessage(ChatColor.DARK_PURPLE + getPrefix() + message);  break;
        }
    }
    
    /**
     * Sends a new message to the sender without a prefix. Will print using LEVEL_NORMAL.
     * @param sender Player to send the message.
     * @param message Message to send to the player.
     */
    public static void sendRaw(CommandSender sender, String message)
    {
        sendRaw(sender, LEVEL_NORMAL, message);
    }
    
    /**
     * Sends a new message to the sender without a prefix and specify its print 
     * level. Will print using LEVEL_NORMAL.
     * @param sender Player to send the message.
     * @param level Level of importance of the message. This dictates the print 
     * color.
     * @param message Message to send to the player.
     */
    public static void sendRaw(CommandSender sender, int level, String message)
    {
        if (level < 0 || level > 2) level = LEVEL_NORMAL;
        switch (level) {
            case LEVEL_NORMAL:  sender.sendMessage(ChatColor.YELLOW + message); break;
            case LEVEL_SUCCESS: sender.sendMessage(ChatColor.GREEN + message);  break;
            case LEVEL_ERROR:   sender.sendMessage(ChatColor.RED + message);    break;
        }
    }
    
    /**
     * Returns the plugin message prefix.
     * @return Message Prefix.
     */
    public static String getPrefix()
    {
        return "[BZ] ";
    }
    
}
