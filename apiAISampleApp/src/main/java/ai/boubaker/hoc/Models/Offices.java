package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 28-Feb-18.
 */

public class Offices {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("swiches")
    @Expose
    Swiches switcher;
    public Offices(){

    }
    public Offices(int id, String name, Swiches switcher, String description) {
        this.id = id;
        this.name = name;
        this.switcher = switcher;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Swiches getSwitcher() {
        return switcher;
    }

    public void setSwitcher(Swiches switcher) {
        this.switcher = switcher;
    }
}
