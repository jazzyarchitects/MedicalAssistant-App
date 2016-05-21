package architect.jazzy.medicinereminder.CustomComponents;

/**
 * Created by Jibin_ism on 04-May-15.
 */
public class TimingClass {



    /*Get the time in correct format*/
    public static String getTime(int h, int m,Boolean is24hrs )
    {
        if(is24hrs)
            return get2digit(h)+":"+get2digit(m);

        String hour,format;
        if(h==0)
        {
            hour="12";
            format="AM";
        }
        else if(h<12)
        {
            hour=get2digit(h);
            format="AM";
        }
        else if(h==12)
        {
            hour="12";
            format="PM";
        }
        else
        {
            hour=get2digit(h-12);
            format="PM";
        }
        return hour+":"+get2digit(m)+" "+format;
    }

    protected static String get2digit(int t)
    {
        if(t<10)
            return "0"+t;
        else
            return String.valueOf(t);
    }

    public static String getEquivalentMonth(int i)
    {
        switch(i)
        {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "Jan";
        }
    }



}
