package com.henrique.camerawifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import object.p2pipcam.nativecaller.NativeCaller;

public class BridgeService extends Service {
    public static final int TYPE_TEXT = 2;
    public static final int MSG_SET_SURFACE = 1;
    private static AddCameraInterface addCameraInterface;
    private static AlarmInterface alarmInterface;
    private static DateTimeInterface dateTimeInterface;
    private static FtpInterface ftpInterface;
    private static String ipAddress;
    private static IPPlayInterface ipPlayInterface;
    private static IpcamClientInterface ipcamClientInterface;
    private static IpcamStatusInterface ipcamStatusInterface;
    private static MailInterface mailInterface;
    private static P2PCloseListener p2pCloseListener;
    private static PictureInterface pictureInterface;
    private static PlayBackInterface playBackInterface;
    private static PlayBackTFInterface playBackTFInterface;
    private static PlayInterface playInterface;
    private static int port;
    private static SDCardInterface sCardInterface;
    private static UserInterface userInterface;
    private static VideoInterface videoInterface;
    private static WifiInterface wifiInterface;
    private NotificationManager mCustomMgr;
    private Notification mNotify2;
    private NotificationManager ntfManager;
    private SharedPreferences preuser;

    public interface AddCameraInterface {
        void callBackSearchResultData(int i, String str, String str2, String str3, String str4, int i2);
    }

    public interface AlarmInterface {
        void callBackAlarmParams(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18, int i19, int i20, int i21, int i22, int i23, int i24, int i25, int i26, int i27, int i28, int i29, int i30, int i31, int i32, int i33, int i34);

        void callBackSetSystemParamsResult(String str, int i, int i2);
    }

    public class ControllerBinder extends Binder {
        public BridgeService getBridgeService() {
            return BridgeService.this;
        }
    }

    public interface DateTimeInterface {
        void callBackDatetimeParams(String str, int i, int i2, int i3, String str2);

        void callBackSetSystemParamsResult(String str, int i, int i2);
    }

    public interface FtpInterface {
        void callBackFtpParams(String str, String str2, String str3, String str4, String str5, int i, int i2, int i3);

        void callBackSetSystemParamsResult(String str, int i, int i2);
    }

    public interface IPPlayInterface {
        void callBaceVideoData(String str, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6);

        void callBackAudioData(byte[] bArr, int i);

        void callBackCameraParamNotify(String str, int i, int i2, int i3, int i4, int i5, int i6);

        void callBackH264Data(byte[] bArr, int i, int i2);

        void callBackMessageNotify(String str, int i, int i2);
    }

    public interface IpcamClientInterface {
        void BSMsgNotifyData(String str, int i, int i2);

        void BSSnapshotNotify(String str, byte[] bArr, int i);

        void callBackUserParams(String str, String str2, String str3, String str4, String str5, String str6, String str7);
    }

    public interface IpcamStatusInterface {
        void onIpcamStatusChanged(String str, int i);
    }

    public interface MailInterface {
        void callBackMailParams(String str, String str2, int i, String str3, String str4, int i2, String str5, String str6, String str7, String str8, String str9);

        void callBackSetSystemParamsResult(String str, int i, int i2);
    }

    public interface P2PCloseListener {
        void onP2PClose();
    }

    public interface PictureInterface {
        void BSMsgNotifyData(String str, int i, int i2);
    }

    public interface PlayBackInterface {
        void callBackPlaybackAudioData(byte[] bArr, int i);

        void callBackPlaybackVideoData(byte[] bArr, int i, int i2, int i3, int i4, int i5);
    }

    public interface PlayBackTFInterface {
        void callBackRecordFileSearchResult(String str, String str2, int i, int i2, int i3);
    }

    public interface PlayInterface {
        void callBaceVideoData(String str, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

        void callBackAudioData(byte[] bArr, int i);

        void callBackCameraParamNotify(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18);

        void callBackH264Data(String str, byte[] bArr, int i, int i2, int i3, int i4, int i5);

        void callBackMessageNotify(String str, int i, int i2);
    }

    public interface SDCardInterface {
        void callBackRecordSchParams(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18, int i19, int i20, int i21, int i22, int i23, int i24, int i25, int i26, int i27, int i28);

