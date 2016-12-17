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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

/**
 * Não é exatamente só o MainPanel, mas sim uma amálgama que também conta com
 * 95% da lógica do programa. Em um breve futuro isso vai ser refatorado.
 *
 * @author Vinicius <vinicius.s.sesti@gmail.com>
 */
public final class MainPanel extends JPanel
{
    public static final Logger logger = Logger.getLogger(MainPanel.class.getName());
    public static Handler handlerXML;
    public static Handler handlerTXT;
    public static int pos = 0;
    public Random cores = new Random();
    private Fachada fachada = Fachada.getInstance();
    private ArrayList<Sintagma> listaSintagma, listaOriginal;
    private ArrayList<Color> colors;
    private JList jListSnSolitarios, listaDeApoio;
    private ArrayList<JList> jlistas;
    private String texto;
    private Container cont;
    private JSplitPane splitGroupPane, splitPane, upSplitPane, splitAllPane,
            splitApoioDeSolitarios;
    private JSplitPane leftEditSplit,rightEditSplit,minorEditGrouping,majorEditGrouping;
    private JPanel rightGroupPanel, leftGroupJPanel, panelTextoPuro, upPanel,
            rightGroupJPanel;
    private JScrollPane leftGroupPanel, scrollSolitarios,
            scrollListaDeApoio;
    private JButton botaoNovoGrupo;
    private JButton leftDecrementButton, leftIncrementButton, rightDecrementButton, rightIncrementButton;
    private JLabel editarSintagmaLabel;
    private JTextPane textoPuroPane;
    private JTextField sintagmaSearchField;
    private JMenuBar jMenuBarMain;
    private JMenu jMenuArquivo, jMenuOrdenar, jMenuAjuda;
    private JMenuItem jMenuItemImportar, jMenuItemExportar, jMenuItemAjuda,jMenuItemSobre;
    private JRadioButtonMenuItem jSortSolitariosPorAparicao, jSortSolitariosPorNomeAZ,
            jSortSolitariosPorNomeZA;
    private static KeyListener mainPanelCancelSelection;
    private ButtonGroup ordenacaoSolitarios;
    private TransferHandler h;
    private Map<JList, JComboBox> listsToBoxes;
    private Map<JComboBox, Color> boxesToColors;
    private Map<JList, JPanel> listsToPanels;
    private JComboBox<String> solitariosBox;
    public static int maiorSet;
    public static int howManySelected = 0;
    public static MainPanel m;
    private static boolean importedAnything;
    private static final String [] AJUDA = {//não consegui contornar o problema do joptionpane não dar wrap automático
            "No presente, a ferramenta é mantida por Vinicius Sesti (vinicius.sesti@acad.pucrs.br).\n"
            + "Para qualquer problema ou dúvida, sinta-se à vontade para entrar em contato por\n"
            + "e-mail, enviando os logs de erros (.xml e .txt) gerados pelo programa."
            ,
            "O CorrefVisual é uma ferramenta para a visualização e manipulação gráfica\n"
            +"de cadeias de correferência (anotação), contando com algumas funcionalidades\n"
            + "específicas para auxílio neste trabalho."};
    private List<Token> listTokens;
    private String tituloTexto;
    private String fileName;
    private List<org.jdom2.Element> cadeias, sentencas, mencoes_unicas, tokens;
    private Comparator<Sintagma> ordenador;

