package architect.jazzy.medicinereminder.BroadcastRecievers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.util.Log;

import architect.jazzy.medicinereminder.Activities.FullScreenLockScreen;

public class DismissNotification extends BroadcastReceiver {
    public DismissNotification() {
    }

    public static final String TAG="DismissNotification";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "On receive");
        Ringtone r = FullScreenLockScreen.r;
        if(r!=null) {
            r.stop();
        }

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_TAG, AlarmReceiver.NOTIFICATION_ID);
    }
}
