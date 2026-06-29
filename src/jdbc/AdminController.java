
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

public class AdminController implements Initializable {

    @FXML
    private Label lblName;
    @FXML
    private Label lbltxt;
    @FXML
    private Label lblType;
    @FXML
    private Button doctorsinfobtn;
    @FXML
    private HBox btnSignout;
    @FXML
    private HBox btnSignout1;
    @FXML
    private Button signoutbtn;
    @FXML
    private Button doctorsinfobtn1;
    @FXML
    private Button doctorsinfobtn11;
    @FXML
    private Label lblCountAdmin;
    @FXML
    private Label lbltxt1;
    @FXML
    private Label lblCountRec;
    @FXML
    private Label lbltxt2;
    @FXML
    private Label lblCountDoc;
    @FXML
    private Label lbltxt3;
    @FXML
    private Label lblCountApp;
    @FXML
    private Label lbltxt4;
    @FXML
    private Label lblCountReq;
    /**
     * Initializes the controller class.
     */
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            countAdminRows();
            countDoctorRows();
            countRecRows();
            countAppReqRows();
            countSettedAppRows();
        } catch (SQLException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
    
    public void countAdminRows() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM reg where userType='admin';";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                String count = rs.getString("Count(user_id)");
                lblCountAdmin.setText(count);
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void countRecRows() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM reg where userType='Receptionist';";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                String count = rs.getString("Count(user_id)");
                lblCountRec.setText(count);
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
    
    public void countDoctorRows() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM reg where userType='Doctor';";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                String count = rs.getString("Count(user_id)");
                lblCountDoc.setText(count);
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void countAppReqRows() throws SQLException{    
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM appointment;";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
                String count = rs.getString("Count(user_id)");
                lblCountReq.setText(count);
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void countSettedAppRows() throws SQLException{
        try {
            Connect();
            String sql = "SELECT COUNT(user_id)FROM patient;";
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
      
    @FXML
    private void signOut(ActionEvent event) {
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
            ((Node)event.getSource()).getScene().getWindow().hide();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }
    }

    @FXML
    private void showusersInfo(ActionEvent event) {
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLadminTable.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
            ((Node)event.getSource()).getScene().getWindow().hide();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }
    }   
}