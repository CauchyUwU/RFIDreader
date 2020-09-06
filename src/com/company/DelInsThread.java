package com.company;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class DelInsThread extends Thread
{
    private ThreadState state;
    private Main main;
    private DefaultListModel model;
    private RFIDUI ui;
    private int index;

    private DefaultListModel ret;

    public DelInsThread(ThreadState state, Main main, DefaultListModel model, RFIDUI ui, int i)
    {
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

    synchronized void delete() {//TODO
        try {
            Properties props = main.getProps();
            FileInputStream in = new FileInputStream("resources/scanners.properties");
            props.load(in);
            props.remove(String.valueOf(index));
            for(int i = index+1; i < Integer.parseInt(props.getProperty("count")); i++)
            {
                props.setProperty(String.valueOf(i), props.getProperty(String.valueOf(i+1)));
            }
            props.remove(String.valueOf(Integer.parseInt(props.getProperty("count"))));
            props.setProperty("count", String.valueOf(Integer.parseInt(props.getProperty("count"))-1));
            OutputStream out = new FileOutputStream("resources/scanners.properties");
            props.store(out, null);
            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.addAll(main.getPropAsList());
    }

    synchronized void copy()
    {
        ret.addAll(main.getPropAsList());
    }

    synchronized DefaultListModel getRet() {
        return ret;
    }

    public void setRet(DefaultListModel ret) {
        this.ret = ret;
    }

}
