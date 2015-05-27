package com.example.tema.testviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tema on 11.05.2015.
 */
public class Grade implements Parcelable{
    public Grade(Parcel dest) {
        name = dest.readString();
        discription = dest.readString();
        id = dest.readInt();
        numberOfPoints = dest.readInt();
    }

    public Grade() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String name;
    private String discription;
    private int numberOfPoints;

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(discription);
        dest.writeInt(id);
        dest.writeInt(numberOfPoints);
    }

    public static final Parcelable.Creator<Grade> CREATOR = new Parcelable.Creator<Grade>() {
        public Grade createFromParcel(Parcel pc) {
            return new Grade(pc);
        }
        public Grade[] newArray(int size) {
            return new Grade[size];
        }
    };
}
