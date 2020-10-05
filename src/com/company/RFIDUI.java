package com.company;

import jmtp.PortableDevice;
import jmtp.PortableDeviceManager;

import javax.sound.sampled.Port;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Runtime.getRuntime;

public class RFIDUI
{
    private Main main;
    final static String NEWSCAN = "Neuen Scanner einrichten";
    final static String SYNCDELPART = "Ausgewählte Logs übertragen";
    final static String SYNCDEL = "Alle Logs übertragen";
    final static String HELP = "Hilfe";
    private Color veryLightGrey = new Color(238,238,238);
    private int newest;
    static String logPath = "Internal shared storage\\handset\\UHF";
    private String currentScanner;
    ScannerHandler handler;

    public RFIDUI(Main main)
    {
        this.main = main;
        MainUISetup();
        handler = new ScannerHandler();

    }

    private void MainUISetup()
    {
        JFrame frame = new JFrame("RFIDreader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(750,500));
        frame.setBackground(new Color(255,255,255));

        JPanel cards = new JPanel(new CardLayout());
        JPanel card0 = newScan();
        cards.add(card0, NEWSCAN);
        JPanel card1 = syncDel(false);
        cards.add(card1, SYNCDELPART);
        JPanel card2 = syncDel(true);
        cards.add(card2, SYNCDEL);
        JPanel card3 = help();
        cards.add(card3, HELP);

        frame.getContentPane().add(cards, BorderLayout.CENTER); //add return to frame
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, NEWSCAN);

        String[] options = {"Neuen Scanner einrichten", "Ausgewählte Logs übertragen", "Alle Logs übertragen", "Hilfe"};
        JList optionsList = new JList(options);
        optionsList.setFixedCellHeight(40);
        optionsList.setCellRenderer(getRenderer());
        optionsList.setSelectedIndex(0);
        optionsList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = optionsList.getSelectedIndex();
                switch (index)
                {
                    case 0:
                        cl.show(cards, NEWSCAN);
                        break;
                    case 1:
                        cl.removeLayoutComponent(card1);
                        JPanel card1 = syncDel(false);
                        cards.add(card1, SYNCDELPART);
                        cl.show(cards, SYNCDELPART);
                        break;
                    case 2:
                        cl.removeLayoutComponent(card2);
                        JPanel card2 = syncDel(true);
                        cards.add(card2, SYNCDEL);
                        cl.show(cards, SYNCDEL);
                        break;
                    case 3:
                        cl.show(cards, HELP);
                        break;
                    default:
                        cl.show(cards, NEWSCAN);
                        break;
                }
            }
        });

        JPanel listPanel = new JPanel();
        listPanel.setSize(new Dimension(250,450));
        listPanel.setBackground(new Color(255,255,255));
        listPanel.add(optionsList);

        frame.getContentPane().add(listPanel, BorderLayout.WEST);

        //Display the window.
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel newScan()
    {
        JPanel newscan = new JPanel();
        newscan.setLayout(new BoxLayout(newscan, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JTextPane newestScannerText = new JTextPane();
        newestScannerText.setMargin(new Insets(5,5,5,5));

        newest = Integer.parseInt(main.getProps().getProperty("count"));
        if(newest >= 0)
        {
            String newestScanner = main.getProps().getProperty(String.valueOf(newest));
            newestScannerText.setText("Der letzte verbundene Scanner war "+ newestScanner + ". " +
                    "\n Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                    "\n Für mehr Informationen siehe \"Hilfe\".");
            currentScanner = newestScanner;
        }
        else
        {
            newestScannerText.setText("Aktuell ist kein Scanner bekannt. Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                    "\n Für mehr Informationen siehe \"Hilfe\".");
        }
        newestScannerText.setEditable(false);
        //newestScannerText.setSize(490, 90);
        newestScannerText.setBackground(veryLightGrey);
        topPanel.add(newestScannerText, BorderLayout.NORTH);
        topPanel.setMaximumSize(new Dimension(495, 200));
        topPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BorderLayout());

        JTextPane addScannerExplanation = new JTextPane();
        addScannerExplanation.setText("Um einen neuen Scanner hinzuzufügen klicken Sie auf \"Durchsuchen\" und wählen Sie den Scanner aus. \n Alternativ können Sie " +
                "aus der Liste unten einen bereits bekannten Scanner durch Klicken auswählen.");
        addScannerExplanation.setMargin(new Insets(5,5,5,5));
        addScannerExplanation.setEditable(false);
        addScannerExplanation.setBackground(veryLightGrey);
        midPanel.add(addScannerExplanation, BorderLayout.NORTH);
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        JButton search = new JButton("Durchsuchen");

        flowPanel.add(search);
        flowPanel.setMaximumSize(new Dimension(495, 30));
        midPanel.add(flowPanel, BorderLayout.CENTER);
        midPanel.setMaximumSize(new Dimension(495, 125));
        midPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        JTextPane scannerListExplanation = new JTextPane();
        scannerListExplanation.setText("Liste bereits bekannter Scanner (zum Auswählen klicken, zum Löschen das X auswählen)");
        scannerListExplanation.setMargin(new Insets(5,5,5,5));
        scannerListExplanation.setEditable(false);
        scannerListExplanation.setBackground(veryLightGrey);
        bottomPanel.add(scannerListExplanation, BorderLayout.NORTH);
        ArrayList scanners = main.getPropAsList();

        JList scannerList = new JList();
        DefaultListModel model = new DefaultListModel();
        model.addAll(scanners);
        scannerList.setModel(model);
        scannerList.setFixedCellHeight(25);
        scannerList.setFixedCellWidth(440);
        scannerList.setCellRenderer(getRendererScanners());
        scannerList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                newest = Integer.parseInt(main.getProps().getProperty("count"))-scannerList.getSelectedIndex();
                currentScanner = main.getProps().getProperty(String.valueOf(newest));
                if(newest >= 0)
                {
                    MTPFileManager manager = new MTPFileManager();
                    PortableDevice[] devices = manager.getDevices();
                    boolean exists = false;
                    for(int i = 0; i < devices.length; i++)
                    {
                        if(devices[i].getFriendlyName().equals(currentScanner))
                        {
                            exists = true;
                        }
                    }
                    if(!exists)
                    {
                        JOptionPane.showMessageDialog(null,
                                "Der Scanner kann nicht gefunden werden. Kopieren und Löschen von Logs nicht möglich.",
                                "Achtung!",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        String text = scannerList.getModel().getElementAt(0).toString();
                        String newestScanner = main.getProps().getProperty(String.valueOf(newest));
                        newestScannerText.setText("Der letzte verbundene Scanner war " + newestScanner + ". " +
                                "\n Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                                "\n Für mehr Informationen siehe \"Hilfe\".");
                    }
                }
                else
                {
                    newestScannerText.setText("Aktuell ist kein Scanner bekannt. Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                            "\n Für mehr Informationen siehe \"Hilfe\".");
                }

            }
        });
        JList scannerListClose = new JList();
        scannerListClose.setCellRenderer(new ImageListCellRenderer());

        JTextPane text = new JTextPane();
        text.setText("X");
        text.setBackground(Color.RED);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Arial", Font.BOLD, 8));
        text.setAlignmentX(100);
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        closePanel.add(text);
        closePanel.setBackground(veryLightGrey);

        Object[] panels = new Object[scannerList.getModel().getSize()];
        for (int i = 0; i < scannerList.getModel().getSize(); i++)
        {
            panels[i] = closePanel;
        }

