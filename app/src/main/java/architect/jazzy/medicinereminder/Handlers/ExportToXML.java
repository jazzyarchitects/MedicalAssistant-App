package architect.jazzy.medicinereminder.Handlers;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

@Deprecated
public class ExportToXML  {

    private static Context mContext;

    private static DataHandler dataHandler;
    public static final String FILENAME="medicineBackup.xml";
    public static  final String PATH=Environment.getExternalStorageDirectory().toString()+"/MedicineReminder/";
    static String path=PATH;


    public static void exportData(Context context) {
        mContext=context;
        dataHandler=new DataHandler(mContext);
        dataHandler.open();
        try {
            writeDataToXml();
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        }
        finally {
            dataHandler.close();
        }

    }


    public static void writeDataToXml() throws FileNotFoundException
    {
        Cursor input;
        input=dataHandler.returnData();
        String outputString="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        if(input.moveToFirst())
        {
            outputString+="<MedicineList>\n";
            do {
                String note="";
                try{
                    note=input.getString(19);
                }
                catch (Exception e)
                {
                    note="";
                }
                outputString+="<medicine>" +
                        "<name>"+input.getString(0)+"</name>" +
                        "<ma>"+input.getString(1)+"</ma>" +
                        "<na>"+input.getString(2)+"</na>" +
                        "<nia>"+input.getString(3)+"</nia>" +
                        "<s>"+input.getString(4)+"</s>"+
                        "<m>"+input.getString(5)+"</m>"+
                        "<t>"+input.getString(6)+"</t>"+
                        "<w>"+input.getString(7)+"</w>"+
                        "<th>"+input.getString(8)+"</th>"+
                        "<f>"+input.getString(9)+"</f>"+
                        "<sa>"+input.getString(10)+"</sa>" +
                        "<sd>"+input.getString(11)+"</sd>" +
                        "<ed>"+input.getString(12)+"</ed>" +
                        "<bk>"+input.getString(13)+"</bk>" +
                        "<ln>"+input.getString(14)+"</ln>" +
                        "<dn>"+input.getString(15)+"</dn>" +
                        "<icon>"+input.getString(16)+"</icon>" +
                        "<ch>"+input.getString(17)+"</ch>" +
                        "<cm>"+input.getString(18)+"</cm>" +
                        "<note>"+note+"</note>"+
                        "</medicine>";
            }while (input.moveToNext());
            outputString+="</MedicineList>";
        }
        File file=new File(path,FILENAME);
        File outputFile;
        if(file.exists())
        {
            outputFile=file;
        }
        else
        {
            File MTDirectory=new File(path);
            MTDirectory.mkdirs();
            outputFile=new File(path,FILENAME);
        }
        FileOutputStream fileOutputStream=new FileOutputStream(outputFile);
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
        try
        {
            outputStreamWriter.write(outputString);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("Export Successful");
        builder.setMessage("Export was successful.File saved in \n"+PATH+FILENAME);
        builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }


}
