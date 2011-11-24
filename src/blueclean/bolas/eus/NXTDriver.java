/*
 * Unai Goñi Otegi / Università degli Studi di Padova / 2010-11
 */
package blueclean.bolas.eus;


import icommand.navigation.Pilot;
import icommand.nxt.CompassSensor;
import icommand.nxt.LightSensor;
import icommand.nxt.Motor;
import icommand.nxt.SensorPort;
import icommand.nxt.UltrasonicSensor;
import icommand.nxt.comm.NXTComm;
import icommand.nxt.comm.NXTCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import blueclean.bolas.eus.R;


//import org.openintents.sensorsimulator.hardware.Sensor;
//import org.openintents.sensorsimulator.hardware.SensorEvent;
//import org.openintents.sensorsimulator.hardware.SensorEventListener;
//import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * The Class NXTDriver.
 */
public class NXTDriver extends Activity implements SensorEventListener {
   
    /** The Constant REQUEST_ENABLE_BT. */
    private static final int REQUEST_ENABLE_BT = 1;
    
    /** The Constant REQUEST_CONNECT_DEVICE. */
    private static final int REQUEST_CONNECT_DEVICE = 2;
    
    /** The Constant REQUEST_SETTINGS. */
    private static final int REQUEST_SETTINGS = 3;
    
    //Handler states
    /** The Constant for describing connection STATE = STATE_NONE. */
    public static final int STATE_NONE = 0;
    
    /** The Constant DISTANCE. Sensor information received*/
    public static final int DISTANCE = 224;
    
    /** The Constant for describing STATE = TRAVELED. */
    public static final int TRAVELED = 229;
    
    /** The Constant for describing STATE = SCANNING. */
    public static final int SCANNING = 230;
    
    /** The Constant for describing STATE = SCAN_FINISHED. */
    public static final int SCAN_FINISHED = 240;
    
    /** The Constant for describing STATE = TRAVELLING. */
    public static final int TRAVELLING = 232;
    
    /** The Constant for describing STATE = TRAVEL_FINISHED. */
    public static final int TRAVEL_FINISHED = 241;
    
    /** The Constant for describing STATE = ROTATING. */
    public static final int ROTATING = 2355;
    
    /** The Constant for describing STATE = ROTATE_FINISHED. */
    public static final int ROTATE_FINISHED = 2415;
    
    /** The Constant for describing STATE = COMPASS_STARTED. */
    public static final int COMPASS_STARTED = 242;
    
    /** The Constant for describing STATE = SONIC_STARTED. */
    public static final int SONIC_STARTED = 243;
    
    /** The Constant for describing STATE = CALIB_START. */
    public static final int CALIB_START = 244;
    
    /** The Constant for describing STATE = CALIB_FIN. */
    public static final int CALIB_FIN = 245;
    
    /** The Constant LIGHT. Sensor information received  */
    public static final int LIGHT = 250;
    
    /** The Constant for describing STATE = LIGHT_STARTED. */
    public static final int LIGHT_STARTED = 251;
    
    /** The Constant DEGREES. Sensor information received */
    public static final int DEGREES = 225;
    
    /** The Constant describing connection STATE = MESSAGE_STATE_CHANGE. */
    public static final int MESSAGE_STATE_CHANGE = 2;

	//Menu Options
    /** The Constant to know the execution module MODE_CONFIG. */
	private static final int MODE_CONFIG = 1;
    
    /** The Constant to know the execution module MODE_VIEW. */
    private static final int MODE_VIEW = 2;
    
    /** The Constant to know the execution module MODE_SCAN. */
    private static final int MODE_SCAN = 3;
    
    /** The Constant to know the execution module MODE_INTRO. */
    private static final int MODE_INTRO = 4;
    
    //Bluetooth control
    /** BT Exists?. */
    private boolean NO_BT = false; 
    
    /** BT device address. */
    private String mDeviceAddress = null;
    
    /** The connection state. */
    private int mState = Speaker.STATE_NONE ;
    
    /** The bluetooth adapter. */
    private BluetoothAdapter mBluetoothAdapter;
    
    /** The connect button. */
    private Button mConnectButton;
    
    /** The disconnect button. */
    private Button mDisconnectButton;
    
    /** The state text. */
    private TextView mStateDisplay;
    
    /** The speaker instance. */
    private static Speaker mSpeaker;
    
    /** The motor power. */
    private int mPower = 60;
    
    //Acelemometer values
	/** The m accel x. */
    private float mAccelX = 0;
	
	/** The m accel y. */
	private float mAccelY = 0;
	
	/** The m accel z. */
	private float mAccelZ = 0;
	
    /** The azimuth. */
    private float azimuth;
    
    /** The pitch. */
    private float pitch;
    
    /** The roll. */
    private float roll;
    
    //Sensor Manager & Simulator
    /** The sensor manager. */
    private SensorManager mSensorManager;
    //private SensorManagerSimulator mSensorManager;

    //Checkboxes & Texts
	/** The mmotor check. */
    private CheckBox mmotorCheck;
    
    /** The motor text. */
    private TextView orientaText;
	
	/** The Ultrasonic check. */
	private CheckBox mdistCheck;
    
    /** The Ultrasonic text. */
    private TextView mUsText;
	
	/** The compass check. */
	private CheckBox mcompassCheck;
    
    /** The compass text. */
    private TextView compassText;

	/** The light check. */
	private CheckBox mlightCheck;
    
    /** The light text. */
    private TextView lightText;
	
	/** The calibrate check. */
	private CheckBox mCaliCheck;
	
	//Sensor, Respective Threads & variables
	
	//UltrasonicSensor
    /** The UltrasonicSensor. */
	private UltrasonicSensor mUsSensor;
    
    /** The distance thread. */
    private DistanceThread mDistanceThread;
	
	/** The distance variable to save the sensor info. */
	private int distance;
	
	/** The init_ultrasonic. To know if sensor is started  */
	protected boolean init_ultrasonic = false;
	
	/** is the first_sonic_read?. */
	public boolean first_sonic_read = true;

	//CompassSensor
	/** The CompassSensor. */
	private CompassSensor mCompass;
	
	/** The compass thread. */
	private CompassThread mCompassThread;
	
	/** The degrees variable to save the compass sensor info. */
	public double degrees;
	
	/** The init_compass. To know if sensor is started */
	private boolean init_compass = false;
	
	/** is The first_compass_read?. */
	public boolean first_compass_read = true;

	/** The calibrating thread. */
	public calibThread mcalibThread;
	
	/** Compass is calibrating?. */
	public boolean compCalibrating;

	//LightSensor
	/** The LightSensor. */
	private LightSensor mLSensor;
	
	/** The light thread. */
	private LightThread mLightThread;
	
	/** The variable to save the light sensor info. */
	private int light;
	
	/** The init_light. To know if sensor is started */
	protected boolean init_light = false;
	
	/** is the first_light_read?. */
	public boolean first_light_read = true ;

	//Menu
    /** The Menu. */
	private Menu mMenu;
    
    /** variable to save execution module. */
    private int mControlsMode = MODE_CONFIG;
	
	//View Objects
	/** The my view. */
	InternalView myView;
	
	/** The my view2. */
	InternalView2 myView2;
	//First onDraw
	/** The view_init?. */
	public static boolean view_init = true;
	
	//Scanning control
	/** scan started?. */
	protected boolean init_scan = false;
	
	/** is first_scan?. */
	private boolean first_scan = true;
	
	/** The scan thread. */
	private scanThread mscanThread;
	
	/** The counter. */
	private int kont;
	
	/** The robot angle. */
	private int angle;
	
	/** The scaled positions array. */
	private ScanInfo dist_array[];
	
	/** The scaned info. */
	private ScanPos pos_array[];

	//Robot moving control
    /** The motor pilot. */
	private Pilot mPilot;
	
	/** The travel thread. */
	public travelThread mtravelThread;
	
	/** The mrotate thread. */
	public rotateThread mrotateThread;
	
