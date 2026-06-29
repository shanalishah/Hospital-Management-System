
package jdbc;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class FXMLPatientController implements Initializable {

    @FXML
    private Label lblName;

    @FXML
    private Label lblType;

    @FXML
    private Button doctorsinfobtn;

    @FXML
    private Button signoutbtn;
    @FXML
    private Button backbtn;
    
     @FXML
    private TextField paName;

    @FXML
    private TextField paPhone;

    @FXML
    private ComboBox<String> paDoctor;
    
    @FXML
    private DatePicker paMettingTime;
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    
    ObservableList<String> combobx = FXCollections.observableArrayList("Martin", "Roza","Josef", "Sara");
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paDoctor.setItems(combobx);   
    } 

    @FXML
    void signOut(ActionEvent event) {
          try{
        ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }
    }

    @FXML
    private void returnBtn(ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("user.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);  
        }
    }
    
    
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
   
     Alert a = new Alert(AlertType.NONE);
    @FXML
    void recepSave(ActionEvent event) {
        
        try{
            Connect();
            String doctor = (String) paDoctor.getValue();
            pst= conn.prepareStatement("insert into appointment ( name, Pphone, Pdoctor, mettingDate) values(?,?,?,?)");

            pst.setString(1,paName.getText());
            pst.setString(2, paPhone.getText());
            pst.setString(3, doctor);
            pst.setString(4,((TextField)paMettingTime.getEditor()).getText());
           
            if(paName.getText().isEmpty()) { 
                a.setAlertType(AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Name field, is required, cant be empty!");
                a.show();
                return;
             }
            
            pst.executeUpdate();
            a.setAlertType(AlertType.INFORMATION);
            a.setContentText("Operation Was Successfull!");
            a.setHeaderText(null);
            a.setTitle("Information");
            a.show();
             
            paName.setText("");
            paPhone.setText("");
            paDoctor.setValue(null);
            paMettingTime.setValue(null);
            paName.requestFocus();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }   
}