package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 28-Feb-18.
 */

public class Interventions {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("date")
    @Expose
    String date;
    @SerializedName("technitions")
    @Expose
    Techniciens technician;
    @SerializedName("swiches")
    @Expose
    Swiches switcher;
    @SerializedName("description")
    @Expose
    String description;

    public Interventions(int id, String date, Techniciens id_technician, Swiches switcher, String description) {
        this.id = id;
        this.date = date;
        this.technician = id_technician;
        this.switcher = switcher;
        this.description = description;
    }
    public Interventions(){

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Techniciens gettechnician() {
        return technician;
    }

    public void settechnician(Techniciens id_technician) {
        this.technician = id_technician;
    }

    public Swiches getswitcher() {
        return switcher;
    }

    public void setswitcher(Swiches switcher) {
        this.switcher = switcher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}