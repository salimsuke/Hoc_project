package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 18-Apr-18.
 */

public class Forecast {
    @SerializedName("Weather")
    @Expose
    Weather[] weather;
    @SerializedName("main")
    @Expose
    temp_hum_press temp_hum_press;
    @SerializedName("Wind")
    @Expose
    Wind wind;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("Sys")
    @Expose
    Sys sys;
    @SerializedName("visibility")
    @Expose
    int visibility;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public ai.boubaker.hoc.Models.temp_hum_press getTemp_hum_press() {
        return temp_hum_press;
    }

    public void setTemp_hum_press(ai.boubaker.hoc.Models.temp_hum_press temp_hum_press) {
        this.temp_hum_press = temp_hum_press;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "Weather=" + weather.toString() +
                ", temp_hum_press=" + temp_hum_press.toString() +
                ", Wind=" + wind.toString() +
                '}';
    }
}
