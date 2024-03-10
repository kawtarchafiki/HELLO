package db;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.SQLException;

	public class DbConnexion {
	    private static final String DB_URL = "jdbc:mysql://localhost:3307/crud_projet";
	    private static final String USER = "root";
	    private static final String PASS = "";
	    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	    static {
	        try {
	            Class.forName(JDBC_DRIVER);
	        } catch (ClassNotFoundException e) {
	            throw new ExceptionInInitializerError(e);
	        }
	    }

	    public static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection(DB_URL, USER, PASS);
	    }

	}
