/*
 * Unai Goñi Otegi / Università degli Studi di Padova / 2010-11
 */
package blueclean.bolas.eus;

import icommand.nxt.comm.NXTComm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * The Class Speaker.
 */
public class Speaker implements NXTComm{
	
    /** The Constant for describing connection STATE = STATE_NONE. */
    public static final int STATE_NONE = 0;
    
    /** The Constant for describing connection STATE = STATE_CONNECTING. */
    public static final int STATE_CONNECTING = 1;
    
    /** The Constant for describing connection STATE = STATE_CONNECTED. */
    public static final int STATE_CONNECTED = 2;
	
    /** The connection state. */
    private int mState;
    
    /** The m handler. */
    private Handler mHandler;
    
    /** The BT adapter. */
    private BluetoothAdapter mAdapter;
    
    /** The connecting thread. */
    private ConnectThread mConnectThread;
    
    /** The connected thread. */
    private ConnectedThread mConnectedThread;

    //------------------------------------------------------------------------------

    /**
     * Instantiates a new speaker.
     *
     * @param handler the handler
     */
    public Speaker(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        setState(STATE_NONE);
    }
    
    //------------------------------------------------------------------------------

    /**
     * Connect.Starts ConnectThread.
     *
     * @param device the device
     */
    public synchronized void connect(BluetoothDevice device) {
        //Log.i("NXT", "NXTTalker.connect()");
        
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    
    //------------------------------------------------------------------------------

    /**
     * Connected.Starts ConnectedThread.
     *
     * @param socket the socket
     * @param device the device
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        
        setState(STATE_CONNECTED);
    }
    
    //------------------------------------------------------------------------------
    
    /**
     * Stop. Sets STATE_NONE & cancel Connect and Connected Threads
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    //------------------------------------------------------------------------------

    /**
     * Connection failed. Sets STATE_NONE.
     */
    private void connectionFailed() {
        setState(STATE_NONE);
    }
    
    //------------------------------------------------------------------------------
    
    /**
     * Connection lost. Sets STATE_NONE.
     */
    private void connectionLost() {
        setState(STATE_NONE);
    }
    
    //------------------------------------------------------------------------------
    
    /**
     * Sets the state and sends it to the handler.
     *
     * @param state the new state
     */
    private synchronized void setState(int state) {
        mState = state;
        if (mHandler != null) {
            mHandler.obtainMessage(NXTDriver.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
        } else {
            //XXX
        }
    }
    
    //------------------------------------------------------------------------------

    /**
     * The Class ConnectThread. Creates and connects to a sockect connection.
     */
    private class ConnectThread extends Thread {
        
        /** The mm socket. */
        private final BluetoothSocket mmSocket;
        
        /** The mm device. */
        private final BluetoothDevice mmDevice;
        
        /**
         * Instantiates a new connect thread.
         *
         * @param device the device
         */
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            String address = "ConnectThread";

            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run() {
            setName("ConnectThread");
            mAdapter.cancelDiscovery();
            
            try {
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
            
            synchronized (Speaker.this) {
                mConnectThread = null;
            }
            
            connected(mmSocket, mmDevice);
        }
        
        /**
         * Cancel.
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //------------------------------------------------------------------------------
    
    /**
     * The Class ConnectedThread. Creates the Input/Output buffers on the socket.
     */
    private class ConnectedThread extends Thread {
        
        /** The mm socket. */
        private final BluetoothSocket mmSocket;
        
        /** The mm in stream. */
        private final InputStream mmInStream;
        
        /** The mm out stream. */
        private final OutputStream mmOutStream;
        
        /** The buffer1. */
        byte[] buffer1 = new byte[1024];
        
        /**
         * Instantiates a new connected thread.
         *
         * @param socket the socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (true) {
            }
        }
        
        /**
         * Gets the _buffer1.
         *
         * @return the _buffer1
         */
        public byte[] get_buffer1() {

			return buffer1;
        }
        
        /**
 * Receive message to the input stream.
 *
 * @return the byte[]
 * @throws IOException Signals that an I/O exception has occurred.
 */
public byte[] receiveMessage() throws IOException {
            if (mmInStream == null)
                throw new IOException();
            int length = mmInStream.read();
            length = (mmInStream.read() << 8) + length;
            byte[] returnMessage = new byte[length];
            mmInStream.read(returnMessage);
            return returnMessage;
        } 
        
        /**
         * Write in the output stream.
         *
         * @param buffer the buffer (byte[])
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                // XXX?
            }
        }
        
        /**
         * Write in the output stream.
         *
         * @param buffer the buffer (byte)
         */
        public void write(byte buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                // XXX?
            }
        }
        
        /**
         * Cancel. Closes the socket.
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
	/* (non-Javadoc)
	 * @see icommand.nxt.comm.NXTComm#open()
	 */
	@Override
	public void open() throws Exception {
		
	}

	/* (non-Javadoc)
	 * @see icommand.nxt.comm.NXTComm#sendData(byte[])
	 */
	@Override
	public void sendData(byte[] request) {
		// TODO Auto-generated method stub
		
		int lsb = request.length;
	    int msb = request.length >>> 8;
	    
	    write((byte) lsb);
	    write((byte) msb);
	    write(request);
	}
    
    //------------------------------------------------------------------------------
    
    //ROBOT COMUNICATION
    
    //------------------------------------------------------------------------------

    
    /**
     * Write.
     *
     * @param msb the msb
     */
    private void write(byte msb) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(msb);
	}

	/**
	 * Write.
	 *
	 * @param out the out
	 */
	private void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        r.write(out);
    }
	
	/* (non-Javadoc)
	 * @see icommand.nxt.comm.NXTComm#close()
	 */
	@Override
	public void close() {
		ConnectedThread r;
		synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
		r.cancel();
	}
	
	/* (non-Javadoc)
	 * @see icommand.nxt.comm.NXTComm#readData()
	 */
	@Override
	public byte[] readData() {        
		
		ConnectedThread r;
		byte[] buffer = new byte[1024];
		
	    synchronized (this) {
	        if (mState == STATE_CONNECTED) {
		        r = mConnectedThread;
		        try {
					buffer = r.receiveMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
		return buffer;
	}
    
//    message[0] = DIRECT_COMMAND_NOREPLY; //0x80
//    message[1] = SET_OUTPUT_STATE; //0x04
//    message[2] = (byte) motor;
//    message[3] = (byte) speed; // Power set option (Range: -100 - 100)
//    message[4] = 0x03; // Mode byte (Bit-field): MOTORON + BREAK
//    message[5] = 0x01; // Regulation mode: REGULATION_MODE_MOTOR_SPEED
//    message[6] = 0x00; // Turn Ratio (SBYTE; -100 - 100
//    message[7] = 0x20; // RunState: MOTOR_RUN_STATE_RUNNING
//    message[8] = 0;
//    message[9] = 0;
//    message[10] = 0;
//    message[11] = 0;
    
    /**
     * Motors. Move the robot with the values of power for the right/left motors.
     *
     * @param l left motor power
     * @param r right motor power
     * @param speedReg the speed reg
     * @param motorSync the motor sync
     */
    public void motors(byte l, byte r, boolean speedReg, boolean motorSync) {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x03, 0x01, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                        0x0c, 0x00, (byte) 0x80, 0x04, 0x01, 0x32, 0x03, 0x01, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
       
        
        data[5] = l;
        data[19] = r;
        
        if (speedReg) {
            data[7] |= 0x01;
            data[21] |= 0x01;
        }
        if (motorSync) {
            data[7] |= 0x02;
            data[21] |= 0x02;
        }
        write(data);
    }

}