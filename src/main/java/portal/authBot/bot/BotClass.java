package portal.authBot.bot;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import portal.authBot.Main;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.events.BotEvents;

import javax.security.auth.login.LoginException;

public class BotClass {
    private static JDA jda;
    static String token = Main.getInstance().getConfig().getString("bot-token");
    public static void main() throws Exception {
            jda = JDABuilder.createDefault(token)
                    .setBulkDeleteSplittingEnabled(false).setCompression(Compression.NONE)
                    .build();
        jda.addEventListener(new BotEvents());
    }

    public static JDA getJda(){
        return jda;
    }

}
