package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 28-Feb-18.
 */

public class Swiches {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("emplacement")
    @Expose
    String emplacement;
    public Swiches(){

    }
    public Swiches(int id, String name, String description, String emplacement) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.emplacement = emplacement;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
}
