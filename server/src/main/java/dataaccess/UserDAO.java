package dataaccess;

import model.UserData;

public interface UserDAO {
    // Methods for accessing UserData
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    boolean authenticateUser(String username, String password) throws DataAccessException;

    void clear();
}