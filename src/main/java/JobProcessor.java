import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobProcessor implements Runnable {

    public static abstract class Command {

        public Connection getConnection() {
            return connection;
        }

        protected final Connection connection;

        protected Command(Connection connection) {
            this.connection = connection;
        }



        public abstract void execute() throws SQLException;
    }

    public static class RemoveAll extends Command {

        public RemoveAll(Connection conn, List<String> params) {
            super(conn);
        }

        @Override
        public void execute() throws SQLException {
            Statement stm = connection.createStatement();
            stm.executeUpdate("TRUNCATE TABLE susers");
        }
    }

    public static class PrintAll extends Command {

        public PrintAll(java.sql.Connection conn, java.util.List<String> params) {
            super(conn);
        }

        @Override
        public void execute() throws SQLException {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT USER_ID, USER_GUID, USER_NAME FROM susers");

            try {
                while (rs.next()) {

                    User userToPrint = new User(rs.getInt("USER_ID"),rs.getString("USER_GUID"),rs.getString("USER_NAME"));
                    userToPrint.printUserDetails();
                }
            } finally {
                rs.close();
            }
            System.out.println("--------------------------------------------");
        }
    }

    public static class AddUser extends Command {

        final User userToBeAdded;

        public AddUser(Connection conn, List<String> params) {
            super(conn);
             userToBeAdded= new User(Integer.parseInt(params.get(0)),params.get(1),params.get(2));
        }

        @Override
        public void execute() throws SQLException {
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO susers (USER_ID, USER_GUID, USER_NAME) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pstmt.setInt(1, userToBeAdded.getUserId());
            pstmt.setString(2, userToBeAdded.getUserGuid());
            pstmt.setString(3, userToBeAdded.getUserName());
            pstmt.executeUpdate();
            System.out.print("user added with name "+userToBeAdded.getUserName());
        }
    }

    public void run() {
        Connection connection = null;
        try {
            String mysqlPasswd = System.getenv("MYSQL_PASSWD");
            if(mysqlPasswd !=null)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:5432/susers_schema","root",mysqlPasswd);

            if (connection == null) {
                System.err.println("FATAL: No database connection.");
                return;
            }




        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
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
                        Command command = (Command)constructor.newInstance(connection, parameterList);
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

                    try {
                        if(rs !=null){
                            rs.close();
                        }
                    }catch(SQLException ex){
                        ex.printStackTrace();
                    }



                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Rolling back data here....");
                try{
                    if(connection!=null)
                        connection.rollback();
                }catch(SQLException se2){
                    se2.printStackTrace();
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {

            }
        }
    }

    public static void main(String[] args) {

        new Thread(new JobProcessor()).start();


        //To test
    }

}
