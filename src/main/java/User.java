public class User {


    private int userId;
    private String userGuid;
    private String userName;

    public User(int userId, String userGuid, String userName) {
        this.userId = userId;
        this.userGuid = userGuid;
        this.userName = userName;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public void printUserDetails() {
        System.out.println("[" +
                "userId=" + userId +
                ", userGuid='" + userGuid +
                ", userName='" + userName + "],");
    }
}
