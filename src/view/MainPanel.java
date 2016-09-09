package view;

import controller.Fachada;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.JDOMException;

public class MainPanel extends JPanel
{

    public Random cores = new Random();
    private Fachada fachada = Fachada.getInstance();
    private ArrayList<Sintagma> listaSintagma, listaOriginal;
    private ArrayList<String> sentencas;
    private ArrayList<Color> colors;
    private ArrayList<JList> jlistas;
    private String barra, texto;
    private Container cont;
    private JSplitPane splitGroupPane, splitPane, upSplitPane, splitAllPane;
    private JPanel rightGroupPanel, leftPanel, upPanel;
    private JScrollPane leftGroupPanel;
    private JButton botao;
    private JTextPane tP;
    private JMenuBar jMenuBarMain;
    private JMenu jMenuArquivo, jMenuAjuda;
    private JMenuItem jMenuImportar, jMenuExportar;
    private TransferHandler h;
    public static int maiorSet;
    private static String path;
    public static MainPanel m;
    private Class leitor;
    private Comparator<Sintagma> ordenador;
    private JList jListSnSolitarios;

    public static void setPath(String path)
    {
        MainPanel.path = path;
    }

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
        jMenuExportar.setText("Exportar alterações");
        jMenuArquivo.add(jMenuImportar);
        jMenuArquivo.add(jMenuExportar);
        jMenuBarMain.add(jMenuArquivo);
        jMenuBarMain.add(jMenuAjuda);
        tP = new JTextPane();
        tP.setEditable(false);

        barra = "/";
        jlistas = new ArrayList<>();
        colors = new ArrayList<>();
        gerarCores(10);

        leitor = Class.forName("Acesso");

        upPanel = createHorizontalBoxPanel(150, 100);
        upPanel.add(jMenuBarMain);
        leftPanel = createVerticalBoxPanel(this.getPreferredSize());
        leftPanel.add(createPanelForComponent(new JScrollPane(tP), ""));
        leftGroupPanel = createVerticalScrollBoxPanel(this.getPreferredSize());
        rightGroupPanel = createVerticalBoxPanel(this.getPreferredSize());

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

                Component component = cont.getComponent(0);
                JPanel jp = (JPanel) component;
                Component component2 = jp.getComponent(0);
                Component component3 = ((JScrollPane) component2).getComponent(0);
                Component component4 = ((JViewport) component3).getComponent(0);
                JList jl = (JList) component4;
                ArrayList<Sintagma> lista = new ArrayList<>();
                JList jListSintagma = makeList(h, lista);
                jlistas.add(jListSintagma);
                JScrollPane jsp = new JScrollPane(jListSintagma);
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

        splitGroupPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftGroupPanel, rightGroupPanel);
        splitGroupPane.setResizeWeight(0.5);
        this.add(splitGroupPane);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, splitGroupPane);

        upSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upPanel, btPanel);
        upSplitPane.setResizeWeight(0.527);

        splitAllPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upSplitPane, splitPane);

        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.55);
        splitAllPane.setResizeWeight(0.01);
        this.add(splitAllPane, BorderLayout.CENTER);

        jMenuImportar.addActionListener(new java.awt.event.ActionListener()
        {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {

                {//faxina tudo antes de importar o próximo texto
                    cont.removeAll();
                    //listaOriginal.clear(); //acredito que tenha que limpar isso aqui tb
                    rightGroupPanel.removeAll();
                    fachada.getGrupos().clear();
                    fachada.getGrupoSolitario().getListaSintagmas().clear();
                    maiorSet = -1;
                }
                try
                {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("XMLs"));
                    chooser.showOpenDialog(null);
                    File xml = chooser.getSelectedFile();
                    botao.setEnabled(true);
                    Document document = new SAXBuilder().build(xml);
                    String texto = document.getRootElement().getChildren().get(0).getAttributeValue("conteudo");
                    ArrayList<Sintagma> listaSintagma = new ArrayList<>();
                    List<org.jdom2.Element> cadeias = document.getRootElement().getChildren().get(2).getChildren();                  
                    List<org.jdom2.Element> mencoes_unicas = document.getRootElement().getChildren().get(3).getChildren();
                    
                    for(org.jdom2.Element mencao_unica : mencoes_unicas)
                    {
                        //para cada sintagma
                            //cria as words
                            boolean prop = false;
                            boolean featured = false;
                            String genero = "";
                            String numero = "";
                            ArrayList<model.Word> words = new ArrayList<>();
                            for (org.jdom2.Element word : mencao_unica.getChildren())
                            {
                                model.Word w = new model.Word(word.getAttributeValue("token"),
                                        word.getAttributeValue("pos"), word.getAttributeValue("features"),
                                        word.getAttributeValue("lemma"), Integer.parseInt(mencao_unica.getAttributeValue("sentenca")));
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
                            Sintagma s = new Sintagma(document.getRootElement().getName(), mencao_unica.getAttributeValue("sintagma"),
                                    Integer.parseInt(mencao_unica.getAttributeValue("sentenca")), words,
                                    Integer.parseInt(mencao_unica.getAttributeValue("id")), Integer.parseInt(mencao_unica.getAttributeValue("id")),
                                    mencao_unica.getAttributeValue("nucleo"), mencao_unica.getAttributeValue("lemma"), prop,
                                    genero, numero, false, "", false, new ArrayList<>(), new Integer(0), "");
                            listaSintagma.add(s);
                            System.out.println();
                    }
                                           
                    for (org.jdom2.Element cadeia : cadeias)//para cada cadeia
                        for (org.jdom2.Element sintagma : cadeia.getChildren())
                        {//para cada sintagma
                            //cria as words
                            boolean prop = false;
                            boolean featured = false;
                            String genero = "";
                            String numero = "";
                            ArrayList<model.Word> words = new ArrayList<>();
                            for (org.jdom2.Element word : sintagma.getChildren())
                            {
                                model.Word w = new model.Word(word.getAttributeValue("token"),
                                        word.getAttributeValue("pos"), word.getAttributeValue("features"),
                                        word.getAttributeValue("lemma"), Integer.parseInt(sintagma.getAttributeValue("sentenca")));
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
                            Sintagma s = new Sintagma(document.getRootElement().getName(), sintagma.getAttributeValue("sintagma"),
                                    Integer.parseInt(sintagma.getAttributeValue("sentenca")), words,
                                    Integer.parseInt(cadeia.getName().split("_")[1]), Integer.parseInt(sintagma.getAttributeValue("id")),
                                    sintagma.getAttributeValue("nucleo"), sintagma.getAttributeValue("lemma"), prop,
                                    genero, numero, false, "", false, new ArrayList<>(), new Integer(0), "");
                            listaSintagma.add(s);
                            System.out.println();
                        }
                    setTexto(texto);
                    for (Sintagma s : listaSintagma)
                    {
                        //System.out.println(s.sn+" : ");
                        if (s.set > maiorSet)
                            maiorSet = s.set;
                        s.sn = fachada.trataString(s.sn);
                        if (s.sn.startsWith(" "))
                            s.sn = s.sn.substring(1);
                        if (s.sn.endsWith(" "))
                            s.sn = s.sn.substring(0, s.sn.length() - 1);
                        fachada.addSintagmaNoGrupo(s);
                    }
                    fachada.organizaGrupos();
                    fachada.ordenaPorQtdFilhos();

                    //guarda os originais antes de qualquer alteração
                    listaOriginal = new ArrayList<>();
                    for (Sintagma s : listaSintagma)
                        listaOriginal.add(s);

                    jListSnSolitarios = makeList(h, fachada.getGrupoSolitario().getListaSintagmas());
                    rightGroupPanel.add(createPanelForComponent(new JScrollPane(jListSnSolitarios), ""));
                  jListSnSolitarios.setSelectionModel(new DefaultListSelectionModel()
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
                        JComboBox<String> categorias = new JComboBox();
                        for(model.CategoriasSemanticas categs : model.CategoriasSemanticas.values())
                            categorias.addItem(categs.getCateg());
                        
//                        categorias.addItem("<html><body style=\"background-color:rgb("
//                                + g.getColor().getRed() + ","
//                                + g.getColor().getGreen() + ","
//                                + g.getColor().getBlue() + ");\">PER</body></html>");
//                        categorias.addItem("OTH");
                        ((BasicComboPopup) categorias.getAccessibleContext().getAccessibleChild(0)).
                                getList().setSelectionBackground(g.getColor());
                        categorias.setRenderer(new DefaultListCellRenderer()
                        {
                            @Override
                            public void paint(Graphics grafix)
                            {
                                //setBackground(g.getColor());
                                //setForeground(g.getColor());
                                super.paint(grafix);
                            }
                        });
                        jsp.setColumnHeaderView(categorias);
                        cont.add(createPanelForComponent(jsp, ""));

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

                    }

                    jlistas.add(jListSnSolitarios);

                    //cont.repaint();
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
                        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
                        InputMap inputMap = jl.getInputMap();
                        inputMap.put(enter, ACTION_KEY);
                        ActionMap actionMap = jl.getActionMap();
                        actionMap.put(ACTION_KEY, actionListener);
                        jl.setActionMap(actionMap);
                    }

                    Component[] component = cont.getComponents();

                    for (int i = 0; i < component.length; i++)
                        if (component[i] instanceof JPanel)
                        {
                            JPanel jp = (JPanel) component[i];
                            jp.setForeground(colors.get(i));
                            jp.setBackground(colors.get(i));
                        }

                    Component[] componentSolitarios = rightGroupPanel.getComponents();

                    for (Component componentSolitario : componentSolitarios)
                        if (componentSolitario instanceof JPanel)
                        {
                            JPanel jp = (JPanel) componentSolitario;
                            jp.setForeground(colors.get(colors.size() - 1));
                            jp.setBackground(colors.get(colors.size() - 1));
                        }
//
//                } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex)
//                {
//                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                splitAllPane.revalidate();
//                splitAllPane.repaint();
                } catch (JDOMException ex)
                {
                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex)
                {
                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        jMenuExportar.addActionListener(new java.awt.event.ActionListener()
        {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jMenuExportarActionPerformed(evt);
            }

            private void jMenuExportarActionPerformed(ActionEvent evt)
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(null);
                chooser.showSaveDialog(null);
                File f = chooser.getSelectedFile();

                listaSintagma.clear();
                HashMap<Integer, ArrayList<Sintagma>> lista = new HashMap<>();
                Component[] component = cont.getComponents();

                for (Component component1 : component)
                {
                    JPanel jp = (JPanel) component1;
                    Component[] component2 = jp.getComponents();
                    Component[] component3 = ((JScrollPane) component2[0]).getComponents();
                    Component[] component4 = ((JViewport) component3[0]).getComponents();
                    JList jl = (JList) component4[0];
                    for (int j = 0; j < jl.getModel().getSize(); j++)
                        listaSintagma.add((Sintagma) jl.getModel().getElementAt(j));
                }

                Component[] componentSolitarios = rightGroupPanel.getComponents();
                Component[] component2 = ((JPanel) componentSolitarios[0]).getComponents();
                Component[] component3 = ((JScrollPane) component2[0]).getComponents();
                Component[] component4 = ((JViewport) component3[0]).getComponents();
                JList jl = (JList) component4[0];
                for (int j = 0; j < jl.getModel().getSize(); j++)
                    listaSintagma.add((Sintagma) jl.getModel().getElementAt(j));

                Class leitor;
                try
                {
                    leitor = Class.forName("Acesso");
                    Method salvarSintagmas = leitor.getMethod("salvarSintagmas", new Class[]
                    {
                        ArrayList.class, File.class
                    });
                    File file;
                    if (f.getName().contains(".dat"))
                        file = new File(f.getPath());
                    else
                        file = new File(f.getPath() + ".dat");

                    salvarSintagmas.invoke(leitor.newInstance(), listaSintagma, file);
                    try (FileWriter arq = new FileWriter(path + barra + "logs" + barra + f.getName().substring(0, f.getName().indexOf(".")) + "-original.txt"))
                    {
                        PrintWriter gravarArq = new PrintWriter(arq);
                        String log = fachada.imprimeCorref(listaOriginal);
                        gravarArq.printf(log);
                    }
                    try (FileWriter arq = new FileWriter(path + barra + "logs" + barra + f.getName().substring(0, f.getName().indexOf(".")) + "-modificado.txt"))
                    {
                        PrintWriter gravarArq = new PrintWriter(arq);
                        String log = fachada.imprimeCorref(listaSintagma);
                        gravarArq.printf(log);
                    }
                    try (FileWriter arq = new FileWriter(path + barra + "logs" + barra + f.getName().substring(0, f.getName().indexOf(".")) + ".csv"))
                    {
                        PrintWriter gravarArq = new PrintWriter(arq);
                        String log = fachada.getLog(listaOriginal, listaSintagma);
                        gravarArq.printf(log);
                    }

                } catch (InstantiationException | IOException | InvocationTargetException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException ex)
                {
                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void highlightSelecionados()
    {
        HashMap<ArrayList<Sintagma>, Color> lista = new HashMap<>();
        Component[] component = cont.getComponents();

        for (int i = 0; i < component.length; i++)
        {
            JPanel jp = (JPanel) component[i];
            Component[] component2 = jp.getComponents();
            Component[] component3 = ((JScrollPane) component2[0]).getComponents();
            Component[] component4 = ((JViewport) component3[0]).getComponents();
            JList jl = (JList) component4[0];
            lista.put(new ArrayList<>(jl.getSelectedValuesList()), colors.get(i));
            jp.setForeground(colors.get(i));
            jp.setBackground(colors.get(i));
        }

        Component[] componentSolitarios = rightGroupPanel.getComponents();
        Component[] component2 = ((JPanel) componentSolitarios[0]).getComponents();
        Component[] component3 = ((JScrollPane) component2[0]).getComponents();
        Component[] component4 = ((JViewport) component3[0]).getComponents();
        JList jl = (JList) component4[0];
        lista.put(new ArrayList<>(jl.getSelectedValuesList()), colors.get(colors.size() - 1));

        int i = 0;
        setTexto(texto);
        for (ArrayList<Sintagma> lst : lista.keySet())
        {
            Color cor = lista.get(lst);
            for (Sintagma s : lst)
                highlightSintagma(s, cor);
        }
    }

    public void setTexto(String texto)
    {
        this.texto = texto;

        tP.setText(texto);
        tP.setEditable(false);
        tP.setBackground(Color.WHITE);
        tP.setBorder(null);

        StyledDocument doc = tP.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setFontSize(keyWord, 15);
//        StyleConstants.setLineSpacing(keyWord, 1f);
        doc.setCharacterAttributes(0, texto.length(), keyWord, false);

        tP.setVisible(true);
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

    private static JList<Sintagma> makeList(TransferHandler handler, ArrayList<Sintagma> lista)
    {
        DefaultListModel<Sintagma> listModel = new DefaultListModel<>();
        for (Sintagma s : lista)
            listModel.addElement(s);

        JList<Sintagma> list = new JList<>(listModel);
        list.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                Component c = super.getListCellRendererComponent(list, ((Sintagma) value).sn, index, isSelected, cellHasFocus);
                return c;
            }
        });
        list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex)
                    {
                        Logger.getLogger(MainPanel.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
        });
    }

    public static void createAndShowGUI() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
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

    public void highlightSintagma(Sintagma s, Color c)
    {
        String sentenca = sentencas.get(s.sentenca);
        if (sentenca.length() > 5)
            sentenca = sentenca.substring(1, sentenca.length() - 2);

        int firstIndex = texto.indexOf(sentenca) - 2;
        if (firstIndex >= 0)
        {
            int lastIndex = firstIndex + sentenca.length() + 2;
            if (s.cor == null)
                for (Grupo g : fachada.getGrupos())
                    for (Sintagma si : g.getListaSintagmas())
                        if (si.equals(s))
                            s.cor = g.getColor();
            onHighlightSintagma(s.sn, firstIndex, lastIndex, s.cor);
        }
    }

    private boolean onHighlightSintagma(String sintagma, int firstIndex, int lastIndex, Color c)
    {
        if (firstIndex < 0)
            return false;
        else
        {
            int index = texto.indexOf(sintagma, firstIndex);
            if (index < 0)
                return false;
            if (index > lastIndex)
                return false;

            int startIndex = index;
            int endIndex = startIndex + sintagma.length();

            StyledDocument doc = tP.getStyledDocument();
            SimpleAttributeSet keyWord = new SimpleAttributeSet();
            StyleConstants.setForeground(keyWord, c);
            StyleConstants.setBold(keyWord, true);
            StyleConstants.setFontSize(keyWord, 14);
            doc.setCharacterAttributes(startIndex, sintagma.length(), keyWord, false);
            tP.setVisible(true);

            return onHighlightSintagma(sintagma, startIndex + 1, lastIndex, c);
        }
    }

    private void gerarCores(int n)
    {
        Random r = new Random();
        for (int i = 0; i < n * 10; i++)
            colors.add(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
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
            localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
        }

        @Override
        protected Transferable createTransferable(JComponent c)
        {
            source = (JList) c;
            indices = source.getSelectedIndices();
            Object[] transferedObjects = source.getSelectedValues();
            return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport info)
        {
            return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
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
                Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
                Color oldColor = null;
                for (Object value : values)
                {
                    if (((Sintagma) value).cor != null)
                        oldColor = ((Sintagma) value).cor;
                    int idx = index++;
                    listModel.add(idx, value);
                    target.addSelectionInterval(idx, idx);
                    if (listModel.size() <= 1)
                        ((Sintagma) value).set = MainPanel.maiorSet++;
                    else
                        ((Sintagma) value).set = ((Sintagma) listModel.get(0)).set;
                }

                addCount = target.equals(source) ? values.length : 0;
                Object[] modelToArray = listModel.toArray();
                ArrayList<Sintagma> sints = new ArrayList<>();
                boolean foundColor = false;
                Color newColor = null;
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
                ordenador = (Sintagma s1, Sintagma s2) -> new Integer(s1.snID).compareTo(s2.snID);
                Collections.sort(sints, ordenador);
                listModel.clear();
                /*se newColor tiver chegado até aqui null, tem alguma coisa
                MUITO errada pq daí VÁRIOS sintagmas sem cor foram selecionados
                então é melhor simplesmente dar uma cor nova para tudo de uma vez
                pq deu pau
                 */
                if (newColor == null)
                    newColor
                            = new Color(cores.nextInt(256), cores.nextInt(256), cores.nextInt(256));
                for (Sintagma sint : sints)
                {//arruma as cores
                    sint.cor = newColor;
                    listModel.addElement(sint);
                }
                return true;
            } catch (UnsupportedFlavorException | IOException ex)
            {
                ex.printStackTrace();
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
