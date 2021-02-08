package portal.authBot.bot.mysql;

import portal.authBot.Main;

import java.sql.*;

public class BDClass {

    private final String DB_NAME = Main.getInstance().getConfig().getString("db_name");
    private final String DB_HOST = Main.getInstance().getConfig().getString("db_host");
    private final String PASS = Main.getInstance().getConfig().getString("db_pass");
    private final String USER = Main.getInstance().getConfig().getString("db_user");
    private final String URL = "jdbc:mysql://" + DB_HOST + "/" + DB_NAME;

    private static Connection connection;

    public BDClass(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        return connection;
    }
}
