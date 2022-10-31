package cafe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RegisterUser extends JFrame implements ActionListener {
	Connection connection;
	JTextField userNameField, passwordField;
	JLabel userNameLabel, passwordLabel, roleLabel;
	JPanel containerPanel;
	JComboBox roleSelect;
	JButton submitBtn;

	RegisterUser(Connection connection) {
		this.connection = connection;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Add a User");
		setSize(400, 150);
		userNameField = new JTextField();
		passwordField = new JTextField(15);
		userNameLabel = new JLabel();
		passwordLabel = new JLabel();

		userNameLabel.setText("User name goes here: ");
		passwordLabel.setText("User password goes here: ");
		roleLabel = new JLabel();
		roleLabel.setText("Select a role");

		String roles[] = { "admin", "ticker" };
		roleSelect = new JComboBox(roles);

		submitBtn = new JButton("Submit");
		submitBtn.addActionListener(this);

		containerPanel = new JPanel(new GridLayout(4, 1));
		containerPanel.add(userNameLabel);
		containerPanel.add(userNameField);
		containerPanel.add(passwordLabel);
		containerPanel.add(passwordField);
		containerPanel.add(roleLabel);
		containerPanel.add(roleSelect);
		containerPanel.add(submitBtn);

		add(containerPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedRoleIdx = roleSelect.getSelectedIndex();
		if (passwordField.getText().isEmpty() || userNameField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "All Fields Must be filled", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			String addUserQuery = String.format(
					"insert into \"user\" (name, password, role, created_at) values ('%s','%s','%s', '%s')",
					userNameField.getText(), passwordField.getText(),
					roleSelect.getSelectedItem().toString().toUpperCase(), new Date());

			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(addUserQuery);
			System.out.println(result);
			if (result == 1) {
				setVisible(false);
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
}
