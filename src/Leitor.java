
import java.io.File;
import java.util.ArrayList;

public class Leitor {
	private ManipulaArquivos m = new ManipulaArquivos();
	
    public String getText(String path) {
        return m.lerTexto(path);
    }
    
    public ArrayList<String> lerSentencas(String path){
    	return m.lerSentencas(path);
    }
    
}
