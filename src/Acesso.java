
import java.io.File;
import java.util.ArrayList;
import model.Sintagma;

public class Acesso
{

    Leitor leitor = new Leitor();

    

    public String getText(String s)
    {
        return leitor.getText(s);
    }

    public ArrayList<String> lerSentencas(String s)
    {
        return leitor.lerSentencas(s);
    }
}
