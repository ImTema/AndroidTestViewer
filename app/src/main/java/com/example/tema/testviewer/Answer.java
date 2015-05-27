package com.example.tema.testviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tema on 11.05.2015.
 */
public class Answer implements Parcelable{
    public Answer(Parcel dest) {
        answerText = dest.readString();
        answerText2 = dest.readString();
        correct  = dest.readInt()==1;
        price = dest.readInt();
        id = dest.readInt();

    }

    public Answer() {
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getAnswerText2() {
        return answerText2;
    }

    public void setAnswerText2(String answerText2) {
        this.answerText2 = answerText2;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String answerText;
    private String answerText2;
    private boolean correct;
    private int price;
    private int id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(answerText);
        dest.writeString(answerText2);
        dest.writeInt(correct ? 1 : 0);
        dest.writeInt(price);
        dest.writeInt(id);
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel pc) {
            return new Answer(pc);
        }
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
