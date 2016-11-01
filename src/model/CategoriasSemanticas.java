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
//    ABS_CRIM("EN_ABS/CRIM"),
//    ABS_DISC("EN_ABS/DISC"),
//    ABS_EPOCA("EN_ABS/EPOCA"),
//    ABS_ESTCOND("EN_ABS/ESTCOND"),
//    ABS_FMENTAL("EN_ABS/FMENTAL"),
//    ABS_ITAX("EN_ABS/ITAX"),
//    AMC_BIBL("EN_AMC/BIBL"),
//    AMC_FILMVID("EN_AMC/FILMVID"),
//    AMC_PRDIC("EN_AMC/PRDIC"),
//    AMC_RTT("EN_AMC/RTT"),
//    EVNT_DESP("EN_EVNT/DESP"),
//    EVNT_EFEMR("EN_EVNT/EFEMR"),
//    EVNT_SOCCUL("EN_EVNT/SOCCUL"),
//    LOC_("EN_LOC"),
//    LOC_COSMO("EN_LOC/COSMO"),
//    LOC_HIDRO("EN_LOC/HIDRO"),
//    LOC_PAIS("EN_LOC/PAIS"),
//    LOC_POV("EN_LOC/POV"),
//    LOC_TERR("EN_LOC/TERR"),
//    MISC_MOEDA("EN_MISC/MOEDA"),
//    MISC_TIT("EN_MISC/TIT"),
//    MISC_UNID("EN_MISC/UNID"),
//    NAT_ANIM("EN_NAT/ANIM"),
//    NAT_FENAT("EN_NAT/FENAT"),
//    NAT_FISI("EN_NAT/FISI"),
//    NAT_MICRORG("EN_NAT/MICRORG"),
//    ORG_("EN_ORG"),
//    ORG_CIVMIL("EN_ORG/CIVMIL"),
//    ORG_EID("EN_ORG/EID"),
//    ORG_EMPR("EN_ORG/EMPR"),
//    ORG_GOVADM("EN_ORG/GOVADM"),
//    PPL_CERT("EN_PPL/CERT"),
//    PPL_DOCS("EN_PPL/DOCS"),
//    PROD_CONS("EN_PROD/CONS"),
//    PROD_FERRINST("EN_PROD/FERRINST"),
//    PROD_GAS("EN_PROD/GAS"),
//    PROD_MARC("EN_PROD/MARC"),
//    SER_("EN_SER"),
//    SER_GEI("EN_SER/GEI"),
//    SER_GHUM("EN_SER/GHUM"),
//    SER_HUM("EN_SER/HUM"),
//    SER_MITLG("EN_SER/MITLG"),
//    SUBS_GRP("EN_SUBS/GRP"),
//    SUBS_SUBS("EN_SUBS/SUBS");
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

    
    
    private String categ;
    
    public String getCateg()
    {
        return categ;
    }

    CategoriasSemanticas(String categ)
    {
        this.categ = categ;
    }
}
