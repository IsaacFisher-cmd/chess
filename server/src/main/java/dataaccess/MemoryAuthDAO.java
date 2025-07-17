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
}
