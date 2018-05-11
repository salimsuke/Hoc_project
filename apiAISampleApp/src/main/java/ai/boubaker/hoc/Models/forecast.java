package ai.boubaker.hoc.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bouba on 18-Apr-18.
 */

public class forecast {
    @SerializedName("weather")
    @Expose
    weather[] weather;
    @SerializedName("main")
    @Expose
    temp_hum_press temp_hum_press;
    @SerializedName("wind")
    @Expose
    wind wind;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("sys")
    @Expose
    sys sys;
    @SerializedName("visibility")
    @Expose
    int visibility;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public ai.boubaker.hoc.Models.sys getSys() {
        return sys;
    }

    public void setSys(ai.boubaker.hoc.Models.sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ai.boubaker.hoc.Models.weather[] getWeather() {
        return weather;
    }

    public void setWeather(ai.boubaker.hoc.Models.weather[] weather) {
        this.weather = weather;
    }

    public ai.boubaker.hoc.Models.temp_hum_press getTemp_hum_press() {
        return temp_hum_press;
    }

    public void setTemp_hum_press(ai.boubaker.hoc.Models.temp_hum_press temp_hum_press) {
        this.temp_hum_press = temp_hum_press;
    }

    public ai.boubaker.hoc.Models.wind getWind() {
        return wind;
    }

    public void setWind(ai.boubaker.hoc.Models.wind wind) {
        this.wind = wind;
    }

    @Override
    public String toString() {
        return "forecast{" +
                "weather=" + weather.toString() +
                ", temp_hum_press=" + temp_hum_press.toString() +
                ", wind=" + wind.toString() +
                '}';
    }
}
