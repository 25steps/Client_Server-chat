package Server;

import java.sql.*;

public class AuthService {
    private static Connection connection; // объект который отвечает за связь между бд и арр
    private static Statement stmt; // объект, который получает запросы от арр и заставляет выполнеться в бд

    public static void connect() throws SQLException { // метод подключения к базе данных
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:userDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static String getNickByLoginAndPass(String login, String pass) throws SQLException {
        String query = String.format("SELECT nickname from main " +                               //формируем запрос
                                    "WHERE login = '%s' " + "and password = '%s'", login, pass);
        ResultSet rs = stmt.executeQuery(query);//ResultSet отвечает за получение информации от запроса...executeQuery - возвращение какого-то результата
        if (rs.next()){ //true если вернёт значение в таблице
            return rs.getString(1); // возвращает результат запроса
        }
        return null;
    }
    public static String checkLoginAndNick(String login, String nick) throws SQLException{
        String query = String.format("SELECT " +                               //формируем запрос
                "(CASE WHEN nickname = '%s' OR login = '%s' " +
                "THEN 'noval' " +
                "ELSE 'val' " +
                "END) as checkName " + "from main LIMIT 1", nick, login);
        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()){ //true если вернёт значение в таблице
            return rs.getString(1); // возвращает результат запроса
        }

        return null;
    }
    public static boolean addLoginPassNick(String log, String pass, String nick) throws SQLException {
        String query = String.format("INSERT INTO main (login, password, nickname) " +
                "VALUES ('%s', '%s', '%s')", log, pass, nick);
        int result = stmt.executeUpdate(query);
        if (result > 0)
            return true;
        return false;
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
