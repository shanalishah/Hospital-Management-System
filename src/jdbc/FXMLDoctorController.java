
package jdbc;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class FXMLDoctorController implements Initializable {
    @FXML
    private Label lblName;

    @FXML
    private Label lblType;

    @FXML
    private Button doctorsinfobtn;

    @FXML
    private HBox btnSignout;

    @FXML
    private Button doctorsinfobtn1;

    @FXML
    private HBox btnSignout1;

    @FXML
    private Button doctorsinfobtn11;

    @FXML
    private Button signoutbtn;

    @FXML
    private Label lbltxt3;

    @FXML
    private Label lblCountApp;

    @FXML
    private Label lbltxt4;

    @FXML
    private Label lblCountReq;

    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            countAppReqRows();
            countPatientPreRows();
          
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDoctorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
       @FXML
    private void signOut(ActionEvent event) {
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
    private void showListofAppointmet (ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDoctorTable.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }
    }
    void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
     }
    
    public void countAppReqRows() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM appointment where Pdoctor='Sara';";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                String count = rs.getString("Count(user_id)");
                lblCountApp.setText(count);
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void countPatientPreRows() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT COUNT(ID)FROM prescription;";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                lblCountReq.setText(rs.getString("Count(ID)"));
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
     
    @FXML
    void btnMakePrescription(ActionEvent event) {
          try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLprescription.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }

    }
}
