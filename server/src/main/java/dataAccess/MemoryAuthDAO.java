package dataAccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO {

    Collection<AuthData> db;

    public MemoryAuthDAO() {
        db = new ArrayList<>();
    }

    @Override
    public void addAuth(AuthData authData) {
        db.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData authData : db) {
            if (authData.authToken().equals(authToken)) {
                db.remove(authData);
                break;
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : db) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Auth Token does not exist: " + authToken);
    }

    @Override
    public void clear() {
        db = new ArrayList<>();
    }
}