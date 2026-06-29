
package jdbc;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class FXMLDocumentController implements Initializable {
    
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
    private ComboBox combobox;
    
    @FXML
    private Label titlelbl;

    @FXML
    private JFXTextField txtusername;

    @FXML
    private JFXPasswordField txtpass;

    @FXML
    private JFXButton btnReg;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private ImageView img;
   
    ObservableList<String> combo = FXCollections.observableArrayList("Patient", "Admin","Receptionist", "Doctor");
    @FXML
    private JFXButton btnCancel;
   
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
    void checkReg(ActionEvent event) {
         
        try{ 
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);    
        }
    }
    Alert a = new Alert(AlertType.NONE);
    @FXML
    void checkLogin(ActionEvent event) throws ClassNotFoundException, IOException {
        
        try{
        Connect();
        
        //2- sql query
        String sql ="select * from reg where name=? and  passw=? and userType=?";
        
        //get values/ from user
        pst = conn.prepareStatement(sql);
        pst.setString(1,txtusername.getText() );
        pst.setString(2,txtpass.getText() );
        String type = (String) combobox.getValue();
        pst.setString(3, type ); 
    
        //execute
        rs = pst.executeQuery();
        String name  = txtusername.getText();
        
        //check for the user login 
        if(rs.next()==true){
            
            if (type.equals("Receptionist")){
                txtusername.setText("");
                txtpass.setText("");
                combobox.setValue(null);
                txtusername.requestFocus();
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText( "Dear "+name+", Login Successful!");
                a.setHeaderText(null);
                a.showAndWait();
                ((Node)event.getSource()).getScene().getWindow().hide();
                 Stage stage = new Stage();
                 Parent root = FXMLLoader.load(getClass().getResource("FXMLmain.fxml"));
                 Scene scene = new Scene(root);
                 stage.setTitle("Hospital Managment System");
                 stage.setScene(scene);
                 stage.show();
                 txtusername.setText(null);
                 txtpass.setText(null);
            }
            else if(type.equals("Patient")){ 
                txtusername.setText("");
                txtpass.setText("");
                combobox.setValue(null);
                txtusername.requestFocus();
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText( "Dear "+name+", Login Successful!");
                a.setHeaderText(null);
                a.showAndWait();
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("user.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("Hospital Managment System");
                stage.setScene(scene);
                stage.show();
                } 
             else if(type.equals("Doctor")){
                txtusername.setText("");
                txtpass.setText("");
                combobox.setValue(null);
                txtusername.requestFocus();
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText( "Dear "+name+", Login Successful!");
                a.setHeaderText(null);
                a.showAndWait();
                ((Node)event.getSource()).getScene().getWindow().hide();
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("FXMLDoctor.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("Hospital Managment System");
                stage.setScene(scene);
                stage.show();
                }
            else if(type.equals("Admin")){
                txtusername.setText("");
                txtpass.setText("");
                combobox.setValue(null);
                txtusername.requestFocus();
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText( "Dear "+name+", Login Successful!");
                a.setHeaderText(null);
                a.showAndWait(); 
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("admin.fxml"));
                Scene scene = new Scene(root);
                stage.setTitle("Hospital Managment System");
                stage.setScene(scene);
                stage.show();
                ((Node)event.getSource()).getScene().getWindow().hide();
            }
            
        }else  { 
            titlelbl.setText("Invalid credential!");
            txtusername.setText("");
            txtpass.setText("");
            combobox.setValue(null);
            txtusername.requestFocus();
            
        } }catch(Exception ex){            
            JOptionPane.showMessageDialog(null, ex);
        } 
    }
  
    @FXML
    void checkCancel(ActionEvent event) {
       
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
   
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
            a.setContentText( name +", your Registeration was Successfull!");
            a.setHeaderText(null);
            a.showAndWait();
          
            
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            ((Node)event.getSource()).getScene().getWindow().hide();
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }   
}