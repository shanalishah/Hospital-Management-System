
package jdbc;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javax.swing.JOptionPane;

public class AdminEditUserTableController implements Initializable {

    @FXML
    private Label lblReg;
    @FXML
    private JFXTextField txtregname;
    @FXML
    private JFXTextField txtregusername;
    @FXML
    private JFXTextField txtregemail;
    @FXML
    private JFXPasswordField txtregpass;
    @FXML
    private JFXComboBox<String> combobox;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXButton btnSave;
    
    ObservableList<String> combo = FXCollections.observableArrayList("Patient", "Admin","Receptionist", "Doctor");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        combobox.setItems(combo);
        
    } 
    //1- prepared statements
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }

    @FXML
    private void checkCancel(ActionEvent event) {   
       ((Node)event.getSource()).getScene().getWindow().hide();    
    }
    
    Alert a = new Alert(AlertType.NONE);
      
    @FXML
    void checkSave(ActionEvent event) {
         
        try{
            Connect();
            String name = txtregname.getText();
            String username=txtregusername.getText();
            String email= txtregemail.getText();
            String passw= txtregpass.getText();
            String type = (String) combobox.getValue();
            
            
            pst= conn.prepareStatement("insert into reg (name, userName, email, passw, userType) values(?,?,?,?,?)");
            pst.setString(1,name);
            pst.setString(2,username);
            pst.setString(3, email);
            pst.setString(4, passw);
            pst.setString(5, type);
          
            if(txtregname.getText().isEmpty()) {
               
                a.setAlertType(AlertType.WARNING);
                a.setContentText("Name field, is required, cant be empty!");
                a.setHeaderText(null);
                a.setTitle("Warning");
                a.show();
                return;
             }
            if(txtregusername.getText().isEmpty()) {
                a.setAlertType(AlertType.WARNING);
                a.setContentText("Username field, is required, cant be empty!");
                a.setHeaderText(null);
                a.setTitle("Warning");
                a.show();
                return;
             }
            if(txtregemail.getText().isEmpty()) {
                a.setAlertType(AlertType.WARNING);
                a.setContentText("Email field, is required, cant be empty!");
                a.setHeaderText(null);
                a.setTitle("Warning");
                a.show();
                return;
             }
            if(txtregpass.getText().isEmpty()) {
                a.setAlertType(AlertType.WARNING);
                a.setContentText("Password field, is required, cant be empty!");
                a.setHeaderText(null);
                a.setTitle("Warning");
                a.show();
                return;
             }
            pst.executeUpdate();
            a.setAlertType(AlertType.INFORMATION);
            a.setContentText( name +"'s Registeration was Successfull!");
            a.setHeaderText(null);
            a.showAndWait();
          
          
            ((Node)event.getSource()).getScene().getWindow().hide();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }    
}