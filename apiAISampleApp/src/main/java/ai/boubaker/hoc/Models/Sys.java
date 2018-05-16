package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 18-Apr-18.
 */

public class Sys {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("country")
    @Expose
    String country;
    @SerializedName("sunrise")
    @Expose
    Long sunrise;
    @SerializedName("sunset")
    @Expose
    Long sunset;
    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
