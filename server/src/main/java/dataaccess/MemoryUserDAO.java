package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDAO implements UserDAO {

    private Collection<UserData> db;

    public MemoryUserDAO() {
        db = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : db) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found: " + username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            getUser(user.username());
        }
        catch (DataAccessException e) {
            db.add(user);
            return;
        }

        throw new DataAccessException("User already exists: " + user.username());
    }


    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        boolean userExists = false;
        for (UserData user : db) {
            if (user.username().equals(username)) {
                userExists = true;
            }
            if (user.username().equals(username) &&
                    user.password().equals(password)) {
                return true;
            }
        }
        if (userExists) {
            return false;
        }
        else {
            throw new DataAccessException("User does not exist: " + username);
        }
    }

    @Override
    public void clear() {
        db = new ArrayList<>();
    }
}