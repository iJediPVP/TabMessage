package me.ijedi.tabmessage;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TabList {

    //Variables
    private String headerStr = "";
    private String footerStr = "";
    private IChatBaseComponent headerChat, footerChat;
    private PacketPlayOutPlayerListHeaderFooter tabListPacket;

    //Set header
    public void setHeader(String headerStr){
        this.headerStr = headerStr;
        headerChat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + headerStr + "\"}");
        tabListPacket = new PacketPlayOutPlayerListHeaderFooter(headerChat);

        //Set footer
        setFooter(footerStr);
    }

    //Set footer
    public void setFooter(String footerStr){
        this.footerStr = footerStr;
        footerChat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footerStr + "\"}");
        try {
            Field field = tabListPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(tabListPacket, footerChat);

        }catch(NullPointerException npe){ //Packet is null
            //Init packet and set footer
            headerChat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + headerStr + "\"}");
            tabListPacket = new PacketPlayOutPlayerListHeaderFooter(headerChat);
            setFooter(footerStr);

        }catch(NoSuchFieldException nfe){
            nfe.printStackTrace();
        }catch(IllegalAccessException iae){
            iae.printStackTrace();
        }
    }

    //Send packet
    public void sendTabList(Player player){
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(tabListPacket);
    }
}
