package model;

import java.io.Serializable;

public class Word implements Serializable
{

    public final String word;
    public final String pos;
    public final String morfo;
    public final String lemma;
    public final int sentenca;
    public final int tokenID;

    public Word(String word, String pos, String morfo, String lemma, int sentenca, int tokenID)
    {
        this.word = word;
        this.pos = pos;
        this.morfo = morfo;
        this.lemma = lemma;
        this.sentenca = sentenca;
        this.tokenID = tokenID;
    }
    
    @Override
    public String toString()
    {
        return word;
    }
}
