/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Observable;

/**
 *
 * @author Andrej Sakal
 */
public class PortListener extends Observable implements SerialPortEventListener{

    SerialPort serialPort = null;

    public SerialPort getSerialPort() {
        return serialPort;
    }
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        "/dev/ttyUSB0", // Linux
        "COM3", "COM6", "COM7", "COM8", "COM9", "COM5" // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    
    /**
     * Saves the inputString
    */
    private String inputString;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    
    public PortListener() {
        System.out.println("CONSTR");//---------------------------
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    System.out.println("CONSTR FOUND PORT: " + portId.getName());//---------------------------
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }
        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
            this.close();
        }
    }
    /**
     * Returns the last read Input from the Port
     * @return 
     */
    public String getInputString() 
    {
        return inputString;
    }
    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    /**
     * Handle an event on the serial port. Read the data and print it.
     * @param oEvent
     */
    @Override
    public void serialEvent(SerialPortEvent oEvent) {
        
        //Cheks if the event is our needed event
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                
                //Cheks the value of the input
                if(input.ready())
                {
                    inputString = input.readLine();
                    
                    //For testing
                    System.out.println(inputString);
                
                    //Notifying all Observers when something new is read from the Prot
                    setChanged();
                    notifyObservers();
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }
    /*
    public void check() {
        
    }
    */
}
