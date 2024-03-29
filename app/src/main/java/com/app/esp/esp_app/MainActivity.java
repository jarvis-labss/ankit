package com.app.esp.esp_app;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Boolean l1, l2, l3, turn;
    RelativeLayout layout_joystick;
    RelativeLayout layout_joystick1;
    JoyStickClass js;
    JoystickClass1 js1;
    static WifiManager wifiManager;
    Context context;
    WifiConfiguration conf;
    SeekBar speed;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    int min = 0;
    int max = 2000;
    int step = 5;
    public static String networkSSID = "esp_abc";
    public static String networkPass = "esp123";
    byte[] buf = new byte[1024];
  //used to sending information to esp is a form of byte
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speed = (SeekBar) findViewById(R.id.seekBar);
        textView1 = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        layout_joystick1 = (RelativeLayout) findViewById(R.id.layout_joystick1);
        textView4 = (TextView) findViewById(R.id.textView4);

        textView6 = (TextView) findViewById(R.id.textView6);

        l1 = l2 = l3 = turn = true;
        context = this;

        // this is for thread policy the AOS doesn't allow to transfer data using wifi module so we take the permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        js = new JoyStickClass(getApplicationContext()
                , layout_joystick, R.drawable.image_button_2);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);




        //second joystick
        js1 = new JoystickClass1(getApplicationContext()
                , layout_joystick1, R.drawable.image_button_2);
        js1.setStickSize(150, 150);
        js1.setLayoutSize(500, 500);
        js1.setLayoutAlpha(150);
        js1.setStickAlpha(100);
        js1.setOffset(90);
        js1.setMinimumDistance(50);


        layout_joystick1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js1.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    int direction = js1.get8Direction();

                    if (direction == JoystickClass1.STICK_UP) {
                        forward();
                    } else if (direction == JoystickClass1.STICK_UPRIGHT) {

                        forwardright();
                    } else if (direction == JoystickClass1.STICK_RIGHT) {

                        right();
                    } else if (direction == JoystickClass1.STICK_DOWNRIGHT) {

                        backwardright();
                    } else if (direction == JoystickClass1.STICK_DOWN) {

                        backward();
                    } else if (direction == JoystickClass1.STICK_DOWNLEFT) {

                        backwardleft();
                    } else if (direction == JoystickClass1.STICK_LEFT) {

                        left();
                    } else if (direction == JoystickClass1.STICK_UPLEFT) {

                        forwardleft();
                    } else if (direction == JoystickClass1.STICK_NONE) {

                        release();
                    } else {
                        release();
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {

                }
                return true;



            }
        });

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if (arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    int direction = js.get8Direction();

                    if (direction == JoyStickClass.STICK_UP) {
                        forward();
                    } else if (direction == JoyStickClass.STICK_UPRIGHT) {

                        forwardright();
                    } else if (direction == JoyStickClass.STICK_RIGHT) {

                        right();
                    } else if (direction == JoyStickClass.STICK_DOWNRIGHT) {

                        backwardright();
                    } else if (direction == JoyStickClass.STICK_DOWN) {

                        backward();
                    } else if (direction == JoyStickClass.STICK_DOWNLEFT) {

                        backwardleft();
                    } else if (direction == JoyStickClass.STICK_LEFT) {

                        left();
                    } else if (direction == JoyStickClass.STICK_UPLEFT) {

                        forwardleft();
                    } else if (direction == JoyStickClass.STICK_NONE) {

                        release();
                    } else {
                        release();
                    }
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {

                }
                return true;



            }
        });
    }

    // conected with a wifi button.. it connect to esp module when it is pressed
    //remember the nework ssid and pasword needs to be the same as given here
    //other it won't connect
    public void wifi_connect(View v) {


        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        if (turn) {

            turnOnOffWifi(context, turn);
            turn = false;
            Toast.makeText(getApplicationContext(), "turning on...", Toast.LENGTH_SHORT).show();

            //wifi configuration .. all the code below is to explain the wifi configuration of which type the wifi is
            //if it is a WPA-PSK protocol then it would work

            conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.preSharedKey = "\"" + networkPass + "\"";
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            int netid = wifiManager.addNetwork(conf);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netid, true);
            wifiManager.reconnect();


        } else {
            turnOnOffWifi(context, turn);
            turn = true;
            Toast.makeText(getApplicationContext(), "turning off...", Toast.LENGTH_SHORT).show();

        }

    }

    void forward() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();

    }


    //second joystick



    void forwardright() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();


    }

    void right() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();
    }

    void backwardright() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();

    }

    void backward() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();
    }

    void left() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();

    }

    void forwardleft() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();

    }

    void release() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();
    }

    void backwardleft() {
        String x = Integer.toString(js.getX());
        String y = Integer.toString(js.getY());
        String X = Integer.toString(js1.getX());
        String Y = Integer.toString(js1.getY());

        textView6.setText(X);
        textView4.setText(Y);
        textView3.setText(x);
        textView1.setText(y);
        Client a = new Client();
        buf = null;
        buf = (x +"A"+ y+"B"+X+"C"+Y).getBytes();
        textView2.setText(Arrays.toString(buf));
        a.run();
    }

    // when LED 1 BUTTON is pressed
    public void led_1(View v) {


        if (l1) {

            l1 = false;
            Client a = new Client();
            buf = null;
            buf = ("9").getBytes();
            a.run();
            Toast.makeText(MainActivity.this, "LED 1 ON", Toast.LENGTH_SHORT).show();
        } else {

            l1 = true;
            Client a = new Client();//object of class client
            buf = null;
            buf = ("10").getBytes();// value to be send to esp
            a.run(); //use run() in class client to send data
            Toast.makeText(MainActivity.this, "LED 1 OFF", Toast.LENGTH_SHORT).show();
        }


    }

    // when LED 3 BUTTON is pressed
    public void led_2(View v) {

        if (l2) {

            l2 = false;
            Client a = new Client();
            buf = null;
            buf = ("11").getBytes();
            a.run();
            Toast.makeText(MainActivity.this, "LED 2 ON", Toast.LENGTH_SHORT).show();
        } else {

            l2 = true;
            Client a = new Client();
            buf = null;
            buf = ("12").getBytes();
            a.run();
            Toast.makeText(MainActivity.this, "LED 2 OFF", Toast.LENGTH_SHORT).show();
        }


    }

    // when LED 3 BUTTON is pressed
    public void led_3(View v) {

        if (l3) {

            l3 = false;
            Client a = new Client();
            buf = null;
            buf = ("13").getBytes();
            a.run();
            Toast.makeText(MainActivity.this, "LED 3 ON", Toast.LENGTH_SHORT).show();
        } else {
            l3 = true;
            Client a = new Client();
            buf = null;
            buf = ("14").getBytes();
            a.run();
            Toast.makeText(MainActivity.this, "LED 3 OFF", Toast.LENGTH_SHORT).show();
        }
        speed.setMax((int) ((max - min) / step));

        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + (progress * step);
                int MIN = 1000;
                if (value > MIN) {
                    String s1 = Integer.toString(value);
                    Client a = new Client();
                    buf = null;
                    buf = (s1).getBytes();
                    a.run();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public static void turnOnOffWifi(Context context, boolean isTurnToOn) {
        wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isTurnToOn);
    }

    //used to send data to esp module
    public class Client implements Runnable {
        private final static String SERVER_ADDRESS = "192.168.4.1";//public ip of my server
        private final static int SERVER_PORT = 8888;


        public void run() {

            InetAddress serverAddr;
            DatagramPacket packet;
            DatagramSocket socket;


            try {
                serverAddr = InetAddress.getByName(SERVER_ADDRESS);
                socket = new DatagramSocket(); //DataGram socket is created
                packet = new DatagramPacket(buf,buf.length,serverAddr,SERVER_PORT);//Data is loaded with information where to send on address and port number
                socket.send(packet);//Data is send in the form of packets
                socket.close();//Needs to close the socket before other operation... its a good programming
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