        void callBackSetSystemParamsResult(String str, int i, int i2);
    }

    public interface UserInterface {
        void callBackPPPPMsgNotifyData(String str, int i, int i2);

        void callBackSetSystemParamsResult(String str, int i, int i2);

        void callBackUserParams(String str, String str2, String str3, String str4, String str5, String str6, String str7);
    }

    public interface VideoInterface {
        void BSMsgNotifyData(String str, int i, int i2);
    }

    public interface WifiInterface {
        void callBackPPPPMsgNotifyData(String str, int i, int i2);

        void callBackSetSystemParamsResult(String str, int i, int i2);

        void callBackWifiParams(String str, int i, String str2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, String str3, String str4, String str5, String str6, int i9, int i10, int i11, int i12, String str7);

        void callBackWifiScanResult(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6);
    }

    public IBinder onBind(Intent intent) {
        return new ControllerBinder();
    }

    public void onCreate() {
        super.onCreate();
        //SystemValue.ISRUN = true;
        this.mCustomMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NativeCaller.PPPPSetCallbackContext(this);
        NativeCaller.PPPPSetBizCallbackContext(this);
        this.preuser = getSharedPreferences("shix_zhao_user", 0);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        //SystemValue.ISRUN = false;
        stopForeground(true);
    }

    public void CallBackAppLog(String appID, String appVer) {
        if (appID != null && this.preuser != null) {
            Editor editor = this.preuser.edit();
            editor.putString("logaid", appID);
            editor.commit();
        }
    }

    public void CallBack_AlarmNotify(String did, int alarmtype) {
        switch (alarmtype) {
            case MSG_SET_SURFACE /*1*/:
//                getNotification(("MSG_SET_SURFACE"), did, true);
                Log.i("CallBack_AlarmNotify","MSG_SET_SURFACE");
            case TYPE_TEXT /*2*/:
                Log.i("CallBack_AlarmNotify","TYPE_TEXT");
                //getNotification(("TYPE_TEXT"), did, true);
            default:
        }
    }

    public void CallBackMessageContent(String content) {
    }

    public void CallBackSaleDevList(String did, String user, String pwd, int bEnd) {
    }

    public void CallBackGroupName(String groupName, String groupDid, int bEnd) {
    }

    public void CallBackUerDid(String userDID, String threeUserName, String threeUserPwd) {
    }

    public void CallBackDevList(String id, String user, String pwd, String name, String groupDid, int bEnd) {
    }

    public void CallBackMessageNumber(int unread, int readed, int bepush) {
    }

    public void CallBackMessageList(int msgID, int pmsgID, String msgTitle, String msgFrom, int msgTag, int msgCTime, int bEnd) {
    }

    public void CallBackTransferMessage(String did, String buffer, int cmd) {
    }

    public void CallBack_AppVersion(String appVersion) {
    }

    public static void setP2PCloseListener(P2PCloseListener l) {
        p2pCloseListener = l;
    }

    public void CallBack_P2PClose(String did) {
        if (p2pCloseListener != null) {
            p2pCloseListener.onP2PClose();
        }
    }

    private void VideoData(String did, byte[] videobuf, int h264Data, int len, int width, int height, int iframe, int second, int millsec, int frameNo) {
        if (ipPlayInterface != null) {
            ipPlayInterface.callBaceVideoData(did, videobuf, h264Data, len, width, height, second, iframe);
        }
        if (playInterface != null) {
            playInterface.callBaceVideoData(did, videobuf, h264Data, len, width, height, iframe, second, millsec, frameNo);
        }
    }

    private void MessageNotify(String did, int msgType, int param) {
        if (playInterface != null) {
            playInterface.callBackMessageNotify(did, msgType, param);
        }
    }

    private void AudioData(byte[] pcm, int len) {
        if (playInterface != null) {
            playInterface.callBackAudioData(pcm, len);
        }
        if (playBackInterface != null) {
            playBackInterface.callBackPlaybackAudioData(pcm, len);
        }
    }

