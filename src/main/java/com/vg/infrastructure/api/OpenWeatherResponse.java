package com.vg.infrastructure.api;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class OpenWeatherResponse implements Serializable {

    private String base;
    private long visibility;
    private long dt;
    private long timezone;
    private long id;
    private String name;
    private long cod;
    private Coord coord;
    private List<Weather> weather;
    private Main main;
    private Wind wind;
    private Clouds clouds;
    private Sys sys;

    @Data
    public static class Coord {
        private long lon;
        private long lat;
    }

    @Data
    public static class Weather {
        private long id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        private long temp;
        private long feelsLike;
        private long tempMin;
        private long tempMax;
        private long pressure;
        private long humidity;
    }

    @Data
    public static class Wind {
        private double speed;
        private double deg;
    }

    @Data
    public static class Clouds {
        private long clouds;
    }

    @Data
    public static class Sys {
        private long type;
        private long id;
        private String country;
        private long sunrise;
        private long sunset;
    }
}
