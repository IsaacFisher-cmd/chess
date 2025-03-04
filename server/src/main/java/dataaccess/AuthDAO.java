package dataaccess;

import model.AuthData;

public interface AuthDAO {
    // Methods for accessing AuthData
    void addAuth(AuthData authData);

    void deleteAuth(String authToken);

    AuthData getAuth(String authToken) throws DataAccessException;

    void clear();
}
