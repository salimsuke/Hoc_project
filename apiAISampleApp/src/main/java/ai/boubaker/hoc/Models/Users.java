package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 14-Feb-18.
 */

public class Users {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String User_name;
    @SerializedName("phoneNumber")
    @Expose
    String User_phone;
    @SerializedName("postalCode")
    @Expose
    String User_postal;
    @SerializedName("birthdate")
    @Expose
    String User_dob;
    @SerializedName("email")
    @Expose
    String email;
    @SerializedName("pin")
    @Expose
    String pin;

    public Users(int id, String user_name, String user_phone, String user_postal, String user_dob, String email, String pin) {
        this.id = id;
        User_name = user_name;
        User_phone = user_phone;
        User_postal = user_postal;
        User_dob = user_dob;
        this.email = email;
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return User_name;
    }

    public void setUser_name(String user_name) {
        User_name = user_name;
    }

    public String getUser_phone() {
        return User_phone;
    }

    public void setUser_phone(String user_phone) {
        User_phone = user_phone;
    }

    public String getUser_postal() {
        return User_postal;
    }

    public void setUser_postal(String user_postal) {
        User_postal = user_postal;
    }

    public String getUser_dob() {
        return User_dob;
    }

    public void setUser_dob(String user_dob) {
        User_dob = user_dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Users() {
    }

}
