package architect.jazzy.medicinereminder;

import android.app.Application;
import android.content.Context;
import java.util.HashMap;

/**
 * Created by Jibin_ism on 30-Dec-14.
 */
public class ThisApplication extends Application {

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    public ThisApplication() {
        super();
    }

    private Thread.UncaughtExceptionHandler exceptionHandler;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exceptionHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    private  Thread.UncaughtExceptionHandler uncaughtExceptionHandler=new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

//            if(!BuildConfig.DEBUG){
//                return;
//            }
//            File root= Environment.getExternalStorageDirectory();
//            File dir=new File(root.getAbsolutePath()+"/MedicineReminder");
//
//            Random random=new Random();
//            int i1=random.nextInt(10);
//
//            dir.mkdirs();
//            try{
//                FileOutputStream f=new FileOutputStream(new File(dir,"MedicineReminderLog("+i1+").txt"));
//                String data=ex.toString();
//                String data2="";
//                for(int i=0;i<ex.getStackTrace().length;i++){
//                    data2+="\n\n****\n\n"+ex.getStackTrace()[i];
//                }
//                try{
//                    f.write(Calendar.getInstance().toString().getBytes());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                f.write(data.getBytes());
//                f.write("\n***********************************".getBytes());
//                f.write(data2.getBytes());
//                f.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            exceptionHandler.uncaughtException(thread, ex);
        }
    };
}
