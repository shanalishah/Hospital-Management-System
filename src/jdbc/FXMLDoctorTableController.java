
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


public class FXMLDoctorTableController implements Initializable {
    
    @FXML
    private TableView<mdlDoctor> table;

    @FXML
    private TableColumn<mdlDoctor, Integer> colltblid;

    @FXML
    private TableColumn<mdlDoctor, String> coltblname;

    @FXML
    private TableColumn<mdlDoctor, Integer> coltblphone;

    @FXML
    private TableColumn<mdlDoctor, String> coltbldate;
    
    @FXML
    private TableColumn<mdlDoctor, String> coltbltime;
    
    @FXML
    private TextField search;

    @FXML
    private JFXButton signoutbtn;
    
    @FXML
    private Button btnLoad;
    
    ObservableList<mdlDoctor> tblObs = FXCollections.observableArrayList();
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showData();  
    }  
      
    public  void Connect() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc", "root", "");
    }
       
    public ObservableList<mdlDoctor> getpatientList() {
        ObservableList<mdlDoctor> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            rs = conn.createStatement().executeQuery("select * from patient");
            mdlDoctor mdlTable;
            while(rs.next()){
                  
                mdlTable = new mdlDoctor(rs.getInt("user_id"),rs.getInt("phone"),
                rs.getString("name"),rs.getString("mettingDate"), rs.getString("mettingTime"));
                tblObs.add(mdlTable);
               
              }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
         return tblObs;
     }
    
    public void showData(){
        ObservableList<mdlDoctor> list = getpatientList();
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlDoctor, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlDoctor,Integer>("phone"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("mettingDate"));
        coltbltime.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("mettingTime"));
        table.setItems(list);    
    }
    
    @FXML
     void btnSearch(ActionEvent event) {
          ObservableList<mdlDoctor> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            String srh = search.getText();
            String query =  "select * from patient WHERE user_id = '"+srh+ "'";
            rs = conn.createStatement().executeQuery(query);
             mdlDoctor mdlTable;
            while(rs.next()){       
                mdlTable = new mdlDoctor(rs.getInt("user_id"),rs.getInt("phone"),
                rs.getString("name"),rs.getString("mettingDate"), rs.getString("mettingTime"));
                tblObs.add(mdlTable);    
            }        
        }catch(Exception ex){
             ex.printStackTrace(); 
        }
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlDoctor, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlDoctor,Integer>("phone"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("mettingDate"));
        coltbltime.setCellValueFactory(new PropertyValueFactory<mdlDoctor,String>("mettingTime"));
        table.setItems(tblObs);
    }
     
    @FXML
    void loadRecord(ActionEvent event) {
        showData();
        search.setText("");
    }
    
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