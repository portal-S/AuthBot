package portal.authBot.bot.executor;

import com.avaje.ebeaninternal.server.jmx.MAdminAutofetch;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import portal.authBot.Main;
import portal.authBot.bot.config.BotConfig;
import portal.authBot.bot.handler.BotHandler;
import portal.authBot.bot.mysql.BDClass;
import portal.authBot.bot.mysql.BDhandler;
import ru.portal.seasons.player.PlayerInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commands implements CommandExecutor {
    BotConfig botConfig = new BotConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player!");
            return true;
        }
            if (sender instanceof Player && (sender.isOp() || PlayerInfo.hasWhiteList(sender.getName()))) {
                if (args.length == 0) {
                    sender.sendMessage(Main.replace("&7-------------------------------------------------------"));
                    sender.sendMessage(Main.replace("&3/bot remove ник - снять привязку с аккаунта"));
                    sender.sendMessage(Main.replace("&3/bot info ник - получить информацию об игроке"));
                    sender.sendMessage(Main.replace("&3/bot dispatch сообщение - отправить привязанным аккаунтам Discord сообщение"));
                    sender.sendMessage(Main.replace("&3/bot pay ник сумма - начислить игроку деньги"));
                    sender.sendMessage(Main.replace("&3/bot guardpay сумма - начислить всем привязанным игрокам деньги"));
                    sender.sendMessage(Main.replace("&3/bot take ник сумма - снять со счёта игрока деньги"));
                    sender.sendMessage(Main.replace("&3/bot bal ник - узнать баланс игрока"));
                    sender.sendMessage(Main.replace("&3/bot reload - перезагрузка конфигураций"));
                    sender.sendMessage(Main.replace("&7-------------------------------------------------------"));
                } else if(args[0].equals("remove")){
                    if(args.length == 2){
                        botConfig.removeUser(args[1], sender);
                    }else sender.sendMessage(Main.replace("&3/bot remove ник - снять привязку с аккаунта"));
                }
                else if(args[0].equals("dispatch")){
                    if(args.length > 1){
                        String message = "";
                        for(int i = 1; i < args.length ; i++){
                            message = message + " " + args[i];
                        }
                        botConfig.userDispatch(message);
                        sender.sendMessage(Main.replace("&aСообщение успешно разослано"));
                    }else sender.sendMessage(Main.replace("&3/bot dispatch сообщение - отправить привязанным аккаунтам Discord сообщение"));
                }
                else if(args[0].equals("info")){
                    if(args.length == 2){
                        if(botConfig.hasPlayer(args[1])){
                            sender.sendMessage(Main.replace("&7Игрок: &3") + args[1]);
                            sender.sendMessage(Main.replace("&7Защита: &3подключена"));
                            sender.sendMessage(Main.replace("&7Discord логин: &3") + botConfig.getNick(args[1]));
                            sender.sendMessage(Main.replace("&7Дата подключения защиты: &3") + BotConfig.firstDate(args[1]));
                            sender.sendMessage(Main.replace("&7Дата обновления защиты: &3") + BotConfig.lastDate(args[1]));
                            sender.sendMessage(Main.replace("&7Привязанные IP игрока:"));
                            for (String ip : BotConfig.getIP(args[1])){
                                sender.sendMessage(Main.replace("&7--- &3") + ip + Main.replace(" &7---"));
                            }
                        } else sender.sendMessage(Main.replace("&cИгрок &6") + args[1] + Main.replace(" &cне подключил аутентификацию"));
                    }else sender.sendMessage(Main.replace("&3/bot info ник - получить информацию об игроке"));
                }
                else if(args[0].equals("pay")){
                    if(args.length == 3){
                        try {
                            if(BDhandler.giveMoney(args[1], Integer.parseInt(args[2]))){
                                sender.sendMessage(Main.replace("&3Игроку &a" + args[1] + Main.replace("&3 успешно начислено &6") + args[2] + "&3 валюты на счёт"));
                            } else sender.sendMessage(Main.replace("&cВозникла ошибка: возможно, такого игрока не существует"));
                            Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Main.replace("&cПеременная &6'" + args[2]) + Main.replace("' &cне является числовой"));
                            e.printStackTrace();
                            return true;
                        }
                    }else sender.sendMessage(Main.replace("&3/bot pay ник сумма - начислить игроку деньги"));
                }
                else if(args[0].equals("guardpay")){
                    if(args.length == 2){
                        try {
                            int count = BDhandler.guardPay(Integer.parseInt(args[1]));
                                sender.sendMessage(Main.replace("&3Игрокам &a(" + count + Main.replace(")&3 успешно начислено &6") + args[1] + "&3 валюты на счёт"));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Main.replace("&cПеременная &6'" + args[1]) + Main.replace("' &cне является числовой"));
                            e.printStackTrace();
                            return true;
                        }
                    }else sender.sendMessage(Main.replace("&3/bot guardpay сумма - начислить всем привязанным игрокам деньги"));
                }
                else if(args[0].equals("take")){
                    if(args.length == 3){
                    try {
                        if(BDhandler.removeMoney(args[1], Integer.parseInt(args[2]))){
                            sender.sendMessage(Main.replace("&3У игрока &a" + args[1] + Main.replace("&3 успешно снято &6") + args[2] + "&3 валюты со счёта"));
                        } else sender.sendMessage(Main.replace("&cВозникла ошибка: возможно, такого игрока не существует, или у указанного игрока не хватает валюты для списания"));
                        Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Main.replace("&cПеременная &6'" + args[2]) + Main.replace("' &cне является числовой"));
                        e.printStackTrace();
                        return true;
                    }
                    }else sender.sendMessage(Main.replace("&3/bot take ник сумма - снять со счёта игрока деньги"));
                }
                else if(args[0].equals("bal")){
                    if(args.length == 2){
                        sender.sendMessage(Main.replace("&3Баланс игрока &a") + args[1] + Main.replace(" &3составляет &6") + BDhandler.getBal(args[1]));
                    }else sender.sendMessage(Main.replace("&3/bot bal ник - узнать баланс игрока"));
                }
                else if(args[0].equals("reload")){
                    sender.sendMessage(ChatColor.BLUE + "Конфигурация перезагружена");
                    Main.getInstance().reloadConfig();
                    Main.getInstance().saveConfig();
                    Main.getInstance().load();
                }
                else {
                    sender.sendMessage(Main.replace("&7-------------------------------------------------------"));
                    sender.sendMessage(Main.replace("&3/bot remove ник - снять привязку с аккаунта"));
                    sender.sendMessage(Main.replace("&3/bot info ник - получить информацию об игроке"));
                    sender.sendMessage(Main.replace("&3/bot dispatch сообщение - отправить привязанным аккаунтам Discord сообщение"));
                    sender.sendMessage(Main.replace("&3/bot pay ник сумма - начислить игроку деньги"));
                    sender.sendMessage(Main.replace("&3/bot guardpay сумма - начислить всем привязанным игрокам деньги"));
                    sender.sendMessage(Main.replace("&3/bot take ник сумма - снять со счёта игрока деньги"));
                    sender.sendMessage(Main.replace("&3/bot bal ник - узнать баланс игрока"));
                    sender.sendMessage(Main.replace("&3/bot reload - перезагрузка конфигураций"));
                    sender.sendMessage(Main.replace("&7-------------------------------------------------------"));
                }
            } else {
                sender.sendMessage(Main.replace("&cУ вас нет прав на выполнение данной команды"));
            }
        return false;
    }
}


