package com.company;

import jmtp.PortableDevice;
import jmtp.PortableDeviceManager;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.company.RFIDUI.logPath;

public class CopyThread extends Thread
{
    private JProgressBar prog;
    private ArrayList toCopy;
    private RFIDUI ui;
    private JList logsList;
    private final static String logDest = System.getProperty("user.home") + "\\Desktop\\RFIDreader\\Logs\\";
    private final static String logSource = "Internal shared storage\\handset\\UHF";
    private String currentScanner;

    private InterruptedException interrupted;

    public CopyThread(JProgressBar prog, ArrayList toCopy, RFIDUI ui, JList logsList, String scanner)
    {
        this.currentScanner = scanner;
        this.prog = prog;
        this.toCopy = toCopy;
        this.ui = ui;
        this.logsList = logsList;
        File temp = new File(logDest);
        if(!temp.exists())
        {
            try {
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run()
    {
        int len = toCopy.size();

        try
        {
            File temp = new File(logDest);
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < len; i++)
        {
            String filepath = "\\" + currentScanner + "\\" + logSource + "\\" + toCopy.get(i).toString();
            File batch = new File(System.getProperty("user.home") + "\\Desktop\\RFIDreader\\copy.bat");
            batch.delete();

            File batchNew = new File(System.getProperty("user.home") + "\\Desktop\\RFIDreader\\copy.bat"); //TODO change this, doesn't work

            String source = "cd C:\\ \n" +
                    "SET File=" + filepath +"\n" +
                    "SET Dest=" + logDest +"\n" +
                    "move %File% %Dest%\n" +
                    "SET File=\n" +
                    "pause";

            try {
                FileWriter f2 = new FileWriter(batchNew, false);
                f2.write(source);
                f2.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean exists = false;

            PortableDeviceManager manager = new PortableDeviceManager();
            for(PortableDevice device : manager)
            {
                if(device.getFriendlyName().equals(currentScanner))
                {
                    MTPFileManager fileManager = new MTPFileManager();
                    fileManager.openDevice(device);
                    if(fileManager.getAllFilesByName(logPath).contains(toCopy.get(i).toString()))
                    {
                        exists = true;
                    }
                }
            }

            if(exists)
            {
                try
                {
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", System.getProperty("user.home") + "\\Desktop\\RFIDreader\\copy.bat");
                    pb.directory(new File(System.getProperty("user.home")));
                    Process p = pb.start();
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
