package com.example.tyhj.schoolmsg;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.ant.liao.GifView;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;
import com.turing.androidsdk.asr.VoiceRecognizeListener;
import com.turing.androidsdk.asr.VoiceRecognizeManager;
import com.turing.androidsdk.tts.TTSListener;
import com.turing.androidsdk.tts.TTSManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adpter.ChatAdpter;
import myViews.SharedData;
import myViews.StatusBarUtil;
import publicinfo.Msg_chat;
import publicinfo.MyFunction;
import service.ChatService;
import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

@EActivity(R.layout.activity_rebot)
public class Rebot extends AppCompatActivity {

    private final String TAG = Rebot.class.getSimpleName();
    private TTSManager ttsManager;
    private VoiceRecognizeManager recognizerManager;
    private TuringApiManager mTuringApiManager;
    /** 返回结果，开始说话 */
    public final int SPEECH_START = 0;
    /** 开始识别 */
    public final int RECOGNIZE_RESULT = 1;
    /** 开始识别 */
    public final int RECOGNIZE_START = 2;

    /**
     * 申请的turing的apikey
     *  **/
    private final String TURING_APIKEY = "f72d289b48934c57a2c7347cc15b4f9b";
    /**
     * 申请的secret
     * **/
    private final String TURING_SECRET = "320b37e7859db612";
    /**
     * 填写一个任意的标示，没有具体要求，，但一定要写，
     * **/
    private final String UNIQUEID = "233333333";
    //百度key
    private final String BD_APIKEY = "ZC2NNfFUkg8rxgmVkfBC6ycX";
    //百度screte
    private final String BD_SECRET = "9a98e53b2ef7339bf03793f0b53fc7e4";



