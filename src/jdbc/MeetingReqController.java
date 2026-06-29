/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class MeetingReqController implements Initializable {
    
    @FXML
    private TableView<mdlMet> table;

    @FXML
    private TableColumn<mdlMet, Integer> colltblid;

    @FXML
    private TableColumn<mdlMet, String> coltblname;

    @FXML
    private TableColumn<mdlMet, Integer> coltblphone;

    @FXML
    private TableColumn<mdlMet, String> coltbldoctor;

    @FXML
    private TableColumn<mdlMet, String> coltbldate;
    
    @FXML
    private TextField search;

    @FXML
    private JFXButton signoutbtn;
    
    @FXML
    private Button btnLoad;

      /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showData();
        // TODO
    }  

    ObservableList<mdlMet> tblObs = FXCollections.observableArrayList();
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
  
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
    
     public ObservableList<mdlMet> getpatientList() {
        ObservableList<mdlMet> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            rs = conn.createStatement().executeQuery("select * from appointment");
            mdlMet mdlTable;
            while(rs.next()){
                  
                mdlTable = new mdlMet(rs.getInt("user_id"), rs.getString("name"),
                rs.getInt("Pphone"), rs.getString("Pdoctor"),rs.getString("mettingDate"));
                tblObs.add(mdlTable);
          
              }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
         return tblObs;
     }
    
    public void showData(){
        ObservableList<mdlMet> list = getpatientList();
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlMet, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlMet,Integer>("Pphone"));
        coltbldoctor.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("Pdoctor"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("mettingDate"));
        table.setItems(list);
         
     }
    
    @FXML
    void loadRecord(ActionEvent event) {
        showData();
        search.setText("");

    }
      
    @FXML
     void btnSearch(ActionEvent event) {
          ObservableList<mdlMet> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            String srh = search.getText();
            String query =  "select * from appointment WHERE user_id = '"+srh+ "'";
            rs = conn.createStatement().executeQuery(query);
             mdlMet mdlTable;
            while(rs.next()){       
                mdlTable = new mdlMet(rs.getInt("user_id"), rs.getString("name"),
                rs.getInt("Pphone"), rs.getString("Pdoctor"),rs.getString("mettingDate"));
                tblObs.add(mdlTable);
                //rs.close();
                
              }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
         colltblid.setCellValueFactory(new PropertyValueFactory<mdlMet, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlMet,Integer>("Pphone"));
        coltbldoctor.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("Pdoctor"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlMet,String>("mettingDate"));
        table.setItems(tblObs);
        

    }
      
    @FXML
    void returnBtn(ActionEvent event) {
         
        try{
        ((Node)event.getSource()).getScene().getWindow().hide();
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("FXMLmain.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Hospital Managment System");
            stage.setScene(scene);
            stage.show();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            
        }
    }
  
     
    @FXML
      public void signOut(ActionEvent event){
        
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

}
