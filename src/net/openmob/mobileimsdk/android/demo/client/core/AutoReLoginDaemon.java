package net.openmob.mobileimsdk.android.demo.client.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import net.openmob.mobileimsdk.android.demo.client.ClientCoreSDK;

/**
 * 自动登录工具
 */
public class AutoReLoginDaemon {
    private static final String TAG = AutoReLoginDaemon.class.getSimpleName();

    private static int AUTO_RE$LOGIN_INTERVAL = 2000;
    private static AutoReLoginDaemon instance = null;
    private Handler handler = null;
    private Runnable runnable = null;
    private boolean autoReLoginRunning = false;
    private boolean isRunning = false;
    private Context context = null;

    private AutoReLoginDaemon(Context context) {
        this.context = context;
        init();
    }

    public static AutoReLoginDaemon getInstance(Context context) {
        if (instance == null)
            instance = new AutoReLoginDaemon(context);
        return instance;
    }

    private void init() {
        this.handler = new Handler();
        this.runnable = () -> {
            if (!AutoReLoginDaemon.this.isRunning) {
                new AsyncTask<Object, Integer, Integer>() {
                    protected Integer doInBackground(Object[] params) {
                        AutoReLoginDaemon.this.isRunning = true;
                        if (ClientCoreSDK.DEBUG)
                            Log.d(AutoReLoginDaemon.TAG, "【IMCORE】自动重新登陆线程执行中, autoReLogin?" + ClientCoreSDK.autoReLogin + "...");
                        int code = -1;

                        if (ClientCoreSDK.autoReLogin) {
                            code = MessageSender.getInstance(
                                    AutoReLoginDaemon.this.context).sendLogin(
                                    ClientCoreSDK.getInstance().getCurrentLoginName()
                                    , ClientCoreSDK.getInstance().getCurrentLoginPsw()
                                    , ClientCoreSDK.getInstance().getCurrentLoginExtra());
                        }
                        return code;
                    }

                    protected void onPostExecute(Integer result) {
                        if (result == 0) {
                            // *********************** 同样的代码也存在于LocalUDPDataSender.SendLoginDataAsync中的代码
                            // 登陆消息成功发出后就启动本地消息侦听线程：
                            // 第1）种情况：首次使用程序时，登陆信息发出时才启动本地监听线程是合理的；
                            // 第2）种情况：因网络原因（比如服务器关闭或重启）而导致本地监听线程中断的问题：
                            //      当首次登陆后，因服务端或其它网络原因导致本地监听出错，将导致中断本地监听线程，
                            //	          所以在此处在自动登陆重连或用户自已手机尝试再次登陆时重启监听线程就可以恢复本地
                            //	          监听线程的运行。
                            MessageReceiver.getInstance(AutoReLoginDaemon.this.context).startup();
                        }

                        //
                        isRunning = false;
                        // 开始下一个心跳循环
                        handler.postDelayed(runnable, AUTO_RE$LOGIN_INTERVAL);
                    }
                }
                        .execute();
            }
        };
    }

    public void stop() {
        this.handler.removeCallbacks(this.runnable);
        this.autoReLoginRunning = false;
    }

    void start(boolean immediately) {
        stop();

        this.handler.postDelayed(this.runnable, immediately ? 0 : AUTO_RE$LOGIN_INTERVAL);
        this.autoReLoginRunning = true;
    }

    public boolean isautoReLoginRunning() {
        return this.autoReLoginRunning;
    }
}