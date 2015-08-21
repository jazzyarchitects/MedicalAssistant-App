package architect.jazzy.medicinereminder.Handlers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import architect.jazzy.medicinereminder.HelperClasses.AlarmSetterService;

/**
 * Created by Jibin_ism on 25-Dec-14.
 */
public class DeleteAllMedicines {

    private Context mcontext;
    DataHandler dataHandler=new DataHandler(mcontext);

    public DeleteAllMedicines(Context context) {
        mcontext=context;
    }

    public void deleteMedicines(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mcontext);
        builder.setTitle("Confirm Reset Data");
        builder.setMessage("Are you sure you want to delete all medicines?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dataHandler = new DataHandler(mcontext);
                dataHandler.open();
                dataHandler.clear_database();
                dataHandler.close();
                Intent startAlarmServiceIntent=new Intent(mcontext, AlarmSetterService.class);
                startAlarmServiceIntent.setAction("CANCEL");
                Log.v("Delete command passed","Passed");
                mcontext.startService(startAlarmServiceIntent);
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
