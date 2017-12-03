package com.example.tema.testviewer;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tema on 10.05.2015.
 */
public class Settings implements Parcelable{
    private boolean randomizeQuestions;
    private boolean randomizeAnswers;
    private Time timeLimit;
    private String currentGrade;
    private HashMap<Integer, Grade> grades;
    int numberOfQuestions;

    public Settings(Parcel dest) {
        currentGrade=dest.readString();
        boolean arr[] = new boolean[2];
        dest.readBooleanArray(arr);
        randomizeAnswers = arr[0];
        randomizeQuestions = arr[1];
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        try {
            timeLimit=new Time(formatter.parse(dest.readString()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        numberOfQuestions = dest.readInt();
        grades = dest.readHashMap(Grade.class.getClassLoader());
    }

    public Settings() {

    }

    public Map<Integer, Grade> getGrades() {
        return grades;
    }

    public void setGrades(HashMap grades) {
        this.grades = grades;
    }


    public Time getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Time timeLimit) {
        this.timeLimit = timeLimit;
    }


    public boolean isRandomizeQuestions() {
        return randomizeQuestions;
    }

    public void setRandomizeQuestions(boolean randomizeQuestions) {
        this.randomizeQuestions = randomizeQuestions;
    }

    public boolean isRandomizeAnswers() {
        return randomizeAnswers;
    }

    public void setRandomizeAnswers(boolean randomizeAnswers) {
        this.randomizeAnswers = randomizeAnswers;
    }

    public String getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentGrade);
        dest.writeBooleanArray(new boolean[]{
                randomizeAnswers, randomizeQuestions
        });
        dest.writeString(timeLimit.toString());
        dest.writeInt(numberOfQuestions);
        dest.writeMap(grades);
    }

    public static final Parcelable.Creator<Settings> CREATOR = new Parcelable.Creator<Settings>() {
        public Settings createFromParcel(Parcel pc) {
            return new Settings(pc);
        }
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
}
