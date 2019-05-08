package com.andrezamoreira.pontos.models;

public class TimeInterval {
    private String start;
    private String end;
    private String duration;

    public TimeInterval(){

    }

    public TimeInterval(String start, String end, String duration){
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
