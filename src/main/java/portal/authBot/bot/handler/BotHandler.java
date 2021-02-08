package portal.authBot.bot.handler;

import com.github.dreamsmoke.attribute.AttributeBukkit;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import portal.authBot.Main;
import portal.authBot.bot.BotClass;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.events.BotEvents;
import portal.authBot.bot.events.ServerEvents;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

public class BotHandler {

    public void timer(String nick, PrivateChannel channels){
        BotEvents botEvents = new BotEvents();
        Runnable r = () -> {
            HashMap pass = botEvents.getPass();
            if(pass.containsKey(nick)){
                pass.remove(nick);
                botEvents.setPass(pass);
                channels.sendMessageFormat("Ваш код истёк.").queue();
            }
        };
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), r,  1200);
    }
    public void authTimer(String nick, PrivateChannel channels){
        ServerEvents serverEvents = new ServerEvents();
        HashMap auth = serverEvents.getAuth();
        Runnable r = () -> {
            if(hasAuth(nick, String.valueOf(auth.get(nick)))){
                auth.remove(nick);
                serverEvents.setAuth(auth);
                channels.sendMessageFormat("Подтверждение истекло.").queue();
            }
        };
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), r,  1200);
    }
    public void dsDelay(String id){
        BotEvents botEvents = new BotEvents();
        Set del = botEvents.getDelay();
        Runnable r = () -> {
            if(botEvents.getDelay().contains(id)){
                del.remove(id);
                botEvents.setDelay(del);
            }
        };
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), r,  600);
    }

    public boolean hasDelay(String id){
        BotEvents botEvents = new BotEvents();
        Set del = botEvents.getDelay();
        if(del.contains(id)) return true;
        return false;
    }

    public boolean hasAuth(String nick, String ip){
        ServerEvents serverEvents = new ServerEvents();
        HashMap auth = serverEvents.getAuth();
        if(auth.containsKey(nick)){
            String ips = String.valueOf(auth.get(nick));
            if(ips.equals(ip)) return true;
        } else return false;
        return false;
    }
    public boolean hasCode(String nick){
        BotEvents botEvents = new BotEvents();
        HashMap pass = botEvents.getPass();
        if(pass.containsKey(nick)) return true;
        else return false;
    }

    public static void autoMessage(){
        for(String message : BotConfig.getMessages()){
            String[] mes = message.split(";");
            Long time = Long.parseLong(mes[1]) * 20;

            Runnable r = () -> {
                BotConfig.userDispatch(mes[0]);
            };
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), r,  time, time);
        }
    }

}
