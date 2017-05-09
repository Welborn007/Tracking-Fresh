package com.kesari.trackingfresh.AddToCart;

/**
 * Created by kesari on 08/05/17.
 */

public class AddCart_model {

    public AddCart_model() {

    }

    String item_name,pageno,volume,instructions,username,thumbnail;


    public AddCart_model(String item_name, String pageno, String volume, String instructions, String username, String thumbnail) {
        this.item_name = item_name;
        this.pageno = pageno;
        this.volume = volume;
        this.instructions = instructions;
        this.username = username;
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPageno() {
        return pageno;
    }

    public void setPageno(String pageno) {
        this.pageno = pageno;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }


    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
