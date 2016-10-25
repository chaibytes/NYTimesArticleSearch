package com.chaibytes.newyorktimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by pdebadarshini on 10/23/16.
 */

public class Settings implements Parcelable {

    public Calendar getCalendar() {
        return calendar;
    }

    public String getOption() {
        return option;
    }

    public boolean isArtChecked() {
        return isArtChecked;
    }

    public boolean isFashionChecked() {
        return isFashionChecked;
    }

    public boolean isSportsChecked() {
        return isSportsChecked;
    }

    Calendar calendar;
    String option;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setArtChecked(boolean artChecked) {
        isArtChecked = artChecked;
    }

    public void setFashionChecked(boolean fashionChecked) {
        isFashionChecked = fashionChecked;
    }

    public void setSportsChecked(boolean sportsChecked) {
        isSportsChecked = sportsChecked;
    }

    boolean isArtChecked;
    boolean isFashionChecked;
    boolean isSportsChecked;

    public Settings() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.calendar);
        dest.writeString(this.option);
        dest.writeByte(this.isArtChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFashionChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSportsChecked ? (byte) 1 : (byte) 0);
    }

    protected Settings(Parcel in) {
        this.calendar = (Calendar) in.readSerializable();
        this.option = in.readString();
        this.isArtChecked = in.readByte() != 0;
        this.isFashionChecked = in.readByte() != 0;
        this.isSportsChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Settings> CREATOR = new Parcelable.Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel source) {
            return new Settings(source);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

}
