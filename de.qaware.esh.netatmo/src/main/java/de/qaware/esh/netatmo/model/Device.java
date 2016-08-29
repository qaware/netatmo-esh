package de.qaware.esh.netatmo.model;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("_id")
    private String id;

    @SerializedName("station_name")
    private String name;

    @SerializedName("dashboard_data")
    private DashboardData dashboardData;

    public String getId() {
        return id;
    }

    public DashboardData getDashboardData() {
        return dashboardData;
    }

    public String getName() {
        return name;
    }
}
