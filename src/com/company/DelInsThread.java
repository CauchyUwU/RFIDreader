package com.company;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class DelInsThread extends Thread
{
    private ThreadState state;
    private Main main;
    private DefaultListModel model;
    private RFIDUI ui;
    private int index;

    private DefaultListModel ret;

    private final static String propsPath = System.getProperty("user.home") + "\\Desktop\\RFIDreader\\scanners.properties";

    public DelInsThread(ThreadState state, Main main, DefaultListModel model, RFIDUI ui, int i)
    {
        File f = new File(propsPath);
        this.state = state;
        this.main = main;
        this.model = model;
        this.ui = ui;
        this.ret = new DefaultListModel();
        this.index = i;
    }

    @Override
    public void run() {
        if(state == ThreadState.COPY)
        {
            copy();
        }
        else
        {
            delete();
        }
    }

    synchronized void delete() {
        try {
            Properties props = main.getProps();

            File temp = new File(propsPath);
            if(temp.isHidden())
            {
                Files.setAttribute(Path.of(propsPath), "dos:hidden", Boolean.FALSE, LinkOption.NOFOLLOW_LINKS);
            }

            InputStream in = new FileInputStream(propsPath);
            props.load(in);
            props.remove(String.valueOf(index));
            for(int i = index; i < Integer.parseInt(props.getProperty("count")); i++)
            {
                props.setProperty(String.valueOf(i), props.getProperty(String.valueOf(i+1)));
            }
            props.remove(String.valueOf(Integer.parseInt(props.getProperty("count"))));
            props.setProperty("count", String.valueOf(Integer.parseInt(props.getProperty("count"))-1));
            if(Integer.parseInt(props.getProperty("count")) < 0)
            {
                props.setProperty("count", String.valueOf(-1));
            }
            OutputStream out = new FileOutputStream(propsPath);
            props.store(out, null);
            in.close();
            out.close();
            Files.setAttribute(Path.of(propsPath), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.addAll(main.getPropAsList());
    }

    synchronized void copy()
    {
        try {
            File temp = new File(propsPath);
            if (temp.isHidden()) {
                Files.setAttribute(Path.of(propsPath), "dos:hidden", Boolean.FALSE, LinkOption.NOFOLLOW_LINKS);
            }
            ret.addAll(main.getPropAsList());
            Files.setAttribute(Path.of(propsPath), "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    synchronized DefaultListModel getRet() {
        return ret;
    }

    public void setRet(DefaultListModel ret) {
        this.ret = ret;
    }

}
