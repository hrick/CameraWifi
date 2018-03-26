package com.henrique.camerawifi;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import object.p2pipcam.nativecaller.NativeCaller;

public class BoroscopioNativeActivity extends AppCompatActivity implements BridgeService.PlayInterface, BridgeService.AddCameraInterface, BridgeService.IpcamClientInterface {
    public static final String SERVER_STRING = "LPTDSXSWSQLOPGLQPAHYPHLRLKSTPITAPNIBIAHUEIEKAUEEASELAVAOSUPDPKLXSXSWSQPEPGLQPALOPHLRLKLNLSLU-$$";
    public static final String DID = "GOS007732DFBCA";
    public static final int STANDARD = 1;
    public static final int ZOOM = 2;
    private String okDID;
    private SurfaceHolder playHolder;
    private SurfaceView playSurface;
    private GLSurfaceView myGlSurfaceView;
    private surfaceCallback videoCallback;
    private Bitmap mBmp;
    Bitmap bmpsss;
    MyRender myRender;
    private volatile boolean isTakepic;
    private int streamType;
    private int mScreenWidth;
    private int mScreenHeight;
    private final String PICTURE_DIR = File.separator + "GD8743" + File.separator + "Picture" + File.separator;
    public String sdPictureFile;
    private volatile boolean isSreach;
    private Button btTirarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startPPPP(DID, "admin", "");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BridgeService.setPlayInterface(this);
        BridgeService.setAddCameraInterface(this);
        BridgeService.setIpcamClientInterface(this);
        Intent intent = new Intent();
        intent.setClass(this, BridgeService.class);

        startService(intent);
        new Thread() {
            public void run() {
                NativeCaller.PPPPInitial(ContentCommon.SERVER_STRING);
                NativeCaller.Init();
                NativeCaller.StartSearch();
                BoroscopioNativeActivity.this.isSreach = true;

            }
        }.start();
        //bindService(intent, BridgeServiceConnnection, Context.BIND_AUTO_CREATE);


