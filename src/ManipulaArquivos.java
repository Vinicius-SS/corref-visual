//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//
//public class ManipulaArquivos {
//
//
//
//	public String lerTexto(String path) {
//		String texto = "";
//		try {
//			BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
//
//			String linha = in.readLine();
//			while (linha != null) {
//				texto += linha;
//				linha = in.readLine();
//			}
//			in.close();
//
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//		return texto;
//	}
//
//	public ArrayList<String> lerSentencas(String path) {
//		try {
//			ObjectInputStream lerArquivo = new ObjectInputStream(new FileInputStream(path));
//			ArrayList<String> lista = (ArrayList<String>) lerArquivo.readObject();
//			lerArquivo.close();
//			return lista;
//		} catch (IOException | ClassNotFoundException e) {
//			System.out.println("N�o foi poss�vel ler o arquivo!");
//			return null;
//		}
//	}
//
//}
