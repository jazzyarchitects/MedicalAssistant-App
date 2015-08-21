package architect.jazzy.medicinereminder.Handlers;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import architect.jazzy.medicinereminder.Activities.MainActivity;

public class ImportToSQL{
    private static Context mContext;

    static DataHandler dataHandler;
    static Cursor c;
    private static final String PATH=ExportToXML.PATH;
    private static final String FILENAME=ExportToXML.FILENAME;

    static String bk="none",ln="none",dn="none";
    static String medicineName, startDate, endDate, breakfast,lunch,dinner,icon,cH,cM,note="";
    static String[] daySelected={"","","","","","",""};
    static String[] daylist={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};



    public static void importData(Context context) {
        mContext=context;
        final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);

       builder.setTitle("Confirm Import");
        builder.setMessage("Do you want to import data from external file? \nNote: This would delete all the existing data from the app.");
        builder.setPositiveButton("Import", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    writeToSQL();
                } catch (FileNotFoundException e) {
                    Toast.makeText(mContext, "Import Error \nMake sure the file is present in \n" + PATH + FILENAME, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.v("File not Found", "No such file found: " + PATH + FILENAME);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    private static void writeToSQL() throws FileNotFoundException, XmlPullParserException, IOException
    {

        File inputFile=new File(PATH,FILENAME);
        FileInputStream fileInputStream=new FileInputStream(inputFile);
        XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
        XmlPullParser xpp=factory.newPullParser();
        xpp.setInput(new InputStreamReader(fileInputStream));
        int eventType=xpp.getEventType();
        int i=0;
        while(eventType!=XmlPullParser.END_DOCUMENT)
        {
            String name;
            switch (eventType)
            {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name=xpp.getName();
                    switch (name)
                    {
                        case "medicine":
                            break;
                        case "name":
                            medicineName=xpp.nextText();
                            break;
                        case "ma":
                            breakfast=xpp.nextText();
                            break;
                        case "na":
                            lunch=xpp.nextText();
                            break;
                        case "nia":
                            dinner=xpp.nextText();
                            break;
                        case "s":
                            daySelected[0]=xpp.nextText();
                            break;
                        case "m":
                            daySelected[1]=xpp.nextText();
                            break;
                        case "t":
                            daySelected[2]=xpp.nextText();
                            break;
                        case "w":
                            daySelected[3]=xpp.nextText();
                            break;
                        case "th":
                            daySelected[4]=xpp.nextText();
                            break;
                        case "f":
                            daySelected[5]=xpp.nextText();
                            break;
                        case "sa":
                            daySelected[6]=xpp.nextText();
                            break;
                        case "sd":
                            startDate=xpp.nextText();
                            break;
                        case "ed":
                            endDate=xpp.nextText();
                            break;
                        case "bk":
                            bk=xpp.nextText();
                            break;
                        case "ln":
                            ln=xpp.nextText();
                            break;
                        case "dn":
                            dn=xpp.nextText();
                            break;
                        case "icon":
                            icon=xpp.nextText();
                            break;
                        case "ch":
                            cH=xpp.nextText();
                            break;
                        case "cm":
                            cM=xpp.nextText();
                            break;
                        case "note":
                            note=xpp.nextText();
                        default:
                            break;

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name=xpp.getName();
                    if (name.equals("medicine")){
                        if(i==0)
                        {

                            dataHandler=new DataHandler(mContext);
                            dataHandler.open();
                            dataHandler.clear_database();
                            i++;
                        }
                        dataHandler.insertData(medicineName, breakfast, lunch, dinner, String.valueOf(daySelected[0]), String.valueOf(daySelected[1]), String.valueOf(daySelected[2]), String.valueOf(daySelected[3]), String.valueOf(daySelected[4]), String.valueOf(daySelected[5]), String.valueOf(daySelected[6]), startDate, endDate,bk,ln,dn,icon,cH,cM,note);
                    }
            }
            eventType=xpp.next();
        }
        if(inputFile.exists())
        {
            Toast.makeText(mContext,"Imported Successfully",Toast.LENGTH_LONG).show();
        }
        MainActivity.setAlarm(mContext);
        dataHandler.close();
    }

}
