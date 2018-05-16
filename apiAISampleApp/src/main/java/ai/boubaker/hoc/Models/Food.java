package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 19-Mar-18.
 */

public class Food {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("day")
    @Expose
    String day;
    @SerializedName("description")
    @Expose
    String description;
    public Food(){
    }

    public Food(int id, String day, String description) {
        this.id = id;
        this.day = day;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
