package eu.sqooss.webui;

import java.util.Dictionary;

public class User {
    
    //java.util.Dictionary allUsers;
    Dictionary allUsers;
    Integer currentUserId;
    String currentUser;

    /* Sets some sample data
     * This has to go, the user data will
     * be retrieved from the database at
     * some point in the near future.
     */
    public User () {
        // Sample data
        allUsers.put(new Integer(0), "nobody");
        allUsers.put(new Integer(1), "admin");
        allUsers.put(new Integer(2), "padams");
        allUsers.put(new Integer(3), "adridg");
        allUsers.put(new Integer(4), "sebas");
        currentUserId = new Integer(0);
    }
    
    public Integer getCurrentUserId () {
        return currentUserId;
    }
    
    public void setCurrentUserId ( Integer userId ) {
        currentUserId = userId;
    }
    
    public String getCurrentUser () {
        return getUser(currentUserId);
    }

    public void setCurrentUser( String user) {
        currentUser = user;
    }

    public String getUser (Integer id) {
        return (String)allUsers.get(id);
    }
    
    public Dictionary getAllUsers() {
        return allUsers;
    }

    public void setAllUsers ( Dictionary users) {
        allUsers = users;
        return;
    }
    
    public String getInfo () {
        String info = "Currently, there are " + allUsers.size() + " users registered.";
        return info;
    }
}
