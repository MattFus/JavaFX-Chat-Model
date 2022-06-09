package application.whatsup.Server;

import application.whatsup.Common.User;

import java.sql.*;

public class DataBase { //I deleted encryption framework, add the one you like the most

    private static DataBase instance = null;
    private Connection con = null;

    private DataBase(){
        try {
            Class.forName("org.sqlite.JDBC");   //LOADING SQLITE DRIVER TO MEMORY
            String url = "jdbc:sqlite:Database.db"; //STARTING DB CONNECTION
            con = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Cannot connect to database");
            this.instance = null;
            con = null;
        }
    }

    public static DataBase getInstance() {
        if(instance == null)
            instance = new DataBase();
        return instance;
    }

    //
    public synchronized boolean insertUser(User user) throws SQLException {
        if( con == null || con.isClosed() || user == null)
            return false;
        if(existsUser(user.getUsername()))    //CONTROLLO SE ESISTE GIà
            return false;
        PreparedStatement statement = con.prepareStatement("INSERT INTO Users VALUES(?,?,?);");
        statement.setString(1,user.getUsername());
        statement.setString(2,encrypt(user.getPassword()));
        statement.setString(3,user.getEmail());
        statement.executeUpdate();
        statement.close();
        return true;
    }

    public synchronized boolean existsUser(String username) throws SQLException {
        if(con == null || con.isClosed() || username == null)
            return false;

        String query = "SELECT * FROM Users WHERE username=?;";
        PreparedStatement p = con.prepareStatement(query);
        p.setString(1, username);
        ResultSet rs = p.executeQuery();
        boolean result = rs.next();
        p.close();
        return result;
    }

    public synchronized boolean checkUser(User user) throws SQLException {
        if( con == null || con.isClosed() || user == null)
            return false;
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Users WHERE username=?;");
        statement.setString(1,user.getUsername());
        ResultSet result = statement.executeQuery();
        boolean check = false;
        if(result.next()){
            String password = result.getString("password");
            check = BCrypt.checkpw(user.getPassword(),password);
        }
        statement.close();
        return check;
    }

    public String changePsw(String username, String generatedPassword) throws SQLException {
        if(con == null || con.isClosed() || username == null)
            return null;
        String email = "";
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Users WHERE username=?;");
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if(result.next()){
            email = result.getString("email");
            PreparedStatement statement1 = con.prepareStatement("UPDATE Users SET password=? WHERE username=?");
            statement1.setString(1, encrypt(generatedPassword));
            statement1.setString(2, username);
            statement1.executeUpdate();
        }
        return email;
    }
}
