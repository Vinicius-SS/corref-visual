package model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe usada para modelar os sintagmas que são agrupados nas cadeias.
 *
 * @author Vinicius <vinicius.s.sesti@gmail.com>
 */
public class Sintagma implements Serializable
{

    public String textName;
    public String sn;
    public int sentenca;
    public List<Word> words;
    public int set;
    public int snID;
    public String nucleo;
    public boolean prop;
    public String genero;
    public String numero;
    public boolean nucleoPronome;
    public String groupedBy;
    public boolean shallow;
    public ArrayList<Integer> paiDe;
    public int filhoDe;
    public Color cor;
    public String categoriaSemantica;
    public int startToken;
    public int endToken;
    public int startChar;
    public int endChar;

    public Sintagma(String textName, String sn, int sentenca, List<Word> words, int set, int snID, String nucleo, String categoriaSemantica)
    {
        this.textName = textName;
        this.sn = sn;
        this.sentenca = sentenca;
        this.words = words;
        this.set = set;
        this.snID = snID;
        this.nucleo = nucleo;
        this.categoriaSemantica = categoriaSemantica;
    }

    @Override
    public String toString()
    {
        return sn;
    }

}
