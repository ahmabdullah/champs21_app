package com.champs21.schoolapp.model;

/**
 * Created by BLACK HAT on 18-May-15.
 */
public class SpellingbeeDataModel {


    private int id = 0;
    private String word;
    private String banglaMeaning;
    private String definition;
    private String sentence;
    private String wType;
    private String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getwType() {
        return wType;
    }

    public void setwType(String wType) {
        this.wType = wType;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getBanglaMeaning() {
        return banglaMeaning;
    }

    public void setBanglaMeaning(String banglaMeaning) {
        this.banglaMeaning = banglaMeaning;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SpellingbeeDataModel(int id, String word, String banglaMeaning, String definition, String sentence, String wType, String level)
    {
        this.id = id;
        this.word = word;
        this.banglaMeaning = banglaMeaning;
        this.definition = definition;
        this.sentence = sentence;
        this.wType = wType;
        this.level = level;
    }

    public SpellingbeeDataModel()
    {

    }
}
