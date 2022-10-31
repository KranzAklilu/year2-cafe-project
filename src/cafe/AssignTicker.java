package cafe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cafe.AdminPage.Time;

public class AssignTicker extends JFrame implements ActionListener {
	Connection connection;
	JLabel tickerSelectLabel, monthSelectLabel, timeSelectLabel, yearFieldLabel;
	JTextField yearField;
	JPanel containerPanel;
	JComboBox tickerSelect, monthSelect, timeSelect;
	JButton submitBtn;
	ArrayList<String> tickerNameList = new ArrayList<String>();
	ArrayList<String> tickerIdList = new ArrayList<String>();

	AssignTicker(Connection connection) {
		this.connection = connection;
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Assign Tickers");
		setSize(400, 150);

		int currentYear = LocalDate.now().getYear();
		yearField = new JTextField("" + currentYear + "");
		tickerSelectLabel = new JLabel("Select ticker name: ");
		monthSelectLabel = new JLabel("Select month: ");
		yearFieldLabel = new JLabel("Enter year");
		timeSelectLabel = new JLabel("Select Time: ");

		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("select * from \"user\" where role = 'TICKER'");
			while (rs.next()) {
				tickerNameList.add(rs.getString("name"));
				tickerIdList.add(rs.getString("id"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		tickerSelect = new JComboBox(tickerNameList.toArray());

		String[] months = new DateFormatSymbols().getMonths();
		monthSelect = new JComboBox(months);
		monthSelect.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
		System.out.println(LocalDate.now().getMonth());

		Time time[] = { Time.BreakFast, Time.Lunch, Time.Dinner };
		timeSelect = new JComboBox(time);

		submitBtn = new JButton("Submit");
		submitBtn.addActionListener(this);

		containerPanel = new JPanel(new GridLayout(5, 2));
		containerPanel.add(tickerSelectLabel);
		containerPanel.add(tickerSelect);
		containerPanel.add(monthSelectLabel);
		containerPanel.add(monthSelect);
		containerPanel.add(yearFieldLabel);
		containerPanel.add(yearField);
		containerPanel.add(timeSelectLabel);
		containerPanel.add(timeSelect);

		containerPanel.add(submitBtn);

		add(containerPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedTickerIdx = tickerSelect.getSelectedIndex();
		if (yearField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "All Fields Must be filled", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			String queryAssignment = String.format(
					"select *  from \"assignment\" where time = '%s' AND month = '%s' AND year = '%s' AND ticker_id = '%s'",
					timeSelect.getSelectedItem(), monthSelect.getSelectedIndex() + 1, yearField.getText(),
					tickerIdList.get(selectedTickerIdx));
			Statement queryAssignemtnStatement = connection.createStatement();
			ResultSet rs = queryAssignemtnStatement.executeQuery(queryAssignment);

			if (rs.next()) {
				System.out.println("Exists");
				JOptionPane.showMessageDialog(null, String.format("%s is already on %s duty",
						tickerSelect.getSelectedItem(), timeSelect.getSelectedItem()), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String assignTickerQuery = String.format(
					"insert into \"assignment\" (time, month, year, ticker_id, assigned_by) values ('%s', %s, %s, '%s', '%s')",
					timeSelect.getSelectedItem(), monthSelect.getSelectedIndex() + 1, yearField.getText(),
					tickerIdList.get(selectedTickerIdx), 1);
			System.out.println(assignTickerQuery);
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(assignTickerQuery);
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
