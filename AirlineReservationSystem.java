import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Yogesh Ghimire
 * 
 *Solution for practice exam 2 as part of Advanced Application Development using Java - University of Central Missouri
 */
public class AirlineReservationSystem extends Frame implements ActionListener {
	private static final long serialVersionUID = 7258721493171843081L;
	final static private String DRIVER = "com.mysql.jdbc.Driver";
	final static private String URL = ""; // change url
	final static private String USERNAME = ""; // change username
	final static private String PASSWORD = ""; // change passowrd
	private Connection connection;
	private Statement statement;
	private ResultSet rs;
	private PreparedStatement preparedStatement;
	private String origin;
	private String destination;
	int originId = 1;
	int destinationId = 1;

	Choice availableFlightChoice = new Choice();
	Choice destinationChoice = new Choice();
	Choice originChoice = new Choice();
	TextField ticketPriceTextField = new TextField(20);
	TextField newFlightTimingTextField = new TextField(20);
	TextArea systemMessageTextArea = new TextArea();

	public AirlineReservationSystem() {

		Label flightIdLabel = new Label("Flight ID:");
		TextField flightIdTextBox = new TextField(20);
		flightIdTextBox.setEditable(false);

		Label originLabel = new Label("Origin:");

		Label destinationLabel = new Label("Destination:");

		Label availableFlightLabel = new Label("Available Flights:");

		Label ticketPriceLabel = new Label("Ticket Price:");

		Label newFlightTimingLabel = new Label("New Flight Timing: ");

		newFlightTimingTextField.setEditable(false);

		systemMessageTextArea.setEditable(false);

		Label seatLabel = new Label("Seats");
		TextField seatTextField = new TextField();
		seatTextField.setEditable(false);

		Label systemMessageLabel = new Label("System Message:");

		Button searchBtn = new Button("Search Button");
		Button modifyBtn = new Button("ModifyBtn");
		Button newFlightBtn = new Button("New Flight Button");
		Button saveBtn = new Button("Save Button");
		Button cancelBtn = new Button("Cancel Button");

		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			statement = connection.createStatement();
			String createAirportTableQuery = "create table if not exists AIRPORT"
					+ "(airportID int, AirportName varchar(50), AirportRegionCode int)";
			statement.executeUpdate(createAirportTableQuery);

			String createScheduleTableQuery = "create table if not exists Schedule"
					+ "(FlightNumber varchar(50), OriginId int, DestinationId int, NumberOfSeatsAvailable int, DepartureTime DATETIME)";
			statement.executeUpdate(createScheduleTableQuery);

			String[] airportInsert = { "insert into AIRPORT values(1, 'Missouri', 1)",
					"insert into AIRPORT values(2, 'NewYork', 2)", "insert into AIRPORT values(3, 'Warrensburg', 1)",
					"insert into Airport values (4, 'Maryland', 3)",
					"insert into Airport values (5, 'Gaithersburg', 3)" };

			String[] scheduleInsert = { "insert into Schedule values(1, 3, 1, 5, '2017-12-11 10:10:10')",
					"insert into Schedule values(2, 4, 2, 5, '2017-10-11 11:12:08')",
					"insert into Schedule values(3, 5, 4, 5, '2017-09-11 08:05:11')",
					"insert into Schedule values(4, 1, 5, 5, '2017-08-11 05:09:29')" };

			for (String airport : airportInsert) {
				statement.executeUpdate(airport);
			}

			for (String schedules : scheduleInsert) {
				statement.executeUpdate(schedules);
			}

			rs = statement.executeQuery("SELECT AirportName from AIRPORT");
			originChoice.add("");
			destinationChoice.add("");
			while (rs.next()) {
				String airportName = rs.getString("AirportName");
				originChoice.add(airportName);
				destinationChoice.add(airportName);
			}
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		originChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				origin = (String) event.getItemSelectable().getSelectedObjects()[0];
			}
		});
		destinationChoice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				destination = (String) event.getItemSelectable().getSelectedObjects()[0];
			}
		});

		searchBtn.addActionListener(this);

		newFlightBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				flightIdTextBox.setEditable(true);
				newFlightTimingTextField.setEditable(true);
				seatTextField.setEditable(true);

				flightIdTextBox.setText("");
				newFlightTimingTextField.setText("");
				seatTextField.setText("");
			}
		});

		modifyBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					flightIdTextBox.setEditable(true);
					newFlightTimingTextField.setEditable(true);
					seatTextField.setEditable(true);
					preparedStatement = connection.prepareStatement(
							"SELECT FlightNumber, NumberOfSeatsAvailable, DepartureTime from Schedule where OriginId = ? and DestinationId = ?");
					preparedStatement.setInt(1, originId);
					preparedStatement.setInt(2, destinationId);
					rs = preparedStatement.executeQuery();
					String flightNumber = "", departureTime = "";
					int numberOfSeatsAvailable = 0;
					while (rs.next()) {
						flightNumber = rs.getString("FlightNumber");
						numberOfSeatsAvailable = rs.getInt("NumberOfSeatsAvailable");
						departureTime = rs.getString("DepartureTime");
					}
					newFlightTimingTextField.setText(departureTime);
					flightIdTextBox.setText(flightNumber);
					seatTextField.setText(String.valueOf(numberOfSeatsAvailable));

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					preparedStatement = connection.prepareStatement(
							"UPDATE SCHEDULE SET NumberOfSeatsAvailable = ?, DepartureTime =? WHERE OriginId = ? and DestinationId = ?");
					preparedStatement.setInt(1, Integer.valueOf(seatTextField.getText()));
					preparedStatement.setString(2, newFlightTimingTextField.getText());
					preparedStatement.setString(3, String.valueOf(originId));
					preparedStatement.setString(4, String.valueOf(destinationId));
					preparedStatement.executeUpdate();
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				// availableFlightChoice.set
				// destinationChoice
				// originChoice
				ticketPriceTextField.setText("");
				newFlightTimingTextField.setText("");
				seatTextField.setText("");
			}
		});

		add(flightIdLabel);
		add(flightIdTextBox);
		add(originLabel);
		add(originChoice);
		add(destinationLabel);
		add(destinationChoice);
		add(availableFlightLabel);
		add(availableFlightChoice);
		add(ticketPriceLabel);
		add(ticketPriceTextField);
		add(newFlightTimingLabel);
		add(newFlightTimingTextField);
		add(seatLabel);
		add(seatTextField);
		add(systemMessageLabel);
		add(systemMessageTextArea);
		add(searchBtn);
		add(modifyBtn);
		add(newFlightBtn);
		add(saveBtn);
		add(cancelBtn);

		setSize(800, 800);
		setLayout(new FlowLayout());
		setVisible(true);

	}

	public static void main(String[] args) {
		new AirlineReservationSystem();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (origin.equals(destination)) {
			systemMessageTextArea.setText("Origin and Destination cannot be same");
		} else {
			try {
				String searchAvailableTimingQuery = "Select DepartureTime from Schedule where OriginId = ? and DestinationId = ?";
				String getOriginId = "Select AirportId from Airport where AirportName = ?";
				String getDestinationId = "Select AirportId from Airport where AirportName = ?";

				preparedStatement = connection.prepareStatement(getOriginId);
				preparedStatement.setString(1, origin);
				rs = preparedStatement.executeQuery();

				while (rs.next()) {
					originId = rs.getInt("AirportId");
				}
				rs.close();
				preparedStatement.close();

				preparedStatement = connection.prepareStatement(getDestinationId);
				preparedStatement.setString(1, destination);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					destinationId = rs.getInt("AirportId");
				}
				rs.close();
				preparedStatement.close();

				preparedStatement = connection.prepareStatement(searchAvailableTimingQuery);
				preparedStatement.setInt(1, originId);
				preparedStatement.setInt(2, destinationId);
				rs = preparedStatement.executeQuery();
				String dateTime = "";
				while (rs.next()) {
					dateTime = rs.getString("DepartureTime");
					availableFlightChoice.add(dateTime);
				}
				rs.close();
				preparedStatement.close();

				String getRegionCodeQuery = "Select AirportRegionCode from Airport where AirportId = ?";
				int originRegionCode = 0;
				int destRegionCode = 0;
				preparedStatement = connection.prepareStatement(getRegionCodeQuery);
				preparedStatement.setInt(1, originId);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					originRegionCode = rs.getInt("AirportRegionCode");
				}
				rs.close();
				preparedStatement.close();

				preparedStatement = connection.prepareStatement(getRegionCodeQuery);
				preparedStatement.setInt(1, destinationId);
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					destRegionCode = rs.getInt("AirportRegionCode");
				}
				rs.close();

				if (originRegionCode - destRegionCode == 0) {
					ticketPriceTextField.setText("200");
				} else {
					int difference = originRegionCode - destRegionCode;
					if (difference < 0) {
						difference = -(difference);
					}
					int totalPrice = 200 + difference * 100;
					ticketPriceTextField.setText(String.valueOf(totalPrice));
				}

			} catch (Exception e) {
				//TODO: handle exception..
				e.printStackTrace();
			}
		}
	}

}
