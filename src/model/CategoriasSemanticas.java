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
    ABSTRAÇÃO("ABSTRAÇÃO"),
    COMUNICAÇÃO("COMUNICAÇÃO"),
    PESSOA("PESSOA"),
    OUTROS_SERES("OUTROS_SERES"),
    ORGLOCAL("ORGANIZAÇÃO|LOCAL"),
    NATUREZA("NATUREZA"),
    EVENTO("EVENTO"),
    DOCUMENTOS("DOCUMENTOS"),
    SUBSTÂNCIAS("SUBSTÂNCIAS"),
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
