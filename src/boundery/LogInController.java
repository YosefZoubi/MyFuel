package boundery;


import javax.swing.JOptionPane;


import Entity.Customer;
import Entity.Employee;
import Entity.Supplier;
import client.ClientUI;
import client.UserCC;
import enums.Commands;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LogInController {

	@FXML
	private PasswordField passtxt;

	@FXML
	private TextField usernametxt;

	@FXML
	private Button loginbtb;

	@FXML
	void login(ActionEvent event) {

		String username = usernametxt.getText();
		String password = passtxt.getText();

		if (password.isEmpty() == true || username.isEmpty() == true)
			JOptionPane.showMessageDialog(null, "username or password are missing");

		else {
			Object obj = UserCC.login(username, password);
			if(obj instanceof Customer) System.out.println("customer");
			//
			else if(obj instanceof Employee) {
				
				System.out.println("employee");
				ClientUI.user=obj;
				MarketingManagerController marketingManager = new MarketingManagerController();
				try {
					marketingManager.start(ClientUI.mainStage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//
			else if(obj instanceof Supplier) System.out.println("supplier");
			else if(obj==null)System.out.println("not exist");
			else if(obj.equals(Commands.UserAlreadyConnected))
					System.out.println("already online");
		}

	}

	public void start(Stage primaryStage) throws Exception {
		Pane mainPane;
		Scene s;

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("LogIn.fxml"));
		mainPane = loader.load();

		// connect the scene to the file
		s = new Scene(mainPane);

		primaryStage.setTitle("MyFuel ltm");
		primaryStage.setScene(s);
		primaryStage.show();

	}

}