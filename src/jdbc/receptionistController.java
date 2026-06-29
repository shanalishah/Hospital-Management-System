
package jdbc;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTimePicker;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;



public class receptionistController implements  Initializable{
 

    @FXML
    private JFXButton signoutbtn;

    @FXML
    private TextField paID;

    @FXML
    private TextField paName;

    @FXML
    private TextField paPhone;

    @FXML
    private ComboBox<String> paDoctor;

    @FXML
    private DatePicker paMettingTime;
    
     @FXML
    private JFXTimePicker paMettingDate;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSave;
    
    @FXML
    private Button backbtn;
    
    @FXML
    private Button btnDelet;
    
    @FXML
    private TextField search;
    
    @FXML
    private Button btnLoad;
  
    @FXML
    private TableView<mdlRec> table;

    @FXML
    private TableColumn<mdlRec, Integer> colltblid;

    @FXML
    private TableColumn<mdlRec, String> coltblname;

    @FXML
    private TableColumn<mdlRec, Integer> coltblphone;

    @FXML
    private TableColumn<mdlRec, String> coltbldoctor;

    @FXML
    private TableColumn<mdlRec, String> coltbldate;
    
    @FXML
    private TableColumn<mdlRec, String> coltbltime;

   
    ObservableList<String> combobx = FXCollections.observableArrayList("Martin", "Roza","Josef", "Sara");
    
    ObservableList<mdlRec> tblObs = FXCollections.observableArrayList();
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rs= null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
        paDoctor.setItems(combobx);
        showData();
    
}   
     @FXML
      public void recordUpdate(ActionEvent event) throws ClassNotFoundException, SQLException{
          Connect();
           String doctor = (String) paDoctor.getValue();
            pst= conn.prepareStatement("update patient set name=?, phone=?, doctor=?, mettingdate=?, mettingTime=? where user_id = ?");
            
            pst.setString(6,paID.getText());
            pst.setString(1,paName.getText());
            pst.setString(2,paPhone.getText());
            pst.setString(3, doctor);
            pst.setString(4,((TextField)paMettingTime.getEditor()).getText());
            pst.setString(5,((TextField)paMettingDate.getEditor()).getText());
            pst.executeUpdate();
            
             if(paID.getText().isEmpty() && paName.getText().isEmpty() && paPhone.getText().isEmpty()) { 
                a.setAlertType(AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("No data have been selected for updating, please select a row!");
                a.show();
                return;
             }
             
            a.setAlertType(AlertType.INFORMATION);
            a.setContentText( " Data Updated Successfully!");
            a.setHeaderText(null);
            a.showAndWait(); 
            showData();
            paID.setText("");
            paName.setText("");
            paPhone.setText("");
            search.setText("");
            paDoctor.setValue(null);
            paMettingTime.setValue(null);
            paName.requestFocus();   
        }
      
   
    @FXML
    public void deletRecord(ActionEvent event) throws SQLException, ClassNotFoundException  {
          
        a.setAlertType(AlertType.CONFIRMATION);
        a.setContentText("Once you clicked ok, you will lose all the data related to this row!");
        a.setHeaderText(null);
        Optional <ButtonType> action = a.showAndWait();
        if(action.get()== ButtonType.OK){
            Connect();
            pst= conn.prepareStatement("DELETE FROM patient WHERE user_id = ?");   
            pst.setString(1,paID.getText());
            pst.executeUpdate();
            
             if(paID.getText().isEmpty() &&paName.getText().isEmpty() && paPhone.getText().isEmpty()) { 
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
            paID.setText("");
            paName.setText("");
            paPhone.setText("");
            search.setText("");
            paDoctor.setValue(null);
            paMettingTime.setValue(null);
            paName.requestFocus();                       
                      
        }  
    }
      
   
    @FXML
      public void signOutRecption(ActionEvent event){
        
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
            pst= conn.prepareStatement("insert into patient ( name, phone, doctor,mettingDate, mettingTime) values(?,?,?,?,?)");
            
           // pst.setString(1,paID.getText());
            pst.setString(1,paName.getText());
            pst.setString(2,paPhone.getText());
            pst.setString(3, doctor);
            pst.setString(4,((TextField)paMettingTime.getEditor()).getText());
            pst.setString(5,((TextField)paMettingDate.getEditor()).getText());
            
            if(paName.getText().isEmpty()) { 
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
            showData();
            paName.setText("");
            paPhone.setText("");
            paDoctor.setValue(null);
            paMettingTime.setValue(null);
            paName.requestFocus();
         
        } catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
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
     void btnSearch(ActionEvent event) {
          ObservableList<mdlRec> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            String srh = search.getText();
            String query =  "select * from patient WHERE user_id = '"+srh+ "'";
            rs = conn.createStatement().executeQuery(query);
             mdlRec mdlTable;
            while(rs.next()){       
                mdlTable = new mdlRec(rs.getInt("user_id"), rs.getString("name"),
                rs.getInt("phone"), rs.getString("doctor"),rs.getString("mettingDate"), rs.getString("mettingTime"));
                tblObs.add(mdlTable);
                //rs.close();
                
              }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlRec, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlRec,Integer>("phone"));
        coltbldoctor.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("doctor"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("mettingDate"));
        coltbltime.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("mettingTime"));
        table.setItems(tblObs);
        

    }
        
        public ObservableList<mdlRec> getpatientList() {
        ObservableList<mdlRec> tblObs = FXCollections.observableArrayList();
         
        try{
            Connect();
            rs = conn.createStatement().executeQuery("select * from patient");
            mdlRec mdlTable;
            while(rs.next()){
                  
                mdlTable = new mdlRec(rs.getInt("user_id"), rs.getString("name"),
                rs.getInt("phone"), rs.getString("doctor"),rs.getString("mettingDate"), rs.getString("mettingTime"));
                tblObs.add(mdlTable);
               
              }
                
         }catch(Exception ex){
             ex.printStackTrace(); 
         }
         return tblObs;
     }
    
    public void showData(){
        ObservableList<mdlRec> list = getpatientList();
        colltblid.setCellValueFactory(new PropertyValueFactory<mdlRec, Integer> ("user_id"));
        coltblname.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("name"));
        coltblphone.setCellValueFactory(new PropertyValueFactory<mdlRec,Integer>("phone"));
        coltbldoctor.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("doctor"));
        coltbldate.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("mettingDate"));
        coltbltime.setCellValueFactory(new PropertyValueFactory<mdlRec,String>("mettingTime"));
        table.setItems(list);
         
     }

    @FXML
    void handleMouseAction(MouseEvent event) {
       mdlRec mdl = table.getSelectionModel().getSelectedItem();
       
        paName.setText(mdl.getName());
        paID.setText(String.valueOf(mdl.getUser_id()));
        paPhone.setText(String.valueOf(mdl.getPhone()));
        paDoctor.setValue("Sara");
        paMettingTime.setValue(LocalDate.now());
        paMettingDate.setValue(LocalTime.now());
        
    }       
}
    