    List<Msg_chat> msg_chatList;
    ChatAdpter chatAdpter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebot);
        StatusBarUtil.setColor(this, Color.parseColor("#00000000"));
        init();
    }


    @ViewById
    EditText et_text_send;

    @ViewById
    ImageView iv_back;

    @ViewById
    GifView iv_heagImage;

    @ViewById
    ImageView iv_sound;

    @ViewById
    ListView lv_msg;

    //显示发送的消息
    private void initSendMsg(String Url,int who) {
        if(Url==null||Url.equals(""))
            return;
        String time;
        if(msg_chatList.size()>0)
            time = MyFunction.getTime(chatAdpter.getMsg_chats().get(chatAdpter.getCount() - 1).getTime());
        else
            time="刚刚";
        if (time != null) {
            chatAdpter.add(new Msg_chat(0, 0, 0, time, null, null, null, MyFunction.getTime()));
        }
        chatAdpter.add(new Msg_chat(who, 0, -1, Url, null, null, "tyhj", MyFunction.getTime()));
        et_text_send.setText("");
    }


    //初始化消息列表
    private void initListView() {
        msg_chatList=new SharedData(Rebot.this).getData("冰果");
        if(msg_chatList==null) {
            msg_chatList = new ArrayList<Msg_chat>();
        }
        setMsgStatus();
        chatAdpter=new ChatAdpter(Rebot.this,0,msg_chatList);
        lv_msg.setAdapter(chatAdpter);
        iv_heagImage.setClipToOutline(true);
        iv_heagImage.setOutlineProvider(MyFunction.getOutline(true,10,0));
        lv_msg.setSelection(chatAdpter.getCount()-1);
        //单人聊天的头像
        chatAdpter.setHeadImage(getResources().getDrawable(R.mipmap.rebbot_head2));
        lv_msg.setOnScrollListener(new PauseOnScrollListener(chatAdpter.getImageLoader(),true,true));
    }


    //设置消息为已读
    private void setMsgStatus() {
        for(int i=msg_chatList.size()-1;i>=0;i--){
            if(msg_chatList.get(i).getWho()==2&&msg_chatList.get(i).getStatus()!=2){
                msg_chatList.get(i).setStatus(2);
            }else {
                break;
            }
        }
    }


    @AfterViews
    void afterView(){
        initListView();
        iv_heagImage.setGifImage(R.mipmap.rebot_head);
        iv_heagImage.setGifImageType(GifView.GifImageType.COVER);
        iv_heagImage.setShowDimension(110, 110);
    }

    @Click(R.id.iv_back)
    void back(){
        ChatService.savaDate(chatAdpter.getMsg_chats(),"冰果");
        recognizerManager.stopRecognize();
        ttsManager.stopTTS();
        this.finish();
    }

    @Click(R.id.iv_sound)
    void send(){
        String str=et_text_send.getText().toString();
        initSendMsg(str,1);
        mTuringApiManager.requestTuringAPI(str);
    }


    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SPEECH_START:
                    ttsManager.startTTS((String) msg.obj);
                    initSendMsg((String) msg.obj,2);
                    chatAdpter.notifyDataSetChanged();
                    break;
                case RECOGNIZE_RESULT:
                    initSendMsg((String) msg.obj,1);
                    break;
                case RECOGNIZE_START:
                    break;
                default:
                    break;
            }
        };
    };


    private void init() {
        /** 支持百度、讯飞，需自行去相关平台申请appid，并导入相应的jar和so文件 */
        recognizerManager = new VoiceRecognizeManager(this, BD_APIKEY, BD_SECRET);
        ttsManager = new TTSManager(this, BD_APIKEY, BD_SECRET);
        recognizerManager.setVoiceRecognizeListener(myVoiceRecognizeListener);
        ttsManager.setTTSListener(myTTSListener);
        // turingSDK初始化
        SDKInitBuilder builder = new SDKInitBuilder(this)
                .setSecret(TURING_SECRET).setTuringKey(TURING_APIKEY).setUniqueId(UNIQUEID);
        SDKInit.init(builder,new InitListener() {
            @Override
            public void onFail(String error) {
                Log.d(TAG, error);
            }
            @Override
            public void onComplete() {
                // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
                mTuringApiManager = new TuringApiManager(Rebot.this);
                mTuringApiManager.setHttpListener(myHttpConnectionListener);
                ttsManager.startTTS("你好啊");
                initSendMsg("你好啊",2);
            }
        });
    }

    /**
     * 网络请求回调
     */
    HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

        @Override
        public void onSuccess(RequestResult result) {
            if (result != null) {
                try {
                    Log.d(TAG, result.getContent().toString());
                    JSONObject result_obj = new JSONObject(result.getContent()
                            .toString());
                    if (result_obj.has("text")) {
                        Log.d(TAG, result_obj.get("text").toString());
                        /*
                        *
                        * 获取的结果
                        *
                        * */
                        myHandler.obtainMessage(SPEECH_START,
                                result_obj.get("text")).sendToTarget();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException:" + e.getMessage());
                }
            }
        }

        @Override
        public void onError(ErrorMessage errorMessage) {
            Log.d(TAG, errorMessage.getMessage());
        }
    };


    /**
     * 语音识别回调
     */
    VoiceRecognizeListener myVoiceRecognizeListener = new VoiceRecognizeListener() {

        @Override
        public void onVolumeChange(int volume) {
            // 仅讯飞回调
        }

        @Override
        public void onStartRecognize() {
            // 仅针对百度回调
        }

        @Override
        public void onRecordStart() {

        }

        @Override
        public void onRecordEnd() {

        }

        @Override
        public void onRecognizeResult(String result) {
            Log.d(TAG, "识别结果：" + result);
            if (result == null ){
                recognizerManager.startRecognize();
                myHandler.sendEmptyMessage(RECOGNIZE_START);
                return;
            }
            /*
            * 开始发送文字
            *
            * */
            mTuringApiManager.requestTuringAPI(result);
            myHandler.obtainMessage(RECOGNIZE_RESULT, result).sendToTarget();

        }

        @Override
        public void onRecognizeError(String error) {
            Log.e(TAG, "识别错误：" + error);
            recognizerManager.startRecognize();
            myHandler.sendEmptyMessage(RECOGNIZE_START);

        }
    };


    /**
     * TTS回调
     */
    TTSListener myTTSListener = new TTSListener() {

        @Override
        public void onSpeechStart() {
            Log.d(TAG, "TTS Start!");
        }

        @Override
        public void onSpeechProgressChanged() {

        }

        @Override
        public void onSpeechPause() {
            Log.d(TAG, "TTS Pause!");
        }

        @Override
        public void onSpeechFinish() {
            recognizerManager.startRecognize();
            myHandler.obtainMessage(RECOGNIZE_START).sendToTarget();
        }

        @Override
        public void onSpeechError(int errorCode) {
            Log.d(TAG, "TTS错误，错误码：" + errorCode);
        }


        @Override
        public void onSpeechCancel() {
            Log.d(TAG, "TTS Cancle!");
        }
    };

    @Override
    public void onBackPressed() {
        ChatService.savaDate(chatAdpter.getMsg_chats(),"冰果");
        recognizerManager.stopRecognize();
        ttsManager.stopTTS();
        this.finish();
    }
}
