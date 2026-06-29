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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javax.swing.JOptionPane;


public class FXMLprescriptionController implements Initializable {
    
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
        // TODO
    }
   
    public  void Connect() throws ClassNotFoundException, SQLException{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
  
    @FXML
    void btnSubmit(ActionEvent event) {
          
        try{
            Connect();
            pst= conn.prepareStatement("insert into prescription ( bill,name, disease, syptoms, drug) values(?,?,?,?,?)");
            pst.setString(1,preBill.getText());
            pst.setString(2,preName.getText());
            pst.setString(3,preDis.getText() );
            pst.setString(4,preSyp.getText());
            pst.setString(5, preDrug.getText());
            
            if(preName.getText().isEmpty()) { 
                a.setAlertType(AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Name field, is required, cant be empty!");
                a.show();
                return;
             }

            pst.executeUpdate();
            a.setAlertType(AlertType.INFORMATION);
            a.setContentText("Data Inserted Successfully!");
            a.setHeaderText(null);
            a.setTitle("Information");
            a.show();
          
            preName.setText("");
            preBill.setText("");
            preDis.setText("");
            preSyp.setText("");
            preDrug.setText("");
            
           
            preName.requestFocus();
         
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    Alert a = new Alert(AlertType.NONE);
    @FXML
    void returnBtn(ActionEvent event) {
            try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDoctor.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
       }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            
        }
    }
   
}
