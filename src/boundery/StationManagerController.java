package boundery;

import java.awt.TextComponent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import Entity.Employee;
import Entity.GasStationOrder;
import Entity.GenericReport;
import javafx.*;
import Entity.StationFuel;
import Entity.StationManager;
import client.ClientUI;
import client.EmployeeCC;
import enums.Commands;
import enums.GasStationOrderFromSupplier;
import enums.StationManagerReportsTypes;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import server.FileManagmentSys;

public class StationManagerController implements Initializable {
	
	private Stage curr_stage;
	
	public static StationManager stationManager;

	private ArrayList<GasStationOrder> selectedOrders = new ArrayList<GasStationOrder>();

	@FXML
	private Text nametxt;

	private AnchorPane curr;

	@FXML
	private Button LogOut;

	@FXML
	private Button quantity;

	@FXML
	private Button StationOrders;

	@FXML
	private Button reports;

	@FXML
	private AnchorPane QuantityView;

	@FXML
	private ComboBox<String> ChooseFuelType;

	@FXML
	private TextField enterAmount;

	@FXML
	private Button update;

	// qutatity table start
	@FXML
	private TableView<StationFuel> minQuantityTable;

	@FXML
	private TableColumn<StationFuel, String> fuelTypecol;

	@FXML
	private TableColumn<StationFuel, Float> Amountcol;

	@FXML
	private TableColumn<StationFuel, Float> minQuantitycol;

	//// qutatity table end

	@FXML
	private AnchorPane StationReportsView;

	// Report table start
	@FXML
	private TableView<GenericReport> ReportTable;

	@FXML
	private TableColumn<GenericReport, String> ReportType;

	@FXML
	private TableColumn<GenericReport, String> ReportDate;

	@FXML
	private TableColumn<GenericReport, String> ReportTime;

	@FXML
	private TableColumn<GenericReport, String> companyNamecol;

	// Report table end

	@FXML
	private AnchorPane StationOrdersView;

	// order table start
	@FXML
	private TableView<GasStationOrder> StationOrderTable;

	@FXML
	private TableColumn<GasStationOrder, Boolean> selectOrderCol;

	@FXML
	private TableColumn<GasStationOrder, Integer> OrdersOrderIDCol;

	@FXML
	private TableColumn<GasStationOrder, String> OrdersSupplierIDCol;

	@FXML
	private TableColumn<GasStationOrder, String> OrdersStatusCol;

	@FXML
	private TableColumn<GasStationOrder, String> OrdersDateCol;

	@FXML
	private TableColumn<GasStationOrder, Float> OrdersOrderPriceCol;

	@FXML
	private TableColumn<GasStationOrder, String> OrdersFuelTypeCol;

	// order table end

	@FXML
	private ComboBox<GasStationOrderFromSupplier> ChooseOrdersStatusCombo;

	@FXML
	private Button refresh;

	@FXML
	private Button confirmBtn;

	@FXML
	private ComboBox<StationManagerReportsTypes> reportTypecombo;

	@FXML
	private Button createReportbtn;

	@FXML
	private Button chooseYearbtn;

	@FXML
	private TextField enterYeartxt;

	@FXML
	void ChooseOrdersStatus(ActionEvent event) {

		ComboBox<GasStationOrderFromSupplier> temp = (ComboBox<GasStationOrderFromSupplier>) event.getSource();

		GasStationOrderFromSupplier s = temp.getSelectionModel().getSelectedItem();

		switch (s) {

		case created:
			StationOrderTable.setItems(getOrders(GasStationOrderFromSupplier.created));
			confirmBtn.setVisible(true);
			break;

		case confirmed:
			StationOrderTable.setItems(getOrders(GasStationOrderFromSupplier.confirmed));
			confirmBtn.setVisible(false);
			break;

		case supplied:
			StationOrderTable.setItems(getOrders(GasStationOrderFromSupplier.supplied));
			confirmBtn.setVisible(false);
			break;

		}

	}

	@FXML
	void confirmOrder(ActionEvent event) {

		Button b = (Button) event.getSource();

		if (b == confirmBtn) {
			EmployeeCC.ApproveOrders(selectedOrders);
			StationOrderTable.setItems(getOrders(GasStationOrderFromSupplier.created));
			selectedOrders.clear();
		}

	}

