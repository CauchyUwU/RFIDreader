package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class Main {

    //private Preferences prefs; //replace with property
    private Properties props;
    InputStream inputStream;

    public Main()
    {
        props = new Properties();
        String propFileName = "scanners.properties";
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
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
        int count = Integer.valueOf(props.getProperty("count"));
        for (int i = 0; i <= count; i++)
        {
            scanners.add(props.getProperty(String.valueOf(i)));
        }
        return scanners;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
