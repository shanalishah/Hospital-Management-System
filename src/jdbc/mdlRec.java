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
public class mdlRec {
    private int user_id, phone;
    private String name, doctor, mettingDate, mettingTime;

    public mdlRec(int user_id, String name, int phone, String doctor, String mettingDate, String mettingTime) {
        this.user_id = user_id;
        this.name = name;
        this.phone = phone;
        this.doctor = doctor;
        this.mettingDate = mettingDate;
        this.mettingTime= mettingTime;
    }
    public mdlRec(){
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getMettingDate() {
        return mettingDate;
    }

    public void setMettingDate(String mettingDate) {
        this.mettingDate = mettingDate;
    }
     public String getMettingTime() {
        return mettingTime;
    }
     public void setMettingTime(String mettingTime) {
        this.mettingTime = mettingTime;
    }
    
}
