package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 07-Mar-18.
 */

public class IALink {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("usr")
    @Expose
    int id_usr;
    @SerializedName("type")
    @Expose
    String type;

    public IALink(){

    }
    public IALink(int id, int id_usr, String type) {
        this.id = id;
        this.id_usr = id_usr;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usr() {
        return id_usr;
    }

    public void setId_usr(int id_usr) {
        this.id_usr = id_usr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
