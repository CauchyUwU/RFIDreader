package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class Main {

    //private Preferences prefs; //replace with property
    private Properties props;
    InputStream inputStream;
    private final static String propFileName = System.getProperty("user.home") + "\\Desktop\\RFIDreader\\scanners.properties";

    public Main()
    {
        props = new Properties();
        File temp = new File(propFileName);
        try {
            if(!temp.exists())
            {
                resetProps(temp);
            }
            inputStream = new FileInputStream(temp);
            if (inputStream != null) {
                props.load(inputStream);
            } else {
                System.out.println("null");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        //m.resetProps();
        m.getPropAsList();
        RFIDUI gui = new RFIDUI(m);
        /*
        System.out.println(m.prefs.get("SCANNER", ""));
        Scanner sc = new Scanner(System.in);
        m.prefs.put("SCANNER", m.prefs.get("SCANNER", "")+sc.nextLine());
        System.out.println(m.prefs.get("SCANNER", ""));*/
    }

    public ArrayList getPropAsList()
    {
        ArrayList scanners = new ArrayList();
        int count = Integer.parseInt(props.getProperty("count"));
        for (int i = count; i >= 0; i--)
        {
            scanners.add(props.getProperty(String.valueOf(i)));
        }
        return scanners;
    }

    public Properties getProps() {
        return props;
    }

    public void resetProps(File file) {
        try {
            file.createNewFile();
            FileInputStream fis = new FileInputStream(file);
            props.load(fis);

            Integer count = Integer.parseInt(props.getProperty("count"));

            for(int i = 0; i <= count; i++)
            {
                props.remove(String.valueOf(count));
            }

            props.put("count", "-1");

            FileOutputStream fos = new FileOutputStream(file);
            props.store(fos,null);
            System.out.println("SUCCESS");
        }
        catch(Exception e) {
            System.out.println("FAILED");
        }
    }

    public void addNewScanner(File selectedFile)
    {
        int count = Integer.parseInt(props.getProperty("count")) + 1;

        try {
            FileInputStream fis = new FileInputStream(propFileName);
            props.load(fis);

            props.setProperty("count", String.valueOf(count));
            props.setProperty(String.valueOf(count), selectedFile.toString());

            FileOutputStream fos = new FileOutputStream(propFileName);
            props.store(fos,null);
            System.out.println("SUCCESS");
            fis.close();
            fos.close();
        }
        catch(Exception e) {
            System.out.println("FAILED");
        }
    }
}
