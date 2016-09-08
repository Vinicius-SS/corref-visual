
import java.io.File;
import java.util.ArrayList;

public class Leitor {
	private ManipulaArquivos m = new ManipulaArquivos();
	
    public ArrayList<model.Sintagma> getSintagmas(String path) {
        ArrayList<model.Sintagma> result = new ArrayList<>();

        ArrayList<Sintagma> lista = m.lerSintagmas(path);

        for (Sintagma s : lista) {
            ArrayList<model.Word> words = new ArrayList<>();
            for (Word w : s.words) 
                words.add(new model.Word(w.word, w.pos, w.morfo, w.lemma, w.sentenca));
            model.Sintagma sintagma = new model.Sintagma(s.TextName, s.sn, s.sentenca, words, s.set, s.snID, s.nucleo, s.lemma, s.prop, s.genero, s.numero, s.NucleoPronome, s.groupedBy, s.shallow, s.Pai_De, s.Filho_De,s.CategoriaSemantica);
            result.add(sintagma);
        }
        return result;
    }
    
    public void saveSintagmas(ArrayList<model.Sintagma> lista, File file) {
        ArrayList<Sintagma> saveList = new ArrayList<Sintagma>();
        
        for (model.Sintagma s : lista) {
            ArrayList<model.Word> words = new ArrayList<>();
            for (model.Word w : s.words) {
                model.Word word = new model.Word(w.word, w.pos, w.morfo, w.lemma, w.sentenca);
                words.add(word);
            }
            Sintagma sintagma = new Sintagma(s.sn, s.sentenca, s.snID, s.set);
            sintagma.TextName = s.textName;
            sintagma.nucleo = s.nucleo;
            sintagma.lemma = s.lemma;
            sintagma.prop = s.prop;
            sintagma.genero = s.genero;
            sintagma.numero = s.nucleo;
            sintagma.NucleoPronome = s.nucleoPronome;
            sintagma.groupedBy = s.groupedBy;
            sintagma.shallow = s.shallow;
            sintagma.Pai_De = s.paiDe;
            sintagma.Filho_De = s.filhoDe;
            
            saveList.add(sintagma);
        }
        
        m.salvarSintagmas(saveList, file);
    }

    public String getText(String path) {
        return m.lerTexto(path);
    }
    
    public ArrayList<String> lerSentencas(String path){
    	return m.lerSentencas(path);
    }
    
}
