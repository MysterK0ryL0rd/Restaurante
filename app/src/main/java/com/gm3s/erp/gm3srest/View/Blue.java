package com.gm3s.erp.gm3srest.View;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gm3s.erp.gm3srest.MenuCajas;
import com.gm3s.erp.gm3srest.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class Blue extends AppCompatActivity {
    private boolean print = true;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String message = "";
    byte[] messageBytes;
    ArrayList < BluetoothDevice > listaBT = new ArrayList();
    ArrayList < String > listaBTS = new ArrayList();
    BluetoothDevice device;
    int selected = 0;
    String id = "";
    int temp;
    boolean esCaja;

    private static Boolean forceCLaim = true;
    private UsbEndpoint mEndPoint;
    final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager mUsbManager;
    UsbDevice mDevice;
    UsbDeviceConnection mConnection;
    UsbInterface mInterface;
    PendingIntent mPermissionIntent;
    UsbDevice dispositivoUSB = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);
        Intent intent = getIntent();
        //message = intent.getStringExtra("data");
        messageBytes = intent.getByteArrayExtra("Ticket");

        if (intent.getSerializableExtra("data") != null) {

            message = intent.getStringExtra("data") + message;
        }


        if (intent.getSerializableExtra("esCaja") != null) {
            esCaja = (Boolean)intent.getSerializableExtra("esCaja");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GM3s Software");
        toolbar.setSubtitle("Envio de Ticket");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (esCaja == true) {
                    Intent intent = new Intent(Blue.this, MenuCajas.class);
                    intent.putExtra("user", "name");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                    finish();


                }else {
                    if (esCaja == false) {
                        Intent intent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                        intent.putExtra("user", "name");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                        finish();


                    } else {
                        Intent localIntent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                        localIntent.putExtra("usercompany", "company");
                        localIntent.putExtra("username", "name");
                        startActivity(localIntent);
                        finish();

                    }
                }

            }
        });



        Button openButton = (Button) findViewById(R.id.open);
        Button nextButton = (Button) findViewById(R.id.next);
        openButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {






                                             /* HashMap<String, UsbDevice> mDeviceList;
                                              Iterator<UsbDevice> mDeviceIterator;

                                              mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                                              mDeviceList = mUsbManager.getDeviceList();
                                              mDeviceIterator = mDeviceList.values().iterator();
                                            //  Toast.makeText(getApplicationContext(), "Device List Size: " + String.valueOf(mDeviceList.size()), Toast.LENGTH_SHORT).show();
                                              String usbDevice = "";

                                              while (mDeviceIterator.hasNext()) {
                                                  UsbDevice usbDevice1 = mDeviceIterator.next();

                                              //    if(usbDevice1.getDeviceClass().toString().contains("USB class for printers")){
                                                  usbDevice += "\n" +
                                                          "DeviceID: " + usbDevice1.getDeviceId() + "\n" +
                                                          "DeviceName: " + usbDevice1.getDeviceName() + "\n" +
                                                          "DeviceClass: " + usbDevice1.getDeviceClass() + " - " + translateDeviceClass(usbDevice1.getDeviceClass()) + "\n" +
                                                          "DeviceSubClass: " + usbDevice1.getDeviceSubclass() + "\n" +
                                                          "VendorID: " + usbDevice1.getVendorId() + "\n" +
                                                          "ProductID: " + usbDevice1.getProductId() + "\n";

                                                  int interfaceCount = usbDevice1.getInterfaceCount();
                                                 // Toast.makeText(getApplicationContext(), "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();
                                                  //Toast.makeText(getApplicationContext(), translateDeviceClass(usbDevice1.getDeviceClass()), Toast.LENGTH_SHORT).show();


                                                  mDevice = usbDevice1;

                                                  if (mDevice == null) {
                                                      //Toast.makeText(getApplicationContext(), "mDevice is null", Toast.LENGTH_SHORT).show();
                                                  }else{
                                                      //Toast.makeText(getApplicationContext(), "mDevice is not null", Toast.LENGTH_SHORT).show();
                                                  }
                                                  //Toast.makeText(getApplicationContext(),usbDevice, Toast.LENGTH_SHORT).show();

                                                  if(usbDevice1.getVendorId()==1305){
                                                  mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                                                  IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                                                  registerReceiver(mUsbReceiver, filter);
                                                  mUsbManager.requestPermission(mDevice, mPermissionIntent);}

                                                  //   mInterface = usbDevice1.getInterface(0);
                                                  //   mEndPoint = mInterface.getEndpoint(0);
                                                  //   mConnection = mUsbManager.openDevice(usbDevice1);
                                                  //   Toast.makeText(getApplicationContext(),mConnection.getSerial(), Toast.LENGTH_SHORT).show();
                                                  //     Toast.makeText(getApplicationContext(),mInterface.toString(), Toast.LENGTH_SHORT).show();

                                              }


               /* List<PortInfo> USBPortList;
                final ArrayList<PortInfo> arrayDiscovery;
                ArrayList<String> arrayPortName;

                arrayDiscovery = new ArrayList<PortInfo>();
                arrayPortName = new ArrayList<String>();

                try {
                    //TCP Salido de la Aplicacion
                    //BT OFF 0 ON Salido de la Aplicacion
                    USBPortList = StarIOPort.searchPrinter("USB:");
                   Toast.makeText(getApplicationContext(),"Tamaño del descubrimiento " + USBPortList.size(), Toast.LENGTH_LONG).show();

                   for (PortInfo portInfo : USBPortList) {
                       arrayDiscovery.add(portInfo);
                    }


                    arrayPortName = new ArrayList<String>();

                    for (PortInfo discovery : arrayDiscovery) {
                        String portName;

                        portName = discovery.getPortName();

                        if (discovery.getMacAddress().equals("") == false) {
                            portName += "\n - " + discovery.getMacAddress();
                            if (discovery.getModelName().equals("") == false) {
                                portName += "\n - " + discovery.getModelName();
                            }
                        } else  {
                            if (!discovery.getModelName().equals("")) {
                                portName += "\n - " + discovery.getModelName();
                            }
                            if (!discovery.getUSBSerialNumber().equals(" SN:")) {
                                portName += "\n - " + discovery.getUSBSerialNumber();
                            }
                        }

                        arrayPortName.add(portName);

                        message=message.substring(8, message.length() - 8);
                        String[] stringArray = message.split("\\\\");
                        ArrayList<byte[]> lineas = new ArrayList<>();
                        for(int i=0; i<stringArray.length;i++){
                            if(stringArray[i].length()>1) {
                                stringArray[i] = stringArray[i].substring(1) + "\r\n";
                                lineas.add(stringArray[i].getBytes());
                            }
                        }

                        lineas.add("\r\n".getBytes());
                        lineas.add("\r\n".getBytes());
                        lineas.add("\r\n".getBytes());
                        lineas.add("\r\n".getBytes());
                        lineas.add("\r\n".getBytes());
                        lineas.add("\r\n".getBytes());

                        Helper.sendCommand(getApplication().getApplicationContext(), portName, "settings",lineas);
                    }
                } catch (StarIOPortException e) {
                    Toast.makeText(getApplicationContext(),"Error: Catch ", Toast.LENGTH_LONG).show();;
                    e.printStackTrace();
                }


*/
                                              try {
                                                  build_popup2();


                                                 /* if(dispositivoUSB == null){
                                                  build_popup2();}
                                                  else{
                                                      if (dispositivoUSB.getVendorId()==1035){
                                                          imprimirUSB(dispositivoUSB);
                                                      }
                                                      imprimirUSB(dispositivoUSB);
                                                  }*/



                  /*  listaBT.clear();
                    listaBTS.clear();
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set < BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {

                        for (BluetoothDevice device: pairedDevices) {
                            String deviceBTName = device.getName();
                            listaBT.add(device);
                            listaBTS.add(deviceBTName);
                        }

                        if(listaBTS.contains("TSP100-F0038")){
                            openConnection(listaBT.get(listaBTS.indexOf("TSP100-F0038")));
                        }
                        else{
                            build_popup2();
                        }
                    }*/




                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }

                                          }
                                      }

        );


        nextButton.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              //   print(mConnection, mInterface);

                                              try {

                                                  if(dispositivoUSB == null){
                                                      initPrint();
                                                      sendData(message);
                                                      close();}
                                                  else{
                                                      print(mConnection, mInterface);
                                                  }


                                              } catch (Exception e) {
                                                  e.printStackTrace();
                                              }

                                              if (esCaja == true) {
                                                  Intent intent = new Intent(Blue.this, MenuCajas.class);
                                                  intent.putExtra("user", "name");
                                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                  getApplicationContext().startActivity(intent);
                                                  finish();


                                              }else {
                                                  if (esCaja == false) {
                                                      Intent intent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                                                      intent.putExtra("user", "name");
                                                      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                      getApplicationContext().startActivity(intent);
                                                      finish();


                                                  } else {
                                                      Intent localIntent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                                                      localIntent.putExtra("usercompany", "company");
                                                      localIntent.putExtra("username", "name");
                                                      startActivity(localIntent);
                                                      finish();

                                                  }
                                              }
                                          }
                                      }

        );


      //  listarDispositivosUsb();

    }

    public void openConnection(BluetoothDevice device_tmp) throws Exception {
        try {
            if (print == false) {
                return;
            }
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.cancelDiscovery();
            String address = device_tmp.getAddress();
            device = device_tmp;
            if (mBluetoothAdapter.checkBluetoothAddress(address.toUpperCase()) == false) {
                throw new Exception("Dirección mac mal formada " + address.toUpperCase());
            }
            mmDevice = mBluetoothAdapter.getRemoteDevice(address.toUpperCase());
            if (mmDevice == null) {
                throw new Exception("Dispositivo no encontrado " + address.toUpperCase());
            } else {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                if (mmSocket != null) {
                    mmSocket.connect();
                    Toast.makeText(getApplicationContext(), "Conectado", Toast.LENGTH_SHORT).show();
                } else {
                    throw new Exception("No se pudo establecer conexión con el dispositivo ");
                }
            }
        } catch (Exception e) {
            throw new Exception("No se pudo establecer conexión con el dispositivo [" + e.getMessage() + "]");
        }
    }


    public void initPrint() throws Exception {
        if (print == false) {
            return;
        }
        if (mmSocket != null) {
            mmOutputStream = mmSocket.getOutputStream();

            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } else {
            throw new Exception("Sin conexión con el dispositivo.");
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) throws Exception {
        if (print == false) {
            return;
        }
        try {
            switch (device.getName()) {
                case "EC MP-2":

                    data=data.substring(8, data.length() - 8);
                    String[] stringArray = data.split("\\\\");
                    ArrayList<String> lineas = new ArrayList<>();
                    for(int i=0; i<stringArray.length;i++){
                        if(stringArray[i].length()>1) {
                            stringArray[i] = stringArray[i].substring(1) + "\r\n";
                            lineas.add(stringArray[i]);
                        }
                    }

                    lineas.add("\r\n");
                    lineas.add("\r\n");
                    lineas.add("\r\n");
                    lineas.add("\r\n");
                    lineas.add("\r\n");
                    lineas.add("\r\n");

                    mmOutputStream.write((byte) 0x1b);
                    mmOutputStream.write((byte) 0x21);
                    mmOutputStream.write((byte) 0x01);
                    for(String tmp: lineas) {
                        mmOutputStream.write(tmp.getBytes());
                    }
                    mmOutputStream.write("\n".getBytes());
                    mmOutputStream.write("\n".getBytes());
                    break;

                case "STAR L200-00716":

                    /*
                     ArrayList<byte[]> list = new ArrayList<byte[]>();
                    list.add(new byte[] {0x1b,0x40}); //ESC ("\u001b*rA").getBytes()
                    list.add(new byte[] {0x1b,0x1e});
                    list.add(new byte[] {0x46,0x01});
                    list.add(messageBytes);

                    for(byte[] tmp: list){
                        mmOutputStream.write(tmp);
                    }
                     */

                    data=data.substring(8, data.length() - 8);
                    String[] stringArray2 = data.split("\\\\");
                    ArrayList<String> lineas2 = new ArrayList<>();
                    for(int i=0; i<stringArray2.length;i++){
                        if(stringArray2[i].length()>1) {
                            stringArray2[i] = stringArray2[i].substring(1) + "\r\n";
                            lineas2.add(stringArray2[i]);
                        }
                    }
                    lineas2.add("\r\n");
                    mmOutputStream.write((byte) 0x1b); //ESC
                    mmOutputStream.write((byte) 0x40); //@
                    mmOutputStream.write((byte) 0x1b);
                    mmOutputStream.write((byte) 0x1e);
                    mmOutputStream.write((byte) 0x46);
                    mmOutputStream.write((byte) 0x01);

                    for(String tmp: lineas2) {
                        mmOutputStream.write(tmp.getBytes());
                    }

                    mmOutputStream.write("\n".getBytes());
                    mmOutputStream.write("\n".getBytes());
                    mmOutputStream.write("\n".getBytes());
                    break;

                case "STAR mPOP-I0242":
                    data=data.substring(8, data.length() - 8);
                    String[] stringArray21 = data.split("\\\\");
                    ArrayList<String> lineas21 = new ArrayList<>();
                    for(int i=0; i<stringArray21.length;i++){
                        if(stringArray21[i].length()>1) {
                            stringArray21[i] = stringArray21[i].substring(1) + "\r\n";
                            lineas21.add(stringArray21[i]);
                        }
                    }
                    lineas21.add("\r\n");

                    mmOutputStream.write((byte) 0x1b);
                    mmOutputStream.write((byte) 0x1e);
                    mmOutputStream.write((byte) 0x46);
                    mmOutputStream.write((byte) 0x01);
                    for(String tmp: lineas21) {
                        mmOutputStream.write(tmp.getBytes());
                    }
                    mmOutputStream.write((byte) 0x1b); //cortar ticket
                    mmOutputStream.write((byte) 0x64);
                    mmOutputStream.write((byte) 0x02);
                    mmOutputStream.write((byte) 0x1d);
                    mmOutputStream.write((byte) 0x56);
                    mmOutputStream.write((byte) 0x48);
                    mmOutputStream.write((byte) 0x07); //abrir cajon
                    mmOutputStream.write((byte) 0x1a);
                    break;
                case "TSP100-F0038":
                   /* data=data.substring(8, data.length() - 8);
                    String[] stringArray1 = data.split("\\\\");
                    ArrayList<String> lineas1 = new ArrayList<>();
                    for(int i=0; i<stringArray1.length;i++){
                        if(stringArray1[i].length()>1) {
                            stringArray1[i] = stringArray1[i].substring(1);
                            lineas1.add(stringArray1[i]);
                        }
                    }*/

                    ArrayList<byte[]> list2 = new ArrayList<byte[]>();
                    list2.add(("\u001b*rA").getBytes());
                    //  list.add(("\u001b*rA\u001b*rT2\0\u001b*rQ1\0\u001b*rP0\0\u001b*rml0\0\u001b*rmr0\0\u001b*rF9\0\u001b*rE9\0").getBytes());
                    list2.add(messageBytes);
                   /* for(String tmp1: lineas1){
                        list.add(tmp1.getBytes());
                    }*/

                   /* list.add(Helper.createRasterCommand(lineas1.get(lineas1.size()-5), 10, Typeface.DEFAULT.getStyle()));
                    list.add(Helper.createRasterCommand(lineas1.get(lineas1.size()-4), 10, Typeface.DEFAULT.getStyle()));
                    list.add(Helper.createRasterCommand(lineas1.get(lineas1.size()-3), 10, Typeface.DEFAULT.getStyle()));
                    list.add(Helper.createRasterCommand(lineas1.get(lineas1.size()-2), 10, Typeface.DEFAULT.getStyle()));
                    list.add(Helper.createRasterCommand(lineas1.get(lineas1.size()-1), 10, Typeface.DEFAULT.getStyle()));*/

                    for(byte[] tmp: list2){
                        mmOutputStream.write(tmp);
                    }

                    break;
                default:
                    mmOutputStream.write(data.getBytes());

                    break;



            }
            mmOutputStream.flush();
            mmOutputStream.close();
            //   mmOutputStream.flush();
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new Exception("Error al imprimir: [" + e.getMessage() + "]");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al imprimir: [" + e.getMessage() + "]");
        }
    }

    public void close() {
        try {
            if (mmSocket != null) {
                mmSocket.close();
            }
            if (mmOutputStream != null) {
                mmOutputStream.close();
            }

            if(esCaja == true){
                Intent intent = new Intent(Blue.this, MenuCajas.class);
                intent.putExtra("user", "name");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();


            }
            if(esCaja==false) {
                Intent intent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                intent.putExtra("user", "name");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                finish();


            }  else {
                Intent localIntent = new Intent(Blue.this.getApplicationContext(), MainActivity.class);
                localIntent.putExtra("usercompany", "company");
                localIntent.putExtra("username", "name");
                startActivity(localIntent);
                finish();

            }
        } catch (Exception e) {

        }
    }


    private void build_popup2() {
        listaBT.clear();
        listaBTS.clear();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set < BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device: pairedDevices) {
                String deviceBTName = device.getName();
                listaBT.add(device);
                listaBTS.add(deviceBTName);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Blue.this);
        builder.setTitle("Dispositivos");
        builder.setSingleChoiceItems(listaBTS.toArray(new String[listaBTS.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                temp = which;
            }


        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = temp;
                switch (selected) {
                    default: try {
                        Toast.makeText(getApplicationContext(), listaBT.get(selected).getName(), Toast.LENGTH_SHORT).show();
                        openConnection(listaBT.get(selected));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                        break;
                }

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog al = builder.create();
        al.show();
    }

    private String translateDeviceClass(int deviceClass) {
        switch (deviceClass) {
            case UsbConstants.USB_CLASS_APP_SPEC:
                return "Application specific USB class";
            case UsbConstants.USB_CLASS_AUDIO:
                return "USB class for audio devices";
            case UsbConstants.USB_CLASS_CDC_DATA:
                return "USB class for CDC devices (communications device class)";
            case UsbConstants.USB_CLASS_COMM:
                return "USB class for communication devices";
            case UsbConstants.USB_CLASS_CONTENT_SEC:
                return "USB class for content security devices";
            case UsbConstants.USB_CLASS_CSCID:
                return "USB class for content smart card devices";
            case UsbConstants.USB_CLASS_HID:
                return "USB class for human interface devices (for example, mice and keyboards)";
            case UsbConstants.USB_CLASS_HUB:
                return "USB class for USB hubs";
            case UsbConstants.USB_CLASS_MASS_STORAGE:
                return "USB class for mass storage devices";
            case UsbConstants.USB_CLASS_MISC:
                return "USB class for wireless miscellaneous devices";
            case UsbConstants.USB_CLASS_PER_INTERFACE:
                return "USB class indicating that the class is determined on a per-interface basis";
            case UsbConstants.USB_CLASS_PHYSICA:
                return "USB class for physical devices";
            case UsbConstants.USB_CLASS_PRINTER:
                return "USB class for printers";
            case UsbConstants.USB_CLASS_STILL_IMAGE:
                return "USB class for still image devices (digital cameras)";
            case UsbConstants.USB_CLASS_VENDOR_SPEC:
                return "Vendor specific USB class";
            case UsbConstants.USB_CLASS_VIDEO:
                return "USB class for video devices";
            case UsbConstants.USB_CLASS_WIRELESS_CONTROLLER:
                return "USB class for wireless controller devices";
            default:
                return "Unknown USB class!";
        }
    }

    private void print(UsbDeviceConnection connection, UsbInterface intrface) {

        String data = message;
        data=data.substring(8, data.length() - 8);
        String[] stringArray2 = data.split("\\\\");
        ArrayList<String> lineas2 = new ArrayList<>();
        for(int i=0; i<stringArray2.length;i++){
            if(stringArray2[i].length()>1) {
                stringArray2[i] = stringArray2[i].substring(1) + "\r\n";
                lineas2.add(stringArray2[i]);
            }
            /*else{
                stringArray2[i] = stringArray2[i].substring(4) + "\r\n";
                Toast.makeText(this, "stringArray2[i] " +stringArray2[i], Toast.LENGTH_SHORT).show();
                lineas2.add(stringArray2[i]);

            }*/
        }

        //lineas2.add("\r\n");





        // byte[] testBytes = test.getBytes();

        if (intrface == null) {
            Toast.makeText(this, "INTERFACE IS NULL", Toast.LENGTH_SHORT).show();
        }
        if (connection == null) {
            Toast.makeText(this, "CONNECTION IS NULL", Toast.LENGTH_SHORT).show();
        }

        if (forceCLaim == null) {
            Toast.makeText(this, "FORCE CLAIM IS NULL", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "connection: " + connection, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "intrface: " + intrface.toString(), Toast.LENGTH_SHORT).show();

        connection.claimInterface(intrface, forceCLaim);

        for(String tmp: lineas2) {
            connection.bulkTransfer(mEndPoint, tmp.getBytes(), tmp.getBytes().length, 0);
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndPoint = mInterface.getEndpoint(0);
                            mConnection = mUsbManager.openDevice(device);

                            //setup();
                        }
                    }
                    else {
                        //Log.d("SUB", "permission denied for device " + device);
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };


    public void listarDispositivosUsb() {
        HashMap<String, UsbDevice> mDeviceList;
        Iterator<UsbDevice> mDeviceIterator;

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mDeviceList = mUsbManager.getDeviceList();
        mDeviceIterator = mDeviceList.values().iterator();
        Toast.makeText(getApplicationContext(), "Device List Size: " + String.valueOf(mDeviceList.size()), Toast.LENGTH_SHORT).show();
        String usbDevice = "";

        while (mDeviceIterator.hasNext()) {
            UsbDevice usbDevice1 = mDeviceIterator.next();
            usbDevice += "\n" +
                    "DeviceID: " + usbDevice1.getDeviceId() + "\n" +
                    "DeviceName: " + usbDevice1.getDeviceName() + "\n" +
                    "DeviceClass: " + usbDevice1.getDeviceClass() + " - " + translateDeviceClass(usbDevice1.getDeviceClass()) + "\n" +
                    "DeviceSubClass: " + usbDevice1.getDeviceSubclass() + "\n" +
                    "VendorID: " + usbDevice1.getVendorId() + "\n" +
                    "ProductID: " + usbDevice1.getProductId() + "\n";


            /*RadioButton rb = new RadioButton(this);
            rb.setText("USB: " + usbDevice1.getDeviceId());
            if(Integer.valueOf(Build.VERSION.SDK_INT) >= 21){
                rb.setText("USB: " + usbDevice1.getDeviceId() + "" + usbDevice1.getProductName());
                dispositivosUSB.put(usbDevice1.getDeviceId()+ "" + usbDevice1.getProductName(),usbDevice1);
            } else{
                dispositivosUSB.put(String.valueOf(usbDevice1.getDeviceId()),usbDevice1);
            }
            radioGroup.addView(rb);*/

            dispositivoUSB = usbDevice1;


            int interfaceCount = usbDevice1.getInterfaceCount();
            Toast.makeText(getApplicationContext(), "INTERFACE COUNT: " + String.valueOf(interfaceCount), Toast.LENGTH_SHORT).show();

            mDevice = usbDevice1;

            if (mDevice == null) {
                Toast.makeText(getApplicationContext(), "mDevice is null", Toast.LENGTH_SHORT).show();
            } else {
                //  TableRow tr = new TableRow(this);

                Toast.makeText(getApplicationContext(), "mDevice is not null", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), usbDevice, Toast.LENGTH_SHORT).show();


        }
    }

    public void imprimirUSB(UsbDevice device){
        if(device.getVendorId()==2843 || device.getVendorId()==1305) {
            mPermissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
            Toast.makeText(getApplicationContext(), " aqui " , Toast.LENGTH_SHORT).show();
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            mUsbManager.requestPermission(mDevice, mPermissionIntent);
            // print(mConnection, mInterface);
        }

    }


}
