package com.company;

import jmtp.PortableDevice;
import jmtp.PortableDeviceManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ScannerHandler
{
    private PortableDeviceManager manager;
    private ArrayList scannerList;

    private MTPFileManager fileManager;

    public ScannerHandler()
    {
        scannerList = new ArrayList();
        manager = new PortableDeviceManager(); //TODO scannerHandler
        manager.refreshDeviceList();
        scannerList.addAll(Arrays.asList(manager.getDevices()));
    }

    public ArrayList getLogsFromScanner(PortableDevice device)
    {
        discardFileManager();
        setFileManager(device);
        return null; //TODO
    }


    public ArrayList getScannerList() {
        return scannerList;
    }

    public MTPFileManager getFileManager() {
        return fileManager;
    }

    public void setFileManager(PortableDevice device) {
        MTPFileManager fileManager = new MTPFileManager();
        fileManager.openDevice(device);
        this.fileManager = fileManager;
    }

    public void discardFileManager()
    {
        PortableDevice device = fileManager.getDevice();
        device.close();
        fileManager = null;
    }
}
