package com.sgxy_ict.onenet_sdk_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chinamobile.iot.onenet.edp.CmdMsg;
import com.chinamobile.iot.onenet.edp.Common;
import com.chinamobile.iot.onenet.edp.ConnectRespMsg;
import com.chinamobile.iot.onenet.edp.EdpMsg;
import com.chinamobile.iot.onenet.edp.PingRespMsg;
import com.chinamobile.iot.onenet.edp.PushDataMsg;
import com.chinamobile.iot.onenet.edp.SaveDataMsg;
import com.chinamobile.iot.onenet.edp.SaveRespMsg;
import com.chinamobile.iot.onenet.edp.toolbox.EdpClient;
import com.chinamobile.iot.onenet.edp.toolbox.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import casio.serial.SerialPort;

public class MainActivity extends AppCompatActivity {

    //根据你的项目，修改这里的代码，替换id和authInfo
    private String id = "1074803160";           //设备ID
    private int connetcType = 1;      //连接类型，1或2，参考模拟器
    private String authInfo = "R=J4k2v8Im6z7mWDxx7ODep7tdQ=";     //api-key
    private int encryptType = -1;    //明文通信

    String Debug = "Debug";

    final String TTY_DEV = "/dev/ttySAC1";
    final int bps = 115200;
    SerialPort mSerialPort = null;                //串口设备描述
    protected OutputStream mOutputStream;        //串口输出描述
    private InputStream mInputStream;
    Auto_Weight weight_thread = new Auto_Weight();//称重
    boolean auto_start = true;
    int search_interval = 800;    //延时时间
    TextView ET_debug, ET_translate, connect_status, ET_Temp, ET_Humi, ET_X, ET_Y,
            ET_RENTI, ET_CHAOSHENGBO, ET_YALI, ET_KERAN, ET_CO, ET_LIGHT, ET_ZHENDONG, ET_VOICE;
    //温度，湿度，X轴，Y轴，超声波，压力，可燃气体，一氧化碳，光照，震动，声音
    Button BTN_Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        find_id();

        // 1、初始化SDK
        EdpClient.initialize(this, connetcType, id, authInfo);
        // 2、设置接收响应的回调
        EdpClient.getInstance().setListener(mEdpListener);
        // 3、设置自动发送心跳的周期（默认4min）
        EdpClient.getInstance().setPingInterval(3 * 60 * 1000);
        // 4、建立TCP连接
        EdpClient.getInstance().connect();

