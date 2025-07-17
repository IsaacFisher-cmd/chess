package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        if(users.containsKey(userData.username())){
            throw new DataAccessException("already taken");
        }
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException{
        users.clear();
    }
}
