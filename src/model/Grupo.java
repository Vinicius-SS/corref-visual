package model;

import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class Grupo
{

    private ArrayList<Sintagma> listaSintagmas;
    private final Color cor;

    public Grupo(ArrayList<Sintagma> lista)
    {
        listaSintagmas = new ArrayList<>();
        listaSintagmas.addAll(lista);
        Random r = new Random();
        cor = new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256));
    }

    public Grupo()
    {
        listaSintagmas = new ArrayList<>();
        Random r = new Random();
        cor = new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256));
    }

    public boolean addSintagma(Sintagma s)
    {
        s.cor = cor;
        return listaSintagmas.add(s);
    }

    public ArrayList<Sintagma> getListaSintagmas()
    {
        return listaSintagmas;
    }

    public boolean remove(Sintagma s)
    {
        for (Sintagma aux : listaSintagmas)
            if (aux.snID == s.snID)
                return listaSintagmas.remove(s);
        return false;
    }
    
    public Color getColor()
    {
        return cor;
    }

    @Override
    public String toString()
    {
        String r = "";
        for (Sintagma s : listaSintagmas)
            r += s.sn + " ";
        return r;
    }

}
