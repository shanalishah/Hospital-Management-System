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
public class mdlDoctor {
    private int user_id, phone;
    private String name, mettingDate, mettingTime;

    public mdlDoctor(int user_id, int phone, String name, String mettingDate, String mettingTime) {
        this.user_id = user_id;
        this.phone = phone;
        this.name = name;
        this.mettingDate = mettingDate;
        this.mettingTime = mettingTime;
    }
    mdlDoctor(){
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
