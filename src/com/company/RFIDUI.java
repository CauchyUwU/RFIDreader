package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RFIDUI
{
    private Main main;
    final static String NEWSCAN = "Neuen Scanner einrichten";
    final static String SYNCDELPART = "Ausgewählte Logs übertragen";
    final static String SYNCDEL = "Alle Logs übertragen";

    public RFIDUI(Main main)
    {
        this.main = main;
        MainUISetup();
    }

    private void MainUISetup()
    {
        JFrame frame = new JFrame("RFIDreader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(750,450));
        frame.setBackground(new Color(255,255,255));

        JPanel cards = new JPanel(new CardLayout());
        JPanel card0 = newScan();
        cards.add(card0, NEWSCAN);
        JPanel card1 = synchroDeletePart();
        cards.add(card1, SYNCDELPART);
        JPanel card2 = synchroDelete();
        cards.add(card2, SYNCDEL);

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
                        System.out.println(0);
                        break;
                    case 1:
                        cl.show(cards, SYNCDELPART);
                        break;
                    case 2:
                        cl.show(cards, SYNCDEL);
                        break;
                    case 3:
                        break; //TODO
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

        int newest = Integer.parseInt(main.getProps().getProperty("count"));
        if(newest >= 0)
        {
            String newestScanner = main.getProps().getProperty(String.valueOf(newest));
            newestScannerText.setText("Der letzte verbundene Scanner war "+ newestScanner + ". " +
                    "\n Um einen neuen Scanner hinzuzufügen, geben Sie im Textfeld unten den Pfad zum neuen Scanner ein." +
                    "\n Für mehr Informationen siehe \"Hilfe\".");
        }
        else
        {
            newestScannerText.setText("Aktuell ist kein Scanner bekannt. Fügen Sie unten einen Scanner hinzu um zu beginnen." +
                    "\n Für mehr Informationen siehe \"Hilfe\".");
        }
        newestScannerText.setEditable(false);
        //newestScannerText.setSize(490, 90);
        Color veryLightGrey = new Color(238,238,238);
        newestScannerText.setBackground(veryLightGrey);
        topPanel.add(newestScannerText, BorderLayout.NORTH);
        topPanel.setMaximumSize(new Dimension(495, 100));
        topPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        JPanel midPanel = new JPanel();
        midPanel.setLayout(new BorderLayout());
        JTextField addScannerText = new JTextField();
        addScannerText.setSize(350, 27);
        addScannerText.setPreferredSize(new Dimension(350, 27));
        JTextPane addScannerExplanation = new JTextPane();
        addScannerExplanation.setText("Um einen neuen Scanner hinzuzufügen klicken Sie auf \"Durchsuchen\" und wählen Sie den Scanner aus. \n Alternativ können Sie " +
                "aus der Liste unten einen bereits bekannten Scanner durch Klicken auswählen.");
        addScannerExplanation.setMargin(new Insets(5,5,5,5));
        addScannerExplanation.setEditable(false);
        addScannerExplanation.setBackground(veryLightGrey);
        midPanel.add(addScannerExplanation, BorderLayout.NORTH);
        JPanel flowPanel = new JPanel();
        flowPanel.setLayout(new FlowLayout());
        flowPanel.add(addScannerText);
        JButton search = new JButton("Durchsuchen");
        search.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //TODO clicker
                System.out.println("Durchsuchen");
            }
    });
        flowPanel.add(search);
        flowPanel.setMaximumSize(new Dimension(495, 30));
        midPanel.add(flowPanel, BorderLayout.CENTER);
        midPanel.setMaximumSize(new Dimension(495, 125));
        midPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        ArrayList scanners = main.getPropAsList();

        JList scannerList = new JList(scanners.toArray());
        scannerList.setFixedCellHeight(25);
        scannerList.setFixedCellWidth(450);
        scannerList.setCellRenderer(getRendererScanners());
        scannerList.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                System.out.println("scannerList"); //TODO
            }
        });
        ArrayList temp = new ArrayList();
        for(int i = 0; i < scannerList.getModel().getSize(); i++)
        {
            temp.add("X"); //TODO
        }
        JList scannerListClose = new JList(temp.toArray());
        scannerListClose.setFixedCellHeight(25);
        scannerListClose.setFixedCellWidth(25);
        scannerListClose.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                System.out.println("scannerListClose"); //TODO
            }
        });
        JPanel listPanel = new JPanel();
        listPanel.add(scannerList, BorderLayout.WEST);
        listPanel.add(scannerListClose, BorderLayout.EAST);
        listPanel.setSize(new Dimension(495, 250));
        JScrollPane scrollFrame = new JScrollPane(listPanel);
        scrollFrame.setPreferredSize(new Dimension(495,250));
        scrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JTextPane scannerListExplanation = new JTextPane();
        scannerListExplanation.setText("Liste bereits bekannter Scanner (zum Auswählen klicken, zum Löschen das X auswählen)"); //TODO clicker delete
        scannerListExplanation.setMargin(new Insets(5,5,5,5));
        scannerListExplanation.setEditable(false);
        scannerListExplanation.setBackground(veryLightGrey);
        bottomPanel.add(scannerListExplanation, BorderLayout.NORTH);
        bottomPanel.add(scrollFrame, BorderLayout.CENTER);
        bottomPanel.setMaximumSize(new Dimension(495,225));

        newscan.add(topPanel);
        //newscan.add(Box.createRigidArea(new Dimension(490,100)));
        newscan.add(midPanel);
        //newscan.add(Box.createRigidArea(new Dimension(490,100)));
        newscan.add(bottomPanel);

        newscan.setSize(new Dimension(500,450));
        return newscan;
    }

    private JPanel synchroDeletePart()
    {
        //explanations TODO
        //copy some (chosen) values from clipboard and delete save file/clipboard
        //maybe save this in file?
        JPanel syncdelPart = new JPanel();
        syncdelPart.setLayout(new BoxLayout(syncdelPart, BoxLayout.PAGE_AXIS));

        JTextPane explanation = new JTextPane();
        explanation.setText("Wählen Sie unten alle Logs aus, die Sie kopieren wollen.");
        explanation.setMaximumSize(new Dimension(495,0));

        syncdelPart.add(explanation);
        syncdelPart.setPreferredSize(new Dimension(500,450));
        return syncdelPart;
    }

    private JPanel synchroDelete()
    {
        JPanel syncdel = new JPanel();
        syncdel.setLayout(new BoxLayout(syncdel, BoxLayout.PAGE_AXIS));

        JTextPane placeholder = new JTextPane();
        placeholder.setText("placeholder");

        syncdel.add(placeholder);
        syncdel.setPreferredSize(new Dimension(500,450));
        return syncdel;
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
                return listCellRendererComponent;
            }
        };
    }
}
