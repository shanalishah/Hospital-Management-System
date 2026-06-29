/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;

import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class FXMLUserPrescriptionController implements Initializable {
    @FXML
    private Button backbtn;

    @FXML
    private JFXTextField preName;

    @FXML
    private JFXTextField preDis;

    @FXML
    private JFXTextField preBill;

    @FXML
    private TextArea preSyp;

    @FXML
    private TextArea preDrug;

    @FXML
    private Button preSubmit;
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            showDoctorsPrescription();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLUserPrescriptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  

    @FXML
    void btnSubmit(ActionEvent event) {
        System.out.println("Print");
    }

    @FXML
    void returnBtn(ActionEvent event) {
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
    
    public void showDoctorsPrescription() throws SQLException{
        
        try {
            Connect();
            String sql = "SELECT * FROM prescription where name='Zahra';";
            pst= conn.prepareStatement( sql); 
            rs =pst.executeQuery();
            if(rs.next()){
              
                preName.setText(rs.getString("name"));
                preDis.setText(rs.getString("disease"));
                preBill.setText(rs.getString("bill"));
                preSyp.setText(rs.getString("syptoms"));
                preDrug.setText(rs.getString("drug"));
            }
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }    
}