    private MainPanel() throws ClassNotFoundException, NoSuchMethodException
    {
        super(new BorderLayout());
        jMenuArquivo = new JMenu();
        jMenuOrdenar = new JMenu();
        jMenuAjuda = new JMenu();
        jMenuBarMain = new JMenuBar();
        jMenuItemImportar = new JMenuItem();
        jMenuItemExportar = new JMenuItem();
        jMenuItemAjuda = new JMenuItem();
        jMenuItemSobre = new JMenuItem();
        jMenuArquivo.setText("Arquivo");
        jMenuOrdenar.setText("Ordenar por...");
        jMenuAjuda.setText("Ajuda");
        jMenuItemImportar.setText("Importar XML");
        jMenuItemExportar.setText("Salvar alterações");
        jMenuItemAjuda.setText("Ajuda");
        jMenuItemSobre.setText("Sobre o CorrefVisual");
        jMenuArquivo.add(jMenuItemImportar);
        jMenuArquivo.add(jMenuItemExportar);
        jMenuAjuda.add(jMenuItemAjuda);
        jMenuAjuda.add(jMenuItemSobre);
        jMenuBarMain.add(jMenuArquivo);
        jMenuBarMain.add(jMenuOrdenar);
        jMenuBarMain.add(jMenuAjuda);
        ordenacaoSolitarios = new ButtonGroup();
        jSortSolitariosPorAparicao = new JRadioButtonMenuItem();
        jSortSolitariosPorAparicao.setText("Aparição no texto");
        jSortSolitariosPorAparicao.setSelected(true);
        jSortSolitariosPorNomeAZ = new JRadioButtonMenuItem();
        jSortSolitariosPorNomeAZ.setText("Ordem alfabética (A->Z)");
        jSortSolitariosPorNomeZA = new JRadioButtonMenuItem();
        jSortSolitariosPorNomeZA.setText("Ordem alfabética (Z->A)");
        ordenacaoSolitarios.add(jSortSolitariosPorAparicao);
        ordenacaoSolitarios.add(jSortSolitariosPorNomeAZ);
        ordenacaoSolitarios.add(jSortSolitariosPorNomeZA);
        jMenuOrdenar.add(jSortSolitariosPorAparicao);
        jMenuOrdenar.add(jSortSolitariosPorNomeAZ);
        jMenuOrdenar.add(jSortSolitariosPorNomeZA);
        editarSintagmaLabel = new JLabel("Editar sintagma");
        textoPuroPane = new JTextPane();
        textoPuroPane.setEditable(false);
        leftDecrementButton = new JButton("-");
        leftIncrementButton = new JButton("+");
        rightDecrementButton = new JButton("-");
        rightIncrementButton = new JButton("+");
        jlistas = new ArrayList<>();
        colors = new ArrayList<>();
        gerarCores(10);
        upPanel = createHorizontalBoxPanel(150, 100);
        upPanel.add(jMenuBarMain);
        panelTextoPuro = createVerticalBoxPanel(getPreferredSize());
        panelTextoPuro.add(createPanelForComponent(new JScrollPane(textoPuroPane), ""));
        leftGroupPanel = createVerticalScrollBoxPanel(this.getPreferredSize());
        rightGroupPanel = createVerticalBoxPanel(this.getPreferredSize());
        importedAnything = false;
        listsToBoxes = new HashMap<>();
        boxesToColors = new HashMap<>();
        listsToPanels = new HashMap<>();
        logger.setLevel(Level.SEVERE);
		try {
                    handlerXML = new FileHandler("LogErros.xml");
                    handlerTXT = new FileHandler("LogErros.txt");
                    SimpleFormatter fmt = new SimpleFormatter();
                    /*
                     * formatação definida em nbproject/project.properties
                     * run.args.extra=-J-java.util.logging.SimpleFormatter.format="%1$tc %2$s%n%4$s: %5$s%6$s%n"
                     */
                    handlerTXT.setFormatter(fmt);
                    logger.addHandler(handlerXML);
                    logger.addHandler(handlerTXT);
		} 
                catch (SecurityException | IOException e1) 
                {
			e1.printStackTrace();
		}
		

        JPanel btPanel = createHorizontalBoxPanel(100, 100);
        botaoNovoGrupo = new JButton("Novo Grupo");
        btPanel.add(botaoNovoGrupo);
        //TODO 28/10/2016
        sintagmaSearchField = new JTextField("Busca de sintagmas");

        mainPanelCancelSelection = new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    for (JList jl : jlistas)
                        jl.clearSelection();
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        };

