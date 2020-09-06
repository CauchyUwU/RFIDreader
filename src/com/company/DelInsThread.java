package com.company;

import javax.swing.*;
import java.io.File;

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
