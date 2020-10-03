package com.company;

import jmtp.*;

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


    public ArrayList getScannerList() {
        return scannerList;
    }
}
