package cafe;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cafe.AdminPage.Time;

class Login extends JFrame implements ActionListener {
	Connection connection;
	JButton submitBtn;
	JPanel containerPanel;
	JLabel userLabel, passLabel;
	JComboBox userRole;
	final JTextField userField, passField;

	Login(Connection connection) {
		this.connection = connection;

		setTitle("Login");
		setSize(500, 200);
		userLabel = new JLabel();
		userLabel.setText("Username");
		userField = new JTextField(15);

		passLabel = new JLabel();
		passLabel.setText("Password");

		passField = new JPasswordField(15);

		String[] roles = { "Ticker", "Admin" };
		userRole = new JComboBox(roles);
		userRole.addActionListener(this);
		submitBtn = new JButton("Login");

		containerPanel = new JPanel(new GridLayout(3, 1));
		containerPanel.add(userLabel);
		containerPanel.add(userField);
		containerPanel.add(passLabel);
		containerPanel.add(passField);
//		containerPanel.add(userRole);
		containerPanel.add(submitBtn);

		add(containerPanel, BorderLayout.CENTER);

		submitBtn.addActionListener(this);
	}

	public static enum UserRoles {
		TICKER, ADMIN
	}

	public void actionPerformed(ActionEvent e) {
		String userValue = userField.getText();
		String passValue = passField.getText();
		String selectedRole = (String) userRole.getSelectedItem();

		if (e.getSource() == submitBtn) {
			try {
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery("select * from \"user\"");
				while (rs.next()) {
					System.out.println(rs.getString("name").equals("kra"));
					if (rs.getString("name").equals(userValue)) {
						System.out.println(rs.getString("name").contains("kranua2"));
						if (rs.getString("password").equals(passValue)) {
							if (rs.getString("role").equals(UserRoles.TICKER.toString())) {
								ArrayList<Time> isValidToTick = new ArrayList<Time>();
								try {
									Statement assignmentSt = connection.createStatement();
									ResultSet assignmenetRs = assignmentSt.executeQuery("select * from \"assignment\"");
									while (assignmenetRs.next()) {
										System.out.println("yaoo");
										String tickerId = assignmenetRs.getString("ticker_id");
										String month = assignmenetRs.getString("month");
										String year = assignmenetRs.getString("year");
										String time = assignmenetRs.getString("time");

										int currentMonthValue = LocalDate.now().getMonthValue();
										int currentYear = LocalDate.now().getYear();

										System.out.println(rs.getString("id") + tickerId + "heree" + time);
										if (rs.getString("id").equals(tickerId)
												&& currentMonthValue == Integer.parseInt(month)
												&& currentYear == Integer.parseInt(year)) {
											System.out.println("yayyousd" + time);
											isValidToTick.add(Time.valueOf(time));
										}
									}
									AdminPage page = new AdminPage(connection, UserRoles.TICKER, isValidToTick);
									page.setVisible(true);
									return;

								} catch (SQLException e1) {
									// TODO: handle exception
									e1.printStackTrace();
								}
								AdminPage page = new AdminPage(connection, UserRoles.TICKER, null);
								page.setVisible(true);
								return;
							}
							if (rs.getString("role").equals(UserRoles.ADMIN.toString())) {
								AdminPage page = new AdminPage(connection, UserRoles.ADMIN, null);
								page.setVisible(true);
								return;
							}

						}
					}
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("here");
				System.out.println(e1);
			}
		}
	}
}
