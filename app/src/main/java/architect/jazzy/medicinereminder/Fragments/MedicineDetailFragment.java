package architect.jazzy.medicinereminder.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;

import java.util.Calendar;
import java.util.HashMap;

import architect.jazzy.medicinereminder.Activities.MedicineDetails;
import architect.jazzy.medicinereminder.Adapters.ImageAdapter;
import architect.jazzy.medicinereminder.CustomViews.LabelledImage;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.HelperClasses.TimingClass;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class MedicineDetailFragment extends Fragment {

    public static String medName;
    public String medicine;
    String changedName = "";
    ImageView medIcon;
    EditText medicineName;
    String doctorId="";
    LabelledImage morning, noon, night, custom;
    int pos = 0;
    SharedPreferences inputPref;
    EditText endDose;
    private View mView;
    private final int[] checkBoxIds = {R.id.check_sun, R.id.check_mon, R.id.check_tue, R.id.check_wed, R.id.check_thu, R.id.check_fri, R.id.check_sat};
    String bk = "none", ln = "none", dn = "none", endDate = "";
    boolean[] daySelected = {false, false, false, false, false, false, false};
    int bbh, bbm, abh, abm, blh, blm, alh, alm, bdh, bdm, adh, adm;
    Boolean is24hr, menuSelected = false;
    Context context;
    int customHour = 0, customMinute = 0;
    int day, month, year;
    EditText notes;
    String customTimeHour, customTimeMinute, ch, cm, icon;
    private static final String TAG="MedicineDetailFragment";

    public static MedicineDetailFragment newInstance(String medicineName) {
        MedicineDetailFragment fragment = new MedicineDetailFragment();
        medName = medicineName;
        Bundle args = new Bundle();
        args.putString(Constants.BUNDLE_MEDICINE_TAG, medicineName);
        fragment.setArguments(args);
        return fragment;
    }

    public MedicineDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();


    }

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_medicine_details, container, false);
        this.mView = v;

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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


        morning = (LabelledImage) v.findViewById(R.id.morning);
        noon = (LabelledImage) v.findViewById(R.id.noon);
        night = (LabelledImage) v.findViewById(R.id.night);
        custom = (LabelledImage) v.findViewById(R.id.custom);


        medIcon = (ImageView) v.findViewById(R.id.medIcon);
        medName = getArguments().getString(Constants.BUNDLE_MEDICINE_TAG);
        changedName = medName;
        medicine = getArguments().getString(Constants.BUNDLE_MEDICINE_TAG);

        DataHandler handler = new DataHandler(getActivity().getApplicationContext());

        Medicine medicine = handler.findRow(medName);
        ((CheckBox) v.findViewById(checkBoxIds[0])).setChecked(medicine.getSun());
        ((CheckBox) v.findViewById(checkBoxIds[1])).setChecked(medicine.getMon());
        ((CheckBox) v.findViewById(checkBoxIds[2])).setChecked(medicine.getTue());
        ((CheckBox) v.findViewById(checkBoxIds[3])).setChecked(medicine.getWed());
        ((CheckBox) v.findViewById(checkBoxIds[4])).setChecked(medicine.getThu());
        ((CheckBox) v.findViewById(checkBoxIds[5])).setChecked(medicine.getFri());
        ((CheckBox) v.findViewById(checkBoxIds[6])).setChecked(medicine.getSat());


        setCheckboxEnabled(false);
        medicineName = (EditText) v.findViewById(R.id.medName);
        medicineName.setText(medName);


        morning.setNormalColor();
        this.doctorId=medicine.getDoctorId();
        //TODO: Change these wrt new table
        if (medicine.getBreakfast().equals("before"))
            morning.setText(TimingClass.getTime(bbh, bbm, is24hr), "Before Breakfast");
        else if (medicine.getBreakfast().equals("after"))
            morning.setText(TimingClass.getTime(abh, abm, is24hr), "After Breakfast");
        else
            morning.setGrayScale();
        this.bk = medicine.getBreakfast();

        noon.setNormalColor();
        if (medicine.getLunch().equals("before"))
            noon.setText(TimingClass.getTime(blh, blm, is24hr), "Before Lunch");
        else if (medicine.getLunch().equals("after"))
            noon.setText(TimingClass.getTime(alh, alm, is24hr), "After Lunch");
        else
            noon.setGrayScale();
        this.ln = medicine.getLunch();

        night.setNormalColor();
        if (medicine.getDinner().equals("before"))
            night.setText(TimingClass.getTime(bdh, bdm, is24hr), "Before Dinner");
        else if (medicine.getDinner().equals("after"))
            night.setText(TimingClass.getTime(adh, adm, is24hr), "After Dinner");
        else
            night.setGrayScale();
        this.dn = medicine.getDinner();



        this.customTimeHour = String.valueOf(medicine.getCustomTime().getHour());
        this.customTimeMinute = String.valueOf(medicine.getCustomTime().getMinute());
        custom.setNormalColor();
        Log.e("MedicineDetailFragment", "Custom Hour: " + ch + " Custom Minute: " + cm);
        if (ch != null && !ch.equals("null")) {
            if (!ch.isEmpty()) {
                Log.e("MedicineDetailFragment", "Integer Custom Hour: " + Integer.parseInt(ch) + " Custom Minute: " + Integer.parseInt(cm));
                custom.setText(TimingClass.getTime(Integer.parseInt(ch), Integer.parseInt(cm), is24hr), "Custom Time");
            }
        } else
            custom.setGrayScale();


        this.icon = String.valueOf(medicine.getIcon());
        this.notes = (EditText) mView.findViewById(R.id.medNotes);
        String note = medicine.getNote();
        notes.setText(note);
        notes.setEnabled(false);

        medIcon.setImageResource(ImageAdapter.emojis[medicine.getIcon()]);

        endDose = (EditText) v.findViewById(R.id.endDate);
        if (!medicine.getEndDate().isEmpty()) {
            if (medicine.getEndDate().equalsIgnoreCase(Constants.INDEFINITE_SCHEDULE) || medicine.getEndDate().isEmpty()) {
                endDate = medicine.getEndDate();
                endDose.setText("");
                ((CheckBox) mView.findViewById(R.id.endIndefinite)).setChecked(true);
            } else {
                String[] d = medicine.getEndDate().split("-");
                int dd = Integer.parseInt(d[0]), mm = Integer.parseInt(d[1]), yyyy = Integer.parseInt(d[2]);
                ((CheckBox) mView.findViewById(R.id.endIndefinite)).setChecked(false);
                endDose.setText(dd + " " + TimingClass.getEquivalentMonth(mm) + " " + yyyy);
                endDate = endDose.getText().toString();
            }
        }
        endDose.setEnabled(false);
        handler.close();


        for (int i = 0; i < checkBoxIds.length; i++) {
            mView.findViewById(checkBoxIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBoxToggle(v);
                }
            });
        }


        return v;
    }

    public void onEmojiSelected(int position) {
        medIcon.setImageResource(ImageAdapter.emojis[position]);
        pos = position;
    }

    public void setCheckboxEnabled(Boolean b) {
        for (int i = 4; i < 11; i++) {
            mView.findViewById(checkBoxIds[i - 4]).setEnabled(b);
        }
    }

    public void checkBoxToggle(View v) {
        int id = v.getId();
        Boolean state = ((CheckBox) v).isChecked();
        switch (id) {
            case R.id.check_sun:
                this.daySelected[0] = state;
                break;
            case R.id.check_mon:
                this.daySelected[1] = state;
                break;
            case R.id.check_tue:
                this.daySelected[2] = state;
                break;
            case R.id.check_wed:
                this.daySelected[3] = state;
                break;
            case R.id.check_thu:
                this.daySelected[4] = state;
                break;
            case R.id.check_fri:
                this.daySelected[5] = state;
                break;
            case R.id.check_sat:
                this.daySelected[6] = state;
                break;
            default:
                break;

        }
    }

    public void edit() {
        medicineName.setEnabled(true);
        setCheckboxEnabled(true);

        medIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiSelectFragment fragment = new EmojiSelectFragment();
                fragment.show(getFragmentManager(), "DialogFragment");
            }
        });
        medicineName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changedName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    changedName = medName;
                }
            }
        });

        /*Activate Notes*/
        notes.setEnabled(true);
        endDose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    DateDialog(v.getId());
                return true;
            }
        });

        morning.setTag("Breakfast");
        noon.setTag("Lunch");
        night.setTag("Dinner");


        LabelledImage.ViewClickListener viewClickListener = new LabelledImage.ViewClickListener() {
            @Override
            public void onImageClick(LabelledImage labelledImageView) {
                if (labelledImageView.getState()) {
                    labelledImageView.setNormalColor();
                    onTopTextClick(labelledImageView);

                } else {
                    labelledImageView.setGrayScale();
                    none(labelledImageView, labelledImageView.getTag());
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
                            if (tag.equalsIgnoreCase("breakfast") && !bk.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }
                            if (tag.equalsIgnoreCase("lunch") && !ln.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }
                            if (tag.equalsIgnoreCase("dinner") && !dn.equalsIgnoreCase("none")) {
                                none(labelledImageView, labelledImageView.getTag());
                                menuSelected = false;
                            }

                        }
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

        custom.setViewClickListener(new LabelledImage.ViewClickListener() {
            @Override
            public void onImageClick(LabelledImage labelledImageView) {
                if (labelledImageView.getState()) {
                    labelledImageView.setNormalColor();
                    onTopTextClick(labelledImageView);

                } else {
                    customTimeHour = ch;
                    customTimeMinute = cm;
                    labelledImageView.setGrayScale();
                    none(labelledImageView, labelledImageView.getTag());
                }
            }

            @Override
            public void onTopTextClick(LabelledImage labelledImageView) {
                TimeDialog(labelledImageView.getId());
            }
        });
        endDose.setEnabled(true);
        CheckBox indefinite = (CheckBox) mView.findViewById(R.id.endIndefinite);
        if (this.endDate.isEmpty()) {
            endDose.setEnabled(false);
        }
        indefinite.setEnabled(true);
        indefinite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                endDose.setEnabled(!isChecked);
                if (isChecked) {
                    endDate = Constants.INDEFINITE_SCHEDULE;
                }

            }
        });
    }

    public void save() {
        setCheckboxEnabled(false);
        medicineName.setEnabled(false);

        /*Activate Notes*/
        notes.setEnabled(false);
        endDose.setEnabled(false);
        endDose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mView.findViewById(R.id.endIndefinite).setEnabled(false);

        morning.removeViewClickListener();
        noon.removeViewClickListener();
        night.removeViewClickListener();

        medIcon.setOnClickListener(null);

        morning.setGrayScale();
        noon.setGrayScale();
        night.setGrayScale();
        custom.setGrayScale();

        mView.findViewById(R.id.endIndefinite).setEnabled(false);
        custom.removeViewClickListener();
        saveToDatabase();
    }

    public void saveToDatabase() {
        HashMap<String, String> dataSet = getData();
        DataHandler handler = new DataHandler(context);
        Log.e("saveToDatabase()", dataSet.get(Constants.MEDICINE_NAME) + "  ***  " + dataSet.get(Constants.NOTES));
        Log.e("MedicineDetailsFragment", "Original Name= " + medicine + " new Name=" + dataSet.get(Constants.MEDICINE_NAME));
        Log.e("MedicineDetailsFragment", "Original Name= " + medicine + " Times: " + bk + " " + ln + " " + dn);

        handler.updateData(medicine,
                dataSet.get(Constants.MEDICINE_NAME),
                dataSet.get(Constants.DOCTOR_ID),
                dataSet.get(Constants.DAY_SUNDAY),
                dataSet.get(Constants.DAY_MONDAY),
                dataSet.get(Constants.DAY_TUESDAY), dataSet.get(Constants.DAY_WEDNESDAY),
                dataSet.get(Constants.DAY_THURSDAY), dataSet.get(Constants.DAY_FRIDAY),
                dataSet.get(Constants.DAY_SATURDAY),
                dataSet.get(Constants.END_DATE), dataSet.get(Constants.BREAKFAST),
                dataSet.get(Constants.LUNCH), dataSet.get(Constants.DINNER),
                dataSet.get(Constants.CUSTOM_TIME_HOUR), dataSet.get(Constants.NOTES));

        Toast.makeText(context, "Medicine Schedule Modified", Toast.LENGTH_LONG).show();
        handler.close();
    }

    public HashMap<String, String> getData() {
        HashMap<String, String> dataSet = new HashMap<>();
        dataSet.put(Constants.MEDICINE_NAME, this.changedName);
        dataSet.put(Constants.DOCTOR_ID,this.doctorId);
        dataSet.put(Constants.DAY_SUNDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[0])).isChecked()));
        dataSet.put(Constants.DAY_MONDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[1])).isChecked()));
        dataSet.put(Constants.DAY_TUESDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[2])).isChecked()));
        dataSet.put(Constants.DAY_WEDNESDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[3])).isChecked()));
        dataSet.put(Constants.DAY_THURSDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[4])).isChecked()));
        dataSet.put(Constants.DAY_FRIDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[5])).isChecked()));
        dataSet.put(Constants.DAY_SATURDAY, String.valueOf(((CheckBox) mView.findViewById(this.checkBoxIds[6])).isChecked()));

        dataSet.put(Constants.END_DATE, this.endDate);

        dataSet.put(Constants.BREAKFAST, this.bk);
        dataSet.put(Constants.LUNCH, this.ln);
        dataSet.put(Constants.DINNER, this.dn);

        dataSet.put(Constants.ICON, String.valueOf(pos));

        dataSet.put(Constants.CUSTOM_TIME_HOUR, this.customTimeHour+","+this.customTimeMinute);

        dataSet.put(Constants.NOTES, this.notes.getText().toString());
        Log.e("getData()", this.changedName + "   Custom Time: " + this.customTimeHour + ":" + this.customTimeMinute);
        return dataSet;
    }

    public void DateDialog(final int id) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener onDateSetListener = new com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.android.datetimepicker.date.DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                endDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                ((EditText) mView.findViewById(id)).setText(dayOfMonth + " " + TimingClass.getEquivalentMonth(monthOfYear + 1) + " " + year);
            }
        };
        com.android.datetimepicker.date.DatePickerDialog.newInstance(onDateSetListener, year, month, day).show(getFragmentManager(), "DATE_PICKER");
    }

    public void TimeDialog(final int id) {
        Calendar c = Calendar.getInstance();
        customHour = c.get(Calendar.HOUR_OF_DAY);
        customMinute = c.get(Calendar.MINUTE);
        ((LabelledImage) mView.findViewById(id)).setGrayScale();
        ((LabelledImage) mView.findViewById(id)).setState(false);

        com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener onTimeSetListener = new com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
                Log.e("State", "Time Set");
                ((LabelledImage) mView.findViewById(id)).setText(TimingClass.getTime(hourOfDay, minute, is24hr), "Custom Time");
                customHour = hourOfDay;
                customMinute = minute;
                customTimeHour = String.valueOf(customHour);
                customTimeMinute = String.valueOf(customMinute);
                ((LabelledImage) mView.findViewById(id)).setNormalColor();
                ((LabelledImage) mView.findViewById(id)).setState(true);
            }
        };
        Calendar calendar = Calendar.getInstance();
        com.android.datetimepicker.time.TimePickerDialog.newInstance(onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24hr).show(getFragmentManager(), "TIME_PICKER");
    }

    public void discard(String name) {
        Log.e(TAG,"Discarding Changes");
        DataHandler handler = new DataHandler(getActivity());
        Medicine medicine = handler.findRow(name);
        ((CheckBox) v.findViewById(checkBoxIds[0])).setChecked(medicine.getSun());
        ((CheckBox) v.findViewById(checkBoxIds[1])).setChecked(medicine.getMon());
        ((CheckBox) v.findViewById(checkBoxIds[2])).setChecked(medicine.getTue());
        ((CheckBox) v.findViewById(checkBoxIds[3])).setChecked(medicine.getWed());
        ((CheckBox) v.findViewById(checkBoxIds[4])).setChecked(medicine.getThu());
        ((CheckBox) v.findViewById(checkBoxIds[5])).setChecked(medicine.getFri());
        ((CheckBox) v.findViewById(checkBoxIds[6])).setChecked(medicine.getSat());

        setCheckboxEnabled(false);
        this.medicineName.setText(name);
        this.medicineName.setEnabled(false);
        this.medIcon.setImageResource(ImageAdapter.emojis[Integer.parseInt(this.icon)]);


        this.morning.setNormalColor();
        if (medicine.getBreakfast().equals("before"))
            morning.setText(TimingClass.getTime(bbh, bbm, is24hr), "Before Breakfast");
        else if (medicine.getBreakfast().equals("after"))
            morning.setText(TimingClass.getTime(abh, abm, is24hr), "After Breakfast");
        else
            morning.setGrayScale();

        noon.setNormalColor();
        if (medicine.getLunch().equals("before"))
            noon.setText(TimingClass.getTime(blh, blm, is24hr), "Before Lunch");
        else if (medicine.getLunch().equals("after"))
            noon.setText(TimingClass.getTime(alh, alm, is24hr), "After Lunch");
        else
            noon.setGrayScale();

        night.setNormalColor();
        if (medicine.getDinner().equals("before"))
            night.setText(TimingClass.getTime(bdh, bdm, is24hr), "Before Dinner");
        else if (medicine.getDinner().equals("after"))
            night.setText(TimingClass.getTime(adh, adm, is24hr), "After Dinner");
        else
            night.setGrayScale();

        customTimeHour = String.valueOf(medicine.getCustomTime().getHour());
        customTimeMinute = String.valueOf(medicine.getCustomTime().getMinute());
        custom.setNormalColor();
        if (ch != null && !ch.equals("null")) {
            if (!ch.isEmpty()) {
                custom.setText(TimingClass.getTime(Integer.parseInt(ch), Integer.parseInt(cm), is24hr), "Custom Time");
            }
        } else
            custom.setGrayScale();

        mView.findViewById(R.id.endIndefinite).setEnabled(false);
        icon = String.valueOf(medicine.getIcon());
        notes = (EditText) mView.findViewById(R.id.medNotes);
        String note = medicine.getNote();
        notes.setText(note);
        notes.setEnabled(false);
        endDose = (EditText) v.findViewById(R.id.endDate);
        endDose.setText(medicine.getEndDate());

        handler.close();
    }

    public String getMedName() {
        return ((EditText) mView.findViewById(R.id.medName)).getText().toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MedicineDetails) activity).setEmojiSetListener(new MedicineDetails.onEmojiSetListener() {
            @Override
            public void onEmojiSet(int position) {
                Log.e("MedicineDetailFragment", "Emoji set recieved " + medName);
                onEmojiSelected(position);
            }
        });
    }


    public void before(LabelledImage view, String tag) {
        int h = 0, m = 0;
        if (tag.equalsIgnoreCase("breakfast")) {
            this.bk = "before";
            h = bbh;
            m = bbm;
        } else if (tag.equalsIgnoreCase("lunch")) {
            this.ln = "before";
            h = blh;
            m = blm;
        } else if (tag.equalsIgnoreCase("dinner")) {
            this.dn = "before";
            h = bdh;
            m = bdm;
        }

        view.setText(TimingClass.getTime(h, m, is24hr), "Before " + tag);
        view.setTextAux("Before " + tag);
        view.setTime(h, m);
    }

    public void after(LabelledImage view, String tag) {
        int h = 0, m = 0;
        if (tag.equalsIgnoreCase("breakfast")) {
            this.bk = "after";
            h = abh;
            m = abm;
        } else if (tag.equalsIgnoreCase("lunch")) {
            this.ln = "after";
            h = alh;
            m = alm;
        } else if (tag.equalsIgnoreCase("dinner")) {
            this.dn = "after";
            h = adh;
            m = adm;
        }
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
        view.setText(" ----- ", "");
        view.setTextAux("");
        view.setState(false);
        view.setGrayScale();
    }

}
