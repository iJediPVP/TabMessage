package me.ijedi.tabmessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {

    //Variables
    private static TabList tabList;
    private HashMap<String, SimpleDateFormat> tabArgs = new HashMap<String, SimpleDateFormat>(){{
        put("<d1>", new SimpleDateFormat("MM/dd/yyyy"));
        put("<d2>", new SimpleDateFormat("dd/MM/yyyy"));
        put("<t1>", new SimpleDateFormat("hh:mm:ss")); //12 hour time
        put("<t2>", new SimpleDateFormat("HH:mm:ss")); //24 hour time
    }};
    private List<String> headerList, footerList = new ArrayList<>();
    private char colorChar;
    boolean headerAnimated, footerAnimated;

    //Enabled
    @Override
    public void onEnable(){
        tabList = new TabList();

        //Config
        saveDefaultConfig();
        if(getConfig().getBoolean("enabled")){
            //Log
            this.getLogger().info("TabMessage has been enabled!");
            configReload();

            //Timer
            new BukkitRunnable(){
                int headInt = 0, footInt = 0;

                @Override
                public void run() {
                    Date date = new Date();
                    String header, footer;

                    //If animated is enabled, try to get the next string in the list. Otherwise try to get the first string
                    if(headerAnimated){
                        headInt = getNextInt(headInt, headerList);
                        header = headerList.get(headInt);
                    }else{
                        try{
                            header = headerList.get(0);
                        }catch(IndexOutOfBoundsException e){
                            header = "";
                        }
                    }
                    if(footerAnimated){
                        footInt = getNextInt(footInt, footerList);
                        footer = footerList.get(footInt);
                    }else{
                        try{
                            footer = footerList.get(0);
                        }catch(IndexOutOfBoundsException e){
                            footer = "";
                        }
                    }

                    //Replace args
                    for(String arg : tabArgs.keySet()){
                        header = header.replaceAll(arg, tabArgs.get(arg).format(date));
                        footer = footer.replace(arg, tabArgs.get(arg).format(date));
                    }
                    header = ChatColor.translateAlternateColorCodes(colorChar, header);
                    footer = ChatColor.translateAlternateColorCodes(colorChar, footer);

                    //Set messages
                    tabList.setHeader(header);
                    tabList.setFooter(footer);

                    //Send to all players
                    for(Player player : Bukkit.getOnlinePlayers()){
                        tabList.sendTabList(player);
                    }
                }
            }.runTaskTimer(this, 0l, 1 * 20l);
        }else{
            //Log
            this.getLogger().info("TabMessage was NOT enabled!");
        }
    }

    //Disabled
    @Override
    public void onDisable(){
        //Log
        this.getLogger().info("TabMessage has been disabled!");
    }

    //Command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        //Check command
        String cmd = command.getName().toUpperCase();
        if(cmd.equals("TABMESSAGE")){

            //Check for reload arg
            if(args.length == 1){
                String arg1 = args[0].toUpperCase();
                if(arg1.equals("RELOAD")){
                    //Check for perms
                    if(!sender.hasPermission("tabmessage.reload")){
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                        return true;
                    }

                    //Reload config
                    this.reloadConfig();
                    configReload();
                    sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[TabMessage] " + ChatColor.RED + "TabMessage config has been reloaded.");
                    return true;
                }
            }
        }
        return true;
    }

    //Set strings
    public void configReload(){
        //Get header & footer lists
        headerList = getConfig().getStringList("header.messages");
        footerList = getConfig().getStringList("footer.messages");

        //Get animated
        headerAnimated = getConfig().getBoolean("header.animated");
        footerAnimated = getConfig().getBoolean("footer.animated");

        //Get color char
        colorChar = getConfig().getString("colorSymbol").charAt(0);

    }

    //Get next int
    public int getNextInt(int x, List<String> list){
        try{
            list.get(x += 1);
            return x;
        }catch(IndexOutOfBoundsException e){
            return 0;
        }
    }
}
