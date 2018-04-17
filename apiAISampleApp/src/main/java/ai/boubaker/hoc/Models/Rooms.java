package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 27-Feb-18.
 */

public class Rooms {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("building")
    @Expose
    String building;
    @SerializedName("img")
    @Expose
    String img;
    @SerializedName("floor")
    @Expose
    String floor;

    public int getId() {
        return id;
    }

    public Rooms(int id, String name, String building, String floor, String img) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.img = img;
    }

    public Rooms(){

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

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
    public String getImg() { return img; }

    public void setImg(String img) { this.img = img; }
}
