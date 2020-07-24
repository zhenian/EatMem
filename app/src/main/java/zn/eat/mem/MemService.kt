package zn.eat.mem

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import java.util.*

class MemService: Service() {

    private var mNotification: Notification? = null
    private var mPendingIntent: PendingIntent? = null
    private var mNotificationManager: NotificationManager? = null

    private var mMemBroadcastReceiver: BroadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when(action){
                NOTIFY_ACTON->{
                    //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                    //INTENT_BUTTONID_TAG
                    //INTENT_BUTTON_ID_TAG
                    val buttonId = intent.getIntExtra(INTENT_BUTTON_ID_TAG, 0)
                    if(buttonId == BUTTON_PREV_ID){
                        this@MemService.showNotify()
                    }
                }
                else ->{}
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return memBinder
    }

    override fun onCreate() {
        super.onCreate()
        L.i("on create")

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java)
        mPendingIntent = PendingIntent.getActivity(
            this,
            ClickNotificationRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intentFilter = IntentFilter()
        intentFilter.addAction(NOTIFY_ACTON)
        registerReceiver(mMemBroadcastReceiver, intentFilter)

        showNotify()
        startTimerTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        L.i("on destroy")
        unregisterReceiver(mMemBroadcastReceiver)
        stopTimerTask()
    }

    fun showNotify() {
        val mRemoteViews = RemoteViews(packageName, R.layout.remote_view_mem)

        val mBuilder = NotificationCompat.Builder(this)
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notify_info, L.getMemInfo(this))

        //点击的事件处理
        val buttonIntent = Intent(NOTIFY_ACTON)
        buttonIntent.putExtra(INTENT_BUTTON_ID_TAG, BUTTON_PREV_ID)
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        val intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.update_btn, intent_prev)

        val i = Intent(this, MainActivity::class.java)
        val channelID = "tts_chanelId"
        val channelName = "TTS 朗读"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        i.putExtra("info", "21M")
        mPendingIntent = PendingIntent.getActivity(this,
            ClickNotificationRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(mPendingIntent)
            .setWhen(System.currentTimeMillis() + 1000) // 通知产生的时间，会在通知信息里显示
            .setTicker("显示内存")
            .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
            .setChannelId(channelID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.notify_icon)
        mBuilder.setCustomContentView(mRemoteViews)
        mNotification = mBuilder.build()
        mNotification?.flags = Notification.FLAG_ONGOING_EVENT
        //会报错，还在找解决思路
        try {
            mNotificationManager?.notify(ForegroundServiceId, mNotification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        L.e("------------>>>>>  在这儿明确启动 Mem Service 后台服务............. ")
        startForeground(ForegroundServiceId, mNotification)
    }

    companion object{
        val memBinder = MemBinder()
        const val SERVICE_ACTION = "zn.eat.mem.service"
        const val NOTIFY_ACTON = "zn.eat.mem.service.broadcast.notifications"
        const val ForegroundServiceId = 1
        const val ClickNotificationRequestCode = 2

        const val INTENT_BUTTON_ID_TAG = "ButtonId"
        const val BUTTON_PREV_ID = 1 //上一首 按钮点击 ID

        const val BUTTON_PALY_ID = 2 //播放/暂停 按钮点击 ID

        const val BUTTON_NEXT_ID = 3 //下一首 按钮点击 ID

        const val BUTTON_DELETE_ID = 4 // 删除 按钮点击 ID

    }

    class MemBinder: Binder(){
        fun updateInfo(){

        }
    }



    private var timer: Timer? = null
    private var task: TimerTask? = null
    var freshTimeHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            showNotify()
        }
    }

    fun startTimerTask() {
        task = object : TimerTask() {
            override fun run() {
                freshTimeHandler.sendEmptyMessage(0)
            }
        }
        timer = Timer()
        // 参数：
        // 100，延时1秒后执行。
        // 1000，每隔60秒执行1次task。
        timer?.schedule(task, 1000, 10 * 1000.toLong())
    }

    fun stopTimerTask() {
        if (timer != null) timer?.cancel()
        if (task != null) task?.cancel()
    }


}