        setContentView(R.layout.play);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.mScreenWidth = dm.widthPixels;
        this.mScreenHeight = dm.heightPixels;
        streamType = STANDARD;
        this.videoCallback = new surfaceCallback();
        this.playSurface = (SurfaceView) findViewById(R.id.playSurface);
        this.btTirarFoto = (Button) findViewById(R.id.btTirarFoto);
        this.myGlSurfaceView = (GLSurfaceView) findViewById(R.id.myhsurfaceview);
        this.myRender = new MyRender(this.myGlSurfaceView);
       // resetGLSurfaceView();
        this.myGlSurfaceView.setRenderer(this.myRender);
        btTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakepic = true;
            }
        });
        this.playHolder = this.playSurface.getHolder();
        this.playHolder.setFormat(PixelFormat.RGB_565);
        this.playHolder.addCallback(this.videoCallback);
        this.playSurface.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        //BridgeService.resetParam();
        new Thread() {
            public void run() {
                BridgeService.resetParam();
            }
        }.start();
        this.sdPictureFile = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath())).append(this.PICTURE_DIR).toString();

    }

    private void resetGLSurfaceView() {
        int orientation = getResources().getConfiguration().orientation;
        FrameLayout.LayoutParams lp;
        if (orientation == STANDARD) {
            int width = this.mScreenWidth;
            int height = width;
//            switch (MSG_CAMERA_PARAM_READY) {
//                case NONE /*0*/:
//                    height = (width * MSG_CAMERA_PARAM_READY) / 4;
//                    Log.i("Screen", "RES_VGA");
//                    break;
//                case MSG_CAMERA_PARAM_READY /*3*/:
            height = (width * 9) / 16;
            Log.i("Screen", "RES_WXGA");
//                    break;

//            Object[] objArr = new Object[ZOOM];
//            objArr[NONE] = Integer.valueOf(width);
//            objArr[STANDARD] = Integer.valueOf(height);
            Log.i("Screen", "W " + width + " H " + height);
            lp = new FrameLayout.LayoutParams(width, height);
            //lp.gravity = 17;
            this.myGlSurfaceView.setLayoutParams(lp);
        }
    }

    @Override
    protected void onDestroy() {
        NativeCaller.StopPPPPLivestream(DID);
        BridgeService.setPlayInterface(null);
        NativeCaller.StopPPPP(DID);
        if (this.myRender != null) {
            this.myRender.destroyShaders();
        }
        if (this.isSreach) {
            NativeCaller.StopSearch();
        }
        super.onDestroy();

//        if (this.brodCast != null) {
//            unregisterReceiver(this.brodCast);
//        }
    }

    @Override
    public void callBaceVideoData(String sis, byte[] videobuf, int h264Data, int len, int width, int height, int iframe, int sec, int millsec, int frameNo) {
        Log.d("callBaceVideoData", "");
//        this.nVideoHeight = height;
//        this.nVideoWidth = width;
//        this.isH264 = true;
//        msg.what = STANDARD;
        if (this.isTakepic) {
            this.isTakepic = false;
            byte[] rgb = new byte[((width * height) * ZOOM)];
            NativeCaller.YUV4202RGB565(videobuf, rgb, width, height);
            ByteBuffer buffer = ByteBuffer.wrap(rgb);
            this.mBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            this.mBmp.copyPixelsFromBuffer(buffer);
            takePicture(this.mBmp);
        }
        this.myRender.writeSample(videobuf, width, height);
    }

    private void takePicture(Bitmap bmp) {
        this.bmpsss = bmp;
        new C02657().start();
    }

    class C02657 extends Thread {
        C02657() {
        }

        public void run() {
            BoroscopioNativeActivity.this.savePicToSDcard(BoroscopioNativeActivity.this.bmpsss);
        }
    }

    private synchronized void savePicToSDcard(Bitmap bmp) {
        createDir();
        Bitmap bitmap = bmp;
        saveBitmap(bitmap, "GD8743" + new Date().getTime() + ".jpg");
        Object[] objArr;
//        try {
//            String imagepath = this.sdPictureFile;
//            if (imagepath != null && imagepath.length() > 0) {
//                runOnUiThread(new C02668(imagepath));
//            }
//            //this.isPictSave = false;
//        } catch (Exception e) {
//            runOnUiThread(new C02679());
//            e.printStackTrace();
//           // this.isPictSave = false;
//        } catch (Throwable th) {
//           // this.isPictSave = false;
//        }
    }

    public void createDir() {
            File picDir = new File(this.sdPictureFile);
            if (!picDir.exists()) {
                boolean b = picDir.mkdirs();
            }

    }

    public void saveBitmap(Bitmap bitmap, String imageName) {
        File file = new File(this.sdPictureFile);
        if (!file.exists()) {
            boolean b = file.mkdirs();
            if (b) {
                Log.i("saveBitmap", "create dir:" + file.getAbsolutePath());
            }
        }
        File picName = new File(file, imageName);
        if (!picName.exists()) {
            try {
                picName.createNewFile();
                FileOutputStream fos = new FileOutputStream(picName);
                if (picName.getName().endsWith(".png")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } else if (picName.getName().endsWith(".jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                }
                fos.flush();
                fos.close();
                exportToGallery(picName.getAbsolutePath(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Uri exportToGallery(String filename, boolean isPic) {
        ContentValues values = new ContentValues(2);
        if (isPic)
            values.put("mime_type", "image/jpeg");
        values.put("_data", filename);
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + filename)));
        return uri;
    }

    @Override
    public void callBackAudioData(byte[] bArr, int i) {
        Log.d("callBackAudioData", "");

    }

    @Override
    public void callBackCameraParamNotify(String did, int resolution, int brightness, int contrast, int hue, int saturation, int osd_enable, int mode, int flip, int main_framerate, int sub_framerate, int video_bitrate, int speed, int ircut, int border_patrol, int left_right_patrol, int patrol_1_8, int watch_status, int alarm_out) {
        Log.d("ParamNotify", "");
    }

    @Override
    public void callBackH264Data(String did, byte[] h264, int size, int iframe, int sec, int millsec, int frameno) {
//        if (did.endsWith(DID)) {
//            if (this.isTakeVideo && this.takeVideoThread != null) {
//                this.takeVideoThread.addVideo(h264, STANDARD, this.nVideoWidth, this.nVideoHeight, iframe, sec, millsec, frameno);
//            }
//        }
    }

    @Override
    public void callBackMessageNotify(String str, int i, int i2) {
        Log.d("callBackH264Data", "");

    }

    @Override
    public void callBackSearchResultData(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
//        this.gotDeviceInfo = true;
//        Object[] objArr = new Object[STANDARD];
//        objArr[NONE] = "here start the media player. \u8fd9\u91cc\u53ef\u4ee5\u5f00\u542f\u89c6\u9891\u54af~";
//        dbg.m11i(objArr);
//        this.isSreach = false;
//        objArr = new Object[ZOOM];
//        objArr[NONE] = "NativeCaller";
//        objArr[STANDARD] = "StopSearch";
//        dbg.m11i(objArr);
//        this.strDID = strDeviceID;
        this.okDID = strDeviceID;
//        SystemValue.nowDID = this.strDID;
        getCameraParams();
    }

    @Override
    public void BSMsgNotifyData(String str, int i, int i2) {

        NativeCaller.StartPPPPLivestream(DID, streamType);
        NativeCaller.PPPPGetSystemParams(DID, 2);
        Log.d("BSMsgNotifyData", "");
    }

    @Override
    public void BSSnapshotNotify(String str, byte[] bArr, int i) {
        Log.d("BSSnapshotNotify", "");

    }

    @Override
    public void callBackUserParams(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        Log.d("callBackUserParams", "");

    }

//    private void startPPPP(String did, String admin, String s) {
//        NativeCaller.StartPPPP(did, admin, s, SERVER_STRING);
//    }


    private class surfaceCallback implements SurfaceHolder.Callback {
        private surfaceCallback() {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (holder == BoroscopioNativeActivity.this.playHolder) {
//                if (this.gotDeviceInfo) {
                NativeCaller.StartPPPPLivestream(DID, BoroscopioNativeActivity.this.streamType);
//                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    private void getCameraParams() {
        new Thread() {
            public void run() {
                NativeCaller.StopSearch();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startBoroscopio(BoroscopioNativeActivity.this.okDID, ContentCommon.DEFAULT_USER_NAME,ContentCommon.DEFAULT_USER_PWD);
                //NativeCaller.StartPPPP(BoroscopioNativeActivity.this.okDID, "admin", "", SERVER_STRING);
                // NativeCaller.StartPPPPLivestream(DID, BoroscopioNativeActivity.this.streamType);
//                NativeCaller.StartPPPP(DID, ContentCommon.DEFAULT_USER_NAME, ContentCommon.DEFAULT_USER_PWD);
//                objArr = new Object[PlayActivity.MSG_CAMERA_PARAM_READY];
//                objArr[PlayActivity.NONE] = "NativeCaller";
//                objArr[PlayActivity.STANDARD] = "StartPPPP";
//                objArr[PlayActivity.ZOOM] = PlayActivity.this.okDID;
//                dbg.m11i(objArr);
//                objArr = new Object[PlayActivity.STANDARD];
//                objArr[PlayActivity.NONE] = "NativeCaller.StartPPPP nowDID:" + SystemValue.nowDID;
//                dbg.m9d(objArr);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e2) {
//                    e2.printStackTrace();
//                }
//                Message message = new Message();
//                message.what = PlayActivity.MSG_CAMERA_STATUS_CALLBACK;
//                message.arg1 = PlayActivity.ZOOM;
//                PlayActivity.this.mHandler.sendMessage(message);
            }
        }.start();
    }

    public void startBoroscopio(String did, String user, String pwd) {
        String server = ContentCommon.SERVER_STRING;
        String serTag = did.substring(0, 3);
        if (serTag.equalsIgnoreCase("HRX")) {
            server = "ATTDSUHXTBSQEHSWTAPAAVLKSVSXPNPEARPLHUSTPGPKEEPHAOPFLSLXLOPDLVSQLNLQLUPALRLKLPLT-$$";
            Log.i("StartPPPP","server-HRX");
        } else if (serTag.equalsIgnoreCase("ESS")) {
            server = "SVLXLRLQLKSTLUPFHUIAPLEELVLPIHIBIEAOIFEMSQPDENELPALOHWHZERLNEOHYLKEPEIHUHXEGEJEEEKEH-$$";
            Log.i("StartPPPP","server-ESS");
        } else if (serTag.equalsIgnoreCase("PSD")) {
            Log.i("StartPPPP","server-PSD");
            server = "EJTDICSTSQPDPGATPALNEMLMLKHXTASVPNAWSZHUEHPLPKEEARSYLOAOSTLVLULXLQPISQPFPJPAPDLSLRLKLNLPLT-$$";
        } else if (serTag.equalsIgnoreCase("NIP") || serTag.equalsIgnoreCase("MCI") || serTag.equalsIgnoreCase("MSE") || serTag.equalsIgnoreCase("MDI") || serTag.equalsIgnoreCase("MIC") || serTag.equalsIgnoreCase("MSI") || serTag.equalsIgnoreCase("MTE") || serTag.equalsIgnoreCase("MUI") || serTag.equalsIgnoreCase("WBT")) {
            Log.i("StartPPPP","server-NIP-MCI-MSE");
            server = "ATTDASPCSUSQAREOSTPAPESVAYLKSWPNLOPDHYHUEIASLTEETAPKAOPFLMLXLRPGSQSULNLQPAPELOLKLULP-$$";
        } else if (serTag.equalsIgnoreCase("HDT") || serTag.equalsIgnoreCase("DFT") || serTag.equalsIgnoreCase("DFZ") || serTag.equalsIgnoreCase("AJT")) {
            Log.i("StartPPPP","server-HDT-DFT-DFZ-AJT");
            server = "ATTDSXIASQSUSTEKPASVAZLKTAPCPNPHAUHUPEPDSWEEPFTBAOPKLULXLRPGSQLOLNLQPALPPLLKLVLM-$$";
        } else if (serTag.equalsIgnoreCase("CPT") || serTag.equalsIgnoreCase("PIP")) {
            Log.i("StartPPPP","server-CPT-PIP");
            server = "LPTDAVIASQLOSTEKPAHYSSLKSYPIPNSXAUHUEIPDSWEEASPCAOPHLVLXLRPGSQSULNLQPAPELMLKLSLO-$$";
        } else if (serTag.equalsIgnoreCase("JDF")) {
            Log.i("StartPPPP","server-JDF");
            server = "PFTDIBTASQLNSZPAHXLOELLKHYSSEHPNAVPKHUARPJEESTEISXAOASPCSULXPHLUSQPDLTPALNPELRLKLOLMLP-$$";
        } else if (serTag.equalsIgnoreCase("MEY")) {
            Log.i("StartPPPP","server-MEY");
            server = "EJTDLOATSQHYLNPAEIHXPJLKEHLTLUPNAVPGHUASSXAREESTPILSAOPDSVPFLXPHLQSQSUPELVPALPLMLKLOLNLR-$$";
        } else if (serTag.equalsIgnoreCase("MIL")) {
            Log.i("StartPPPP","server-MIL");
            server = "SVTDLRSWSQSUIBPEPAHXAYLMLKEHTAARPNELPGHULOPFAVEESTSXPKAOHYEIPDLXPHLQSQASLPSUPAPELOLSLKLNLRLU-$$";
        } else if (serTag.equalsIgnoreCase("PSD")) {
            Log.i("StartPPPP","server-PSD");
            server = "EJTDICSTSQPDPGATPALNEMLMLKHXTASVPNAWSZHUEHPLPKEEARSYLOAOSTLVLULXLQPISQPFPJPAPDLSLRLKLNLPLT-$$";
        } else if (serTag.equalsIgnoreCase("RSZ")) {
            Log.i("StartPPPP","server-RSZ");
            server = "ASTDHXEHSUSQELPAARTBSYLKSTSVLQPNPDLNPEHUAVEEHXPLPIAOEHPFSXLXARSTLOSQPHPAPDLVLSLKLNLPLR-$$";
        } else if (serTag.equalsIgnoreCase("JWE") || serTag.equalsIgnoreCase("WNS") || serTag.equalsIgnoreCase("TSD") || serTag.equalsIgnoreCase("OPC")) {
            Log.i("StartPPPP","server-JWE-WNS-TSD-OPC");
            server = "SVTDIBEKSQEIAUPFPALVPJLKASPCSYPNELSWHUSUAVHXEEEHARLPAOPESXSTLXPHPGSQLOLQPIPAPDLMLTLKLNLSLR-$$";
        } else if (serTag.equalsIgnoreCase("EST") || serTag.equalsIgnoreCase("CTW")) {
            Log.i("StartPPPP","server-EST-CTW");
            server = "PFTDSTAXSWSQPDASEPPASUPELULKLNSZLPPNHXPJPGHUEHLOAZEEHYEITBAOARLMASLXSTLTLQSQPDSUPLPAPELOLVLKLNLR-$$";
        } else if (serTag.equalsIgnoreCase("PIX") || serTag.equalsIgnoreCase("IPC")) {
            Log.i("StartPPPP","server-PIX-IPC");
            server = "EJTDAVAUSQLOHYPDPAEISWPCLKLNPLHXPNSXPGHUASEHPHEEARATSVAOSUPFLULXLRLQSQPELOLVPASTLPPDLKLNLM-$$";
        } else if (serTag.equalsIgnoreCase("DYN") || serTag.equalsIgnoreCase("PAR")) {
            Log.i("StartPPPP","server-DYN-PAR");
            server = "LPTDHXEKSQHZEHPAARAVLKSTEJAUPNPDSWHUATLNEEHXSXAOEHSVPGLXARLQSQPFSTPAPDPHLKLNLPLR-$$";
        } else if (serTag.equalsIgnoreCase("HTS")) {
            Log.i("StartPPPP","server-HTS");
            server = "SVTDPDLNPHSQEITAPAHXPFASLKSWPNEHARLRHUSUPKEESTLPPEAOPGLXLQLOSQLVPIPAPDLSPCLKLNLULM-$$";
        } else if (serTag.equalsIgnoreCase("TWS")) {
            Log.i("StartPPPP","server-TWS");
            server = "LPTDIBSWSQEILMHWPALTSTLKPDELASPNAVPGHUSUEGAQEEAYSXAOLNTASSLXPHLQSQPEPCLMPAPKLOLKLRLU-$$";
        } else if (serTag.equalsIgnoreCase("XXC")) {
            Log.i("StartPPPP","server-XXC");
            server = "PFTDELEKSQLOAVHYPAEIHXASLKSYPCPNSXAUHUSUSWPIEEPELPEHAOARPGLULXPHLQSQLOSTLRPAPDLVLMLKLNLSLT-$$";
        } else if (serTag.equalsIgnoreCase("PTP")) {
            Log.i("StartPPPP","server-PTP");
            server = "PFTDLREKSQHYEILVPAPDSYLNLKHXIBLTPNELAUHUASEHAVEESUARSWAOPELPLMLXSXPGSQLOPHLQPASTLRPILKPDLNLS-$$";
        } else if (serTag.equalsIgnoreCase("HVC")) {
            Log.i("StartPPPP","server-HVC");
            server = "PFTDSXSWSQSZEIPAASPCLKLNAYHXPNPHPGHUEHTAPJEESUARAOSTLPPKLXLRLQSQLTPEPALOLMLKPDLULN-$$";
        } else if (serTag.equalsIgnoreCase("ZLA")) {
            Log.i("StartPPPP","server-ZLA");
            server = "SVTDEHARELSQHYAZPASTAVLKPDEIPFPNLNHXASHUEHSULQEELSSXAOTBPLLXARSTPHSQPELVPAPDLRLKLNLOLP-$$";
        } else if (serTag.equalsIgnoreCase("MHK") || serTag.equalsIgnoreCase("EPC")) {
            Log.i("StartPPPP","server-MHK-EPC");
            server = "ATTDLREKSQEIIBELPAHXAUASLKSUPESVPNAVSWHUEHPLPFEELVLMAOARLPSTLXSXPGSQPKPHPAPDLULOLKLNLQLR-$$";
        } else if (serTag.equalsIgnoreCase("GOS")) {
            Log.i("StartPPPP","server-GOS");
            server = "LPTDSXSWSQLOPGLQPAHYPHLRLKSTPITAPNIBIAHUEIEKAUEEASELAVAOSUPDPKLXSXSWSQPEPGLQPALOPHLRLKLNLSLU-$$";
        } else if (serTag.equalsIgnoreCase("NTP")) {
            Log.i("StartPPPP","server-NTP");
            server = "HZTDHYSTTASQSXSWPAEJEGLKEIAQPJPNASPDPKHUPHPGEEATSSAOSUSVPFLXPELNLUSQLRLQPALPPCLKLOLMLT-$$";
        } else if (serTag.equalsIgnoreCase("IVT")) {
            Log.i("StartPPPP","server-IVT");
            server = "PFTDAUPESQEMLOPASXPKLKHYSTAWPNSWEIHUSYASEEPGPDAOLPPHLXLQSUSQPIPEPALRLULKLOLNLS-$$";
        } else if (serTag.equalsIgnoreCase("ZEO")) {
            Log.i("StartPPPP","server-ZEO");
            server = "ATTDSXLQSQEIIAAZPAASPKLKARSVPCPNPHEKHUSTTBPLEESUAUPFAOPDLSSWLXLRPGSQPELQLVPALOLULKLNLPLM-$$";
        } else if (serTag.equalsIgnoreCase("KSC")) {
            Log.i("StartPPPP","server-KSC");
            server = "SVTDSXSWSQPELNTBPAHXLSLOLKEHARSTPNPHPGHUPDPLPFEELNHXAOEHPJPCLXLRLQSQARLVLPPASTPDLKLNLTLM-$$";
        } else if (serTag.equalsIgnoreCase("BLM")) {
            Log.i("StartPPPP","server-BLM");
            server = "PFTDSXEKSQPEEGSTPALOHYLKEILPPNPHAUHUASAQSSEESUSWPJAOPDPLPILXLRPGSQPEPCLMPALOLQLTLKLNLVLS-$$";
        } else if (serTag.equalsIgnoreCase("OBJ") || serTag.equalsIgnoreCase("SIP") || serTag.equalsIgnoreCase("ESN") || serTag.equalsIgnoreCase("ZLD") || serTag.equalsIgnoreCase("TCM") || serTag.equalsIgnoreCase("IPC") || serTag.equalsIgnoreCase("BSI") || serTag.equalsIgnoreCase("MEG")) {
            Log.i("StartPPPP","server-OBJ-SIP-ESN");
            server = ContentCommon.SERVER_STRING;
        } else {
            Log.i("StartPPPP","server-n/a");
        }
        Log.d("test", "server:" + serTag);
        NativeCaller.StartPPPP(did, user, pwd, server);
    }
}
