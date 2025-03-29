package application;



public class User {
    private String userName;
    private String password;
    private String role;
    private String fullName;
    private String email;

    // Constructor for user with full details
    public User(String userName, String fullName, String email, String role) {
        this.userName = userName;
        //this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }

    // Constructor for user without full details
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}