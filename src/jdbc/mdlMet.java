/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc;

/**
 *
 * @author DELL
 */
public class mdlMet {
    private int user_id, Pphone;
    private String name, Pdoctor, mettingDate;

    public mdlMet(int user_id, String name,int Pphone,  String Pdoctor, String mettingDate) {
        this.user_id = user_id;
        this.name = name;
        this.Pphone = Pphone;
        this.Pdoctor = Pdoctor;
        this.mettingDate= mettingDate;
    }
    
    public mdlMet(){
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPphone() {
        return Pphone;
    }

    public void setPphone(int Pphone) {
        this.Pphone = Pphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPdoctor() {
        return Pdoctor;
    }
    
     public String getMettingDate() {
        return mettingDate;
    }
     
    public void setMettingDate(String mettingDate) {
        this.mettingDate = mettingDate;
    }
    
    
}
