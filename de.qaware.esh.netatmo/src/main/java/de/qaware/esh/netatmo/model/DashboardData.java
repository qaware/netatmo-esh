package de.qaware.esh.netatmo.model;

import com.google.gson.annotations.SerializedName;

public class DashboardData {
    @SerializedName("Temperature")
    private double temperature;

    @SerializedName("Humidity")
    private double humidity;

    @SerializedName("CO2")
    private double co2;

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getCo2() {
        return co2;
    }
}
