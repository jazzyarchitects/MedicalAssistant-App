package architect.jazzy.medicinereminder.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import architect.jazzy.medicinereminder.Services.AlarmSetterService;

public class BootCompleteReciever extends BroadcastReceiver {
    public BootCompleteReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        Intent i=new Intent(context,AlarmSetterService.class);
        i.setAction("CREATE");
        context.startService(i);
    }
}
