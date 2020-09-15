package com.company;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CopyThread extends Thread
{
    private JProgressBar prog;
    private ArrayList toCopy;
    private RFIDUI ui;
    private JList logsList;

    private InterruptedException interrupted;

    public CopyThread(JProgressBar prog, ArrayList toCopy, RFIDUI ui, JList logsList)
    {
        this.prog = prog;
        this.toCopy = toCopy;
        this.ui = ui;
        this.logsList = logsList;
    }

    public void run()
    {
        int len = toCopy.size();
        for (int i = 0; i < len; i++)
        {
            File temp = new File(toCopy.get(i).toString());
            if(temp.exists())
            {
                try
                {
                    if(temp.renameTo(new File("D:\\logDest" + "\\" + temp.getName())))
                    {
                        temp.delete();
                    }
                    else
                    {
                        throw new FileNotFoundException();
                    }
                }
                catch (Exception e)
                {
                    InterruptedException ex = new InterruptedException(e.getMessage());
                    interrupted = ex;
                    ui.copyError(prog);
                    return;
                }
            }
            else
            {
                InterruptedException e = new InterruptedException("Interrupted");
                interrupted = e;
                ui.copyError(prog);
                return;
            }
            prog.setValue(prog.getValue() + 100/len);
        }
        ui.copySuccess(prog, ui, logsList);
    }

    public InterruptedException getInterrupted() {
        return interrupted;
    }

}