    //Robot action time control (no used)
	/** The last time robot was moved . */
    private long prev_time;
	
	/** Actual time. */
	private long act_time;
	
	//Scan time control
	/** Scan start_time. */
	protected long start_time;
	
	//Waiting Dialogs
	/** The scan_dialog. */
	protected ProgressDialog scan_dialog;
	
	/** The travel_dialog. */
	protected ProgressDialog travel_dialog;
	
	/** The compass_dialog. */
	private ProgressDialog compass_dialog;
	
	/** The calibration_dialog. */
	private ProgressDialog calibration_dialog;
	
	/** The ultrasonic_dialog. */
	private ProgressDialog ultrasonic_dialog;
	
	/** The light_dialog. */
	private ProgressDialog light_dialog;
	
	//Painting state changes
	/** is scanning?. */
	private boolean isScanning = false;
	
	/** Inside ScanMode, is angle_selecting?. */
	public boolean angle_selecting = false;
	
	/** Inside ScanMode, is distance_selecting?. */
	public boolean distance_selecting = false;
	
	/** Inside ScanMode, is seeing?. */
	public boolean isSeeing = false;
	
	/** Inside ScanMode, is fin?. */
	public boolean isFin = false;
	
	/** Inside ScanMode,  last movement is move?. */
	public boolean last_isMove = false;
	
	/** Don't paint more on isSeeing state. */
	public boolean stopSeeing = false;
	
	/** Inside ScanMode,is_finish?. */
	private boolean is_finish = false;

	
	//Bitmaps
	/** The angle_mob Bitmap. */
	public Bitmap angle_mob;
	
	/** The instrucs1 Bitmap. */
	public Bitmap instrucs1;
	
	/** The instrucs2 Bitmap. */
	public Bitmap instrucs2;
	
	/** The scan_img Bitmap. */
	public Bitmap scan_img;
	
	/** The scan_disable Bitmap. */
	public Bitmap scan_disable;
	
	/** The select_img Bitmap. */
	public Bitmap select_img;
	
	/** The robot Bitmap. */
	private Bitmap robot;
	
	/** The regla Bitmap. */
	public Bitmap regla;
	
	/** The protractor Bitmap. */
	public Bitmap protractor;
	
	/** The move button Bitmap. */
	public Bitmap move;

	/** Angle selected by the user to move the robot. */
	public float roll2;
	
	/** Distance selected by the user to move the robot. */
	public float pitch2;
	
	//Obstacles & Robot positioning variables
	
	/** The robot_x. */
	public float robot_x = 0;
	
	/** The robot_y. */
	public float robot_y = 0;
	
	/** The scaled robot angle. */
	public float robo_angle;
	
	/** The scaled robot distance. */
	public float robo_distance = 0;
	
	/** The canvas_width. */
	public int canvas_width;
	
	/** The canvas_height. */
	public int canvas_height;
	
	/** The angle_x. */
	public double angle_x;
	
	/** The angle_y. */
	public double angle_y;
	
	/** The line_y. */
	public float line_y;

	/** The robot_position scaled x. */
	public int robot_sx;
	
	/** The robot_position scaled y. */
	public int robot_sy;
	
	/** The robot previous scaled x position. */
	public int robot_sx_prev;
	
	/** The robot previous scaled y position. */
	public int robot_sy_prev;
	
	//Intro Button
	/** The intro button. */
	private Button mIntroButton;
	
	//Robot turn angle correction
	/** angle correction - start degress. */
	private double startdeg;
	
	/** angle correction - finish degress. */
	private double findeg;
	
	/** degrees difference. */
	private double dif;
	
	/** angle correction - rotating start degress. */
	public double angle_startdeg;
	
	/** rotating degrees difference. */
	public double angle_dif;
	
	/** angle correction - rotating finish degress. */
	public double angle_findeg;
	
    //Program function variables
	/** the app is writing on the robot socket?. */
    private boolean isWriting = false;
	
	/** is the first init?. */
	private boolean first_init = true;
	
	//Wakelock to hold the screen (not working)
//	private int defTimeOut = 0;
//	private static final int DELAY = 3000;
    
	/** The representation scale. */
	private int scale = 255;
	
	/** The maximun detected distance value . */
	private int max;
	
	/** The rotate_dialog. */
	protected ProgressDialog rotate_dialog;

