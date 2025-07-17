package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDao, AuthDAO authDao){
        this.userDAO = userDao;
        this.authDAO = authDao;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException{
        String username = request.username();
        String pass = request.password();
        String email = request.email();

        if(username == null || pass == null || email == null){
            throw new DataAccessException("bad request");
        }

        if(userDAO.getUser(username) != null){
            throw new DataAccessException("already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        userDAO.createUser(user);

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token, user.username()));

        return new RegisterResult(user.username(), token);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        String username = request.username();
        String pass = request.password();

        if(username == null || pass == null){
            throw new DataAccessException("bad request");
        }

        if(!username.equals(pass)){
            throw new DataAccessException("unauthorized");
        }

        String token = UUID.randomUUID().toString();
        authDAO.createAuth(new AuthData(token,username));

        return new LoginResult(username, token);
    }

    public void clear() throws DataAccessException{
        userDAO.clear();
        authDAO.clear();
    }
}
