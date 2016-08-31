import java.io.Serializable;
import java.util.ArrayList;


public class Sintagma  implements Serializable{

public String TextName;	 //Nome do documento o qual o sintagma pertence
public String sn;  //Sintagma nominal
public int sentenca;  //Senten�a a qual este pertence
public ArrayList <Word> words=new ArrayList<Word>(); //Possui cada palavra do sintagma, contendo: lemma, pos e morfologia (ver objeto Word)
public int set;  //set define o grupo de correfer�ncia do Sn. (sintagmas com sets iguais s�o correferentes)
public int snID;  //Id �nico para o sintagma (auxilia em casos em que temos sintagmas com escrita id�ntica)
public String nucleo="--"; //nucleo do sintagma
public String lemma=""; //Lemma do n�cleo do sintagma
public boolean prop=false; // se � nome pr�prio
public String genero; // G�nero masculino/feminino
public String numero; // singular/plural
public boolean NucleoPronome=false; //Se o nucleo � um pronome
public String groupedBy="";  //armazena todas as regras as quais foram utilizadas para un�-lo a determinada cadeia
public boolean shallow=false; //Quando o sintagma possui adjuntos que podem ser outros sintagmas (Marca se este sintagma possui filhos)
public ArrayList<Integer>Pai_De=new ArrayList<Integer>(); //id do(s) Sns em que este � pai (se for o caso).
public int Filho_De=-1; // -1 = n�o � filho de ningu�m
public String CategoriaSemantica="--";
public int tokeninicial;
public int tokenfinal;
boolean aposto =false;
String cor;

public Sintagma(String sn, int sentenca, int snID,int set){
	
this.sn=sn;
this.sentenca=sentenca;
this.snID=snID;
this.set=set;
}

}
