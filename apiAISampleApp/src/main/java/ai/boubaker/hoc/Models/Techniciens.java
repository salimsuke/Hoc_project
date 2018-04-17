package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 28-Feb-18.
 */
public class Techniciens {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("email")
    @Expose
    String email;
    @SerializedName("birthdate")
    @Expose
    String birthdate;
    @SerializedName("postalCode")
    @Expose
    String postal_code;
    @SerializedName("phoneNumber")
    @Expose
    String phone_number;
    @SerializedName("fullName")
    @Expose
    String full_name;
    public Techniciens(){

    }
    public Techniciens(int id, String name, String email, String birthdate, String postal_code, String phone_number, String full_name) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.postal_code = postal_code;
        this.phone_number = phone_number;
        this.full_name = full_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
