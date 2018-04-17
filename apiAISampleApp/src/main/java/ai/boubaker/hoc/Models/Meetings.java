package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 27-Feb-18.
 */

public class Meetings {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("uri")
    @Expose
    String uri;
    @SerializedName("date")
    @Expose
    String date;
    @SerializedName("rooms")
    @Expose
    Rooms room;
    @SerializedName("chairs")
    @Expose
    Chairs chair;
    @SerializedName("description")
    @Expose
    String description;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rooms getRoom() {
        return room;
    }

    public void setRoom(Rooms room) {
        this.room = room;
    }

    public Chairs getChair() {
        return chair;
    }

    public void setChair(Chairs chair) {
        this.chair = chair;
    }

    public Meetings(){

    }
    public Meetings(int id, String name, String uri, String date, Rooms room, Chairs chair, String description) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.date = date;
        this.room = room;
        this.chair = chair;
        this.description = description;
    }
}
