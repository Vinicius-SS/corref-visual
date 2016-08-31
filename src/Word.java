import java.io.Serializable;
import java.util.ArrayList;


public class Word implements Serializable{
	
String word; //palavra
String pos; //part of speech
String morfo; //morfologia  (contem genero e numero da palavra)
String lemma; //lemma
int sentenca; //sentença em que ocorre
String id;
int tokenID;
int SNid;
boolean cor=false; //usado em Functions.outputmaker para saber se ja foi atribuido cor uma vez
String wordintacta; //usado em Functions.outputmaker
String cororiginal;

public Word (String word, String pos, String morfo,String lemma,int sentenca, int tokenID){
//	
this.word=word;
wordintacta=word;
this.pos=pos;
this.morfo=morfo;
this.lemma=lemma;
this.sentenca=sentenca;
this.tokenID=tokenID;
//}
}


public Word(String id, String word){
    this.id=id;
    this.word=word;
       
}

}