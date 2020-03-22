import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static final String CONNECTION = "jdbc:mysql://localhost:5432/susers_schema";

    private Connection connection;

    private DataSource ds;

    public UserDao(DataSource ds) {
        this.ds = ds;
    }


    public void openConnection() {
        try {
            String mysqlPasswd = System.getenv("MYSQL_PASSWD");
            if (mysqlPasswd != null)
                connection = DriverManager.getConnection(CONNECTION, "root", mysqlPasswd);


        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    public User getUserData(int id) throws SQLException {


        try {
            this.openConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT id, type, param1, param2, param3 FROM queue_item WHERE id = ? ");
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();
            if(!rs.first()){
                return null;
            }

            User u= new User(rs.getInt(1),rs.getString(2),  rs.getString(3));

            connection.close();
            stmt.close();
            return u;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException();

        }
    }



    public void insertUser(User user) {
        if (user != null) {
            try {
                this.openConnection();
                PreparedStatement prepStmt = connection.prepareStatement("INSERT INTO susers (USER_ID, USER_GUID, USER_NAME) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                prepStmt.setInt(1, user.getUserId());
                prepStmt.setString(2, user.getUserGuid());
                prepStmt.setString(3, user.getUserName());
                int result = prepStmt.executeUpdate();
                this.connection.close();

            } catch (SQLException e) {
                System.out.println(e.getMessage());

            }
        }

    }
}

