package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;

    void clear() throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void removeAuth(String authToken)  throws DataAccessException;
}