	// user click on show
	@FXML
	void getAllReportByYear(ActionEvent event) {
		int val;
		String s;
		Button b = (Button) event.getSource();

		if (b == chooseYearbtn) {

			s = enterYeartxt.getText();

			if (enterYeartxt.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "you didnt insert nothing");
				return;
			}

			try {
				val = Integer.parseInt(s);
				if (val <= 0) {
					JOptionPane.showMessageDialog(null, "Year Value must be greater then zero");
					return;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "input error try again");
				return;
			}

			// All tests passed. Input is correct
			 ReportTable.setItems(getAllReportsByYear(s));
		}

	}

	@FXML
	void mouseClicked(MouseEvent event) {

		Button b = (Button) event.getSource();

		if (b == quantity) {
			changeCurrAncorPane(QuantityView);
		}

		else if (b == reports) {
			changeCurrAncorPane(StationReportsView);
		}

		else if (b == StationOrders) {
			changeCurrAncorPane(StationOrdersView);
		}

		else if (b == update) {
			String s = enterAmount.getText();
			int val;

			if (ChooseFuelType.getValue().equals("Choose fuel type")) {
				JOptionPane.showMessageDialog(null, "you must choose fuel  type");
				return;
			}

			if (s.isEmpty())
				JOptionPane.showMessageDialog(null, "you didnt insert nothing");

			else {

				try {
					val = Integer.parseInt(s);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "input error try again");
					return;
				}

				if (val <= 0) {
					JOptionPane.showMessageDialog(null, "input must be greater then 0");
					return;
				}

				// All tests passed. Input is correct
				EmployeeCC.updateFuelMinQuantitybyType(stationManager.getStationID(), ChooseFuelType.getValue(), val);
				JOptionPane.showMessageDialog(null, "changed succesfully");
				ChooseFuelType.setValue("Choose fuel type");
				enterAmount.clear();
				ObservableList<StationFuel> fuels = getAllStationFuel(stationManager.getStationID());
				minQuantityTable.setItems(fuels);
			}

		}

	}

	@FXML
	void refresh(ActionEvent event) {
		System.out.println();
	}

	@FXML
	void CretaeReportByType(ActionEvent event) {

		Button b = (Button) event.getSource();
		File f;
		StationManagerReportsTypes s;

		if (b == createReportbtn) {
			s = reportTypecombo.getValue();
			if (s == null)
				JOptionPane.showMessageDialog(null, "You Must Choose Report Type");

			else {
				f = EmployeeCC.createFuelStationReports(stationManager.getStationID(),stationManager.getCompanyName(), s);
				if(f == null) JOptionPane.showMessageDialog(null, "report already exist");
				else showReport(f, null);
			}

		}
	}

	public void start(Stage primaryStage) throws Exception {
		Pane mainPane;
		Scene s;
		curr_stage = primaryStage;

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("StationManagerGUI.fxml"));
		mainPane = loader.load();

		// connect the scene to the file
		s = new Scene(mainPane);

		curr_stage.setTitle("MyFuel ltm");
		curr_stage.setScene(s);
		curr_stage.show();

	}

	public void changeCurrAncorPane(AnchorPane p) {
		curr.setVisible(false);
		p.setVisible(true);
		curr = p;
	}

	public void initializeOrderTable() {
		// Check Box
		selectOrderCol.setCellValueFactory(
				new Callback<CellDataFeatures<GasStationOrder, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(CellDataFeatures<GasStationOrder, Boolean> param) {
						GasStationOrder gasStationOrder = param.getValue();
						SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(gasStationOrder.getSelect());

						booleanProp.addListener(new ChangeListener<Boolean>() {
							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if (newValue == true)
									selectedOrders.add(gasStationOrder);
								else
									selectedOrders.remove(gasStationOrder);
								gasStationOrder.setSelect(newValue);
							}
						});
						return booleanProp;
					}
				});

		// set 3 types of order status into combox ----default created
		ObservableList<GasStationOrderFromSupplier> orderTypes = FXCollections
				.observableArrayList(GasStationOrderFromSupplier.values());
		ChooseOrdersStatusCombo.setItems(orderTypes);
		ChooseOrdersStatusCombo.getSelectionModel().select(0);

		selectOrderCol.setCellFactory(new Callback<TableColumn<GasStationOrder, Boolean>, //
				TableCell<GasStationOrder, Boolean>>() {
			@Override
			public TableCell<GasStationOrder, Boolean> call(TableColumn<GasStationOrder, Boolean> p) {
				CheckBoxTableCell<GasStationOrder, Boolean> cell = new CheckBoxTableCell<GasStationOrder, Boolean>();
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});

		// station order table start
		OrdersOrderIDCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, Integer>("orderID"));
		OrdersSupplierIDCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, String>("fuelType"));
		OrdersStatusCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, String>("status"));
		OrdersDateCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, String>("date"));
		OrdersOrderPriceCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, Float>("orderPrice"));
		OrdersFuelTypeCol.setCellValueFactory(new PropertyValueFactory<GasStationOrder, String>("fuelType"));

		StationOrderTable.setItems(getOrders(GasStationOrderFromSupplier.created));

		selectedOrders.clear();

		// station order table end
	}

	public void initializeMinQuantityTable() {

		// quantity table start -------------
		fuelTypecol.setCellValueFactory(new PropertyValueFactory<StationFuel, String>("fueltype"));
		Amountcol.setCellValueFactory(new PropertyValueFactory<StationFuel, Float>("amount"));
		minQuantitycol.setCellValueFactory(new PropertyValueFactory<StationFuel, Float>("minQuantity"));

		ObservableList<StationFuel> fuels = getAllStationFuel(stationManager.getStationID());
		minQuantityTable.setItems(fuels);

		Iterator<StationFuel> itr = fuels.iterator();
		ObservableList<String> fueltypes = FXCollections.observableArrayList();

		while (itr.hasNext()) {
			fueltypes.add(itr.next().getFueltype());
		}

		ChooseFuelType.setItems(fueltypes);

		ChooseFuelType.setValue("Choose fuel type");
		// quantity table end ---------------
	}

	public void initializeReportTable() {
		
		//clicked row-------------------------
		ReportTable.setRowFactory(tv -> {
	    TableRow<GenericReport> row = new TableRow<>();
	    row.setOnMouseClicked(event -> {
	        if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY 
	             && event.getClickCount() == 2) { 

	        	GenericReport clickedRow = row.getItem();
	        	showReport(null,clickedRow);
	        	
	            System.out.println(clickedRow.toString());
	        }
	    });
	    return row ;
	});
		//----------------------
		nametxt.setText(stationManager.getFirstName());
		enterYeartxt.setText("Enter Year");

		// set up comboxbox with types of reports start--------------
		ObservableList<StationManagerReportsTypes> reportTypes = FXCollections
				.observableArrayList(StationManagerReportsTypes.values());
		reportTypecombo.setItems(reportTypes);
		// end comboxbox with types of reports start-----------------

		//report table start
			
		 

		 ReportType.setCellValueFactory(new PropertyValueFactory<GenericReport, String>("reportType"));
		 ReportDate.setCellValueFactory(new PropertyValueFactory<GenericReport, String>("date"));
		 ReportTime.setCellValueFactory(new PropertyValueFactory<GenericReport, String>("time"));
		 companyNamecol.setCellValueFactory(new PropertyValueFactory<GenericReport, String>("companyName"));
		//report table end

	}
	
	public void showReport(File f,GenericReport r) {
		
		QuarterReportController.file =  f;
		QuarterReportController.report = r;
		QuarterReportController c = new QuarterReportController();
    	Stage st = new Stage();
    	
    	try {
			c.start(st);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// load all station order by status to table
	public ObservableList<GasStationOrder> getOrders(GasStationOrderFromSupplier s) {
		ObservableList<GasStationOrder> orders = FXCollections
				.observableArrayList(EmployeeCC.getAllstationOrders(stationManager.getStationID(), s.toString()));

		return orders;
	}

	// load all fuel types to minquantity table
	public ObservableList<StationFuel> getAllStationFuel(int stationId) {

		ObservableList<StationFuel> fuel = FXCollections
				.observableArrayList(EmployeeCC.getAllStationFuelById(stationId));

		return fuel;
	}

	public ObservableList<GenericReport> getAllReportsByYear(String year) {
		ArrayList<GenericReport> rp = EmployeeCC.getAllReportByYear(year);
		if(rp.isEmpty()) 
			JOptionPane.showMessageDialog(null, "there is no report in this year");
		ObservableList<GenericReport> reports = FXCollections.observableArrayList(rp);
		return reports;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		StationOrdersView.setVisible(false);
		StationReportsView.setVisible(false);
		QuantityView.setVisible(false);
		curr = QuantityView;
		initializeOrderTable();
		initializeMinQuantityTable();
		initializeReportTable();
	}

}