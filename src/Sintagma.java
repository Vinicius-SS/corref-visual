import java.io.Serializable;
import java.util.ArrayList;


public class Sintagma  implements Serializable{

public String TextName;	 //Nome do documento o qual o sintagma pertence
public String sn;  //Sintagma nominal
public int sentenca;  //Sentença a qual este pertence
public ArrayList <Word> words=new ArrayList<Word>(); //Possui cada palavra do sintagma, contendo: lemma, pos e morfologia (ver objeto Word)
public int set;  //set define o grupo de correferência do Sn. (sintagmas com sets iguais são correferentes)
public int snID;  //Id único para o sintagma (auxilia em casos em que temos sintagmas com escrita idêntica)
public String nucleo="--"; //nucleo do sintagma
public String lemma=""; //Lemma do núcleo do sintagma
public boolean prop=false; // se é nome próprio
public String genero; // Gênero masculino/feminino
public String numero; // singular/plural
public boolean NucleoPronome=false; //Se o nucleo é um pronome
public String groupedBy="";  //armazena todas as regras as quais foram utilizadas para uní-lo a determinada cadeia
public boolean shallow=false; //Quando o sintagma possui adjuntos que podem ser outros sintagmas (Marca se este sintagma possui filhos)
public ArrayList<Integer>Pai_De=new ArrayList<Integer>(); //id do(s) Sns em que este é pai (se for o caso).
public int Filho_De=-1; // -1 = não é filho de ninguém
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
