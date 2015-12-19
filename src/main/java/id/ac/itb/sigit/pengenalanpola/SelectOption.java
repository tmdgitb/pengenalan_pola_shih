package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;

/**
 * Created by Yusfia Hafid A on 11/1/2015.
 */
public class SelectOption implements Serializable{
    private String key;
    private int value;

    public SelectOption(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String toString(){
        return key;
    }
}
