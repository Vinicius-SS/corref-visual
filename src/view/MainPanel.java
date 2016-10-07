package view;

import controller.Fachada;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.*;
import javax.swing.JList;
import javax.swing.JPanel;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.text.*;
import model.Grupo;
import model.Sintagma;
import model.Token;
import model.Word;
import org.jdom2.Attribute;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public final class MainPanel extends JPanel
{

    public static int pos = 0;
    public Random cores = new Random();
    private Fachada fachada = Fachada.getInstance();
    private ArrayList<Sintagma> listaSintagma, listaOriginal;
    private ArrayList<Color> colors;
    private ArrayList<JList> jlistas;
    private String barra, texto;
    private Container cont;
    private JSplitPane splitGroupPane, splitPane, upSplitPane, splitAllPane;
    private JPanel rightGroupPanel, leftPanel, upPanel;
    private JScrollPane leftGroupPanel;
    private JButton botao;
    private JTextPane textPane;
    private JMenuBar jMenuBarMain;
    private JMenu jMenuArquivo, jMenuAjuda;
    private JMenuItem jMenuImportar, jMenuExportar;
    private TransferHandler h;
    private Map<javax.swing.JList, javax.swing.JComboBox> listsToBoxes;
    private Map<javax.swing.JComboBox, Color> boxesToColors;
    public static int maiorSet;
    public static MainPanel m;
    private List<Token> listTokens;
    private String tituloTexto;
    private List<org.jdom2.Element> cadeias, sentencas, mencoes_unicas, tokens;

    private MainPanel() throws ClassNotFoundException, NoSuchMethodException
    {
        super(new BorderLayout());
        jMenuArquivo = new javax.swing.JMenu();
        jMenuAjuda = new javax.swing.JMenu();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuImportar = new javax.swing.JMenuItem();
        jMenuExportar = new javax.swing.JMenuItem();
        jMenuArquivo.setText("Arquivo");
        jMenuAjuda.setText("Ajuda");
        jMenuImportar.setText("Importar texto");
        jMenuExportar.setText("Salvar alterações");
        jMenuArquivo.add(jMenuImportar);
        jMenuArquivo.add(jMenuExportar);
        jMenuBarMain.add(jMenuArquivo);
        jMenuBarMain.add(jMenuAjuda);
        textPane = new JTextPane();
        textPane.setEditable(false);
        barra = "/";
        jlistas = new ArrayList<>();
        colors = new ArrayList<>();
        gerarCores(10);
        upPanel = createHorizontalBoxPanel(150, 100);
        upPanel.add(jMenuBarMain);
        leftPanel = createVerticalBoxPanel(getPreferredSize());
        leftPanel.add(createPanelForComponent(new JScrollPane(textPane), ""));
        leftGroupPanel = createVerticalScrollBoxPanel(this.getPreferredSize());
        rightGroupPanel = createVerticalBoxPanel(this.getPreferredSize());
        listsToBoxes = new HashMap<>();
        boxesToColors = new HashMap<>();

        JPanel btPanel = createHorizontalBoxPanel(100, 100);
        botao = new JButton("Novo Grupo");
        btPanel.add(botao);
        botao.setEnabled(false);

        botao.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addBox();
            }

            private void addBox()
            {
                //TODO arrumar a categoria semântica aqui depois
                ArrayList<Sintagma> lista = new ArrayList<>();
                JList jListSintagma = makeList(h, lista);
                jlistas.add(jListSintagma);
                JScrollPane jsp = new JScrollPane(jListSintagma);
                JComboBox<String> categorias = new JComboBox();
                for (model.CategoriasSemanticas categs
                        : model.CategoriasSemanticas.values())
                    categorias.addItem(categs.getCateg());
                ((BasicComboPopup) categorias.getAccessibleContext().
                        getAccessibleChild(0)).
                        getList().setSelectionBackground(new Color(cores.
                                nextInt(256), cores.nextInt(256),
                                cores.nextInt(256)));
                categorias.setRenderer(new DefaultListCellRenderer()
                {
                    @Override
                    public void paint(Graphics grafix)
                    {
                        super.paint(grafix);
                    }
                });
                //TODO acho que dá para tirar um hashmap daqui
                listsToBoxes.put(jListSintagma, categorias);
                boxesToColors.put(categorias, ((BasicComboPopup) categorias
                        .getAccessibleContext().getAccessibleChild(0))
                        .getList().getSelectionBackground());
                categorias.setSelectedItem("--");

                categorias.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String newCateg = (String) categorias.getSelectedItem();
                        DefaultListModel<Sintagma> listaAlterada = (DefaultListModel) jListSintagma.getModel();
                        for (int i = 0; i < listaAlterada.getSize(); i++)
                            ((Sintagma) listaAlterada.getElementAt(i)).categoriaSemantica = newCateg;
                    }
                });

                jsp.setColumnHeaderView(categorias);
                cont.add(createPanelForComponent(jsp, ""), 0);
                jListSintagma.setSelectionModel(new DefaultListSelectionModel()
                {
                    @Override
                    public void setSelectionInterval(int start, int end)
                    {
                        if (start != end)
                            super.setSelectionInterval(start, end);
                        else if (isSelectedIndex(start))
                        {
                            removeSelectionInterval(start, end);
                            highlightSelecionados();
                        } else
                        {
                            addSelectionInterval(start, end);
                            highlightSelecionados();
                        }
                    }
                });
                splitAllPane.revalidate();
                splitAllPane.repaint();
            }
        });

        h = new ListItemTransferHandler();
        cont = new Container();
        cont.setLayout(new BoxLayout(cont, BoxLayout.PAGE_AXIS));

        leftGroupPanel.setViewportView(cont);

        this.add(leftPanel);
        this.add(leftGroupPanel);
        this.add(rightGroupPanel);

        splitGroupPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftGroupPanel, rightGroupPanel);
        splitGroupPane.setResizeWeight(0.5);
        this.add(splitGroupPane);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
                splitGroupPane);

        upSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upPanel,
                btPanel);
        upSplitPane.setResizeWeight(0.527);

        splitAllPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upSplitPane,
                splitPane);

        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.55);
        splitAllPane.setResizeWeight(0.01);
        this.add(splitAllPane, BorderLayout.CENTER);

        jMenuImportar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                {//faxina tudo antes de importar o próximo texto
                    pos = 0;
                    cont.removeAll();
                    rightGroupPanel.removeAll();
                    fachada.getGrupos().clear();
                    fachada.getGrupoSolitario().getListaSintagmas().clear();
                    listsToBoxes.clear();
                    boxesToColors.clear();
                    maiorSet = -1;
                    listTokens = new ArrayList<>();
                }
                try
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(
                            new File("XML Novos aspas fixed"));
                    chooser.showOpenDialog(null);
                    File xml = chooser.getSelectedFile();
                    botao.setEnabled(true);
                    Document document = new SAXBuilder().build(xml);
                    String texto = document.getRootElement().getChildren().
                            get(0).getAttributeValue("conteudo");
                    tituloTexto = document.getRootElement().getName();
                    listaSintagma = new ArrayList<>();
                    cadeias = document.getRootElement().getChildren().get(3).
                            getChildren();
                    sentencas = document.getRootElement().getChildren().get(1).
                            getChildren();
                    mencoes_unicas = document.getRootElement().getChildren().
                            get(4).getChildren();
                    tokens = document.getRootElement().getChildren().get(2).
                            getChildren();
                    for (org.jdom2.Element mencao_unica : mencoes_unicas)
                    {
                        //para cada sintagma cria as words
                        boolean prop = false;
                        boolean featured = false;
                        String genero = "";
                        String numero = "";
                        ArrayList<model.Word> words = new ArrayList<>();
                        for (org.jdom2.Element word : mencao_unica.getChildren())
                        {
                            model.Word w = new model.Word(word.
                                    getAttributeValue("token"),
                                    word.getAttributeValue("pos"), word.
                                    getAttributeValue("features"),
                                    word.getAttributeValue("lemma"), Integer.
                                    parseInt(mencao_unica.getAttributeValue(
                                            "sentenca")),
                                    Integer.parseInt(
                                            word.getName().split("_")[1]));
                            words.add(w);
                            if (w.pos.equals("prop"))
                                prop = true;
                            if (!featured && w.morfo.contains("="))
                            {
                                String[] morfos = w.morfo.split("=");
                                genero = morfos[0];
                                numero = morfos[1];
                                featured = true;
                            }
                        }
                        //cria o sintagma
                        Sintagma s = new Sintagma(document.getRootElement().
                                getName(), mencao_unica.getAttributeValue(
                                        "sintagma"),
                                Integer.parseInt(mencao_unica.getAttributeValue(
                                        "sentenca")), words,
                                Integer.parseInt(mencao_unica.getAttributeValue(
                                        "id")), Integer.parseInt(mencao_unica.
                                        getAttributeValue("id")),
                                mencao_unica.getAttributeValue("nucleo"),
                                mencao_unica.getAttributeValue("lemma"), prop,
                                genero, numero, false, "", false,
                                new ArrayList<>(), new Integer(0), mencao_unica.
                                getAttributeValue("Categoria"));
                        listaSintagma.add(s);
                    }
                    for (org.jdom2.Element cadeia : cadeias)
                        //para cada cadeia
                        for (org.jdom2.Element sintagma : cadeia.getChildren())
                        {
                            //para cada sintagma
                            //cria as words
                            boolean prop = false;
                            boolean featured = false;
                            String genero = "";
                            String numero = "";
                            ArrayList<model.Word> words = new ArrayList<>();
                            for (org.jdom2.Element word : sintagma.getChildren())
                            {
                                model.Word w = new model.Word(word.
                                        getAttributeValue("token"),
                                        word.getAttributeValue("pos"), word.
                                        getAttributeValue("features"),
                                        word.getAttributeValue("lemma"),
                                        Integer.parseInt(sintagma.
                                                getAttributeValue("sentenca")),
                                        Integer.parseInt(word.getName().split(
                                                "_")[1]));
                                words.add(w);
                                if (w.pos.equals("prop"))
                                    prop = true;
                                if (!featured && w.morfo.contains("="))
                                {
                                    String[] morfos = w.morfo.split("=");
                                    genero = morfos[0];
                                    numero = morfos[1];
                                    featured = true;
                                }
                            }
                            //cria o sintagma
                            Sintagma s = new Sintagma(document.getRootElement().
                                    getName(), sintagma.getAttributeValue(
                                            "sintagma"),
                                    Integer.parseInt(sintagma.getAttributeValue(
                                            "sentenca")), words,
                                    Integer.parseInt(
                                            cadeia.getName().split("_")[1]),
                                    Integer.parseInt(sintagma.getAttributeValue(
                                            "id")),
                                    sintagma.getAttributeValue("nucleo"),
                                    sintagma.getAttributeValue("lemma"), prop,
                                    genero, numero, false, "", false,
                                    new ArrayList<>(), new Integer(0), sintagma.
                                    getAttributeValue("Categoria"));
                            listaSintagma.add(s);
                        }
                    for (Sintagma s : listaSintagma)
                    {
                        if (s.set > maiorSet)
                            maiorSet = s.set;
                        //s.sn = fachada.trataString(s.sn);
                        fachada.addSintagmaNoGrupo(s);
                    }
                    setTexto(texto);
                    fachada.organizaGrupos();
                    fachada.ordenaPorQtdFilhos();
                    for (int i = 0; i < listaSintagma.size(); i++)
                    {
                        Sintagma s = listaSintagma.get(i);
                        s.startToken = s.words.get(0).tokenID;
                        s.endToken = s.words.get(s.words.size() - 1).tokenID;
                    }
                    for (org.jdom2.Element token : tokens)
                    {
                        Token tt = new Token(token.getAttributeValue("token"),
                                pos);
                        tt.endChar = tt.startChar + tt.token.length() + 1;
                        pos += tt.token.length() + 1;
                        listTokens.add(tt);
                    }
                    //guarda os originais antes de qualquer alteração
                    listaOriginal = new ArrayList<>();
                    for (Sintagma s : listaSintagma)
                        listaOriginal.add(s);
                    cont.removeAll();
                    rightGroupPanel.removeAll();
                    JList jListSnSolitarios = makeList(h, fachada.
                            getGrupoSolitario().getListaSintagmas());
                    rightGroupPanel.add(createPanelForComponent(new JScrollPane(
                            jListSnSolitarios), ""));
                    jListSnSolitarios.setSelectionModel(
                            new DefaultListSelectionModel()
                    {
                        @Override
                        public void setSelectionInterval(int start, int end)
                        {
                            if (start != end)
                                super.setSelectionInterval(start, end);
                            else if (isSelectedIndex(start))
                            {
                                removeSelectionInterval(start, end);
                                highlightSelecionados();
                            } else
                            {
                                addSelectionInterval(start, end);
                                highlightSelecionados();
                            }
                        }
                    });
                    for (Grupo g : fachada.getGrupos())
                    {
                        JList jListSintagma = makeList(h, g.getListaSintagmas());
                        jlistas.add(jListSintagma);
                        JScrollPane jsp = new JScrollPane(jListSintagma);
                        cont.add(createPanelForComponent(jsp, ""));
                        JComboBox<String> categorias = new JComboBox();
                        for (model.CategoriasSemanticas categs
                                : model.CategoriasSemanticas.values())
                            categorias.addItem(categs.getCateg());
                        ((BasicComboPopup) categorias.getAccessibleContext().
                                getAccessibleChild(0)).
                                getList().setSelectionBackground(g.getColor());
                        categorias.setRenderer(new DefaultListCellRenderer()
                        {
                            @Override
                            public void paint(Graphics grafix)
                            {
                                super.paint(grafix);
                            }
                        });
                        categorias.setSelectedItem(g.getListaSintagmas().get(0).categoriaSemantica);

                        categorias.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent e)
                            {
                                String newCateg = (String) categorias.getSelectedItem();
                                DefaultListModel<Sintagma> listaAlterada = (DefaultListModel) jListSintagma.getModel();
                                for (int i = 0; i < listaAlterada.getSize(); i++)
                                    ((Sintagma) listaAlterada.getElementAt(i)).categoriaSemantica = newCateg;
                            }
                        });

                        jsp.setColumnHeaderView(categorias);
                        jListSintagma.setSelectionModel(
                                new DefaultListSelectionModel()
                        {
                            @Override
                            public void setSelectionInterval(int start, int end)
                            {
                                if (start != end)
                                    super.setSelectionInterval(start, end);
                                else if (isSelectedIndex(start))
                                {
                                    removeSelectionInterval(start, end);
                                    highlightSelecionados();
                                } else
                                {
                                    addSelectionInterval(start, end);
                                    highlightSelecionados();
                                }
                            }
                        });
                    }
                    jlistas.add(jListSnSolitarios);
                    cont.repaint();
                    for (JList jl : jlistas)
                    {
                        String ACTION_KEY = "theAction";
                        Action actionListener = new AbstractAction()
                        {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent)
                            {
                                for (JList jlista : jlistas)
                                {
                                    jlista.clearSelection();
                                    highlightSelecionados();
                                }
                            }
                        };
                        KeyStroke enter = KeyStroke.getKeyStroke(
                                KeyEvent.VK_ESCAPE, 0, true);
                        InputMap inputMap = jl.getInputMap();
                        inputMap.put(enter, ACTION_KEY);
                        ActionMap actionMap1 = jl.getActionMap();
                        actionMap1.put(ACTION_KEY, actionListener);
                        jl.setActionMap(actionMap1);
                    }
                    Component[] component1 = cont.getComponents();
                    for (int i = 0; i < component1.length; i++)
                        if (component1[i] instanceof JPanel)
                        {
                            JPanel jp = (JPanel) component1[i];
                            jp.setForeground(colors.get(i));
                            jp.setBackground(colors.get(i));
                        }
                    Component[] componentSolitarios = rightGroupPanel.
                            getComponents();
                    for (Component componentSolitario : componentSolitarios)
                        if (componentSolitario instanceof JPanel)
                        {
                            JPanel jp = (JPanel) componentSolitario;
                            jp.setForeground(colors.get(colors.size() - 1));
                            jp.setBackground(colors.get(colors.size() - 1));
                        }
                } catch (HeadlessException | JDOMException | IOException |
                        NumberFormatException e)
                {
                }
                splitAllPane.revalidate();
                splitAllPane.repaint();
            }
        });

        jMenuExportar.addActionListener(new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                org.jdom2.Element root = new org.jdom2.Element(tituloTexto);
                Document saida = new Document(root);
                /*saida.setRootElement(root);
                tudo é content da root
                Texto*/
                org.jdom2.Element elementTexto = new org.jdom2.Element("Texto");
                elementTexto.setAttribute(new Attribute("conteudo", texto));
                saida.getRootElement().addContent(elementTexto);
                /*Sentencas
                não mexi nas sentenças, então só copio
                 */
                org.jdom2.Element elementSentencas = new org.jdom2.Element(
                        "Sentencas");
                for (org.jdom2.Element sentenca : sentencas)
                {
                    org.jdom2.Element sentencaCopy = (org.jdom2.Element) sentenca.
                            clone();
                    elementSentencas.addContent(sentencaCopy.detach());
                }
                saida.getRootElement().addContent(elementSentencas);
                /*Tokens
                mesma lógica das sentenças
                 */
                org.jdom2.Element elementTokens = new org.jdom2.Element("Tokens");
                for (org.jdom2.Element token : tokens)
                {
                    org.jdom2.Element tokenCopy = (org.jdom2.Element) token.
                            clone();
                    elementTokens.addContent(tokenCopy.detach());
                }
                saida.getRootElement().addContent(elementTokens);
                //Cadeias
                org.jdom2.Element elementCadeias = new org.jdom2.Element(
                        "Cadeias");
                saida.getRootElement().addContent(elementCadeias);
                Component[] component = cont.getComponents();
                for (Component component1 : component)
                {//para cada cadeia
                    JList jl = (JList) ((JViewport) ((JScrollPane) ((JPanel) component1).
                            getComponents()[0]).getComponents()[0]).
                            getComponents()[0];
                    if (jl.getModel().getSize() < 1)
                    {
                    } //se não tiver nenhum item, nada a se fazer aqui
                    else
                    {
                        int setNumber = ((Sintagma) jl.getModel().
                                getElementAt(0)).set;
                        org.jdom2.Element cadeia = new org.jdom2.Element(
                                "Cadeia_" + setNumber);
                        elementCadeias.addContent(cadeia);
                        for (int j = 0; j < jl.getModel().getSize(); j++)
                        {
                            //TODO passar isso para dentro de um método no futuro para despoluir e não repetir o código
                            Sintagma sint = (Sintagma) jl.getModel().
                                    getElementAt(j);
                            org.jdom2.Element sintagmaElement = new org.jdom2.Element(
                                    "sn");
                            cadeia.addContent(sintagmaElement);
                            sintagmaElement.setAttribute("id", Integer.
                                    toString(sint.snID));
                            sintagmaElement.setAttribute("tokens",
                                    sint.startToken + "..." + sint.endToken);
                            sintagmaElement.setAttribute("nucleo", sint.nucleo);
                            sintagmaElement.setAttribute("sintagma", sint.sn);
                            sintagmaElement.setAttribute("Categoria",
                                    sint.categoriaSemantica);
                            sintagmaElement.setAttribute("sentenca", Integer.
                                    toString(sint.sentenca));
                            for (Word word : sint.words)
                            {
                                org.jdom2.Element wordElement = new org.jdom2.Element(
                                        "word_" + word.tokenID);
                                sintagmaElement.addContent(wordElement);
                                wordElement.setAttribute("token", word.word);
                                wordElement.setAttribute("lemma", word.lemma);
                                wordElement.setAttribute("pos", word.pos);
                                wordElement.setAttribute("features", word.morfo);
                            }
                        }
                    }
                }
                JList jl = (JList) ((JViewport) ((JScrollPane) ((JPanel) rightGroupPanel.getComponents()[0]).
                        getComponents()[0]).getComponents()[0]).getComponents()[0];
                //Mencoes_Unicas
                org.jdom2.Element solitariosElement = new org.jdom2.Element(
                        "Mencoes_Unicas");
                saida.getRootElement().addContent(solitariosElement);
                for (int j = 0; j < jl.getModel().getSize(); j++)
                {//para cada sintagma
                    Sintagma sint = (Sintagma) jl.getModel().
                            getElementAt(j);
                    org.jdom2.Element sintagmaElement = new org.jdom2.Element(
                            "sn");
                    solitariosElement.addContent(sintagmaElement);
                    sintagmaElement.setAttribute("id", Integer.
                            toString(sint.snID));
                    sintagmaElement.setAttribute("tokens",
                            sint.startToken + "..." + sint.endToken);
                    sintagmaElement.setAttribute("nucleo", sint.nucleo);
                    sintagmaElement.setAttribute("sintagma", sint.sn);
                    sintagmaElement.setAttribute("Categoria",
                            sint.categoriaSemantica);
                    sintagmaElement.setAttribute("sentenca", Integer.
                            toString(sint.sentenca));
                    for (Word word : sint.words)
                    {
                        org.jdom2.Element wordElement = new org.jdom2.Element(
                                "word_" + word.tokenID);
                        sintagmaElement.addContent(wordElement);
                        wordElement.setAttribute("token", word.word);
                        wordElement.setAttribute("lemma", word.lemma);
                        wordElement.setAttribute("pos", word.pos);
                        wordElement.setAttribute("features", word.morfo);
                    }
                }
                Comparator<org.jdom2.Element> ordenador = (org.jdom2.Element cadeia1,
                        org.jdom2.Element cadeia2) -> cadeia1.getName().compareTo(cadeia2.getName());
                Collections.sort(saida.getRootElement().getChildren().get(3).getChildren(), ordenador);
                try
                {
                    XMLOutputter exporter = new XMLOutputter();
                    exporter.setFormat(Format.getPrettyFormat());
                    File dirSaida = new File("saida");
                    if (!dirSaida.exists())
                        dirSaida.mkdir();
                    exporter.output(saida, new FileWriter(dirSaida + "/" + tituloTexto + ".xml"));
                    JOptionPane.showMessageDialog(null, "Alterações salvas com sucesso no diretório de saída");
                } catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(null, "Houve um erro no salvamento das alterações.", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public void highlightSelecionados()
    {
        Component[] component = cont.getComponents();
        List<List<Sintagma>> listaLista = new ArrayList<>();
        for (int i = 0; i < component.length; i++)
        {
            JPanel jp = (JPanel) component[i];
            Component[] component2 = jp.getComponents();
            Component[] component3 = ((JScrollPane) component2[0]).getComponents();
            Component[] component4 = ((JViewport) component3[0]).getComponents();
            JList jl = (JList) component4[0];
            listaLista.add(new ArrayList<>(jl.getSelectedValuesList()));
            jp.setForeground(colors.get(i));
            jp.setBackground(colors.get(i));
        }
        listaLista.add(new ArrayList<>(((JList) ((JViewport) ((JScrollPane) ((JPanel) rightGroupPanel.getComponents()[0]).getComponents()[0]).getComponents()[0]).getComponents()[0]).getSelectedValuesList()));

        setTexto(texto);
        for (List<Sintagma> lst : listaLista)
            for (Sintagma s : lst)
                highlightSintagma(s);
    }

    public void highlightSintagma(Sintagma s)
    {
        Token t = listTokens.get(s.startToken);
        int startChar = t.startChar;
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, s.cor);
        StyleConstants.setBold(keyWord, true);
        StyleConstants.setFontSize(keyWord, 14);
        doc.setCharacterAttributes(startChar, s.sn.length(), keyWord, false);
    }

    public void setTexto(String texto)
    {
        this.texto = texto;
        textPane.setText(texto);
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setBorder(null);
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setFontSize(keyWord, 15);
        StyleConstants.setAlignment(keyWord, StyleConstants.ALIGN_JUSTIFIED);
        textPane.getStyledDocument().setParagraphAttributes(0, texto.length(),
                keyWord, false);
        textPane.setVisible(true);
    }

    protected JScrollPane createVerticalScrollBoxPanel(Dimension d)
    {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setSize(d);
        return scrollPane;
    }

    protected JPanel createVerticalBoxPanel(Dimension d)
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.setSize(d);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return p;
    }

    private JPanel createHorizontalBoxPanel(int w, int h)
    {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setSize(w, h);
        return p;
    }

    public JPanel createPanelForComponent(JComponent comp, String title)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(comp, BorderLayout.CENTER);
        if (title != null)
            panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private static JList<Sintagma> makeList(TransferHandler handler,
            ArrayList<Sintagma> lista)
    {
        DefaultListModel<Sintagma> listModel = new DefaultListModel<>();
        for (Sintagma s : lista)
            listModel.addElement(s);

        JList<Sintagma> list = new JList<>(listModel);
        list.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus)
            {
                Component c = super.getListCellRendererComponent(list,
                        ((Sintagma) value).sn, index, isSelected, cellHasFocus);
                return c;
            }
        });
        list.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setDropMode(DropMode.INSERT);
        list.setDragEnabled(true);
        list.setTransferHandler(handler);

        ActionMap map = list.getActionMap();
        AbstractAction dummy = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            }
        };
        map.put(TransferHandler.getCutAction().getValue(Action.NAME), dummy);
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME), dummy);
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), dummy);

        return list;
    }

    public static void main(String... args)
    {
        EventQueue.invokeLater(()
                -> 
                {
                    try
                    {
                        createAndShowGUI();

                    } catch (ClassNotFoundException | NoSuchMethodException |
                            IllegalAccessException | IllegalArgumentException |
                            InvocationTargetException | InstantiationException ex)
                    {
                        Logger.getLogger(MainPanel.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
        });
    }

    public static void createAndShowGUI() throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            InstantiationException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
        }
        JFrame frame = new JFrame("CorrefVisual");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        m = new MainPanel();
        frame.getContentPane().add(m);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void gerarCores(int n)
    {
        Random r = new Random();
        for (int i = 0; i < n * 10; i++)
            colors.
                    add(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }

    public class ListItemTransferHandler extends TransferHandler
    {

        private final DataFlavor localObjectFlavor;
        private JList source;
        private int[] indices;
        private int addIndex = -1;
        private int addCount;

        public ListItemTransferHandler()
        {
            super();
            localObjectFlavor = new ActivationDataFlavor(Object[].class,
                    DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
        }

        @Override
        protected Transferable createTransferable(JComponent c)
        {
            source = (JList) c;
            indices = source.getSelectedIndices();
            Object[] transferedObjects = source.getSelectedValues();
            return new DataHandler(transferedObjects, localObjectFlavor.
                    getMimeType());
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport info)
        {
            return info.isDrop() && info.
                    isDataFlavorSupported(localObjectFlavor);
        }

        @Override
        public int getSourceActions(JComponent c)
        {
            return MOVE;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport info)
        {
            if (!canImport(info))
                return false;
            TransferHandler.DropLocation tdl = info.getDropLocation();
            if (!(tdl instanceof JList.DropLocation))
                return false;
            JList.DropLocation dl = (JList.DropLocation) tdl;
            JList target = (JList) info.getComponent();
            DefaultListModel listModel = (DefaultListModel) target.getModel();
            int index = dl.getIndex();
            int max = listModel.getSize();
            if (index < 0 || index > max)
                index = max;
            addIndex = index;

            try
            {
                Object[] values = (Object[]) info.getTransferable().
                        getTransferData(localObjectFlavor);
                Color oldColor = null;
                Color newColor = null;
                boolean foundColor = false;
                for (Object value : values)
                {
                    if (((Sintagma) value).cor != null)
                        oldColor = ((Sintagma) value).cor;
                    int idx = index++;
                    listModel.add(idx, value);
                    target.addSelectionInterval(idx, idx);
                    //ajusta o set dos sintagmas arrastados
                    if (listModel.size() <= 1)
                    {
                        ((Sintagma) value).set = MainPanel.maiorSet++;
                        foundColor = true;
                        newColor = boxesToColors.get(listsToBoxes.get(target));
                    } else
                        ((Sintagma) value).set = ((Sintagma) listModel.get(0)).set;
                }

                addCount = target.equals(source) ? values.length : 0;
                Object[] modelToArray = listModel.toArray();
                ArrayList<Sintagma> sints = new ArrayList<>();
                for (Object obj : modelToArray)
                {//descobre a cor dos sintagmas;
                    sints.add((Sintagma) obj);
                    if (!foundColor && ((Sintagma) obj).cor != oldColor)
                    {
                        newColor = ((Sintagma) obj).cor;
                        if (((Sintagma) obj).cor != null)
                            foundColor = true;
                    }
                }
                Comparator<Sintagma> ordenador = (Sintagma s1, Sintagma s2) -> new Integer(
                        s1.snID).compareTo(s2.snID);
                Collections.sort(sints, ordenador);
                listModel.clear();
                /*se newColor tiver chegado até aqui null, tem alguma coisa
                MUITO errada pq daí VÁRIOS sintagmas sem cor foram selecionados
                então é melhor simplesmente dar uma cor nova para tudo de uma vez
                pq deu pau
                 */
                if (newColor == null)
                    newColor
                            = new Color(cores.nextInt(256), cores.nextInt(256),
                                    cores.nextInt(256));

                for (Sintagma sint : sints)
                {//arruma as cores
                    sint.cor = newColor;
                    listModel.addElement(sint);
                }
                
                return true;
            } catch (UnsupportedFlavorException | IOException ex)
            {
            }
            return false;
        }

        @Override
        protected void exportDone(JComponent c, Transferable data, int action)
        {
            cleanup(c, action == MOVE);
        }

        private void cleanup(JComponent c, boolean remove)
        {
            if (remove && indices != null)
            {
                if (addCount > 0)
                    for (int i = 0; i < indices.length; i++)
                        if (indices[i] >= addIndex)
                            indices[i] += addCount;
                JList src = (JList) c;
                DefaultListModel model = (DefaultListModel) src.getModel();
                for (int i = indices.length - 1; i >= 0; i--)
                    model.remove(indices[i]);
            }
            indices = null;
            addCount = 0;
            addIndex = -1;
            MainPanel.m.highlightSelecionados();
        }
    }
}
