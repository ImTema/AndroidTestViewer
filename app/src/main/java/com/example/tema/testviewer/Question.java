package com.example.tema.testviewer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tema on 11.05.2015.
 */
public class Question implements Parcelable{

    public Question(Parcel dest) {
        id = dest.readInt();
        count = dest.readInt();
        answers = dest.readHashMap(Answer.class.getClassLoader());
        questionText = dest.readString();
        answerExplanation=dest.readString();
        fileName=dest.readString();
        questionType = dest.readInt();
    }

    public Question() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<Integer, Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<Integer, Answer> answers) {
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerExplanation() {
        return answerExplanation;
    }

    public void setAnswerExplanation(String answerExplanation) {
        this.answerExplanation = answerExplanation;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private int id;
    private int count;
    private HashMap<Integer, Answer> answers;
    private String questionText;
    private String answerExplanation;
    private int questionType;
    private String fileName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeMap(answers);
        dest.writeString(questionText);
        dest.writeString(answerExplanation);
        dest.writeString(fileName);
        dest.writeInt(questionType);
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel pc) {
            return new Question(pc);
        }
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