// tell the jlist to use the panel array for its data
        scannerListClose.setListData(panels);
        scannerListClose.setBackground(veryLightGrey);
        scannerListClose.setFixedCellHeight(25);
        scannerListClose.setFixedCellWidth(25);
        scannerListClose.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int index = Integer.parseInt(main.getProps().getProperty("count")) - scannerListClose.getSelectedIndex();
                DefaultListModel model = delButtonClicked(newscan, index);
                scannerList.setModel(model);
                Object[] panels = new Object[scannerList.getModel().getSize()];
                for (int i = 0; i < scannerList.getModel().getSize(); i++)
                {
                    panels[i] = closePanel;
                }

                scannerListClose.setListData(panels);
                if(newest > Integer.parseInt(main.getProps().getProperty("count")));
                {
                    newest = Integer.parseInt(main.getProps().getProperty("count"));
                }
                if(newest >= 0)
                {
                    String newestScanner = main.getProps().getProperty(String.valueOf(newest));
                    newestScannerText.setText("Der letzte verbundene Scanner war "+ newestScanner + ". " +
                            "\n Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                            "\n Für mehr Informationen siehe \"Hilfe\".");
                }
                else
                {
                    newestScannerText.setText("Aktuell ist kein Scanner bekannt. Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                            "\n Für mehr Informationen siehe \"Hilfe\".");
                }
            }
        });
        JPanel listPanel = new JPanel();
        listPanel.add(scannerList, BorderLayout.WEST);
        listPanel.add(scannerListClose, BorderLayout.EAST);
        listPanel.setSize(new Dimension(495, 200));
        JScrollPane scrollFrame = new JScrollPane(listPanel);
        scrollFrame.setPreferredSize(new Dimension(495,200));
        scrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        bottomPanel.add(scrollFrame, BorderLayout.CENTER);
        bottomPanel.setMaximumSize(new Dimension(495,200));

        search.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                DefaultListModel model = searchButtonClicked(newscan);
                scannerList.setModel(model);
                Object[] panels = new Object[scannerList.getModel().getSize()];
                for (int i = 0; i < scannerList.getModel().getSize(); i++)
                {
                    panels[i] = closePanel;
                }
                scannerListClose.setListData(panels);
                String text = scannerList.getModel().getElementAt(0).toString();
                try
                {
                    String newestScanner = text;
                    newestScannerText.setText("Der letzte verbundene Scanner war " + newestScanner + ". " +
                            "\n Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                            "\n Für mehr Informationen siehe \"Hilfe\".");
                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                    newestScannerText.setText("Aktuell ist kein Scanner bekannt. " +
                            "\n Um einen neuen Scanner hinzuzufügen, klicken Sie auf \"Durchsuchen\"." +
                            "\n Für mehr Informationen siehe \"Hilfe\".");
                }

            }
        });


        newscan.add(topPanel);
        //newscan.add(Box.createRigidArea(new Dimension(490,100)));
        newscan.add(midPanel);
        //newscan.add(Box.createRigidArea(new Dimension(490,100)));
        newscan.add(bottomPanel);

        newscan.setSize(new Dimension(500,450));
        return newscan;
    }

    @Deprecated
    private JPanel synchroDeletePart()
    {
        //copy some (chosen) values from clipboard and delete save file/clipboard
        //maybe save this in file?
        JPanel syncdelPart = new JPanel();
        syncdelPart.setLayout(new BoxLayout(syncdelPart, BoxLayout.Y_AXIS));

        JTextPane explanation = new JTextPane();
        explanation.setText("Wählen Sie unten alle Logs aus, die Sie kopieren wollen. \nAchtung: die kopierten Logs werden vom " +
                "Scanner gelöscht. Trennen Sie wärend des Kopierens nicht die Verbindung zum Scanner, da sonst Daten verloren gehen " +
                "oder beschädigt werden können!");
        explanation.setMinimumSize(new Dimension(495,75));
        explanation.setMaximumSize(new Dimension(495,75));
        explanation.setPreferredSize(new Dimension(495,75));
        explanation.setBackground(veryLightGrey);
        explanation.setEditable(false);

        JTextPane labelList = new JTextPane();
        labelList.setText("Liste der aktuellen Logs:");
        labelList.setMinimumSize(new Dimension(495,25));
        labelList.setMaximumSize(new Dimension(495,25));
        labelList.setPreferredSize(new Dimension(495,25));
        labelList.setBackground(veryLightGrey);
        labelList.setEditable(false);

        ArrayList logs;
        File tempFolder = new File(logPath);
        logs = new ArrayList(Arrays.asList(tempFolder.listFiles()));

        JList logsList = new JList(logs.toArray());
        logsList.setFixedCellHeight(25);
        logsList.setFixedCellWidth(480);
        logsList.setBackground(veryLightGrey);
        logsList.setCellRenderer(getRendererScanners());
        logsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        logsList.setSelectionModel(new DefaultListSelectionModel() {
            private static final long serialVersionUID = 1L;

            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(!gestureStarted){
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }

        });
        JScrollPane scrollFrame = new JScrollPane(logsList);
        scrollFrame.setMinimumSize(new Dimension(495,200));
        scrollFrame.setMaximumSize(new Dimension(495,200));
        scrollFrame.setPreferredSize(new Dimension(495,200));
        scrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JProgressBar progBar = new JProgressBar();
        progBar.setValue(0);
        progBar.setStringPainted(true);

        JButton confirm = new JButton("Kopieren beginnen");
        confirm.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                copy(progBar, new ArrayList(logsList.getSelectedValuesList()), logsList);
                ArrayList logs;
                logs = new ArrayList(Arrays.asList(tempFolder.listFiles()));
                logsList.setListData(logs.toArray());
            }
        });
        confirm.setAlignmentX(Component.CENTER_ALIGNMENT);

        syncdelPart.add(explanation);
        syncdelPart.add(labelList);
        syncdelPart.add(scrollFrame);
        syncdelPart.add(confirm);
        syncdelPart.add(Box.createVerticalStrut(10));
        syncdelPart.add(progBar);

        syncdelPart.setPreferredSize(new Dimension(500,450));
        return syncdelPart;
    }

    @Deprecated
    private JPanel synchroDelete()
    {
        JPanel syncdel = new JPanel();
        syncdel.setLayout(new BoxLayout(syncdel, BoxLayout.Y_AXIS));

        JTextPane explanation = new JTextPane();
        explanation.setText("Der Button \"Kopieren\" kopiert alle unten angezeigten Logs. \nAchtung: die kopierten Logs werden vom " +
                "Scanner gelöscht. Trennen Sie wärend des Kopierens nicht die Verbindung zum Scanner, da sonst Daten verloren gehen " +
                "oder beschädigt werden können!");
        explanation.setMinimumSize(new Dimension(495,75));
        explanation.setMaximumSize(new Dimension(495,75));
        explanation.setPreferredSize(new Dimension(495,75));
        explanation.setBackground(veryLightGrey);
        explanation.setEditable(false);

        JTextPane labelList = new JTextPane();
        labelList.setText("Liste der aktuellen Logs:");
        labelList.setMinimumSize(new Dimension(495,25));
        labelList.setMaximumSize(new Dimension(495,25));
        labelList.setPreferredSize(new Dimension(495,25));
        labelList.setBackground(veryLightGrey);
        labelList.setEditable(false);

        ArrayList logs;
        File tempFolder = new File(logPath);
        logs = new ArrayList(Arrays.asList(tempFolder.listFiles()));

        JList logsList = new JList(logs.toArray());
        logsList.setFixedCellHeight(25);
        logsList.setFixedCellWidth(480);
        logsList.setCellRenderer(getRendererScanners());
        logsList.setBackground(veryLightGrey);
        logsList.setSelectionModel(new DefaultListSelectionModel() {
            public void setSelectionInterval(int i, int j) {
                super.setSelectionInterval(-1,-1);
            }
            public void addSelectionInterval(int i, int j) {
                    super.addSelectionInterval(-1, -1);
            }
            public void setLeadSelectionIndex(int i) {
                super.setLeadSelectionIndex(-1);
            }
            public void setAnchorSelectionIndex(int i) {
                super.setAnchorSelectionIndex(-1);
            }
        });
        JScrollPane scrollFrame = new JScrollPane(logsList);
        scrollFrame.setMinimumSize(new Dimension(495,200));
        scrollFrame.setMaximumSize(new Dimension(495,200));
        scrollFrame.setPreferredSize(new Dimension(495,200));
        scrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JProgressBar progBar = new JProgressBar();
        progBar.setValue(0);
        progBar.setStringPainted(true);

        JButton confirm = new JButton("Kopieren beginnen");
        confirm.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ArrayList temp = new ArrayList();
                for(int i = 0; i < logsList.getModel().getSize(); i++)
                {
                    temp.add(logsList.getModel().getElementAt(i));
                }

                copy(progBar, temp, logsList);
            }
        });
        confirm.setAlignmentX(Component.CENTER_ALIGNMENT);

        syncdel.add(explanation);
        syncdel.add(labelList);
        syncdel.add(scrollFrame);
        syncdel.add(confirm);
        syncdel.add(Box.createVerticalStrut(10));
        syncdel.add(progBar);

        syncdel.setPreferredSize(new Dimension(500,450));
        return syncdel;
    }

    private JPanel syncDel(Boolean full)
    {
        JPanel syncdel = new JPanel();
        syncdel.setLayout(new BoxLayout(syncdel, BoxLayout.Y_AXIS));

        JTextPane explanation = new JTextPane();

        if(!full)
        {
            explanation.setText("Wählen Sie unten alle Logs aus, die Sie kopieren wollen. \nAchtung: die kopierten Logs werden vom " +
                    "Scanner gelöscht. Trennen Sie wärend des Kopierens nicht die Verbindung zum Scanner, da sonst Daten verloren gehen " +
                    "oder beschädigt werden können!");
        }
        else
        {
            explanation.setText("Der Button \"Kopieren\" kopiert alle unten angezeigten Logs. \nAchtung: die kopierten Logs werden vom " +
                    "Scanner gelöscht. Trennen Sie wärend des Kopierens nicht die Verbindung zum Scanner, da sonst Daten verloren gehen " +
                    "oder beschädigt werden können!");
        }

        explanation.setMinimumSize(new Dimension(495,75));
        explanation.setMaximumSize(new Dimension(495,75));
        explanation.setPreferredSize(new Dimension(495,75));
        explanation.setBackground(veryLightGrey);
        explanation.setEditable(false);
        JTextPane labelList = new JTextPane();
        labelList.setText("Liste der aktuellen Logs:");
        labelList.setMinimumSize(new Dimension(495,25));
        labelList.setMaximumSize(new Dimension(495,25));
        labelList.setPreferredSize(new Dimension(495,25));
        labelList.setBackground(veryLightGrey);
        labelList.setEditable(false);


        ArrayList logs = new ArrayList(Arrays.asList("Log-Ordner nicht gefunden"));

        PortableDeviceManager manager = new PortableDeviceManager();
        for(PortableDevice device : manager)
        {
            if(device.getFriendlyName().equals(currentScanner))
            {
                MTPFileManager fileManager = new MTPFileManager();
                fileManager.openDevice(device);
                logs.clear();
                logs.addAll(0, fileManager.getAllFilesByName(logPath)); //TODO no folder
            }
        }

        JList logsList = new JList(logs.toArray());
        logsList.setFixedCellHeight(25);
        logsList.setFixedCellWidth(480);
        logsList.setCellRenderer(getRendererScanners());
        logsList.setBackground(veryLightGrey);

        if(!full)
        {
            logsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            logsList.setSelectionModel(new DefaultListSelectionModel() {
                private static final long serialVersionUID = 1L;
                boolean gestureStarted = false;
                @Override
                public void setSelectionInterval(int index0, int index1) {
                    if(!gestureStarted){
                        if (isSelectedIndex(index0)) {
                            super.removeSelectionInterval(index0, index1);
                        } else {
                            super.addSelectionInterval(index0, index1);
                        }
                    }
                    gestureStarted = true;
                }
                @Override
                public void setValueIsAdjusting(boolean isAdjusting) {
                    if (isAdjusting == false) {
                        gestureStarted = false;
                    }
                }
            });
        }
        else
        {
            logsList.setSelectionModel(new DefaultListSelectionModel() {
                public void setSelectionInterval(int i, int j) {
                    super.setSelectionInterval(-1,-1);
                }
                public void addSelectionInterval(int i, int j) {
                    super.addSelectionInterval(-1, -1);
                }
                public void setLeadSelectionIndex(int i) {
                    super.setLeadSelectionIndex(-1);
                }
                public void setAnchorSelectionIndex(int i) {
                    super.setAnchorSelectionIndex(-1);
                }
            });
        }

        JScrollPane scrollFrame = new JScrollPane(logsList);
        scrollFrame.setMinimumSize(new Dimension(495,200));
        scrollFrame.setMaximumSize(new Dimension(495,200));
        scrollFrame.setPreferredSize(new Dimension(495,200));
        scrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JProgressBar progBar = new JProgressBar();
        progBar.setValue(0);
        progBar.setStringPainted(true);
        JButton confirm = new JButton("Kopieren beginnen");

        if(!full)
        {
            confirm.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    PortableDeviceManager manager = new PortableDeviceManager();
                    boolean exists = false;
                    for(PortableDevice device : manager)
                    {
                        if(device.getFriendlyName().equals(currentScanner))
                        {
                            exists = true;
                        }
                    }
                    if(exists)
                    {
                        copy(progBar, new ArrayList(logsList.getSelectedValuesList()), logsList);
                    }
                    else
                    {
                        copyError(progBar);
                    }
                }
            });
        }
        else
        {
            confirm.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    PortableDeviceManager manager = new PortableDeviceManager();
                    boolean exists = false;
                    for(PortableDevice device : manager)
                    {
                        if(device.getFriendlyName().equals(currentScanner))
                        {
                            exists = true;
                        }
                    }
                    if(exists)
                    {
                        ArrayList temp = new ArrayList();
                        for(int i = 0; i < logsList.getModel().getSize(); i++)
                        {
                            temp.add(logsList.getModel().getElementAt(i));
                        }
                        copy(progBar, temp, logsList);
                    }
                    else
                    {
                        copyError(progBar);
                    }
                }
            });
        }

        confirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        syncdel.add(explanation);
        syncdel.add(labelList);
        syncdel.add(scrollFrame);
        syncdel.add(confirm);
        syncdel.add(Box.createVerticalStrut(10));
        syncdel.add(progBar);
        syncdel.setPreferredSize(new Dimension(500,450));
        return syncdel;
    }

    private JPanel help() //TODO
    {
        JPanel help = new JPanel();
        help.setLayout(new BoxLayout(help, BoxLayout.Y_AXIS));

        JTextPane helpText = new JTextPane();
        helpText.setContentType("text/html");
        helpText.setText("<b>Mein Scanner wird nicht erkannt <br></b>" +
                "Prüfen Sie die Verbindung zum Scanner. Um erfolgreich erkannt zu werden, muss auf dem Scanner selbst der USB-Modus aktiviert sein. <br>" +
                "<b>Das Programm erkennt den Scanner, kann aber die Logs nicht finden</b>"); //TODO this case
        //helpText.setMinimumSize(new Dimension(495,75));
        //helpText.setMaximumSize(new Dimension(495,75));
        //helpText.setPreferredSize(new Dimension(495,75));
        helpText.setBackground(veryLightGrey);
        helpText.setEditable(false);

        help.add(helpText);

        return help;
    }

    private void copy(JProgressBar prog, ArrayList toCopy, JList logsList)
    {
        prog.setValue(0);
        CopyThread copyThread = new CopyThread(prog, toCopy, this, logsList, currentScanner);
        copyThread.start();
    }

    public boolean copySuccess(JProgressBar prog, RFIDUI ui, JList logsList)
    {
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null,
                "Kopieren erfolgreich","Erfolg",
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);
        prog.setValue(0);

        DefaultListModel model = new DefaultListModel();
        ArrayList list = new ArrayList();
        PortableDeviceManager manager = new PortableDeviceManager();
        for(PortableDevice device : manager)
        {
            if(device.getFriendlyName().equals(currentScanner))
            {
                MTPFileManager fileManager = new MTPFileManager();
                fileManager.openDevice(device);
                list.clear();
                list.addAll(0, fileManager.getAllFilesByName(logPath)); //TODO no folder
            }
        }

        model.addAll(list);
        logsList.setModel(model);
        Object[] data = new Object[logsList.getModel().getSize()];
        for (int i = 0; i < logsList.getModel().getSize(); i++)
        {
            data[i] = logsList.getModel().getElementAt(i);
        }
        logsList.setListData(data);
        logsList.repaint();
        return true;
    }

    public boolean copyError(JProgressBar prog)
    {
        JOptionPane.showMessageDialog(null,
                "Kopiervorgang unterbrochen. Bitte Verbindung prüfen.",
                "Achtung!",
                JOptionPane.ERROR_MESSAGE);
        prog.setValue(0);
        return false;
    }

    private ListCellRenderer<? super String> getRenderer() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,Color.GRAY));
                return listCellRendererComponent;
            }
        };
    }

    private ListCellRenderer<? super String> getRendererScanners() {
        return new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,Color.GRAY));
                listCellRendererComponent.setBackground(Color.white);
                listCellRendererComponent.setBackground (isSelected ? Color.lightGray : Color.white);
                return listCellRendererComponent;
            }
        };
    }

    private DefaultListModel searchButtonClicked(JPanel parent)
    {
        ArrayList<PortableDevice> scannerList = new ArrayList();
        PortableDeviceManager manager = new PortableDeviceManager(); //TODO scannerHandler
        manager.refreshDeviceList();
        scannerList.addAll(Arrays.asList(manager.getDevices()));

        String[] choices = new String[scannerList.size()];
        for(int i = 0; i < scannerList.size(); i++)
        {
            choices[i] = scannerList.get(i).getFriendlyName();
        }
        if(choices.length<=0)
        {
            choices = new String[1];
            choices[0] = "Kein Scanner bekannt";
        }
        String input = (String) JOptionPane.showInputDialog(null, "Scanner zum Hinzufügen auswählen",
                "Scanner hinzufügen", JOptionPane.PLAIN_MESSAGE, null,
                choices, // Array of choices
                choices[0]); // Initial choice

        if (input != null && !input.equals("Kein Scanner bekannt")) {
            main.addNewScanner(input);
        }

        DefaultListModel model = new DefaultListModel();
        DelInsThread thread = new DelInsThread(ThreadState.COPY, main, model, this, -1);
        thread.start();
        try {
            thread.join();
            model = thread.getRet();
        } catch (InterruptedException e) {
            e.printStackTrace();
            model.add(0, "Fehler beim Laden der Liste.");
        }
        return model;
    }

    private DefaultListModel delButtonClicked(JPanel newscan, int selectedIndex)
    {
        DefaultListModel model = new DefaultListModel();
        DelInsThread thread = new DelInsThread(ThreadState.DELETE, main, model, this, selectedIndex);
        thread.start();
        try {
            thread.join();
            model = thread.getRet();
        } catch (InterruptedException e) {
            e.printStackTrace();
            model.add(0, "Fehler beim Laden der Liste.");
        }
        return model;
    }
}
