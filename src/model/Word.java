package model;

import java.io.Serializable;

public class Word implements Serializable
{

    public String word;
    public String pos;
    public String morfo;
    public String lemma;
    public int sentenca;
    public int tokenID;

    public Word(String word, String pos, String morfo, String lemma, int sentenca, int tokenID)
    {
        this.word = word;
        this.pos = pos;
        this.morfo = morfo;
        this.lemma = lemma;
        this.sentenca = sentenca;
        this.tokenID = tokenID;
    }
}
