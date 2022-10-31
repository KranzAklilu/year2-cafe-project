package cafe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RegisterStudents extends JFrame implements ActionListener {
	Connection connection;
	AdminPage parent;
	JTextField idField, nameField;
	JLabel idLabel, nameLabel, departmentNameLabel;
	JPanel containerPanel;
	JComboBox departmentNameSelect;
	JButton submitBtn;
	ArrayList<String> departmentListName = new ArrayList<String>();
	ArrayList<String> departmentListCode = new ArrayList<String>();

	RegisterStudents(Connection connection, AdminPage parent) {
		this.parent = parent;
		this.connection = connection;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Add Students");
		setSize(400, 150);
		Random rand = new Random();
		idField = new JTextField("UGR/" + rand.nextInt(10000) + "/" + rand.nextInt(100));
		nameField = new JTextField(15);
		idLabel = new JLabel();
		nameLabel = new JLabel();

		idLabel.setText("Students Id goes here: ");
		nameLabel.setText("Students Name goes here: ");
		departmentNameLabel = new JLabel();
		departmentNameLabel.setText("Select Department Name");
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("select * from \"department\"");
			while (rs.next()) {
				departmentListName.add(rs.getString("name"));
				departmentListCode.add(rs.getString("code"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		departmentNameSelect = new JComboBox(departmentListName.toArray());

		submitBtn = new JButton("Submit");
		submitBtn.addActionListener(this);

		containerPanel = new JPanel(new GridLayout(4, 1));
		containerPanel.add(idLabel);
		containerPanel.add(idField);
		containerPanel.add(nameLabel);
		containerPanel.add(nameField);
		containerPanel.add(departmentNameLabel);
		containerPanel.add(departmentNameSelect);
		containerPanel.add(submitBtn);

		add(containerPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedDepartmentIdx = departmentNameSelect.getSelectedIndex();
		if (nameField.getText().isEmpty() || idField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "All Fields Must be filled", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			String addStudentQuery = "insert into \"students\" (id, full_name, department_code,registered_by) values ('"
					+ idField.getText() + "', '" + nameField.getText() + "', '"
					+ departmentListCode.get(selectedDepartmentIdx) + "'," + 1 + ")";
			System.out.println(addStudentQuery);
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(addStudentQuery);
			System.out.println(result);
			if (result == 1) {
				setVisible(false);
				parent.setVisible(false);
				AdminPage adminPage = new AdminPage(connection, Login.UserRoles.ADMIN, null);
				adminPage.setVisible(true);
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
	}
}
