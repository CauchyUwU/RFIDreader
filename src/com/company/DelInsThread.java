package com.company;

import javax.swing.*;

public class DelInsThread extends Thread
{
    private ThreadState state;
    private Main main;
    private DefaultListModel model;
    private RFIDUI ui;

    private DefaultListModel ret;

    public DelInsThread(ThreadState state, Main main, DefaultListModel model, RFIDUI ui)
    {
        this.state = state;
        this.main = main;
        this.model = model;
        this.ui = ui;
        this.ret = new DefaultListModel();
    }

    @Override
    public void run() {
        if(state == ThreadState.COPY)
        {
            copy();
        }
        else
        {
            System.out.println("delete");
        }
    }

    synchronized void copy()
    {
        ret.addAll(main.getPropAsList());
    }

    public DefaultListModel getRet() {
        return ret;
    }

    public void setRet(DefaultListModel ret) {
        this.ret = ret;
    }

}
