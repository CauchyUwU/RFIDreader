package com.company;

import javax.swing.*;
import java.util.ArrayList;

public class CopyThread extends Thread
{
    private JProgressBar prog;
    private ArrayList toCopy;

    public CopyThread(JProgressBar prog, ArrayList toCopy)
    {
        this.prog = prog;
        this.toCopy = toCopy;
    }

    public void run() //TODO interrupted exception when connection lost
    {
        int len = toCopy.size();
        for (int i = 0; i < len; i++)
        {
            prog.setValue(prog.getValue() + 100/len);
        }
    }

}