        if (Common.Algorithm.NO_ALGORITHM == encryptType) {
            // 5、如果使用明文通信，则建立连接后直接发送连接请求
            EdpClient.getInstance().sendConnectReq();
        } else if (Common.Algorithm.ALGORITHM_AES == encryptType) {
            // 6、如果使用加密通信，则先发送加密请求，然后在加密响应回调中发送连接请求
            EdpClient.getInstance().requestEncrypt(Common.Algorithm.ALGORITHM_AES);
        }
        weight_thread.start();//线程启动
    }

    public void upload_zigbee() {

        String Str_CHAOSHENGBO = ET_CHAOSHENGBO.getText().toString();
//超声波
        JSONObject UT = new JSONObject();
        try {
            UT.put("超声波", Float.parseFloat(Str_CHAOSHENGBO));

            EdpClient.getInstance().saveData(
                    id, 3, null, UT.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //气压
        String Str_YALI = ET_YALI.getText().toString();
        JSONObject qiya = new JSONObject();
        try {
            qiya.put("气压", Float.parseFloat(Str_YALI));

            EdpClient.getInstance().saveData(
                    id, 3, null, qiya.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void upload_lora() {
        //可燃气体
        String Str_KEYAN = ET_KERAN.getText().toString();
        JSONObject keyan = new JSONObject();
        try {
            keyan.put("可燃气体", Float.parseFloat(Str_KEYAN));

            EdpClient.getInstance().saveData(
                    id, 3, null, keyan.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //一氧化碳
        String Str_CO = ET_CO.getText().toString();
        JSONObject co = new JSONObject();
        try {
            co.put("一氧化碳", Float.parseFloat(Str_CO));

            EdpClient.getInstance().saveData(
                    id, 3, null, co.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upload_wifi() {
        //GPS
        String Xmove = ET_X.getText().toString();
        String Ymove = ET_Y.getText().toString();
        JSONObject gps = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            gps.put("X轴", Float.parseFloat(Xmove));
            gps.put("Y轴", Float.parseFloat(Ymove));
            SaveDataMsg.packSaveData1Msg(
                    data, null, "三轴加速度", null, gps);
            EdpClient.getInstance().saveData(
                    id, 1, null, data.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //光照
        String Str_LIGHT = ET_LIGHT.getText().toString();
        JSONObject LIGHT = new JSONObject();
        try {
            LIGHT.put("光照", Float.parseFloat(Str_LIGHT));

            EdpClient.getInstance().saveData(
                    id, 3, null, LIGHT.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //震动
        String Str_ZHENDONG = ET_ZHENDONG.getText().toString();
        JSONObject VIB = new JSONObject();
        try {
            VIB.put("震动", Str_ZHENDONG);

            EdpClient.getInstance().saveData(
                    id, 3, null, VIB.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void upload_bluetooth() {
        //温湿度
        String Str_temp = ET_Temp.getText().toString();
        String Str_Humi = ET_Humi.getText().toString();

        JSONObject temp = new JSONObject();
        try {
            temp.put("温度", Integer.parseInt(Str_temp));
            temp.put("湿度", Integer.parseInt(Str_Humi));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EdpClient.getInstance().saveData(
                id, 3, null, temp.toString().getBytes());

        String Str_VOICE = ET_VOICE.getText().toString();
        JSONObject VOICE = new JSONObject();
        try {
            VOICE.put("声音", Str_VOICE);

            EdpClient.getInstance().saveData(
                    id, 3, null, VOICE.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String Str_RENTI = ET_RENTI.getText().toString();

        JSONObject RENTI = new JSONObject();
        try {
            RENTI.put("人体感应", Str_RENTI);

            EdpClient.getInstance().saveData(
                    id, 3, null, RENTI.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void find_id() {
        ET_Temp = findViewById(R.id.wendu);
        ET_Humi = findViewById(R.id.shidu);
        ET_X = findViewById(R.id.xmove);
        ET_Y = findViewById(R.id.ymove);
        connect_status = findViewById(R.id.status);
        ET_CHAOSHENGBO = findViewById(R.id.chaoshengbo);
        ET_YALI = findViewById(R.id.yali);
        ET_KERAN = findViewById(R.id.keranqiti);
        ET_CO = findViewById(R.id.co);
        ET_LIGHT = findViewById(R.id.guangzhao);
        ET_ZHENDONG = findViewById(R.id.zhendong);
        ET_VOICE = findViewById(R.id.voice);
        ET_debug = findViewById(R.id.debug);
        ET_translate = findViewById(R.id.translate);
        ET_RENTI = findViewById(R.id.rentiganying);
    }

    private Listener mEdpListener = new Listener() {

        @Override
        public void onReceive(List<EdpMsg> msgList) {
            if (null == msgList) {
                return;
            }
            for (EdpMsg msg : msgList) {
                if (null == msg) {
                    continue;
                }
                switch (msg.getMsgType()) {
                    // 连接响应
                    case Common.MsgType.CONNRESP:
                        ConnectRespMsg connectRespMsg = (ConnectRespMsg) msg;
                        if (connectRespMsg.getResCode() == Common.ConnResp.ACCEPTED) {
                            connect_status.setText("已连接到服务器");

                            Log.d(Debug, "连接成功 getResCode: " + connectRespMsg.getResCode());
                        } else {
                            connect_status.setText("连接失败到服务器");
                            Toast.makeText(MainActivity.this,
                                    "连接失败，返回值" + connectRespMsg.getResCode(), Toast.LENGTH_SHORT).show();
                            Log.d(Debug, "连接失败 getResCode: " + connectRespMsg.getResCode());
                        }
                        break;
                    // 心跳响应
                    case Common.MsgType.PINGRESP:
                        PingRespMsg pingRespMsg = (PingRespMsg) msg;
                        Toast.makeText(MainActivity.this,
                                "心跳响应", Toast.LENGTH_SHORT).show();
                        Log.d(Debug, "心跳响应");
                        break;
                    // 存储确认
                    case Common.MsgType.SAVERESP:
                        SaveRespMsg saveRespMsg = (SaveRespMsg) msg;
                        Toast.makeText(MainActivity.this,
                                "存储确认" + new String(saveRespMsg.getData()), Toast.LENGTH_SHORT).show();
                        Log.d(Debug, "存储确认" + new String(saveRespMsg.getData()));
                        break;
                    // 转发（透传）
                    case Common.MsgType.PUSHDATA:
                        PushDataMsg pushDataMsg = (PushDataMsg) msg;
                        Toast.makeText(MainActivity.this,
                                "透传：" + new String(pushDataMsg.getData()), Toast.LENGTH_SHORT).show();
                        Log.d(Debug, "透传：" + new String(pushDataMsg.getData()));
                        break;
                    // 存储（转发）
                    case Common.MsgType.SAVEDATA:
                        SaveDataMsg saveDataMsg = (SaveDataMsg) msg;
                        for (byte[] bytes : saveDataMsg.getDataList()) {
                            //       Toast.makeText(MainActivity.this,
                            //           "存储（转发）："+ new String(bytes), Toast.LENGTH_SHORT).show();
                            Log.d(Debug, "存储（转发）：" + new String(bytes));
                        }
                        break;
                    // 命令请求
//                    //十六进制无法正确显示？
                    case Common.MsgType.CMDREQ:
                        CmdMsg cmdMsg = (CmdMsg) msg;
                        Toast.makeText(MainActivity.this,
                                "命令请求：\n cmdId:" + cmdMsg.getCmdId() +
                                        "\n cmdData：" + new String(cmdMsg.getData()), Toast.LENGTH_LONG).show();
                        Log.d(Debug, "命令请求：\n cmdId:" + cmdMsg.getCmdId() +
                                "\n cmdData：" + new String(cmdMsg.getData()));
                        EdpClient.getInstance().sendCmdResp(cmdMsg.getCmdId(), "发送命令成功".getBytes());
                        break;
                    // 加密响应
                    case Common.MsgType.ENCRYPTRESP:
                        break;
                }
            }
        }

        @Override
        public void onFailed(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onDisconnect() {

        }
    };

    class Auto_Weight extends Thread {
        @Override
        public void run() {
            try {
                mSerialPort = new SerialPort(new File(TTY_DEV), bps, 0);//串口名，波特率，接收
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();//将错误信息打印在控制台
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int buf_num = 0;
            byte[] receive_buf = new byte[20];            //组织接收
            boolean receive_state = false;
            String guangzhao = "0";
            String chaoshengbo = "0";
            String qiya = "0";
            String rentiganying = "无人";
            String zhendong = "无";
            String shengying = "无";
            String kernqiti = "0";
            String Co = "0";
            String wendu = "0";
            String shidu = "0";
            String XMOVE = "0";
            String YMOVE = "0";
            String ZMOVE = "0";
            while (true) {
                if (auto_start) {
                    //每次接收一个字节,然后甩出去!累计13个字节,组成一个字符串
                    byte[] buf = new byte[1];            //组织接收
                    int size;
                    try {
                        size = mInputStream.read(buf);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //如果获取到了0x02,开始记录接下来的13个字节,最后组成字符串
                    if (receive_state || buf[0] == 0x02) {
                        receive_buf[buf_num] = (byte) (0xff & buf[0]);
                        buf_num++;
                        receive_state = true;
                        if (buf_num == 13) {
                            String receice_string = byte2HexStr(receive_buf);
                            String receice_dev = "Zigbee";
                            Log.d("MMMMMMMMMM", receice_string);

                            //数据解析的部分如下：
                            //首先判断是来自哪个网关，wifi？Rola？Zigbee？BlueTooth？
                            //通过[2]和[3]来判断。

                            //ZigBee网关
                            if (receive_buf[2] == (byte) 0xB8) {
                                receice_dev = "Zigbee";
                                if (receive_buf[10] == (byte) 0x06) {
                                    int smoke = Integer.parseInt(String.valueOf(receive_buf[11]));
                                    receice_dev = receice_dev + "     超声波传感器数据是：" + smoke;
                                    chaoshengbo = "" + smoke;
                                }
                            }

                            if (receive_buf[2] == (byte) 0xB8) {
                                receice_dev = "Zigbee";
                                if (receive_buf[10] == (byte) 0x0e) {
                                    int qwe = Integer.parseInt(String.valueOf(receive_buf[11]));
                                    receice_dev = receice_dev + "     气压传感器数据是：" + qwe;
                                    qiya = "" + qwe;
                                }
                            }
                            //lora网关
                            if (receive_buf[2] == (byte) 0xBE) {
                                receice_dev = "Lora";
                                if (receive_buf[9] == (byte) 0x04) {
                                    int smoke = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    receice_dev = receice_dev + "     烟雾传感器数据是：" + smoke;
                                    kernqiti = "" + smoke;
                                }
                                if (receive_buf[9] == (byte) 0x0B) {
                                    int CO = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    receice_dev = receice_dev + "     一氧化碳传感器数据是：" + CO;
                                    Co = "" + CO;
                                }

                            }

                            //wifi网关
                            if (receive_buf[2] == (byte) 0xBA) {
                                receice_dev = "wifi";
                                if (receive_buf[9] == (byte) 0x02) {
                                    int asd = receive_buf[10] & 0xff;
                                    receice_dev = receice_dev + "     光照传感器数据是：" + asd;
                                    guangzhao = "" + asd;
                                }
                                if (receive_buf[9] == (byte) 0x03) {
                                    int CO = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    if (CO == 1) {
                                        zhendong = "有";
                                    } else {
                                        zhendong = "没";
                                    }
                                }
                                if (receive_buf[9] == (byte) 0x07) {
                                    int xmove = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    int ymove = Integer.parseInt(String.valueOf(receive_buf[11]));
                                    int zmove = Integer.parseInt(String.valueOf(receive_buf[12]));
                                    receice_dev = receice_dev + "   三轴传感器数据是：" + xmove + ymove + zmove;
                                    XMOVE = "" + xmove;
                                    YMOVE = "" + ymove;
                                    ZMOVE = "" + zmove;
                                }
                            }
                            //蓝牙网关
                            if (receive_buf[2] == (byte) 0xBB) {
                                receice_dev = "bluetooth";
                                if (receive_buf[9] == (byte) 0x01) {
                                    int smoke = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    if (smoke == 1) {
                                        rentiganying = "有人";
                                    } else {
                                        rentiganying = "没人";
                                    }
                                    receice_dev = receice_dev + "   人体感应传感器数据是：" + rentiganying;
                                }
                                if (receive_buf[9] == (byte) 0x00) {
                                    int temp = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    int humi = Integer.parseInt(String.valueOf(receive_buf[11]));
                                    receice_dev = receice_dev + "   温湿度传感器数据是：" + temp + humi;
                                    wendu = "" + temp;
                                    shidu = "" + humi;
                                }

                                if (receive_buf[9] == (byte) 0x11) {
                                    int voice = Integer.parseInt(String.valueOf(receive_buf[10]));
                                    if (voice == 1) {
                                        shengying = "有";
                                    } else {
                                        shengying = "无";
                                    }
                                }
                            }
                            Message msg = new Message();
                            String get_weight = receice_string.toString();
                            //该部分是传参并更新控件
                            Bundle bundle = new Bundle();
                            msg.what = 0;
                            bundle.putString("from_dev", receice_dev);
                            bundle.putString("get_weight", get_weight);
                            bundle.putString("get_guangzhao", guangzhao);
                            bundle.putString("get_chaoshengbo", chaoshengbo);
                            bundle.putString("get_rentiganying", rentiganying);
                            bundle.putString("get_kerenqiti", kernqiti);
                            bundle.putString("get_co", Co);
                            bundle.putString("get_temp", wendu);
                            bundle.putString("get_humi", shidu);
                            bundle.putString("get_x", XMOVE);
                            bundle.putString("get_y", YMOVE);
                            bundle.putString("get_z", ZMOVE);
                            bundle.putString("get_qiya", qiya);
                            bundle.putString("get_voice", shengying);
                            bundle.putString("get_zhendong", zhendong);
                            msg.setData(bundle);
                            //发送消息到Handler
                            handler.sendMessage(msg);
                            switch (receive_buf[2]) {
                                case (byte) 0xB8:
                                    upload_zigbee();
                                    break;
                                case (byte) 0xBA:
                                    upload_wifi();
                                    break;
                                case (byte) 0xBB:
                                    upload_bluetooth();
                                    break;
                                case (byte) 0xBE:
                                    upload_lora();
                                    break;
                            }
                            buf_num = 0;
                            receive_state = false;
                        } else {
                        }
                    } else {
                    }
                }

            }
        }
    }

    public Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    //取出参数更新控件
                    ET_debug.setText("" + msg.getData().getString("get_weight"));
                    ET_translate.setText("" + msg.getData().getString("from_dev"));
                    ET_LIGHT.setText("" + msg.getData().getString("get_guangzhao"));
                    ET_CHAOSHENGBO.setText("" + msg.getData().getString("get_chaoshengbo"));
                    ET_RENTI.setText("" + msg.getData().getString("get_rentiganying"));
                    ET_KERAN.setText("" + msg.getData().getString("get_kerenqiti"));
                    ET_CO.setText("" + msg.getData().getString("get_co"));
                    ET_Temp.setText("" + msg.getData().getString("get_temp"));
                    ET_Humi.setText("" + msg.getData().getString("get_humi"));
                    ET_X.setText("" + msg.getData().getString("get_x"));
                    ET_Y.setText("" + msg.getData().getString("get_y"));
                    ET_YALI.setText("" + msg.getData().getString("get_qiya"));
                    ET_VOICE.setText("" + msg.getData().getString("get_voice"));
                    ET_ZHENDONG.setText("" + msg.getData().getString("get_zhendong"));
                }
                break;
                case 1: {
                    ET_debug.setText("0.00");
                }
                break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * bytes转换成十六进制字符串
     *
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

}
