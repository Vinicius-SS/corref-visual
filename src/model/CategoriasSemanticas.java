/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Vinicius <vinicius.s.sesti@gmail.com>
 */
public enum CategoriasSemanticas
{
    ABSTRA플O("ABSTRA플O"),
    COMUNICA플O("COMUNICA플O"),
    PESSOA("PESSOA"),
    OUTROS_SERES("OUTROS_SERES"),
    ORGLOCAL("ORGANIZA플O|LOCAL"),
    NATUREZA("NATUREZA"),
    EVENTO("EVENTO"),
    DOCUMENTOS("DOCUMENTOS"),
    SUBST헞CIAS("SUBST헞CIAS"),
    PRODUTOS("PRODUTOS"),
    OUTRO("OUTRO")
    ;
    
    private final String categ;
    
    public String getCateg()
    {
        return categ;
    }

    CategoriasSemanticas(String categ)
    {
        this.categ = categ;
    }
}
