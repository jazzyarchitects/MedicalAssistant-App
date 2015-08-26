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
import architect.jazzy.medicinereminder.Models.MedTime;
import architect.jazzy.medicinereminder.Models.Medicine;

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
        Medicine medicine=new Medicine();
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
                            medicine=new Medicine();
                            break;
                        case "name":
                            medicineName=xpp.nextText();
                            medicine.setMedName(medicineName);
                            break;
//                        case "ma":
//                            breakfast=xpp.nextText();
//                            break;
//                        case "na":
//                            lunch=xpp.nextText();
//                            break;
//                        case "nia":
//                            dinner=xpp.nextText();
//                            break;
                        case "s":
                            daySelected[0]=xpp.nextText();
                            medicine.setSun(Boolean.parseBoolean(daySelected[0]));
                            break;
                        case "m":
                            daySelected[1]=xpp.nextText();
                            medicine.setMon(Boolean.parseBoolean(daySelected[1]));
                            break;
                        case "t":
                            daySelected[2]=xpp.nextText();
                            medicine.setTue(Boolean.parseBoolean(daySelected[2]));
                            break;
                        case "w":
                            daySelected[3]=xpp.nextText();
                            medicine.setWed(Boolean.parseBoolean(daySelected[3]));
                            break;
                        case "th":
                            daySelected[4]=xpp.nextText();
                            medicine.setThu(Boolean.parseBoolean(daySelected[4]));
                            break;
                        case "f":
                            daySelected[5]=xpp.nextText();
                            medicine.setFri(Boolean.parseBoolean(daySelected[5]));
                            break;
                        case "sa":
                            daySelected[6]=xpp.nextText();
                            medicine.setSat(Boolean.parseBoolean(daySelected[6]));
                            break;
//                        case "sd":
//                            startDate=xpp.nextText();
//                            break;
                        case "ed":
                            endDate=xpp.nextText();
                            medicine.setEndDate(endDate);
                            break;
                        case "bk":
                            bk=xpp.nextText();
                            medicine.setBreakfast(bk);
                            break;
                        case "ln":
                            ln=xpp.nextText();
                            medicine.setLunch(ln);
                            break;
                        case "dn":
                            dn=xpp.nextText();
                            medicine.setDinner(dn);
                            break;
                        case "icon":
                            icon=xpp.nextText();
                            medicine.setIcon(Integer.parseInt(icon));
                            break;
                        case "ch":
                            cH=xpp.nextText();
                            medicine.setCustomTime(MedTime.parseTime(cH,"0"));
                            break;
                        case "cm":
                            cM=xpp.nextText();
                            medicine.setCustomTime(MedTime.parseTime(cH,cM));
                            break;
                        case "note":
                            note=xpp.nextText();
                            medicine.setNote(note);
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
                            dataHandler.clear_database();
                            i++;
                        }
                        //TODO: insert Function
//                        dataHandler.insertData(medicineName, breakfast, lunch, dinner, String.valueOf(daySelected[0]), String.valueOf(daySelected[1]), String.valueOf(daySelected[2]), String.valueOf(daySelected[3]), String.valueOf(daySelected[4]), String.valueOf(daySelected[5]), String.valueOf(daySelected[6]), startDate, endDate,bk,ln,dn,icon,cH,cM,note);
                        dataHandler.insertData(medicine);
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
