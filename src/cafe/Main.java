package cafe;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
	public static void main(String args[]) {

		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cafe", "postgres",
					"kranuaonpostgres");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
//		AdminPage adminPage = new AdminPage(connection, Login.UserRoles.ADMIN, null);
//		adminPage.setVisible(true);

		Login form = new Login(connection);
		form.setVisible(true);

	}
}