	//Digital filter values
	public double new_angle;
	public double old_angle;
	public double new_distance;
	public double old_distance;
	public double alpha = 0.5d;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        mMenu = menu;
        return true;
    }
	
    /**
     * Updates the menu options when called.
     *
     * @param disabled the disabled
     */
    private void updateMenu(int disabled) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menuitem_config).setEnabled(disabled != R.id.menuitem_config).setVisible(disabled != R.id.menuitem_config);
            mMenu.findItem(R.id.menuitem_image).setEnabled(disabled != R.id.menuitem_image).setVisible(disabled != R.id.menuitem_image);
            mMenu.findItem(R.id.menuitem_scan).setEnabled(disabled != R.id.menuitem_scan).setVisible(disabled != R.id.menuitem_scan);
        }
    }
    


    /* (non-Javadoc)
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuitem_config:
        	
            mControlsMode = MODE_CONFIG;
            setupUI();
            
            mmotorCheck.setChecked(false);
            mdistCheck.setChecked(false);
            mcompassCheck.setChecked(false);
            mlightCheck.setChecked(false);
            
            break;
            
        case R.id.menuitem_scan:
        	if (mState == Speaker.STATE_CONNECTED){
        		
	        	if (!init_ultrasonic || !init_compass || !init_light ){
	        		Toast.makeText(NXTDriver.this, "UltraSonic not initialized?\nLightSensor not initialized?\nCompass not initialized?\nCheck the Checkboxes", Toast.LENGTH_SHORT).show();
	        	}else if(init_ultrasonic){
	                mControlsMode = MODE_SCAN;
	            	setupUI();
	            	
		            mmotorCheck.setChecked(false);
		            mdistCheck.setChecked(false);
		            mcompassCheck.setChecked(false);
		            mlightCheck.setChecked(false);
		            
		            dist_array = new ScanInfo [13];
		            pos_array = new ScanPos [13];
		            light = 0;
	        	}
	        	
        	}else{
        		Toast.makeText(this, "Not connected to NXT", Toast.LENGTH_SHORT).show();
        	}
            break;
            
        case R.id.menuitem_image:
        	
        	if (mState == Speaker.STATE_CONNECTED){
        		
            	if (!init_ultrasonic){
            		
            		Toast.makeText(NXTDriver.this, "UltraSonic not initialized", Toast.LENGTH_SHORT).show();
            		
            	}else if(init_ultrasonic){
                	
                	mControlsMode = MODE_VIEW;
                	setupUI();
                	
            		mmotorCheck.setChecked(true);
                	mdistCheck.setChecked(true);
            	}
        	}else{
        		Toast.makeText(this, "Not connected to NXT", Toast.LENGTH_SHORT).show();
        	}

            break;
        case R.id.menuitem_exit:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Are you sure you want to exit?")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
	                		mSpeaker.motors((byte)0,(byte) 0, false, false);
	                        mSpeaker.stop(); 
        	        	   finish();
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                dialog.cancel();
        	           }
        	       });
        	AlertDialog alert = builder.create();
        	alert.show();
        	
            break;
        default:
            return false;    
        }
        return true;
    }
    
    /**
     * According to selected mode, the necessary objects are initialized and displayed.
     */
    private void setupUI() {

    	if (mControlsMode == MODE_CONFIG) {
    		
	            setContentView(R.layout.main);
	            updateMenu(R.id.menuitem_config);
	            
	            mStateDisplay = (TextView) findViewById(R.id.state_display);
	            
	            mUsText = (TextView) findViewById(R.id.us_text);
	            orientaText = (TextView) findViewById(R.id.orienta_text);
	            
	            compassText = (TextView) findViewById(R.id.deg_text);
	            lightText = (TextView) findViewById(R.id.light_text);
	            
	            mmotorCheck = (CheckBox) findViewById(R.id.motorBox);
	            mdistCheck = (CheckBox) findViewById(R.id.distBox);
	            
	            
	            mdistCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						if ( isChecked )
				        {
							if (init_ultrasonic){
				    			mDistanceThread = new DistanceThread(mUsSensor);
				    			mDistanceThread.start();
							}
				        }
					}
				});
	            
	            mcompassCheck = (CheckBox) findViewById(R.id.compassBox);
	            mcompassCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						if ( isChecked )
				        {
							if (init_compass){
				    	        mCompassThread = new CompassThread(mCompass);
				    	        mCompassThread.start();

							}
						}
					}
				});

	            mlightCheck = (CheckBox) findViewById(R.id.lightBox);
	            mlightCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						if ( isChecked )
				        {
							if (init_light ){
								
				    			mLightThread = new LightThread();
				    			mLightThread.start();
							}
				        }
					}
				});
	            
	            mCaliCheck = (CheckBox) findViewById(R.id.caliBox);
	            mCaliCheck.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	
	                	mCaliCheck.setVisibility(View.GONE);
	                	
	                	mcalibThread = new calibThread();
	                	mcalibThread.start();
	                }
	            });

	    	//BUTTONS
	            
	            mConnectButton = (Button) findViewById(R.id.connect_button);
	            mConnectButton.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    if (!NO_BT) {
	                        findBrick();
	                    } else {
	                        mState = Speaker.STATE_CONNECTED;
	                        displayState();
	                    }
	                }
	            });
	            
	            mDisconnectButton = (Button) findViewById(R.id.disconnect_button);
	            mDisconnectButton.setOnClickListener(new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	while (isWriting){
	                	}
	            		//if (!isWriting && mmotorCheck.isChecked()){
	            		if (!isWriting){

	            			isWriting  = true;
	                		mSpeaker.motors((byte)0,(byte) 0, false, false);
	                        mSpeaker.stop();
	            			isWriting  = false;
	            			
		                    mmotorCheck.setEnabled(false);
		                    mdistCheck.setEnabled(false);
		                    mcompassCheck.setEnabled(false);
		                    mCaliCheck.setEnabled(false);
		                    mlightCheck.setEnabled(false);
	            			
	            		}

	                    
	                }
	            });
	            
	            SeekBar powerSeekBar = (SeekBar) findViewById(R.id.power_seekbar);
	            powerSeekBar.setProgress(mPower);
	            powerSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	                @Override
	                public void onProgressChanged(SeekBar seekBar, int progress,
	                        boolean fromUser) {
	                    mPower = progress;
	                }

	                @Override
	                public void onStartTrackingTouch(SeekBar seekBar) {
	                }

	                @Override
	                public void onStopTrackingTouch(SeekBar seekBar) {
	                }            
	            });
	            
	            displayState();
	            
	            if(first_init){
	            	
		            mmotorCheck.setChecked(false);
		            mdistCheck.setChecked(false);
		            mcompassCheck.setChecked(false);
		            mlightCheck.setChecked(false);
		            
		            first_init=false;
 	               
		            setContentView(R.layout.main2);
		            
		            mIntroButton = (Button) findViewById(R.id.introbutton);
		            mIntroButton.setOnClickListener(new OnClickListener() {
		                @Override
		                public void onClick(View v) {
		                	setupUI();
		                	
		                    mmotorCheck.setEnabled(false);
		                    mdistCheck.setEnabled(false);
		                    mcompassCheck.setEnabled(false);
		                    mCaliCheck.setEnabled(false);
		                    mlightCheck.setEnabled(false);
		                }
		            });
		            
	            }
	            
    	}
    	
    	if (mControlsMode == MODE_VIEW) {
            	setContentView(myView);
            	updateMenu(R.id.menuitem_image);
    	}
    	
    	if (mControlsMode == MODE_SCAN) {
    		
    		isSeeing = true;
        	angle_selecting = false;
        	distance_selecting=false;
        	isScanning = false;
        	last_isMove = false;
        	
        	first_scan = true;
        	
        	setContentView(myView2);
        	updateMenu(R.id.menuitem_scan);
    	}
	}
    

    /**
     * Returns Speaker instance.
     *
     * @return the speaker
     */
	public Speaker getSpeaker() {
        return mSpeaker;
    }


    /**
     * Starts "ChooseDeviceActivity" Intent to select the BT element to connect.
     *
     */
    private void findBrick() {
        Intent intent = new Intent(this, ChooseDeviceActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }
    

    /**
     * Displays the connection state on the corresponding TextView.
     */
    private void displayState() {
        String stateText = null;
        int color = 0;
        switch (mState){ 
        case Speaker.STATE_NONE:
            stateText = "Not connected";
            color = 0xffff0000;
            mConnectButton.setVisibility(View.VISIBLE);
            mDisconnectButton.setVisibility(View.GONE);
            setProgressBarIndeterminateVisibility(false);
            
        	mmotorCheck.setChecked(false);
            mdistCheck.setChecked(false);
            mcompassCheck.setChecked(false);
            break;
            
        case Speaker.STATE_CONNECTING:
            stateText = "Connecting...";
            color = 0xffffff00;
            mConnectButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.GONE);
            setProgressBarIndeterminateVisibility(true);
            break;
            
        case Speaker.STATE_CONNECTED:
            stateText = "Connected";
            color = 0xff00ff00;
            mConnectButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.VISIBLE);
            setProgressBarIndeterminateVisibility(false);
            
        }
        mStateDisplay.setText(stateText);
        mStateDisplay.setTextColor(color);
    }



    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
            	
            } else {
                Toast.makeText(this, "Bluetooth not enabled, exiting.", Toast.LENGTH_LONG).show();
                finish();
            }
            break;
        case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(ChooseDeviceActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                Toast.makeText(this, "Connecting to NXT: " + address, Toast.LENGTH_SHORT ).show();
                mDeviceAddress = address;
                mSpeaker.connect(device);
            }
            break;
        case REQUEST_SETTINGS:
            //XXX?
            break;
        }
    }
    



    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!NO_BT) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }
    
    

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        if (!NO_BT) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }    
        
        mSpeaker = new Speaker(mHandler);

        
        //mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //mSensorManager.connectSimulator();
        
        setupUI();
        
        mPilot = new Pilot(5.6f,11.5f,Motor.B, Motor.C,true);
        
        prev_time = System.currentTimeMillis();
        
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("NXTDriver 1.0 'Rodeo'\n\nUniPD - DEI - Padova \n\n2011-07 Unai Goñi Otegi")
    	       .setCancelable(false)
    	       .setPositiveButton("Start", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	alert.show();
    	
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        
    	//Wakelock to hold the screen
        
//        Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, DELAY);

    }
    


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE), 
        		SensorManager.SENSOR_DELAY_FASTEST);
	}
    

   
    /** The m handler. Displays the sensor information / When connected start all sensors */
    private Handler mHandler = new Handler() {

		@Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                mState = msg.arg1;
                displayState();
                if (mState == Speaker.STATE_CONNECTED){
                    
                    NXTCommand.open();
                    
                    NXTCommand.getSingleton().keepAlive();
                    
                    myView = new InternalView(NXTDriver.this);
                    myView2 = new InternalView2(NXTDriver.this);
                    
                    mmotorCheck.setEnabled(true);
                    mdistCheck.setEnabled(true);
                    mcompassCheck.setEnabled(true);
                    mlightCheck.setEnabled(true);
                    
                    initUltraSonic();
	    	        initCompass();
					mCaliCheck.setEnabled(true);
					initLightSensor();

                }
                break;
            case DISTANCE:
            	distance = msg.arg1;
            	mUsText.setText("Distance: " + distance );
            	
            	if (mControlsMode == MODE_VIEW){
                	setContentView(myView);
            	}
            	
            	break;
            case DEGREES:
            	degrees = msg.arg1;
            	compassText.setText("Degrees: " + degrees);
            	break;
            case LIGHT:
            	int light = msg.arg1;
            	lightText.setText("Light: " + light);
            	break;
            case TRAVELED:
            	int traveled = msg.arg1;
            	mUsText.setText("Traveled: " + traveled );
            	
            	break;
            case SCANNING:
            	
            	mmotorCheck.setChecked(false);
                mdistCheck.setChecked(false);
                mcompassCheck.setChecked(false);
                
                scan_dialog = ProgressDialog.show(NXTDriver.this, "", 
                        "Scanning. Please wait...", true);
                
            	break;
            case SCAN_FINISHED:
            	
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = true;
                	isSeeing = false;
                	last_isMove = false;
                	
                	if (first_scan) {
                		
                		start_time= System.currentTimeMillis();
                		first_scan = false;
                	}
                	scan_dialog.dismiss();
                    setContentView(myView2);
                    
            	break;
            case TRAVELLING:
                
                travel_dialog = ProgressDialog.show(NXTDriver.this, "", 
                        "Moving the robot. Please wait...", true);
                
            	break;
            case TRAVEL_FINISHED:
            	
            	if (!is_finish){
            		
        	        isSeeing = true;
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = false;
                	
                	travel_dialog.dismiss();
                    setContentView(myView2);
                    
            	}else{
            		
	            	angle_selecting = false;
	            	distance_selecting=false;
	            	isScanning = false;
	            	isSeeing = false;
	            	last_isMove = false;
	            	stopSeeing  = false;
	            	isFin = true;
	            	
	            	is_finish = false;
	            	travel_dialog.dismiss();
	            	setContentView(myView2);
                	}
            	break;
            case ROTATING:
                
                rotate_dialog = ProgressDialog.show(NXTDriver.this, "", 
                        "Rotating the robot. Please wait...", true);
                
            	break;
            case ROTATE_FINISHED:
            		
	            	angle_selecting = false;
	            	distance_selecting=true;
	            	isScanning = false;
	            	isSeeing = false;
                	
                	rotate_dialog.dismiss();
                    setContentView(myView2);
                    
                 break;
            case COMPASS_STARTED:
            	compass_dialog.dismiss();
            	break;
            case SONIC_STARTED:
            	ultrasonic_dialog.dismiss();
            	break;
            case LIGHT_STARTED:
            	light_dialog.dismiss();
            	break;
            case CALIB_START:
                calibration_dialog = ProgressDialog.show(NXTDriver.this, "", 
                        "Calibrating Compass Sensor. Please wait...", true);
            	break;
            case CALIB_FIN:
                calibration_dialog.dismiss();
            	break;
            }
        }
    };

	/**
	 * Gets the single instance of Speaker.
	 *
	 * @return single instance of Speaker
	 */
	public static NXTComm getInstance() {
		return mSpeaker;
	}

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		/** orientation (tilt) readings */
		
		//int sensor = event.type;
		int sensor = event.sensor.getType();

		
		float[] values = event.values;
		switch(sensor) {
		case Sensor.TYPE_ACCELEROMETER:
			
			mAccelX = 0 - event.values[2];
			mAccelY = 0 - event.values[1];
			mAccelZ = event.values[0];
			
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			break;
			
		case Sensor.TYPE_ORIENTATION:
			orientaText.setText("Orient: " 
					+ values[0] + ", " 
					+ values[1] + ", "
					+ values[2]);
			
			azimuth = event.values[0];     // azimuth
            pitch = event.values[1];     // pitch
            roll = event.values[2];        // roll
            
            switch (mState){ 
            case Speaker.STATE_NONE:
            	
            	if (!isWriting  && mmotorCheck.isChecked()){
        			isWriting  = true;
                	mSpeaker.motors((byte)0, (byte)0, false, false);
        			isWriting  = false;
        		}

                break;
                
            case Speaker.STATE_CONNECTED:
            	if (mControlsMode == MODE_SCAN){
            		if (!isFin){
            			setContentView(myView2);
            		}
            	}else{       	
            		doMotorMovement(pitch, -roll);
            	}

                break;
            }
            
			break;
		}
	}



	/**
	 * Do motor movement according pitch /roll values
	 *
	 * @param pitch the pitch
	 * @param roll the roll
	 */
	public void doMotorMovement(float pitch, float roll) {
		
		int left = 0;
		int right = 0;
		float left2 = 0;
		float right2 = 0;
		float pow2 = 0;
		
		// only when phone is little bit tilted
		if ((Math.abs(pitch) > 10.0) || (Math.abs(roll) > 10.0)) {
			
			// limit pitch and roll
			if (pitch > 33.3){
				pitch = (float) 33.3;
			}else if (pitch < -33.3){
				pitch = (float) -33.3;}

			if (roll > 22.2){
				roll = (float) 22.2;
			}else if (roll < -22.2){
				roll = (float) -22.2;
			}


			// when pitch is very small then do a special turning function    
			if (Math.abs(pitch) > 10.0) {
				
				left = (int) Math.round(3.3 * pitch * (1.0 + roll / 60.0));
				right = (int) Math.round(3.3 * pitch * (1.0 - roll / 60.0));
				
			} 
			else {
				left = (int) Math.round(3.3 * roll - Math.signum(roll) * 3.3 * Math.abs(pitch));
				right = -left;
			}
						

			left2 = (float) (left * (mPower / 100.0));
			right2 = (float) (right * (mPower/100.0));
			pow2 = (float) (mPower/100.0);
			
			
			// limit the motor outputs
			if (left2 > 100)
				left2 = 100;
			else if (left2 < -100)
				left2 = -100;

			if (right2 > 100)
				right2 = 100;
			else if (right2 < -100)
				right2 = -100;

		}
		
        byte l = (byte) ((int)left2);
        byte r = (byte) ((int)right2);
		
		if (mmotorCheck.isChecked()){
				
				act_time = System.currentTimeMillis();
				
//				if (act_time > (prev_time + 200)){
					if (!isWriting){
						isWriting  = true;
							mSpeaker.motors(l, r, false, false);
//							prev_time = System.currentTimeMillis();
						isWriting  = false;
				}
					
//Another way to move the motors
					
//					mPilot._left.setSpeed(l*9);
//					mPilot._right.setSpeed(r*9);
//					mPilot._left.forward();
//					mPilot._right.forward();
		}


	}
	
	//Compass Sensor


    /**
	 * The Class CompassThread. Takes info for the compass sensor to display it at the corresponding TextView
	 */
	private class CompassThread extends Thread {

    	/** The mm compass. */
	    private final CompassSensor mmCompass;
    	
	    /**
    	 * Instantiates a new compass thread.
    	 *
    	 * @param mCompass the m compass
    	 */
    	public CompassThread(CompassSensor mCompass) {
	    	mmCompass = mCompass;
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	
	    	while(true){
	    		
	    		if (!isWriting && (mState == Speaker.STATE_CONNECTED)&& (mcompassCheck.isChecked()) && (init_compass)){
	    			
	    			try {	
	    					if (!compCalibrating){
	    						isWriting  = true;
	    						degrees = mmCompass.getDegrees();
	    						isWriting  = false;
	    					}
	    					
	    					
		    		} catch (Exception e) {
						//Se ha perdido la conexion con el robot?
						mState = Speaker.STATE_NONE;
						mHandler.obtainMessage(NXTDriver.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
					}
	    		}
	    		mHandler.obtainMessage(NXTDriver.DEGREES, (int) degrees, -1).sendToTarget();
	    		
	    		try {
	    			Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				if (first_compass_read){
//					mHandler.obtainMessage(NXTDriver.COMPASS_STARTED, 1, -1).sendToTarget();
//					first_compass_read = false;
//				}
				
			}
	    }
  }
    
 
    /**
     * Inits the compass.
     */
    private void initCompass() {
    	
		if (!init_compass){
            compass_dialog = ProgressDialog.show(NXTDriver.this, "", 
                    "Initializing Compass Sensor. Please wait...", true);
        	while (isWriting){
        	}
        	if (!isWriting){
    			isWriting  = true;
					
	    	        mCompass = new CompassSensor(SensorPort.S2);
	    	        mCompass.setRegion(mCompass.EU_MODE);
	    	        
					init_compass = true;
	    			isWriting  = false;
	    			
	    			mHandler.obtainMessage(NXTDriver.COMPASS_STARTED, 1, -1).sendToTarget();
        	}
		}
    }
    

    /**
     * Calibrates compass.
     */
    private void calibrateCompass() {
    	
    	while (isWriting){
    	}
    	if (!isWriting){
			isWriting  = true;
			
		    	if (init_compass){
		    		compCalibrating = true;
		    		
		    		mCompass.startCalibration();
		    		mPilot.setSpeed(50);
		    		mPilot.rotate(720);
		            mCompass.stopCalibration();
		            
		            compCalibrating = false;
		    	}
		    	
			isWriting  = false;
    	}
    }
    

    /**
     * The Class calibThread. Calibrates compass
     */
    private class calibThread extends Thread {
    	
    	/**
	     * Instantiates a new calib thread.
	     */
	    public calibThread() {
		}

	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	
	    	mHandler.obtainMessage(NXTDriver.CALIB_START, 1, -1).sendToTarget();

        	calibrateCompass();
        	
	    	mHandler.obtainMessage(NXTDriver.CALIB_FIN, 1, -1).sendToTarget();

	    }
  }
    
    //UltraSonic Sensor
    

    /**
     * The Class DistanceThread. Takes info for the sensor (UltraSonic) to display it at the corresponding TextView
     */
    private class DistanceThread extends Thread {
        
    	/** The mm us sensor. */
	    private final UltrasonicSensor mmUsSensor;
		
    	
	    /**
    	 * Instantiates a new distance thread.
    	 *
    	 * @param mUsSensor the m us sensor
    	 */
    	public DistanceThread(UltrasonicSensor mUsSensor) {
	    	mmUsSensor = mUsSensor;
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	int distance = 0;
	    	while(true){
	    		
	    		if (!isWriting && mdistCheck.isChecked() && (mState == Speaker.STATE_CONNECTED)){
	    			isWriting  = true;
	    			try {
	    				distance = mmUsSensor.getDistance();
					} catch (Exception e) {
						//Se ha perdido la conexion con el robot?
						mState = Speaker.STATE_NONE;
						mHandler.obtainMessage(NXTDriver.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
					}
	    			isWriting  = false;
	    		}

	            mHandler.obtainMessage(NXTDriver.DISTANCE, distance, -1).sendToTarget();
	    		
	    		try {
	    			Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
//	    		if (first_sonic_read){
//	    			mHandler.obtainMessage(NXTDriver.SONIC_STARTED, 1, -1).sendToTarget();
//	    			first_sonic_read = false;
//	    		}
			}
	    }
  }
    
 
    /**
     * Inits the ultra sonic sensor.
     */
    private void initUltraSonic() {
    	
		if (!init_ultrasonic){
			ultrasonic_dialog = ProgressDialog.show(NXTDriver.this, "", 
                    "Initializing Ultrasonic Sensor. Please wait...", true);
        	while (isWriting){
        	}
        	if (!isWriting){
    			isWriting  = true;
    			
	    			mUsSensor = new UltrasonicSensor(SensorPort.S4);
	    			isWriting  = false;
        	}
            init_ultrasonic = true;
            mHandler.obtainMessage(NXTDriver.SONIC_STARTED, 1, -1).sendToTarget();
		}
    }
    
    //Light Sensor
    

    /**
     * Inits the light sensor.
     */
    private void initLightSensor() {
    	
		if (!init_light){
			light_dialog = ProgressDialog.show(NXTDriver.this, "", 
                    "Initializing Light Sensor. Please wait...", true);
        	while (isWriting){
        	}
        	if (!isWriting){
    			isWriting  = true;
	    			mLSensor = new LightSensor(SensorPort.S1);
	    			isWriting  = false;

        	}
            init_light = true;
		}
		mLSensor.passivate();
		mHandler.obtainMessage(NXTDriver.LIGHT_STARTED, 1, -1).sendToTarget();
    }
    

    /**
     * The Class LightThread. Takes info for the light sensor to display it at the corresponding TextView
     */
    private class LightThread extends Thread {
    	
	    /**
    	 * Instantiates a new light thread.
    	 */
    	public LightThread() {
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	int light = 0;
	    	while(true){
	    		
	    		if (!isWriting && mlightCheck.isChecked() && (mState == Speaker.STATE_CONNECTED)){
	    			isWriting  = true;
	    			try {
	    				light = mLSensor.getLightPercent();
					} catch (Exception e) {
						//Se ha perdido la conexion con el robot?
						mState = Speaker.STATE_NONE;
						mHandler.obtainMessage(NXTDriver.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
					}
	    			isWriting  = false;
	    		}
	    		mHandler.obtainMessage(NXTDriver.LIGHT, light, -1).sendToTarget();
	    		
	    		try {
	    			Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
//	    		if (first_light_read){
//	    			mHandler.obtainMessage(NXTDriver.LIGHT_STARTED, 1, -1).sendToTarget();
//	    			first_light_read = false;
//	    		}
			}
	    }
	}
    

	/**
	 * Light control. Takes LightPercent to decide if we are on the finish.
	 */
	public void lightControl() {
		
		mLSensor.activate();
		light = mLSensor.getLightPercent();
		mLSensor.passivate();
		if (light > 95) {
			is_finish = true;
		}
	}
    
    //Scan
    

    /**
     * Do scan. Do the scan to know how many object surround the Robot in a 180 degrees range.
     */
    private void doScan(){
        
    	NXTCommand.getSingleton().keepAlive();
    	
    	kont = 0;
    	angle = 0;
    	
    	mHandler.obtainMessage(NXTDriver.SCANNING, 1, -1).sendToTarget();
	    	
    		if (first_scan) {
	    		startdeg = mCompass.getDegrees();
	    	}
    		
            mPilot.setSpeed(100);
            
            mPilot.resetTachoCount();
            
			init_scan = true;
			
            kont = 0;
            
            mPilot.rotate(90);
            
            max = 0;
            
            while (kont<13){
            	
            	if (!isWriting && (mState == Speaker.STATE_CONNECTED)){
        			isWriting  = true;
        			try {
        				angle = mPilot.getAngle();
        				
        				distance = mUsSensor.getDistance();
        				
        				if (distance == 255) distance = 2000;
        				
        				if (distance > max && distance != 2000) max = distance;
        				
        				ScanInfo mSI = new ScanInfo(distance, angle);
        				dist_array[kont] = mSI;

                        mPilot.rotate(-15);

    				} catch (Exception e) {
    					//Se ha perdido la conexion con el robot?
    					mState = Speaker.STATE_NONE;
    					mHandler.obtainMessage(NXTDriver.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
    				}
        			isWriting  = false;
        			kont++;
            	}
            }
            
            mPilot.rotate(90);
            
//            for (int i = 0; i < 13; i++) {
//            	//info1Text.setText(info1Text.getText() + "/" + dist_array[i].mdistance + "," + dist_array[i].mangle);
//			}
            
            findeg = mCompass.getDegrees();
            
			if ((int)startdeg > (int)findeg){
        		dif = startdeg - findeg;
        		mPilot.rotate(-((int)dif));
        	}else if ((int)startdeg < (int)findeg){
        		dif = findeg - startdeg;
        		mPilot.rotate((int) dif);
        	}
			scanAngleCorrect();
			
            scale = (int) (max * 2.5f);
			
            if (scale > 255) scale = 255;
            
            scaled_positions();
            
            mHandler.obtainMessage(NXTDriver.SCAN_FINISHED, 1, -1).sendToTarget();
  }
    

    /**
     * Correct the robot inaccuracy returning to the start position when scan is finished.
     */
    private void scanAngleCorrect() {
    	
    	//Angle correction
    	double startdeg2;
    	double dif2 = 0;
    	
    	startdeg2 = mCompass.getDegrees();
    	
    	if ((int)startdeg > (int)startdeg2){
    		dif2 = startdeg - startdeg2;
    	}else if ((int)startdeg < (int)startdeg2){
    		dif2 = startdeg2 - startdeg;
    	}
    	
    	while (dif2 > 4){
    		
        	if ((int)startdeg > (int)startdeg2){
        		mPilot.rotate(-((int)dif2));
        	}else if ((int)startdeg < (int)startdeg2){
        		mPilot.rotate((int) dif2);
        	}
        	
        	startdeg2 = mCompass.getDegrees();
        	
        	if ((int)startdeg > (int)startdeg2){
        		dif2 = startdeg - startdeg2;
        	}else if ((int)startdeg < (int)startdeg2){
        		dif2 = startdeg2 - startdeg;
        	}else dif2 = 0;

    	}
		
	}


    /**
     * Calculate the scaled positions of the obstacles to draw them on the screen.
     */
    private void scaled_positions() {
    	
		int m_angle = 0;
		int m_distance = 0;
        
		for (int i = 0; i < 13; i++) {
			
			m_angle = dist_array[i].mangle;
			m_distance = dist_array[i].mdistance;
			
			m_angle = 90 - m_angle;
			
			double h = m_distance; //hipotenusa
			double h2 = (m_distance * canvas_height) / scale;
			double sin = Math.sin(Math.toRadians(m_angle));
			double cos = Math.cos(Math.toRadians(m_angle));
			
			double x =  (cos * h2); //Albokoa
			double sx = x + (canvas_width/2);
			
			double y =  (sin * h2); //Aurkakoa
			double sy = canvas_height-y-2.5;
			
			ScanPos mSP = new ScanPos(sx, sy);
			
			pos_array[i] = mSP;
		}
	}
    

    /**
     * The Class scanThread. Calls doScan()
     */
    private class scanThread extends Thread {
    	
    	/**
	     * Instantiates a new scan thread.
	     */
	    public scanThread() {
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	doScan();
	    }
  }
	
//Distance View
    

	/**
 * The Class InternalView. To paint everything on the Module DistanceView.
 */
private class InternalView extends View{
        
        /**
         * Instantiates a new internal view.
         *
         * @param context the context
         */
        public InternalView(Context context){
            super(context);
        }
 
        /* (non-Javadoc)
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas) {
        	
            super.onDraw(canvas);
            Paint paint = new Paint();
            
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawPaint(paint);
            
            //Robot
            paint.setColor(Color.GREEN);
            paint.setAntiAlias(true);
            canvas.drawRect(getWidth()/2 -25, getHeight()-10 ,getWidth()/2 + 25, getHeight()-35, paint);
            
            //Obstacle
            float osoa = getHeight()- 35 - 35 - 10;
            float zatia = distance * (osoa / scale);
            
            float pos_top = (getHeight()-45-zatia);
            float pos_bottom = (getHeight()-35-zatia);
            
            
            if (distance < 50) paint.setColor(Color.RED);
            else if (distance > 50 && distance < 150) paint.setColor(Color.YELLOW);
            else paint.setColor(Color.BLUE);
            
            paint.setAntiAlias(true);
            canvas.drawRect((getWidth()/2 - getWidth()/4), pos_top  ,(getWidth()/2 + getWidth()/4) ,pos_bottom, paint);
            
            //Text
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(30);
            canvas.drawText("Distance: " + distance ,20, 60, paint);
            
            //Up Line
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            canvas.drawRect(0, 30  ,getWidth(),32, paint);
            
        }
    }
    
//Scan Obstacles
    

	/**
 * The Class InternalView2. To paint everything on the Module Scan Obstacles.
 */
private class InternalView2 extends View{
		
		/** The actual time. */
		private long now;
		
		/** The time. */
		private long time;
		
		/** finish time_writed?. */
		private boolean fin_time_writed = false;
		
        /**
         * Instantiates a new internal view2.
         *
         * @param context the context
         */
        public InternalView2(Context context){
            super(context);
        }
        
        /* (non-Javadoc)
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas) {
        	
            super.onDraw(canvas);
            
        	if (view_init){
                robot = BitmapFactory.decodeResource(getResources(), R.drawable.logo_new);
                protractor = BitmapFactory.decodeResource(getResources(), R.drawable.protractor);
                regla = BitmapFactory.decodeResource(getResources(), R.drawable.regla);
                instrucs1 = BitmapFactory.decodeResource(getResources(), R.drawable.instrucs1);
                instrucs2 = BitmapFactory.decodeResource(getResources(), R.drawable.instrucs2);
                scan_img = BitmapFactory.decodeResource(getResources(), R.drawable.scan);
                scan_disable = BitmapFactory.decodeResource(getResources(), R.drawable.scan_disable);
                select_img = BitmapFactory.decodeResource(getResources(), R.drawable.select);
                move = BitmapFactory.decodeResource(getResources(), R.drawable.move);
                
                robot_x = (float) ((getWidth()/2) -2.5);
                robot_y = getHeight()- 5;
                
                canvas_width = getWidth();
                canvas_height = getHeight();
                
                view_init = false;
        	}
        	
            Paint paint = new Paint();
            
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            
//Angle selecting-------------------------------------------------
            
            if (angle_selecting){
            	
            	paint.setColor(Color.RED);
            	if (last_isMove){
            		canvas.drawCircle( robot_sx, robot_sy, 5*(255/scale), paint);
            	}else{
            		canvas.drawCircle( robot_x, robot_y, 5*(255/scale), paint);
            	}
            	
            	old_angle = new_angle;
            	
                if (roll > 30){
                	roll2 = -90;
                }else if (roll < -30){
                	roll2 = 90;
                }else{
                	roll2 = -(roll * 3);
                	roll2 = ((int)roll2 / 10)*10;
                }
                //Yberria = Yzaharra * alpha + Yirakurria*(1-alpha) //alpha=0.5
                
            	new_angle = old_angle * alpha + roll2*(1-alpha);
            	new_angle = ((int)new_angle / 10) *10;

                robo_angle = (float) (90 -new_angle);
                
                
                
                paint.setAlpha(255);
                canvas.drawBitmap(select_img, 10, 10 , paint);
                
                paint.setColor(Color.BLACK);
                paint.setTextSize(65);
                
                paint.setAlpha(100);
                canvas.drawText("Degrees: "  + (int)robo_angle, 75, 190, paint);
                
                paint.setAlpha(100);
                canvas.drawBitmap(instrucs1, getWidth()/2 -(instrucs1.getWidth()/2), 220 , paint);
                       		
                
      //Angle line ------------------------------------------------     
        		
        		int r_angle = (int) robo_angle;
    			int r_distance = 1000;
    			
    			double h = r_distance; //hipotenusa
    			
    			double sin = Math.sin(Math.toRadians(r_angle));
    			double cos = Math.cos(Math.toRadians(r_angle));
    			
    			angle_x =  (cos * h) + getWidth()/2; //Albokoa
    			
    			angle_y =  getHeight() - (sin * h); //Aurkakoa
    			
    			paint.setColor(Color.MAGENTA);  
    			
                if(last_isMove){
            		canvas.drawLine(robot_sx, robot_sy, (float)angle_x, (float)angle_y, paint);
                    //Protractor
                    paint.setAlpha(100);
                    canvas.drawBitmap(protractor, robot_sx - protractor.getWidth()/2, robot_sy - protractor.getHeight() , paint);
                }else{
                	canvas.drawLine((float)getWidth()/2, (float)getHeight(), (float)angle_x, (float)angle_y, paint);
                    //Protractor
                    paint.setAlpha(100);
                    canvas.drawBitmap(protractor, getWidth()/2 -(protractor.getWidth()/2), getHeight()- (protractor.getHeight()) , paint);
                }
      //Angle line finish  ------------------------------------------------   
                
//Distance selecting-------------------------------------------------
                
            }else if (distance_selecting){
            	
            	paint.setColor(Color.RED);
            	if (last_isMove){
            		canvas.drawCircle( robot_sx, robot_sy, 5*(255/scale), paint);
            	}else{
            		canvas.drawCircle( robot_x, robot_y, 5*(255/scale), paint);
            	}
            	
            	old_distance = robo_distance;
            	
                if (pitch < 40 && pitch > 0){
                	robo_distance = (int) (pitch/ 1.6)*10;
                }else{
                	robo_distance = 0;
                }
                
                //Yberria = Yzaharra * alpha + Yirakurria*(1-alpha) //alpha=0.5
                
            	new_distance = old_distance * alpha + robo_distance*(1-alpha);
            	new_distance = (int) (new_distance/ 10)*10;
            	
            	robo_distance = (float) new_distance;
                
                paint.setAlpha(255);
                canvas.drawBitmap(select_img, 10, 10 , paint);
                
                paint.setColor(Color.BLACK);
                paint.setTextSize(55);
                
                paint.setAlpha(100);
                canvas.drawText("Dist: (cm): "  + (int)robo_distance, 75, 190, paint);
                
                paint.setAlpha(100);
                canvas.drawBitmap(instrucs2, getWidth()/2 -(instrucs2.getWidth()/2), 250 , paint);
                
    			paint.setColor(Color.MAGENTA);
    			line_y = getHeight() - (getHeight()*robo_distance/scale);
    			
                if(last_isMove){
            		line_y = robot_sy - (getHeight()*robo_distance/scale);
                }else{
                	line_y = getHeight() - (getHeight()*robo_distance/scale);
                }            	
                
    			//Distance
    			canvas.drawLine(0,line_y , getWidth(),line_y , paint);
    			
                if(last_isMove){
            		canvas.drawLine(robot_sx, robot_sy, (float)angle_x, (float)angle_y, paint);
            		//Regla
                    paint.setAlpha(100);
                    canvas.drawBitmap(regla, 0, robot_sy - (float)getHeight() , paint);
                }else{
                	canvas.drawLine((float)getWidth()/2, (float)getHeight(), (float)angle_x, (float)angle_y, paint);
                	//Regla
                    paint.setAlpha(100);
                    canvas.drawBitmap(regla, 0, 0 , paint);
                }
                
//Is Seeing------------------------------------------------------------
                
            }else if (isSeeing){
            	
            	if (first_scan) {
            		paint.setColor(Color.RED);
            		canvas.drawCircle( robot_x, robot_y, 5*(255/scale), paint);
            	}else{
            		
     //Move robot on screen ------------------------------------------------  
            		
            		robot_sx_prev = robot_sx;
            		robot_sy_prev = robot_sy;
            		
            		int r_angle = (int) robo_angle;
        			int r_distance = (int) robo_distance;
        			
        			double h = r_distance; //hipotenusa
        			double h2 = (r_distance * canvas_height) / scale;
        			double sin = Math.sin(Math.toRadians(r_angle));
        			double cos = Math.cos(Math.toRadians(r_angle));
        			
        			double x =  (cos * h2); //Albokoa
        			double y =  (sin * h2); //Aurkakoa
        			
                	if (last_isMove){
                		if (!stopSeeing){
                			robot_sx = (int) (robot_sx + x);
                			robot_sy = (int) (robot_sy_prev - y);
                		}
                	}else{
                		robot_sx = (int) (x + robot_x);
                		robot_sy = (int) (robot_y - y);
                	}
                	if (robo_distance != 0){
                		//Robot
            			paint.setColor(Color.RED);
                		canvas.drawCircle( robot_sx, robot_sy, 5*(255/scale), paint);
                	}else{
                		//Robot
            			paint.setColor(Color.RED);
                		canvas.drawCircle( robot_sx_prev, robot_sy_prev, 5*(255/scale), paint);
                	}
            		
     //Move robot finish  ------------------------------------------------          
            		
            		//Robot Moved (Text)
                	paint.setColor(Color.BLACK);
                    paint.setTextSize(50);
                    paint.setAlpha(50);
                    canvas.drawText("Robot moved", 75, getHeight()/2, paint);
                    
                    //Move Button
                    paint.setAlpha(255);
                    canvas.drawBitmap(move, getWidth() - move.getWidth() - 10, 10 , paint);
                    
                    //'Move' to move the robot (Text)
                	paint.setColor(Color.BLACK);
                	paint.setTextSize(30);
                    paint.setAlpha(50);
                    canvas.drawText("'Move' to move the robot", 75, getHeight()/2 +130, paint);
                    
                    // Distance & Angle
                    paint.setColor(Color.MAGENTA);
                    //Distance
        			canvas.drawLine(0,line_y , getWidth(),line_y , paint);
        			//Angle
                    if(last_isMove){
                		canvas.drawLine(robot_sx, robot_sy, (float)angle_x, (float)angle_y, paint);
                    }else{
                    	canvas.drawLine((float)getWidth()/2, (float)getHeight(), (float)angle_x, (float)angle_y, paint);
                    }
        			
            	}
            	
            	//Scan Button
            	paint.setAlpha(255);
            	canvas.drawBitmap(scan_img, 10, 10 , paint);
            	
            	//Text
            	paint.setColor(Color.BLACK);
            	paint.setTextSize(30);
                paint.setAlpha(50);
                canvas.drawText("Press the 'Scan' button", 75, getHeight()/2 +50, paint);
                canvas.drawText("    to scan obstacles", 75, getHeight()/2 +90, paint);
                
                stopSeeing  = true;
            
//Is Scanning-------------------------------------------------
                
            }else if (isScanning){
            	
            	//Robot on start
            	paint.setColor(Color.RED);
            	canvas.drawCircle( robot_x, robot_y, 5*(255/scale), paint);
            	
            	//Scan Button
                paint.setAlpha(255);
                canvas.drawBitmap(scan_img, 10, 10 , paint);
                
                //Move Button
                paint.setAlpha(255);
                canvas.drawBitmap(move, getWidth() - move.getWidth() - 10, 10 , paint);
                
                //Text
                paint.setColor(Color.BLACK);
                paint.setTextSize(40);
                paint.setAlpha(50);
                canvas.drawText("Seeing scanned", 75, getHeight()/2, paint);
                canvas.drawText("    obstacles..", 75, getHeight()/2 + 40, paint);
                
	            for (int z = 0; z < 13; z++) {
	                paint.setColor(Color.BLACK);
	                paint.setTextSize(15);
                	canvas.drawText((int)dist_array[z].mangle + "/" + (int)dist_array[z].mdistance,10,270+20*z,paint);
                	
    	            paint.setColor(Color.BLACK);
    	            paint.setTextSize(30);
    	            paint.setAlpha(150);
    	            canvas.drawText("Light: " + light,10,40 + scan_img.getHeight(),paint);
    	            canvas.drawText("Simulated distance: " + scale,10,85 + scan_img.getHeight(),paint);
    	            
                	//canvas.drawText("x: " + (int)pos_array[z].mx + "/ y: " + (int)pos_array[z].my,250,250+20*z,paint);
	            }
//	            canvas.drawText("Light: " + light + " isFin:" + isFin,10,120+20*20,paint);
//	            canvas.drawText("START:" + startdeg + " FIN:" + findeg + " DIF:" + dif + " SCALE:" + scale +" MAX:" + max, 10,120+20*21,paint);
            }
            
//Othe cases-------------------------------------------------------
            
            //Obstacles
            if ((!first_scan) && (!isFin)) {
	            paint.setColor(Color.BLUE);
	            paint.setAntiAlias(true);
	            
	            int prev_count = 0;
	            int count = 0;
	            
	            for (int z = 0; z < 13; z++) {
	                
//	            	prev_count = count;
//	                count = 0;
//	                
//	                //See how many times appears the number. If count is > 5, don't paint the obstacle 
//	                for(int j = 0; j < 13; j++) {
//	                	
//	                	 if(dist_array[j].mdistance == dist_array[z].mdistance) {
//		                        count++;
//		                    }
//	                	 
//	                }
//	                if (count < 5){
	                	if (z!=0){
	                		if (	(dist_array[z].mdistance == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance + 1 == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance + 2 == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance + 3 == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance - 1 == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance - 2 == dist_array[z-1].mdistance) ||
	                				(dist_array[z].mdistance - 3 == dist_array[z-1].mdistance)
	                				
	                		)canvas.drawLine((float)pos_array[z-1].mx, (float)pos_array[z-1].my, (float)pos_array[z].mx, (float)pos_array[z].my, paint);
	                	}
	                	canvas.drawCircle( (float)pos_array[z].mx, (float)pos_array[z].my, 5*(255/scale), paint);
//	                }
	            }
	            
	            //Timer
	            paint.setColor(Color.BLACK);
	            paint.setTextSize(30);
	            paint.setAlpha(150);
	            now = System.currentTimeMillis();
	            time = now - start_time;
	            canvas.drawText("Time:" + time + " ms", getWidth() - move.getWidth()-5, 40 + scan_img.getHeight(), paint);
	            
	            if (light > 65){
	            	paint.setTextSize(40);
		            paint.setColor(Color.RED);
		            canvas.drawText("  YOU ARE NEAR", 75, getHeight()/2 - 120, paint);
		            canvas.drawText("  TO THE FINISH!", 75, getHeight()/2 - 60, paint);
	            }
            }
            
//Is Fin ---------------------------------------------------------------------
            
            if (isFin){
            	if (!fin_time_writed){
		            now = System.currentTimeMillis();
		            time = now - start_time;
		            fin_time_writed = true;
            	}
	            	
		            paint.setTextSize(40);
		            paint.setColor(Color.RED);
		            canvas.drawText("      FINISH !!!", 75, getHeight()/2 - 60, paint);
		            paint.setColor(Color.BLACK);
	                canvas.drawText("   Your time is:", 75, getHeight()/2, paint);
		            canvas.drawText(" Time:" + time + " ms", 75, getHeight()/2 +60, paint);
		            setContentView(myView2);
            }
        }
        

        /* (non-Javadoc)
         * @see android.view.View#onTouchEvent(android.view.MotionEvent)
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
			
        	 if (isFin){
        		 isFin = false;
                 mControlsMode = MODE_CONFIG;
                 setupUI();
        	 }
        	
            if (( event.getX() > 10) && (event.getX() < select_img.getWidth() + 10)
            		&& ( event.getY() > 10) && (event.getY() < (10 + select_img.getHeight()))){
            	
            	if (angle_selecting){
            		
	            	mrotateThread = new rotateThread();
	            	mrotateThread.start();
	            	
                	//Para que no pinte mientras se mueve
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = false;
                	isSeeing = false;
                	
            	}else if (distance_selecting){
            		
	            	mtravelThread = new travelThread();
	            	mtravelThread.start();
	            	
                	//Para que no pinte mientras se mueve
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = false;
                	isSeeing = false;
	            	
            	}else if (isScanning){
            		
                	mscanThread = new scanThread();
                	mscanThread.start();
                	
                	//Para que no pinte mientras escanea
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = false;
                	isSeeing = false;
                	
            	}else if (isSeeing){
            		
                	mscanThread = new scanThread();
                	mscanThread.start();
                	
                	//Para que no pinte mientras escanea
                	angle_selecting = false;
                	distance_selecting=false;
                	isScanning = false;
                	isSeeing = false;
            	}
            }
            //MOVE
            if (( event.getX() > getWidth() - move.getWidth()) && (event.getX() < getWidth())
            		&& ( event.getY() > 10) && (event.getY() < (10 + move.getHeight()))){
            	
            		if (isSeeing){
            		
    	            	angle_selecting = true;
    	            	distance_selecting=false;
    	            	isScanning = false;
    	            	isSeeing = false;
    	            	last_isMove = true;
    	            	stopSeeing  = false;
    	            	
            		}else  if (isScanning){
            		
    	            	angle_selecting = true;
    	            	distance_selecting=false;
    	            	isScanning = false;
    	            	isSeeing = false;
    	            	last_isMove = false;
    	            	stopSeeing  = false;
            		}
            		
            }
            
        	return super.onTouchEvent(event);
        }
    }

    

    /**
     * The Class travelThread. Move the robot when the user selected the distance
     */
    private class travelThread extends Thread {
    	
    	/**
	     * Instantiates a new travel thread.
	     */
	    public travelThread() {
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	
	    	mHandler.obtainMessage(NXTDriver.TRAVELLING, 1, -1).sendToTarget();
	    	
	    	if (robo_distance != 0) mPilot.travel(-robo_distance);
	    	
        	
        	//mirar hacia delante
        	if (!(roll2 == 0)) {
        		
        		mPilot.rotate((int) roll2);
        		
        		angle_findeg = mCompass.getDegrees();
            	
            	if ((int)angle_startdeg > (int)angle_findeg){
            		angle_dif = angle_startdeg - angle_findeg;
            		mPilot.rotate(-((int)angle_dif));
            	}else if ((int)angle_startdeg < (int)angle_findeg){
            		angle_dif = angle_findeg - angle_startdeg;
            		mPilot.rotate((int) angle_dif);
            	}
            	
            rotateAngleCorrect();
        	}
        	
	    	lightControl();
	    	
	    	mHandler.obtainMessage(NXTDriver.TRAVEL_FINISHED, 1, -1).sendToTarget();
      }
	}


		/**
		 * Rotate angle correct.Correct the robot inaccuracy returning to the start position when rotate is finished.
		 */
		private void rotateAngleCorrect() {
			
			double angle_startdeg2;
        	double angle_dif2;
        	
        	angle_startdeg2 = mCompass.getDegrees();
        	
        	if ((int)angle_startdeg > (int)angle_startdeg2){
        		angle_dif2 = angle_startdeg - angle_startdeg2;
        	}else if ((int)angle_startdeg < (int)angle_startdeg2){
        		angle_dif2 = angle_startdeg2 - angle_startdeg;
        	
        	while (angle_dif2 > 4){
        		
            	if ((int)angle_startdeg > (int)angle_startdeg2){
            		mPilot.rotate(-((int)angle_dif2));
            	}else if ((int)angle_startdeg < (int)angle_startdeg2){
            		mPilot.rotate((int) angle_dif2);
            	}
            	
            	angle_startdeg2 = mCompass.getDegrees();
            	
            	if ((int)angle_startdeg > (int)angle_startdeg2){
            		angle_dif2 = angle_startdeg - angle_startdeg2;
            	}else if ((int)angle_startdeg < (int)angle_startdeg2){
            		angle_dif2 = angle_startdeg2 - angle_startdeg;
            	}else angle_dif2 = 0;

        	}
			
		}
}
    
    /**
     * The Class rotateThread.  Rotate the robot when the user selected the angle
     */
    private class rotateThread extends Thread {
    	
    	/**
	     * Instantiates a new rotate thread.
	     */
	    public rotateThread() {
		}
	    	
	    /* (non-Javadoc)
    	 * @see java.lang.Thread#run()
    	 */
    	@Override
	    public void run() {
	    	
	    	mHandler.obtainMessage(NXTDriver.ROTATING, 1, -1).sendToTarget();
	    	
	    	if (!(roll2 == 0)) {
        		
        		angle_startdeg = mCompass.getDegrees();
        		
        		mPilot.rotate((int) -roll2);
        	}
	    	
	    	mHandler.obtainMessage(NXTDriver.ROTATE_FINISHED, 1, -1).sendToTarget();
	    }
  }
}