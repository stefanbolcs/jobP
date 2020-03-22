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


    public boolean openConnection() {
        try {
            String mysqlPasswd = System.getenv("MYSQL_PASSWD");
            if (mysqlPasswd != null)
                connection = DriverManager.getConnection(CONNECTION, "root", mysqlPasswd);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public void getUserData() {


        try {
            this.openConnection();
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, type, param1, param2, param3 FROM queue_item WHERE processed = 0 ORDER BY id ASC");
            Boolean hasPendingItems = false;
            try {
                while (rs.next()) {
                    hasPendingItems = true;
                    // Execute job.
                    Class<?> commandClass = Class.forName(rs.getString("type"));
                    Constructor<?> constructor = commandClass.getDeclaredConstructor(Connection.class, List.class);
                    List<String> parameterList = new ArrayList<String>(3);

                    parameterList.add(rs.getString("param1"));
                    parameterList.add(rs.getString("param2"));
                    parameterList.add(rs.getString("param3"));
                    JobProcessor.Command command = (JobProcessor.Command) constructor.newInstance(connection, parameterList);
                    command.execute();
                    // Update queue item.
                    PreparedStatement pstmt = connection.prepareStatement(
                            "UPDATE queue_item SET processed = 1 WHERE id = ?"
                    );
                    pstmt.setInt(1, rs.getInt("id"));
                    pstmt.executeUpdate();
                }
                if (!hasPendingItems) {
                    System.out.println("No commands to execute.");
                }
            } finally {
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
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

