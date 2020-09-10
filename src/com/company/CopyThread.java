package com.company;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class CopyThread extends Thread
{
    private JProgressBar prog;
    private ArrayList toCopy;

    private InterruptedException interrupted;

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
            File temp = new File(toCopy.get(i).toString());
            if(temp.exists())
            {

            }
            else
            {
                InterruptedException e = new InterruptedException();
                interrupted = e;
                return;
            }
            prog.setValue(prog.getValue() + 100/len);
        }
    }

    public InterruptedException getInterrupted() {
        return interrupted;
    }

}
