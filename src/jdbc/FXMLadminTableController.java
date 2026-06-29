/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class FXMLadminTableController implements Initializable {

    @FXML
    private TableView<mdlAdmin> adminTable;
    @FXML
    private TableColumn<mdlAdmin, Integer> colltblid;
    @FXML
    private TableColumn<mdlAdmin, String> coltblname;
    @FXML
    private TableColumn<mdlAdmin, String> coltblusername;
    @FXML
    private TableColumn<mdlAdmin, String> coltblemail;
    @FXML
    private TableColumn<mdlAdmin,String> coltblpassword;
    @FXML
    private TableColumn<mdlAdmin, String> coltblusertype;   
    @FXML
    private ComboBox combobox;

    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    
    ObservableList<String> combo = FXCollections.observableArrayList("Patient", "Admin","Receptionist", "Doctor");
    ObservableList<mdlAdmin> tblObs = FXCollections.observableArrayList();
    @FXML
    private TextField search;
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelet;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button backbtn;

    
    // Initializes the controller class.
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showData();
        combobox.setItems(combo);
       
    }    
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
    
    @FXML
    private void handleMouseAction(MouseEvent event) {
        mdlAdmin mdl = adminTable.getSelectionModel().getSelectedItem();
        search.setText(String.valueOf(mdl.getUser_id()));
       
    }
    
    public ObservableList<mdlAdmin> getpatientList() {
    ObservableList<mdlAdmin> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            rs = conn.createStatement().executeQuery("select * from reg" );
            mdlAdmin mdlTable;
            while(rs.next()){
                  
                mdlTable = new mdlAdmin(rs.getInt("user_id"), rs.getString("name"),
                rs.getString("userName"),rs.getString("email"), rs.getString("passw"), rs.getString("userType"));
                tblObs.add(mdlTable);
            }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
         return tblObs;
     }
    
    public void showData(){
        ObservableList<mdlAdmin> list = getpatientList();
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlAdmin, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("name"));
        coltblusername.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userName"));
        coltblemail.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("email"));
        coltblpassword.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("passw"));
        coltblusertype.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userType"));
        adminTable.setItems(list);
         
     }
   
    @FXML
    private void loadRecord(ActionEvent event) {
        showData();
        search.setText("");
    }
  
    @FXML
    private void recepSave(ActionEvent event) {
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("adminEditUserTable.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Add New User");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);   
        }
    }
    
    Alert a = new Alert(AlertType.NONE);
   
    @FXML
    private void deletRecord(ActionEvent event) throws ClassNotFoundException, SQLException {
        a.setAlertType(AlertType.CONFIRMATION);
        a.setContentText("Once you clicked ok, you will lose all the data related to this row!");
        a.setHeaderText(null);
        Optional <ButtonType> action = a.showAndWait();
        if(action.get()== ButtonType.OK){
            Connect();
            pst= conn.prepareStatement("DELETE FROM reg WHERE user_id = ?");   
            pst.setString(1,search.getText());
            pst.executeUpdate();
            
             if(search.getText().isEmpty()) { 
                a.setAlertType(AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("No data have been selected for updating, please select a row!");
                a.show();
                return;
             }

            a.setAlertType(AlertType.INFORMATION);
            a.setContentText( " Data Deleted Successfully!");
            a.setHeaderText(null);    
            a.showAndWait();
            showData();
            search.setText("");
            search.requestFocus();                                    
        } 
    }


    @FXML
    private void btnSearch(ActionEvent event) {
        ObservableList<mdlAdmin> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            String srh = search.getText();
            String query =  "select * from reg WHERE user_id = '"+srh+ "'";
            rs = conn.createStatement().executeQuery(query);
             mdlAdmin mdlTable;
            while(rs.next()){       
                mdlTable = new mdlAdmin(rs.getInt("user_id"), rs.getString("name"),
                rs.getString("userName"),rs.getString("email"), rs.getString("passw"), rs.getString("userType"));
                tblObs.add(mdlTable);    
            }
                
        }catch(Exception ex){
             ex.printStackTrace(); 
        }
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlAdmin, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("name"));
        coltblusername.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userName"));
        coltblemail.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("email"));
        coltblpassword.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("passw"));
        coltblusertype.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userType"));
        adminTable.setItems(tblObs);
   
    }
    @FXML
    private void getSelected(ActionEvent event) {
        ObservableList<mdlAdmin> tbl = FXCollections.observableArrayList();
         
        try{
            Connect();
            String combox = combobox.getSelectionModel().getSelectedItem().toString();
            String query =  "select * from reg WHERE userType = '"+combox+ "'";
            rs = conn.createStatement().executeQuery(query);
             mdlAdmin mdlTable;
            while(rs.next()){       
                mdlTable = new mdlAdmin(rs.getInt("user_id"), rs.getString("name"),
                rs.getString("userName"),rs.getString("email"), rs.getString("passw"), rs.getString("userType"));
                tbl.add(mdlTable);
            }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlAdmin, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("name"));
        coltblusername.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userName"));
        coltblemail.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("email"));
        coltblpassword.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("passw"));
        coltblusertype.setCellValueFactory(new PropertyValueFactory<mdlAdmin,String>("userType"));
        adminTable.setItems(tbl);
   }
   
    @FXML
    private void returnBtn(ActionEvent event) {
        try{
            ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("admin.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            
        }
    }  
    
}