package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        auths.put(authData.authToken(), authData);
    }

    @Override
    public void clear() throws DataAccessException{
        auths.clear();
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        return auths.get(authToken);
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException{
        auths.remove(authToken);
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException{
        return auths.get(authToken).username();
    }
}