        FocusListener sintagmaSearchFocus = new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                sintagmaSearchField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                String textoBuscado = sintagmaSearchField.getText();
                if (textoBuscado.isEmpty())
                {
                    sintagmaSearchField.setText("Busca de sintagmas");
                    return;
                }
                    for (JList jl : jlistas)
                {
                    ListModel model = jl.getModel();
                    for (int i = 0; i < model.getSize(); i++)
                    {
                        Sintagma sint = (Sintagma) model.getElementAt(i);
                        if (sint.sn.toLowerCase().contains(textoBuscado.toLowerCase()))
                        {
                            int [] indices = jl.getSelectedIndices();
                            int selectedIndex = Arrays.binarySearch(indices, i);
                            if(selectedIndex<0)
                                {
                                    howManySelected++;
                                    jl.addSelectionInterval(i, i);
                                }
                        }
                    }
                }
                highlightSelecionados();
            }
        };
        sintagmaSearchField.addFocusListener(sintagmaSearchFocus);
        sintagmaSearchField.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                String textoBuscado = sintagmaSearchField.getText();
                if (textoBuscado.isEmpty())
                    return;
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_ENTER)
                {
                    sintagmaSearchField.setFocusable(false);
                    sintagmaSearchField.setFocusable(true);
                }

            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        });

        jMenuItemAjuda.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextArea encapsulation = new JTextArea(MainPanel.AJUDA[0]);
                encapsulation.setSize(encapsulation.getPreferredSize());
                encapsulation.setEditable(false);
                encapsulation.setLineWrap(true);
                JOptionPane.showMessageDialog(null,MainPanel.AJUDA[0], "Ajuda",  JOptionPane.INFORMATION_MESSAGE);
            }   
        }        
        );
        
        jMenuItemSobre.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextArea encapsulation = new JTextArea(MainPanel.AJUDA[1]);
                encapsulation.setSize(encapsulation.getPreferredSize());
                encapsulation.setEditable(false);
                encapsulation.setLineWrap(true);
                encapsulation.setWrapStyleWord(true);
                JOptionPane.showMessageDialog(null,MainPanel.AJUDA[1], "Sobre o CorrefVisual",  JOptionPane.INFORMATION_MESSAGE);
            }   
        }        
        );
        
        botaoNovoGrupo.setEnabled(false);

        solitariosBox = new JComboBox<>();
        solitariosBox.setSelectedItem("OUTRO");

        botaoNovoGrupo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addBox();
            }

            private void addBox()
            {
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
                categorias.setSelectedItem("OUTRO");
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
                listsToPanels.put(jListSintagma, (JPanel) cont.getComponent(0));
                jListSintagma.setSelectionModel(new JListSelection());
                splitAllPane.revalidate();
                splitAllPane.repaint();
            }
        });

        h = new ListItemTransferHandler();
        cont = new Container();
        cont.setLayout(new BoxLayout(cont, BoxLayout.PAGE_AXIS));

        leftGroupPanel.setViewportView(cont);
        leftGroupJPanel = createPanelForComponent(leftGroupPanel, "");
        rightGroupJPanel = new JPanel(new BorderLayout());
        rightGroupJPanel.setBorder(BorderFactory.createTitledBorder(""));
        this.add(panelTextoPuro);
        this.add(leftGroupJPanel);
        this.add(rightGroupJPanel);
        listaDeApoio = makeList(h, new ArrayList<>());
        jlistas.add(listaDeApoio);
        listaDeApoio.setSelectionModel(new JListSelection());
       
        scrollListaDeApoio = new JScrollPane(listaDeApoio);
        JLabel apoioLabel = new JLabel("Painel auxiliar");
        scrollListaDeApoio.setColumnHeaderView(apoioLabel);

        rightGroupJPanel.add(rightGroupPanel);

        splitGroupPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftGroupPanel, rightGroupPanel);
        splitGroupPane.setResizeWeight(0.5);
        this.add(splitGroupPane);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTextoPuro,
                splitGroupPane);

        JPanel editarSintagmaPanel = new JPanel(new BorderLayout());
        editarSintagmaPanel.add(editarSintagmaLabel);
        
        leftEditSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,leftIncrementButton,leftDecrementButton);
        leftEditSplit.setResizeWeight(0.5);
        rightEditSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,rightIncrementButton,rightDecrementButton);
        rightEditSplit.setResizeWeight(0.5);
        minorEditGrouping = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftEditSplit,editarSintagmaPanel);
        leftEditSplit.setDividerLocation(0.8);
        majorEditGrouping = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,minorEditGrouping,rightEditSplit);
        
        upSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upPanel,
                majorEditGrouping);
        upSplitPane.setResizeWeight(0.527);
        
        JSplitPane anotherUpSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,upSplitPane,btPanel);
        anotherUpSplitPane.setResizeWeight(0.5);
        splitApoioDeSolitarios = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollListaDeApoio,
                scrollSolitarios);
        this.add(splitApoioDeSolitarios);

        upSplitPane.setResizeWeight(0.5);

        splitAllPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, anotherUpSplitPane,
                splitPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.55);
        splitAllPane.setResizeWeight(0.01);
        this.add(splitAllPane, BorderLayout.CENTER);
        ordenador = (Sintagma s1, Sintagma s2) -> new Integer(
                s1.snID).compareTo(s2.snID);

        jSortSolitariosPorAparicao.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                ordenador = (Sintagma s1, Sintagma s2) -> new Integer(
                        s1.snID).compareTo(s2.snID);
                ordenaTudo();
            }
        }
        );

        jSortSolitariosPorNomeAZ.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                ordenador = (Sintagma s1, Sintagma s2)
                        -> s1.sn.toLowerCase().compareTo(s2.sn.toLowerCase());
                ordenaTudo();
            }
        }
        );

        jSortSolitariosPorNomeZA.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                ordenador = (Sintagma s1, Sintagma s2)
                        -> s2.sn.toLowerCase().compareTo(s1.sn.toLowerCase());
                ordenaTudo();
            }
        }
        );

        jMenuItemImportar.addActionListener(new ActionListener()
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
                    listsToPanels.clear();
                    maiorSet = -1;
                    listTokens = new ArrayList<>();

                    rightGroupPanel.add(scrollListaDeApoio);
                    listsToBoxes.put(listaDeApoio, solitariosBox);
                }
                try
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(
                            new File("XML_Baseline"));
                    chooser.showOpenDialog(null);
                    botaoNovoGrupo.setEnabled(true);
                    String filePath = chooser.getSelectedFile().getAbsolutePath();
                    fileName = chooser.getSelectedFile().getName();
                    FileInputStream is = new FileInputStream(filePath);
                    InputStreamReader isr = new InputStreamReader(is, StandardCharsets.ISO_8859_1);
                    Document document = new SAXBuilder().build(isr);
                    tituloTexto = document.getRootElement().getName();
                    String texto = document.getRootElement().getChildren().
                            get(0).getAttributeValue("conteudo");
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
                                -1, Integer.parseInt(mencao_unica.
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
                    jListSnSolitarios = makeList(h, fachada.
                            getGrupoSolitario().getListaSintagmas());
                    listsToBoxes.put(jListSnSolitarios, solitariosBox);
                    scrollSolitarios = new JScrollPane(jListSnSolitarios);
                    JLabel solitariosLabel = new JLabel("Menções únicas");
                    scrollSolitarios.setColumnHeaderView(solitariosLabel);
                    rightGroupPanel.add(createPanelForComponent(scrollSolitarios, ""));
                    jListSnSolitarios.setSelectionModel((new JListSelection()));
                    for (Grupo g : fachada.getGrupos())
                    {
                        JList jListSintagma = makeList(h, g.getListaSintagmas());
                        jlistas.add(jListSintagma);
                        JScrollPane jsp = new JScrollPane(jListSintagma);
                        cont.add(createPanelForComponent(jsp, ""));
                        listsToPanels.put(jListSintagma, (JPanel) cont.getComponent(cont.getComponentCount() - 1));
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
                        listsToBoxes.put(jListSintagma, categorias);
                        boxesToColors.put(categorias, ((BasicComboPopup) categorias
                                .getAccessibleContext().getAccessibleChild(0))
                                .getList().getSelectionBackground());
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
                        jListSintagma.setSelectionModel((new JListSelection()));
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
                    splitAllPane.revalidate();
                    splitAllPane.repaint();
                    btPanel.add(createPanelForComponent(sintagmaSearchField, ""));
                    importedAnything = true;
                } catch (HeadlessException | JDOMException | IOException |
                        NumberFormatException e)
                {
                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });

        jMenuItemExportar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                export();
            }
        });
    }

    public boolean export()
    {
        if (listaDeApoio.getModel().getSize() > 0)
        {
            JOptionPane.showMessageDialog(null, "Esvazie o painel auxiliar"
                    + " para salvar as alterações.", "Não foi possível salvar",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
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
                    if (sint.categoriaSemantica == null)
                        sintagmaElement.setAttribute("Categoria", "--");
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
        JList jl = (JList) ((JViewport) ((JScrollPane) ((JPanel) rightGroupPanel.getComponents()[1]).
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
            if (sint.categoriaSemantica == null)
                sintagmaElement.setAttribute("Categoria", "--");
            else
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
        Comparator<org.jdom2.Element> elementOrdenador = (org.jdom2.Element cadeia1,
                org.jdom2.Element cadeia2) -> cadeia1.getName().compareTo(cadeia2.getName());
        Collections.sort(saida.getRootElement().getChildren().get(3).getChildren(), elementOrdenador);
        try
        {
            XMLOutputter exporter = new XMLOutputter(Format.getPrettyFormat().setEncoding("ISO-8859-1"));   
            File dirSaida = new File("saida");
            if (!dirSaida.exists())
                dirSaida.mkdir();
            exporter.output(saida, new FileWriter(dirSaida + File.separator + fileName));
            JOptionPane.showMessageDialog(null, "Alterações salvas com sucesso no diretório de saída");
        } catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, "Houve um erro no salvamento das alterações.", "ERRO", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
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
        listaLista.add(new ArrayList<>(((JList) ((JViewport) ((JScrollPane) ((JPanel) rightGroupPanel.getComponents()[1]).getComponents()[0]).getComponents()[0]).getComponents()[0]).getSelectedValuesList()));
        listaLista.add(new ArrayList<>(((JList) ((JViewport) ((JScrollPane) rightGroupPanel.getComponents()[0]).getComponents()[0]).getComponents()[0]).getSelectedValuesList()));

        setTexto(texto);
        for (List<Sintagma> lst : listaLista)
            for (Sintagma s : lst)
                highlightSintagma(s);
    }

    public void highlightSintagma(Sintagma s)
    {
        Token t = listTokens.get(s.startToken);
        int startChar = t.startChar;
        StyledDocument doc = textoPuroPane.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, s.cor);
        StyleConstants.setBold(keyWord, true);
        StyleConstants.setFontSize(keyWord, 18);
        doc.setCharacterAttributes(startChar, s.sn.length(), keyWord, false);
    }

    public void setTexto(String texto)
    {
        this.texto = texto;
        textoPuroPane.setText(texto);
        textoPuroPane.setEditable(false);
        textoPuroPane.setBackground(Color.WHITE);
        textoPuroPane.setBorder(null);
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setFontSize(keyWord, 15);
        StyleConstants.setAlignment(keyWord, StyleConstants.ALIGN_JUSTIFIED);
        textoPuroPane.getStyledDocument().setParagraphAttributes(0, texto.length(),
                keyWord, false);
        textoPuroPane.setVisible(true);
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
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
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

        EventQueue.invokeLater(() ->
        {
            try
            {
                createAndShowGUI();
                
            } catch (ClassNotFoundException | NoSuchMethodException |
                    IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException | InstantiationException ex)
            {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void createAndShowGUI() throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            InstantiationException
    {
        Locale.setDefault(new Locale("pt", "BR"));
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        JFrame frame = new JFrame("CorrefVisual");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        m = new MainPanel();
        frame.getContentPane().add(m);
        m.addKeyListener(mainPanelCancelSelection);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int option = 1;
                if (MainPanel.importedAnything)
                    option = JOptionPane.showConfirmDialog(null, "Deseja salvar as alterações feitas?");
                switch (option)
                {
                    case JOptionPane.YES_OPTION:
                        if (m.export())
                        {
                        	handlerXML.close();
                                handlerTXT.close();
                            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        }
                        break;
                    case JOptionPane.NO_OPTION:
                    	handlerXML.close();
                        handlerTXT.close();
                        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        break;
                }
            }
        }
        );

    }

    public void ordenaTudo()
    {
        for (JList cadeia : jlistas)
            sortJList(cadeia, ordenador);
        sortJList(jListSnSolitarios, ordenador);
    }

    private void sortJList(JList sortee, Comparator<Sintagma> comparator)
    {
        DefaultListModel model = (DefaultListModel) sortee.getModel();
        Object[] modelToArray = model.toArray();
        model.clear();
        ArrayList<Sintagma> sints = new ArrayList<>();
        for (Object obj : modelToArray)
            sints.add((Sintagma) obj);
        Collections.sort(sints, comparator);
        for (Sintagma s : sints)
            model.addElement(s);
    }

    private void gerarCores(int n)
    {
        Random r = new Random();
        for (int i = 0; i < n * 10; i++)
            colors.
                    add(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }

    public void destroyBox(JPanel destroyable, JList destroyedKey)
    {
        jlistas.remove(destroyedKey);
        cont.remove(destroyable);
        cont.repaint();
        leftGroupPanel.setViewportView(cont);
        leftGroupPanel.repaint();
        boolean pause = true;
    }
    
    public class JListSelection extends DefaultListSelectionModel
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
            } 
            else
            {
                addSelectionInterval(start, end);
                highlightSelecionados();
            }
        }
    }

    public class ListItemTransferHandler extends TransferHandler
    {

        private final DataFlavor localObjectFlavor;
        private JList source;
        private int[] indices;
        private int addIndex = -1;
        private int addCount;
        private boolean removal;

        public ListItemTransferHandler()
        {
            super();
            localObjectFlavor = new ActivationDataFlavor(Object[].class,
                    DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
            removal = true;
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
            TransferHandler.DropLocation tdl = info.getDropLocation();
            if (!(canImport(info) && tdl instanceof JList.DropLocation))
            {
                removal = false;
                return false;
            }
            JList.DropLocation dl = (JList.DropLocation) tdl;
            JList target = (JList) info.getComponent();
            DefaultListModel listModel = (DefaultListModel) target.getModel();
            int index = dl.getIndex();
            int max = listModel.getSize();
            if (index < 0 || index > max)
                index = max;
            addIndex = index;
            try
            {//(O(|S|)
                Object[] values = (Object[]) info.getTransferable().
                        getTransferData(localObjectFlavor);
                //estou tentando arrastar para a mesma cadeia?
                if (listModel.size() > 0)
                    if (((Sintagma) values[0]).set == ((Sintagma) listModel.elementAt(0)).set)
                    {
                        removal = false;
                        return false;
                    }
                removal = true;
                Color oldColor = null;
                Color newColor = null;
                boolean foundColor = false;
                for (Object value : values)
                {//O(|S|)
                    if (((Sintagma) value).cor != null)
                        oldColor = ((Sintagma) value).cor;
                    int idx = index++;
                    listModel.add(idx, value);
                    target.addSelectionInterval(idx, idx);
                    //ajusta o set dos sintagmas arrastados
                    if (listModel.size() <= 1)
                    {
                        ((Sintagma) value).set = ++MainPanel.maiorSet;
                        foundColor = true;
                        newColor = boxesToColors.get(listsToBoxes.get(target));
                    } else
                        ((Sintagma) value).set = ((Sintagma) listModel.get(0)).set;
                }

                addCount = target.equals(source) ? values.length : 0;
                Object[] modelToArray = listModel.toArray();
                ArrayList<Sintagma> sints = new ArrayList<>();
                String novaCategoria = (String) listsToBoxes.get(target).getSelectedItem();
                for (Object obj : modelToArray)
                {//descobre a cor dos sintagmas;
                    //O(|S|)
                    sints.add((Sintagma) obj);
                    if (!foundColor && ((Sintagma) obj).cor != oldColor)
                    {
                        newColor = ((Sintagma) obj).cor;
                        if (((Sintagma) obj).cor != null)
                            foundColor = true;
                    }
                }
                //TODO exterminar esse sort sujo aqui no meio depois que eu garantir
                //que o meu novo sort com os botões tá funcionando
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
                //O(|S|)
                {//arruma as cores e a categoria semântica
                    sint.categoriaSemantica = novaCategoria;
                    sint.cor = newColor;
                    listModel.addElement(sint);
                }
                cleanup(source, this.removal);
                MainPanel.m.highlightSelecionados();
                return true;
            } catch (UnsupportedFlavorException | IOException ex)
            {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            removal = false;
            return false;
        }

        @Override
        protected void exportDone(JComponent c, Transferable data, int action)
        {
            //cleanup(c, this.removal);
        }

        //TODO destruir a caixa se estiver vazia
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
                if (model.isEmpty())
                    m.destroyBox(listsToPanels.get(src), src);
            }
            indices = null;
            addCount = 0;
            addIndex = -1;
            MainPanel.m.highlightSelecionados();
        }
    }

}
