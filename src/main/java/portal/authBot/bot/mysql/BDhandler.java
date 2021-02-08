package portal.authBot.bot.mysql;

import portal.authBot.bot.config.BotConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BDhandler {

    static BDClass bdClass = new BDClass();

    public static boolean giveMoney(String nick, int value){
        try {
            PreparedStatement preparedStatement = bdClass.getConnection().prepareStatement("UPDATE mcr_iconomy SET realmoney = realmoney + ? WHERE login = ?");
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, nick);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean removeMoney(String nick, int value){
        try {
            if(getBal(nick) >= value){
                PreparedStatement preparedStatement = bdClass.getConnection().prepareStatement("UPDATE mcr_iconomy SET realmoney = realmoney - ? WHERE login = ?");
                preparedStatement.setInt(1, value);
                preparedStatement.setString(2, nick);
                preparedStatement.executeUpdate();
                return true;
            }else return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static int getBal(String nick) {
        try {
            PreparedStatement preparedStatement = bdClass.getConnection().prepareStatement("SELECT * FROM mcr_iconomy WHERE login = ?");
            preparedStatement.setString(1, nick);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int bal = resultSet.getInt("realmoney");
            return bal;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public static int guardPay(int value){
        int count = 0;
        List<String> users = BotConfig.getGuardList();
        for(String user : users){
            count++;
            giveMoney(user, value);
        }
        return count;
    }
}
