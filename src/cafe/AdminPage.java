package cafe;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import cafe.Login.UserRoles;

class AdminPage extends JFrame implements ActionListener {

	Connection connection;
	UserRoles userRoles;
	JTextField searchField;
	JButton registerStudentsBtn, registerUserBtn, assignTickerBtn, searchBtn, printBtn;
	JPanel header, toolBar, footer;
	JTable studentsTable;
	DefaultTableModel tableModel;
	TableRowSorter<DefaultTableModel> tableSorter;
	JScrollPane studentsTableScrollPlane;
	ArrayList<Time> isValidToTick;

	AdminPage(Connection connection, UserRoles userRoles, ArrayList<Time> isValidToTick) {
		System.out.println("Constructor" + isValidToTick);
		this.isValidToTick = isValidToTick;
		this.connection = connection;
		this.userRoles = userRoles;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Students List");
		setSize(500, 700);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		searchField = new JTextField("Search here");
		searchField.setPreferredSize(new Dimension(200, 30));
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				search(searchField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				search(searchField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				search(searchField.getText());
			}

			public void search(String str) {
				if (str.length() == 0) {
					tableSorter.setRowFilter(null);
				} else {
					tableSorter.setRowFilter(RowFilter.regexFilter(str));
				}
			}
		});
		registerStudentsBtn = new JButton("Add Students");
		registerStudentsBtn.addActionListener(this);
		registerUserBtn = new JButton("Add Users");
		registerUserBtn.addActionListener(this);
		assignTickerBtn = new JButton("Assign Ticker");
		assignTickerBtn.addActionListener(this);
		searchBtn = new JButton("Search");
		searchBtn.addActionListener(this);

		header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
		header.setAlignmentX(RIGHT_ALIGNMENT);
		toolBar = new JPanel();

		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(searchField);
		toolBar.setMaximumSize(new Dimension(900, 50));
		toolBar.add(searchBtn);

		if (userRoles == Login.UserRoles.ADMIN) {
			header.add(registerStudentsBtn);
			header.add(registerUserBtn);
			header.add(assignTickerBtn);
		}
		add(header);
		add(toolBar);

		this.initializeTable();
		studentsTableScrollPlane = new JScrollPane(studentsTable);
		add(studentsTableScrollPlane);

		footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		printBtn = new JButton("Print");
		printBtn.addActionListener(this);
		footer.setMaximumSize(new Dimension(500, 50));
		footer.add(printBtn);
		add(footer);
	}

	public static enum Time {
		BreakFast, Lunch, Dinner
	}

	private void initializeTable() {
		String column[] = { "Id", "Full Name", "Department", "BreakFast", "Lunch", "Dinner" };

		tableModel = new DefaultTableModel(column, 0);

		String selectStudentsQuery = "select * from \"students\"";

		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(selectStudentsQuery);

			Statement departmentSt = connection.createStatement();
			Statement mealSt = connection.createStatement();

			while (rs.next()) {
				String ateBreakfast = "No";
				String ateLunch = "No";
				String ateDinner = "No";
				String findDepartmentNameQuery = "select name from \"department\" where code = '"
						+ rs.getString("department_code") + "'";
				String mealSelectQuery = String.format("select * from \"meal\" where student_id ='%s' and date = '%s'",
						rs.getString("id"), new Date());

				ResultSet mealRs = mealSt.executeQuery(mealSelectQuery);
				while (mealRs.next()) {
					if (mealRs.getString("time").equals("BreakFast")) {
						ateBreakfast = "Yes";
					}
					if (mealRs.getString("time").equals("Lunch")) {
						ateLunch = "Yes";
					}
					if (mealRs.getString("time").equals("Dinner")) {
						ateDinner = "Yes";
					}
				}
				ResultSet departmentRs = departmentSt.executeQuery(findDepartmentNameQuery);

				departmentRs.next();

				Object[] data = { rs.getString("id"), rs.getString("full_name"), departmentRs.getString("name"),
						ateBreakfast, ateLunch, ateDinner };
				tableModel.addRow(data);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableSorter = new TableRowSorter<DefaultTableModel>(tableModel);
		studentsTable = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false;
			};

			public boolean isCellSelectable(int row, int column) {
				return false;
			};
		};
		studentsTable.setRowSelectionAllowed(false);
		studentsTable.setDragEnabled(false);
		studentsTable.setColumnSelectionAllowed(false);
		studentsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String selectedData = null;
				int selectedRow = studentsTable.getSelectedRow();
				if (selectedRow == -1) {
					return;
				}
				int selectedColumn = studentsTable.getSelectedColumn();

				if (selectedColumn != 4 && selectedColumn != 5 && selectedColumn != 3) {
					return;
				}
				System.out.println(userRoles.equals(UserRoles.TICKER) + column[selectedColumn]);
				if (!userRoles.equals(UserRoles.TICKER)) {
					JOptionPane.showMessageDialog(null, "Admins Can't Tick", "Anauthorized",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (isValidToTick.isEmpty()) {
					JOptionPane.showMessageDialog(null, "You are not assigned to tick ask to be assigned",
							"Not assigned", JOptionPane.WARNING_MESSAGE);
					return;

				}
				if (!isValidToTick.contains(Time.valueOf(column[selectedColumn]))) {
					JOptionPane.showMessageDialog(null, "You are only allowed to tick " + isValidToTick.toString() + "",
							"Not assigned", JOptionPane.WARNING_MESSAGE);
					return;
				}

				String studentId = (String) studentsTable.getValueAt(selectedRow, 0);
				System.out.println(studentId);
				selectedData = (String) studentsTable.getValueAt(selectedRow, selectedColumn);
				String inputMessage = "Do you like to change the state for student #" + studentId;

				String inputTitle = "Toggle " + studentsTable.getColumnName(selectedColumn);
				int input = JOptionPane.showConfirmDialog(null, inputMessage, inputTitle,
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (input == 0) {
					if (selectedData == "No") {
						insertMeal(studentId, "20", Time.valueOf(studentsTable.getColumnName(selectedColumn)),
								new Date());
						studentsTable.setValueAt("Yes", selectedRow, selectedColumn);
					} else {
						deleteMeal(studentId, Time.valueOf(studentsTable.getColumnName(selectedColumn)), new Date());
						studentsTable.setValueAt("No", selectedRow, selectedColumn);
					}
				}

			}
		});
		studentsTable.setRowSorter(tableSorter);
	}

	private void insertMeal(String studentId, String ticker_id, Time time, Date date) {
		String query = String.format(
				"insert into \"meal\" (student_id, ticker_id, time, date) values ('%s','%s','%s','%s')", studentId,
				ticker_id, time, date);
		try {
			Statement st = this.connection.createStatement();
			int rs = st.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteMeal(String studentId, Time time, Date date) {
		String query = String.format("delete  from \"meal\" where student_id = '%s' and time = '%s' and date = '%s'",
				studentId, time, date);
		try {
			Statement st = this.connection.createStatement();
			int rs = st.executeUpdate(query);
			System.out.println(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == registerStudentsBtn) {
			RegisterStudents page = new RegisterStudents(connection, this);
			page.setVisible(true);
		}
		if (e.getSource() == registerUserBtn) {
			RegisterUser registerUser = new RegisterUser(connection);
			registerUser.setVisible(true);
		}
		if (e.getSource() == assignTickerBtn) {
			AssignTicker assignTicker = new AssignTicker(connection);
			assignTicker.setVisible(true);
		}
	}
}