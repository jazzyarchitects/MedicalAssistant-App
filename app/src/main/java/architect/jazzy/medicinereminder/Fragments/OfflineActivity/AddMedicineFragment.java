package architect.jazzy.medicinereminder.Fragments.OfflineActivity;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Adapters.DoctorSpinnerAdapter;
import architect.jazzy.medicinereminder.Adapters.FeedAdapter;
import architect.jazzy.medicinereminder.Adapters.ImageAdapter;
import architect.jazzy.medicinereminder.CustomComponents.TimingClass;
import architect.jazzy.medicinereminder.CustomViews.DaySelectorFragmentDialog;
import architect.jazzy.medicinereminder.CustomViews.LabelledImage;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.FeedItem;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.Parsers.FeedParser;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.AlarmSetterService;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMedicineFragment extends Fragment {

    public static final String MEDICINE_NAME = "medName";
    private static final String TAG = "AddMedicineFragment";

    View v;
    private Context context;
    CardView progressCard;
    EditText endDose, notes;
    AutoCompleteTextView medName;
    ImageView medIcon;
    LabelledImage morning, noon, night, custom;
    ScrollView mainScrollView;
    Spinner spinner;
    CheckBox indefinite;
    Button addButton;
    ViewPager feedPager;
    int pos = 54;
    String doctorId = "0";
    ArrayList<Doctor> doctors;


    boolean[] daySelected = {true, true, true, true, true, true, true};
    DataHandler dataHandler;
    Cursor al;
    ArrayList<String> autoCompleteList;
    ArrayAdapter<String> arrayAdapter;
    int day, month, year;
    int customHour = 0, customMinute = 0, bbh, bbm, abh, abm, blh, blm, alh, alm, bdh, bdm, adh, adm;
    String customTimeHour = "", customTimeMinute = "", medicineName, startDate, endDate = "",
            breakfast, lunch, dinner, bk = "none", ln = "none", dn = "none", noteValue = "";
    private final int[] checkBoxIds = {R.id.check_sun, R.id.check_mon, R.id.check_tue, R.id.check_wed, R.id.check_thu, R.id.check_fri, R.id.check_sat};
    private final int[] textBoxIds = {R.id.text_sun, R.id.text_mon, R.id.text_tue, R.id.text_wed, R.id.text_thu, R.id.text_fri, R.id.text_sat};
    SharedPreferences inputPref;
    Boolean is24hr, isDaySelectionOpen = false, menuSelected = false;


    public AddMedicineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView scheduleText;
    RelativeLayout newsHolder;
    TextInputLayout t1, t2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_medicine, container, false);
        context = getActivity();
        getDefaultTime();

        for(int id: textBoxIds){
            ((TextView)v.findViewById(id)).setTextColor(Constants.getThemeColor(getActivity()));
        }

        return v;
    }



    void setColorScheme() {
        int color = Constants.getThemeColor(getActivity());
        if(color==getResources().getColor(R.color.themeColorDefault)){
            t1.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_background));
            notes.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_background2));
            scheduleText.setBackgroundDrawable(getResources().getDrawable(R.drawable.schedule_background));
            addButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_states));
            addButton.setPadding(10,0,10,0);
        }else {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[1] = 0.05f;
            if (hsv[2] > 0.2) {
                hsv[2] = 0.95f;
            } else {
                hsv[1] = 0.01f;
                hsv[2] = 0.5f;
            }
            int lightColor = Color.HSVToColor(hsv);
            GradientDrawable medNoteBackground = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.WHITE, lightColor, Color.WHITE});
            GradientDrawable medNameBackground = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{lightColor, Color.WHITE});
            medName.setBackgroundColor(Color.TRANSPARENT);
            notes.setBackgroundColor(Color.TRANSPARENT);
            t1.setBackgroundDrawable(medNameBackground);
            t2.setBackgroundDrawable(medNoteBackground);

            hsv[1] = 0.4f;
            int accentColor = Constants.getFABColor(getActivity());
            Drawable scheduleBackground = scheduleText.getBackground().mutate();
            scheduleBackground.setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
            if (hsv[2] <= 0.3) {
                scheduleText.setTextColor(Color.WHITE);
            } else {
                scheduleText.setTextColor(Color.parseColor("#15152f"));
            }
            newsHolder.setBackgroundColor(color);


            Color.colorToHSV(accentColor, hsv);
            hsv[2] -= 0.2f;
            if (hsv[2] <= 0.3) {
                addButton.setTextColor(Color.WHITE);
            }
            int buttonColorNormal = Color.HSVToColor(hsv);
            hsv[2] -= 0.2f;
            int buttonColorPressed = Color.HSVToColor(hsv);
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(buttonColorPressed));
            stateListDrawable.addState(new int[]{}, new ColorDrawable(buttonColorNormal));
            addButton.setBackgroundDrawable(stateListDrawable);
        }
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        medName = (AutoCompleteTextView) v.findViewById(R.id.medName);
        morning = (LabelledImage) v.findViewById(R.id.morning);
        night = (LabelledImage) v.findViewById(R.id.night);
        noon = (LabelledImage) v.findViewById(R.id.noon);
        custom = (LabelledImage) v.findViewById(R.id.custom);
        endDose = (EditText) v.findViewById(R.id.endDate);
        notes = (EditText) v.findViewById(R.id.medNotes);
        indefinite = (CheckBox) v.findViewById(R.id.endIndefinite);
        mainScrollView = (ScrollView) v.findViewById(R.id.scrollView);
        feedPager = (ViewPager) v.findViewById(R.id.newsFeedPager);
        progressCard = (CardView) v.findViewById(R.id.progressCard);
        addButton = (Button) v.findViewById(R.id.but_add_medicine);
        spinner = (Spinner) v.findViewById(R.id.doctorSpinner);
        scheduleText = (TextView) v.findViewById(R.id.emojis_rl);
        newsHolder = (RelativeLayout) v.findViewById(R.id.newsHolder);
        t1 = (TextInputLayout) v.findViewById(R.id.textInput1);
        t2 = (TextInputLayout) v.findViewById(R.id.textInput2);


        dataHandler = new DataHandler(context);
        doctors = dataHandler.getDoctorList();
        DoctorSpinnerAdapter adapter = new DoctorSpinnerAdapter(getActivity(), doctors);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    doctorId = "0";
                } else {
                    doctorId = doctors.get(position - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicine(v);
            }
        });

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add Medicine");
        } catch (Exception e) {
            e.printStackTrace();
        }

        indefinite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                endDose.setEnabled(!isChecked);
                if (isChecked) {
                    endDose.setText("");
                }
                endDate = Constants.INDEFINITE_SCHEDULE;
            }
        });

        new FeedsRetriever().execute();

        morning.setTag("Breakfast");
        noon.setTag("Lunch");
        night.setTag("Dinner");

        loadAutoComplete();

        for (int position = 0; position < 7; position++) {
            TextView correspondingText = (TextView) v.findViewById(textBoxIds[position]);
            String s = correspondingText.getText().toString();
            if (daySelected[position]) {
                correspondingText.setTextColor(Constants.getThemeColor(getActivity()));
                correspondingText.setText(Html.fromHtml("<b>" + s + "</b>"));
            }
        }
        LabelledImage.ViewClickListener viewClickListener = new LabelledImage.ViewClickListener() {
            @Override
            public void onImageClick(LabelledImage labelledImageView) {
                if (labelledImageView.getState()) {
                    labelledImageView.setNormalColor();
                    onTopTextClick(labelledImageView);

                } else {
                    labelledImageView.setGrayScale();
                    none(labelledImageView, labelledImageView.getTag());
                    labelledImageView.setState(false);
                }

            }

            @Override
            public void onTopTextClick(final LabelledImage labelledImageView) {
                if (!labelledImageView.getState()) {
                    labelledImageView.setNormalColor();
                    labelledImageView.setState(true);
                }
                PopupMenu popupMenu = new PopupMenu(context, labelledImageView);
                PopupMenu.OnMenuItemClickListener clickListener = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        getDefaultTime();
                        switch (item.getItemId()) {
                            case R.id.before:
                                before(labelledImageView, labelledImageView.getTag());
                                menuSelected = true;
                                break;
                            case R.id.after:
                                after(labelledImageView, labelledImageView.getTag());
                                menuSelected = true;
                                break;
                            case R.id.none:
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            default:
                                none(labelledImageView, labelledImageView.getTag());
                                onImageClick(labelledImageView);
                                menuSelected = false;
                                break;
                        }
                        return false;
                    }
                };


                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        String tag = labelledImageView.getTag();
                        if (!menuSelected) {
                            if (tag.equalsIgnoreCase("breakfast") && bk.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }
                            if (tag.equalsIgnoreCase("lunch") && ln.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }
                            if (tag.equalsIgnoreCase("dinner") && dn.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }

                        }
                        menuSelected = false;
                    }
                });
                popupMenu.setOnMenuItemClickListener(clickListener);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
            }
        };

        morning.setViewClickListener(viewClickListener);
        noon.setViewClickListener(viewClickListener);
        night.setViewClickListener(viewClickListener);

        morning.setTextColor(Color.BLACK);
        noon.setTextColor(Color.BLACK);
        night.setTextColor(Color.BLACK);

        none(morning, morning.getTag());
        none(noon, noon.getTag());
        none(night, night.getTag());

        Constants.scaleEditTextImage(getActivity(), endDose, R.drawable.ic_action_calendar_month, Constants.getThemeColor(getActivity()));

        endDose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    DateDialog(v.getId());
                return true;
            }
        });

        custom.setViewClickListener(new LabelledImage.ViewClickListener() {
            @Override
            public void onImageClick(LabelledImage labelledImageView) {
                if (labelledImageView.getState()) {
                    labelledImageView.setNormalColor();
                    onTopTextClick(labelledImageView);
                } else {
                    labelledImageView.setGrayScale();
                    customTimeHour = "";
                    customTimeMinute = "";
                    none(labelledImageView, labelledImageView.getTag());
                }
            }

            @Override
            public void onTopTextClick(LabelledImage labelledImageView) {
                TimeDialog(labelledImageView.getId());
            }
        });

        medIcon = (ImageView) v.findViewById(R.id.medIcon);
        medIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiSelectFragment fragment = new EmojiSelectFragment();
                fragment.show(getFragmentManager(), "DialogFragment");
            }
        });

        setColorScheme();

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void onEmojiSelected(int position) {
        medIcon.setImageResource(ImageAdapter.emojis[position]);
        pos = position;
    }

    public void before(LabelledImage view, String tag) {
        int h = 0, m = 0;
        if (tag.equalsIgnoreCase("breakfast")) {
            bk = "before";
            h = bbh;
            m = bbm;
        } else if (tag.equalsIgnoreCase("lunch")) {
            ln = "before";
            h = blh;
            m = blm;
        } else if (tag.equalsIgnoreCase("dinner")) {
            dn = "before";
            h = bdh;
            m = bdm;
        }

        view.setTextSize(18);
        view.setText(TimingClass.getTime(h, m, is24hr), "Before " + tag);
        view.setTextAux("Before " + tag);
        view.setTime(h, m);
    }

    public void after(LabelledImage view, String tag) {
        int h = 0, m = 0;
        if (tag.equalsIgnoreCase("breakfast")) {
            bk = "after";
            h = abh;
            m = abm;
        } else if (tag.equalsIgnoreCase("lunch")) {
            ln = "after";
            h = alh;
            m = alm;
        } else if (tag.equalsIgnoreCase("dinner")) {
            dn = "after";
            h = adh;
            m = adm;
        }
        view.setTextSize(18);
        view.setText(TimingClass.getTime(h, m, is24hr), "After " + tag);
        view.setTextAux("After " + tag);
        view.setTime(h, m);
    }

    public void none(LabelledImage view, String tag) {
        if (tag.equalsIgnoreCase("breakfast")) {
            bk = "none";
        } else if (tag.equalsIgnoreCase("lunch")) {
            ln = "none";
        } else if (tag.equalsIgnoreCase("dinner")) {
            dn = "none";
        }
        view.clearTime();
        view.setText("Set " + tag + " time", "");
        view.setTextSize(14);
        view.setTextAux("");
        view.setState(false);
        view.setGrayScale();
    }

    public void loadAutoComplete() {
        autoCompleteList = new ArrayList<>();
        al = dataHandler.findColumn(DataHandler.MedicineTable.COL_NAME);
        if (al.moveToFirst()) {
            do {
                autoCompleteList.add(al.getString(0));
            } while (al.moveToNext());
        }
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        medName.setAdapter(arrayAdapter);
    }

    public void DateDialog(final int id) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                endDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                ((EditText) v.findViewById(id)).setText(dayOfMonth + " " + TimingClass.getEquivalentMonth(monthOfYear + 1) + " " + year);
            }
        };
        DatePickerDialog.newInstance(onDateSetListener, year, month, day).show(getFragmentManager(), "DATE_PICKER");

    }

    public void TimeDialog(final int id) {
        Calendar c = Calendar.getInstance();
        customHour = c.get(Calendar.HOUR_OF_DAY);
        customMinute = c.get(Calendar.MINUTE);
        ((LabelledImage) v.findViewById(id)).setGrayScale();
        ((LabelledImage) v.findViewById(id)).setState(false);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                Log.e("State", "Time Set");
                ((LabelledImage) v.findViewById(id)).setText(TimingClass.getTime(hourOfDay, minute, is24hr), "Custom Time");
                customHour = hourOfDay;
                customMinute = minute;
                customTimeHour = String.valueOf(hourOfDay);
                customTimeMinute = String.valueOf(minute);
                ((LabelledImage) v.findViewById(id)).setNormalColor();
                ((LabelledImage) v.findViewById(id)).setState(true);
            }
        };
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.newInstance(onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24hr).show(getFragmentManager(), "TIME_PICKER");
    }

    public void addMedicine(View view) {
        getData();
        if (medicineName.isEmpty()) {
            medName.setError("This cannot be empty");
        } else if (endDate.isEmpty()) {
            endDose.setError("Neccessary End Date or select indefinite");
            mainScrollView.fullScroll(View.FOCUS_DOWN);
        } else if (bk.equalsIgnoreCase("none") && ln.equalsIgnoreCase("none") && dn.equalsIgnoreCase("none") && customTimeMinute.isEmpty()) {
            Log.e("MainActivity", "Custom Time Minutes " + customTimeMinute);
            Toast.makeText(context, "No time selected", Toast.LENGTH_LONG).show();
        } else {
            writeToFile();
            loadAutoComplete();
        }
        setAlarm(getActivity());
    }

    public void getData() {
        medicineName = medName.getText().toString();
        noteValue = notes.getText().toString();
    }

    public void writeToFile() {
        getData();
        Medicine medicine = dataHandler.findRow(medicineName);
        if (medicine != null) {
            startDate = Calendar.getInstance().get(Calendar.DATE) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.YEAR);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirm Modify");
            builder.setMessage("This medicine is already present in reminders. Do you want to replace it?");
            builder.setPositiveButton("Yes,\n Update it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dataHandler.updateData(medicineName, medicineName, bk, ln, dn, String.valueOf(daySelected[0]), String.valueOf(daySelected[1]), String.valueOf(daySelected[2]), String.valueOf(daySelected[3]), String.valueOf(daySelected[4]), String.valueOf(daySelected[5]), String.valueOf(daySelected[6]), endDate, String.valueOf(pos), customTimeHour + "," + customTimeMinute, noteValue);
                    Toast.makeText(context, "Medicine details modified", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("No,\n Create new", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int i = 0;
                    Cursor temp = dataHandler.searchDynamic(medicineName);
                    if (temp.moveToFirst()) {
                        do {
                            i++;
                        } while (temp.moveToNext());
                    }
                    dataHandler.insertData(medicineName + " (" + i + ")", doctorId, String.valueOf(daySelected[0]), String.valueOf(daySelected[1]), String.valueOf(daySelected[2]), String.valueOf(daySelected[3]), String.valueOf(daySelected[4]), String.valueOf(daySelected[5]), String.valueOf(daySelected[6]), endDate, bk, ln, dn, String.valueOf(pos), customTimeHour, customTimeMinute, noteValue);
                    Toast.makeText(context, "Medicine Added", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();

        } else {
            Log.e("MainActivity", "Create " + medicineName + " Custom hour: " + customTimeHour + " Custom Minute: " + customTimeMinute);
            dataHandler.insertData(medicineName, doctorId, String.valueOf(daySelected[0]), String.valueOf(daySelected[1]), String.valueOf(daySelected[2]), String.valueOf(daySelected[3]), String.valueOf(daySelected[4]), String.valueOf(daySelected[5]), String.valueOf(daySelected[6]), endDate, bk, ln, dn, String.valueOf(pos), customTimeHour, customTimeMinute, noteValue);
            Toast.makeText(context, "Medicine Added", Toast.LENGTH_SHORT).show();
            fragmentInteractionListener.addMedicine(false);
        }
        setAlarm(getActivity());
    }

    public static void setAlarm(Context context) {
        Intent startAlarmServiceIntent = new Intent(context, AlarmSetterService.class);
        startAlarmServiceIntent.setAction("CANCEL");
        context.startService(startAlarmServiceIntent);
        startAlarmServiceIntent.setAction("CREATE");
        context.startService(startAlarmServiceIntent);
    }

    public void showDaySelection() {
        DaySelectorFragmentDialog daySelectorFragmentDialog = new DaySelectorFragmentDialog();
        Bundle args = new Bundle();
        args.putBooleanArray("SELECTED_DAYS", daySelected);
        daySelectorFragmentDialog.setArguments(args);
        daySelectorFragmentDialog.show(getFragmentManager(), "DAY_SELECTION");
    }

    public void onDaySelectionChanged(int position, Boolean isChecked) {
//        Log.e("MainActivity", "Fragment Day Clicked " + position + isChecked);
        daySelected[position] = isChecked;
        TextView correspondingText = (TextView) v.findViewById(textBoxIds[position]);
        String s = correspondingText.getText().toString();
        if (isChecked) {
//            correspondingText.setTextColor(getResources().getColor(R.color.selectedDay));
            correspondingText.setTextColor(Constants.getFabBackground(getActivity()));
            correspondingText.setText(Html.fromHtml("<b>" + s + "</b>"));
        } else {
            correspondingText.setTextColor(Color.GRAY);
            correspondingText.setText(s);
        }
    }


    public class FeedsRetriever extends AsyncTask<Void, Void, ArrayList<FeedItem>> {
        @Override
        protected ArrayList<FeedItem> doInBackground(Void... params) {
            try {
                Log.e("MainActivity", "DoInBackground");
                URL url = new URL(FeedParser.feedUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                if (inputStream == null)
                    return null;

                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmpMR");
                if (inputStream != null) {
//                    Log.e("FeedParser","In not null");
                    folder.mkdirs();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, "tmpMR00.tmp"));
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
//                        Log.e("FeedParser","Try 1");
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
//                            Log.e("FeedParser","In While");
                            fileOutputStream.write(buffer, 0, bufferLength);
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        File file = new File(folder, "tmpMR00.tmp");
                        File out = new File(folder, "tmpMR01.tmp");

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        PrintWriter writer = new PrintWriter(new FileWriter(out));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line.replace("&lt;", "<").replace("&gt;", ">"));
                        }
                        reader.close();
                        writer.close();
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return FeedParser.parse();
            } catch (Exception e) {
                e.getStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<FeedItem> items) {
            super.onPostExecute(items);
            Log.e("MainActivity", "In Post Execute");
            if (items == null)
                return;

            progressCard.setVisibility(View.GONE);
            feedPager.setVisibility(View.VISIBLE);
            FeedAdapter adapter = new FeedAdapter(getFragmentManager(), items);
            try {
                feedPager.setAdapter(adapter);
            } catch (NullPointerException e) {
                Log.e("AddMedicine", "Null Adapter");
            }
        }
    }

    public void getDefaultTime() {
        inputPref = context.getSharedPreferences(Constants.INPUT_SHARED_PREFS, Context.MODE_PRIVATE);

        bbh = inputPref.getInt(Constants.BEFORE_BREAKFAST_HOUR, 8);
        bbm = inputPref.getInt(Constants.BEFORE_BREAKFAST_MINUTE, 0);
        abh = inputPref.getInt(Constants.AFTER_BREAKFAST_HOUR, 10);
        abm = inputPref.getInt(Constants.AFTER_BREAKFAST_MINUTE, 0);

        blh = inputPref.getInt(Constants.BEFORE_LUNCH_HOUR, 12);
        blm = inputPref.getInt(Constants.BEFORE_LUNCH_MINUTE, 0);
        alh = inputPref.getInt(Constants.AFTER_LUNCH_HOUR, 14);
        alm = inputPref.getInt(Constants.AFTER_LUNCH_MINUTE, 0);

        bdh = inputPref.getInt(Constants.BEFORE_DINNER_HOUR, 20);
        bdm = inputPref.getInt(Constants.BEFORE_DINNER_MINUTE, 0);
        adh = inputPref.getInt(Constants.AFTER_DINNER_HOUR, 22);
        adm = inputPref.getInt(Constants.AFTER_DINNER_MINUTE, 0);

        is24hr = inputPref.getBoolean(Constants.IS_24_HOURS_FORMAT, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).setActivityClickListener(new MainActivity.ActivityClickListener() {
            @Override
            public void daySelectionChanged(int position, boolean isCheck) {
                onDaySelectionChanged(position, isCheck);
            }

            @Override
            public void daySelectionClick() {
                showDaySelection();
            }

            @Override
            public void emojiClick(int position) {
                onEmojiSelected(position);
            }


        });
        fragmentInteractionListener = (FragmentInteractionListener) activity;
    }

    FragmentInteractionListener fragmentInteractionListener;

    public interface FragmentInteractionListener {
        void addMedicine(boolean addToBackStack);
    }

    public void setFragmentInteractionListener(FragmentInteractionListener fragmentInteractionListener) {
        this.fragmentInteractionListener = fragmentInteractionListener;
    }

}
