package com.company;

import jmtp.PortableDevice;
import jmtp.PortableDeviceManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class Main {

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
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        PortableDeviceManager ma = new PortableDeviceManager();
        for (PortableDevice d : ma)
        {
            d.open();
        }
        m.getPropAsList();
        RFIDUI gui = new RFIDUI(m);
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
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewScanner(String scanner)
    {
        int count = Integer.parseInt(props.getProperty("count")) + 1;

        try {
            File temp = new File(propFileName);
            if (temp.isHidden()) {
                Files.setAttribute(Path.of(propFileName), "dos:hidden", Boolean.FALSE, LinkOption.NOFOLLOW_LINKS);
            }

            FileInputStream fis = new FileInputStream(propFileName);
            props.load(fis);

            props.setProperty("count", String.valueOf(count));
            props.setProperty(String.valueOf(count), scanner);

            FileOutputStream fos = new FileOutputStream(propFileName);
            props.store(fos,null);
            fis.close();
            fos.close();
            Files.setAttribute(Path.of(propFileName), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
