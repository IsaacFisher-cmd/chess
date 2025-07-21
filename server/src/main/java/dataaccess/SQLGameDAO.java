package dataaccess;

public class SQLGameDAO {
    private final String[] gameStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
            'id' int NOT NULL AUTO-INCREMENT,
            'name' VARCHAR(255) NOT NULL,
            'white' VARCHAR(255) DEFAULT NULL,
            'black' VARCHAR(255) DEFAULT NULL,
            'game' TEXT
            PRIMARY KEY ('id')
            )
            """
    };
}
