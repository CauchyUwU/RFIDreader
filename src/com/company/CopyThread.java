package com.company;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class CopyThread extends Thread
{
    private JProgressBar prog;
    private ArrayList toCopy;
    private RFIDUI ui;

    private InterruptedException interrupted;

    public CopyThread(JProgressBar prog, ArrayList toCopy, RFIDUI ui)
    {
        this.prog = prog;
        this.toCopy = toCopy;
        this.ui = ui;
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
                ui.copyError(prog);
                return;
            }
            prog.setValue(prog.getValue() + 100/len);
        }
        ui.copySuccess(prog);
    }

    public InterruptedException getInterrupted() {
        return interrupted;
    }

}