    private void PPPPMsgNotify(String did, int type, int param) {
        if (ipPlayInterface != null) {
            ipPlayInterface.callBackMessageNotify(did, type, param);
        }
        if (ipcamClientInterface != null) {
            ipcamClientInterface.BSMsgNotifyData(did, type, param);
        }
        if (wifiInterface != null) {
            wifiInterface.callBackPPPPMsgNotifyData(did, type, param);
        }
        if (userInterface != null) {
            userInterface.callBackPPPPMsgNotifyData(did, type, param);
        }
        if (ipcamStatusInterface != null && type == 0) {
            ipcamStatusInterface.onIpcamStatusChanged(did, param);
        }
    }

    public void SearchResult(int cameraType, String strMac, String strName, String strDeviceID, String strIpAddr, int port) {
        if (strDeviceID.length() != 0 && addCameraInterface != null) {
            addCameraInterface.callBackSearchResultData(cameraType, strMac, strName, strDeviceID, strIpAddr, port);
            ipAddress = strIpAddr;
            this.port = port;
            resetParam();
        }
    }

    public static void resetParam() {
        if (ipAddress != null && port != 0) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("http://" + ipAddress + ":" + port + "/camera_control.cgi?loginuse=admin&loginpas=&param=7&value=0&14540519233040.3619268073234707").openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()), AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String temp = br.readLine();
                    if (temp == null) {
                        return;
                    }
                    sb.append(temp);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void CallBack_SetSystemParamsResult(String did, int paramType, int result) {
        switch (paramType) {
            case ContentCommon.MSG_TYPE_SET_USER /*10*/:
                if (userInterface != null) {
                    userInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case ContentCommon.MSG_TYPE_SET_WIFI /*11*/:
                if (wifiInterface != null) {
                    wifiInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case ContentCommon.MSG_TYPE_SET_DATETIME /*12*/:
                if (dateTimeInterface != null) {
                    dateTimeInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case TransportMediator.FLAG_KEY_MEDIA_PAUSE /*16*/:
                if (mailInterface != null) {
                    mailInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case ContentCommon.MSG_TYPE_SET_FTP /*17*/:
                if (ftpInterface != null) {
                    ftpInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case ContentCommon.MSG_TYPE_SET_ALARM /*18*/:
                if (alarmInterface != null) {
                    alarmInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            case ContentCommon.MSG_TYPE_SET_RECORD_SCH /*28*/:
                if (sCardInterface != null) {
                    sCardInterface.callBackSetSystemParamsResult(did, paramType, result);
                }
            default:
        }
    }

    public void CallBack_CameraParams(String did, int resolution, int brightness, int contrast, int hue, int saturation, int osd_enable, int mode, int flip, int main_framerate, int sub_framerate, int video_bitrate, int speed, int ircut, int border_patrol, int left_right_patrol, int patrol_1_8, int watch_status, int alarm_out) {
        if (playInterface != null) {
            playInterface.callBackCameraParamNotify(did, resolution, brightness, contrast, hue, saturation, osd_enable, mode, flip, main_framerate, sub_framerate, video_bitrate, speed, ircut, border_patrol, left_right_patrol, patrol_1_8, watch_status, alarm_out);
        }
    }

    public void CallBack_WifiParams(String did, int enable, String ssid, int wifi_link_status, int channel, int mode, int authtype, int encryp, int keyformat, int defkey, String key1, String key2, String key3, String key4, int key1_bits, int key2_bits, int key3_bits, int key4_bits, String wpa_psk) {
        if (wifiInterface != null) {
            wifiInterface.callBackWifiParams(did, enable, ssid, wifi_link_status, channel, mode, authtype, encryp, keyformat, defkey, key1, key2, key3, key4, key1_bits, key2_bits, key3_bits, key4_bits, wpa_psk);
        }
    }

    public void CallBack_UserParams(String did, String user1, String pwd1, String user2, String pwd2, String user3, String pwd3) {
        if (userInterface != null) {
            userInterface.callBackUserParams(did, user1, pwd1, user2, pwd2, user3, pwd3);
        }
        if (ipcamClientInterface != null) {
            ipcamClientInterface.callBackUserParams(did, user1, pwd1, user2, pwd2, user3, pwd3);
        }
    }

    public void CallBack_FtpParams(String did, String svr_ftp, String user, String pwd, String dir, int port, int mode, int upload_interval) {
        if (ftpInterface != null) {
            ftpInterface.callBackFtpParams(did, svr_ftp, user, pwd, dir, port, mode, upload_interval);
        }
    }

    public void CallBack_DDNSParams(String did, int service, String user, String pwd, String host, String proxy_svr, int ddns_mode, int proxy_port) {
    }

    public void CallBack_MailParams(String did, String svr, int port, String user, String pwd, int ssl, String sender, String receiver1, String receiver2, String receiver3, String receiver4) {
        if (mailInterface != null) {
            mailInterface.callBackMailParams(did, svr, port, user, pwd, ssl, sender, receiver1, receiver2, receiver3, receiver4);
        }
    }

    public void CallBack_DatetimeParams(String did, int now, int tz, int ntp_enable, String ntp_svr, int xialingshi) {
        if (dateTimeInterface != null) {
            dateTimeInterface.callBackDatetimeParams(did, now, tz, ntp_enable, ntp_svr);
        }
    }

    private void PPPPSnapshotNotify(String did, byte[] bImage, int len) {
        if (ipcamClientInterface != null) {
            ipcamClientInterface.BSSnapshotNotify(did, bImage, len);
        }
    }

    public void CallBack_Snapshot(String did, byte[] data, int len) {
        if (ipcamClientInterface != null) {
            ipcamClientInterface.BSSnapshotNotify(did, data, len);
        }
    }

    public void CallBack_APParams(String did, String ssid, String pwd) {
    }

    public void CallBack_NetworkParams(String did, String ipaddr, String netmask, String gateway, String dns1, String dns2, int dhcp, int port, int rtsport) {
    }

    public void CallBack_CameraStatusParams(String did, String sysver, String devname, String devid, int alarmstatus, int sdcardstatus, int sdcardtotalsize, int sdcardremainsize) {
    }

    public void CallBack_PTZParams(String did, int led_mod, int ptz_center_onstart, int ptz_run_times, int ptz_patrol_rate, int ptz_patrul_up_rate, int ptz_patrol_down_rate, int ptz_patrol_left_rate, int ptz_patrol_right_rate, int disable_preset) {
    }

    public void CallBack_WifiScanResult(String did, String ssid, String mac, int security, int dbm0, int dbm1, int mode, int channel, int bEnd) {
        if (wifiInterface != null) {
            wifiInterface.callBackWifiScanResult(did, ssid, mac, security, dbm0, dbm1, mode, channel, bEnd);
        }
    }

    public void CallBack_AlarmParams(String did, int motion_armed, int motion_sensitivity, int input_armed, int ioin_level, int iolinkage, int ioout_level, int alarmpresetsit, int mail, int snapshot, int record, int upload_interval, int schedule_enable, int enable_alarm_audio, int schedule_sun_0, int schedule_sun_1, int schedule_sun_2, int schedule_mon_0, int schedule_mon_1, int schedule_mon_2, int schedule_tue_0, int schedule_tue_1, int schedule_tue_2, int schedule_wed_0, int schedule_wed_1, int schedule_wed_2, int schedule_thu_0, int schedule_thu_1, int schedule_thu_2, int schedule_fri_0, int schedule_fri_1, int schedule_fri_2, int schedule_sat_0, int schedule_sat_1, int schedule_sat_2) {
        if (alarmInterface != null) {
            alarmInterface.callBackAlarmParams(did, motion_armed, motion_sensitivity, input_armed, ioin_level, iolinkage, ioout_level, alarmpresetsit, mail, snapshot, record, upload_interval, schedule_enable, enable_alarm_audio, schedule_sun_0, schedule_sun_1, schedule_sun_2, schedule_mon_0, schedule_mon_1, schedule_mon_2, schedule_tue_0, schedule_tue_1, schedule_tue_2, schedule_wed_0, schedule_wed_1, schedule_wed_2, schedule_thu_0, schedule_thu_1, schedule_thu_2, schedule_fri_0, schedule_fri_1, schedule_fri_2, schedule_sat_0, schedule_sat_1, schedule_sat_2);
        }
    }

    private void CallBack_RecordFileSearchResult(String did, String filename, int nFileSize, int nRecordCount, int nPageCount, int nPageIndex, int nPageSize, int bEnd) {
        if (playBackTFInterface != null) {
            playBackTFInterface.callBackRecordFileSearchResult(did, filename, nFileSize, nPageCount, bEnd);
        }
    }

    private void CallBack_PlaybackVideoData(String did, byte[] videobuf, int h264Data, int len, int width, int height, int time) {
        if (playBackInterface != null) {
            playBackInterface.callBackPlaybackVideoData(videobuf, h264Data, len, width, height, time);
        }
    }

    private void CallBack_H264Data(String did, byte[] h264, int size, int iframe, int sec, int millsec, int frameno) {
        if (playInterface != null) {
            playInterface.callBackH264Data(did, h264, size, iframe, sec, millsec, frameno);
        }
    }

    public void CallBack_RecordSchParams(String did, int record_cover_enable, int record_timer, int record_size, int record_time_enable, int record_schedule_sun_0, int record_schedule_sun_1, int record_schedule_sun_2, int record_schedule_mon_0, int record_schedule_mon_1, int record_schedule_mon_2, int record_schedule_tue_0, int record_schedule_tue_1, int record_schedule_tue_2, int record_schedule_wed_0, int record_schedule_wed_1, int record_schedule_wed_2, int record_schedule_thu_0, int record_schedule_thu_1, int record_schedule_thu_2, int record_schedule_fri_0, int record_schedule_fri_1, int record_schedule_fri_2, int record_schedule_sat_0, int record_schedule_sat_1, int record_schedule_sat_2, int record_sd_status, int sdtotal, int sdfree) {
        if (sCardInterface != null) {
            sCardInterface.callBackRecordSchParams(did, record_cover_enable, record_timer, record_size, record_time_enable, record_schedule_sun_0, record_schedule_sun_1, record_schedule_sun_2, record_schedule_mon_0, record_schedule_mon_1, record_schedule_mon_2, record_schedule_tue_0, record_schedule_tue_1, record_schedule_tue_2, record_schedule_wed_0, record_schedule_wed_1, record_schedule_wed_2, record_schedule_thu_0, record_schedule_thu_1, record_schedule_thu_2, record_schedule_fri_0, record_schedule_fri_1, record_schedule_fri_2, record_schedule_sat_0, record_schedule_sat_1, record_schedule_sat_2, record_sd_status, sdtotal, sdfree);
        }
    }


    public static void setIpcamClientInterface(IpcamClientInterface ipcInterface) {
        ipcamClientInterface = ipcInterface;
    }

    public static void setIpcamStatusInterface(IpcamStatusInterface ipcInterface) {
        ipcamStatusInterface = ipcInterface;
    }

    public static void setPictureInterface(PictureInterface pi) {
        pictureInterface = pi;
    }

    public static void setVideoInterface(VideoInterface vi) {
        videoInterface = vi;
    }

    public static void setWifiInterface(WifiInterface wi) {
        wifiInterface = wi;
    }

    public static void setUserInterface(UserInterface ui) {
        userInterface = ui;
    }

    public static void setAlarmInterface(AlarmInterface ai) {
        alarmInterface = ai;
    }

    public static void setDateTimeInterface(DateTimeInterface di) {
        dateTimeInterface = di;
    }

    public static void setMailInterface(MailInterface mi) {
        mailInterface = mi;
    }

    public static void setFtpInterface(FtpInterface fi) {
        ftpInterface = fi;
    }

    public static void setSDCardInterface(SDCardInterface si) {
        sCardInterface = si;
    }

    public static void setPlayInterface(PlayInterface pi) {
        playInterface = pi;
    }

    public static void setPlayBackTFInterface(PlayBackTFInterface pbtfi) {
        playBackTFInterface = pbtfi;
    }

    public static void setPlayBackInterface(PlayBackInterface pbi) {
        playBackInterface = pbi;
    }

    public static void setAddCameraInterface(AddCameraInterface aci) {
        addCameraInterface = aci;
    }

    public static void setIpPlayInterface(IPPlayInterface pi) {
        ipPlayInterface = pi;
    }
}
