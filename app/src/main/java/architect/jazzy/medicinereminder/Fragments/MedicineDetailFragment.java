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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.android.datetimepicker.time.RadialPickerLayout;

import java.util.ArrayList;
import java.util.Calendar;

import architect.jazzy.medicinereminder.Activities.MedicineDetails;
import architect.jazzy.medicinereminder.Adapters.DoctorSpinnerAdapter;
import architect.jazzy.medicinereminder.Adapters.ImageAdapter;
import architect.jazzy.medicinereminder.CustomComponents.TimingClass;
import architect.jazzy.medicinereminder.CustomViews.LabelledImage;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.Models.MedTime;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class MedicineDetailFragment extends Fragment {

    String changedName = "",customTimeHour, customTimeMinute,icon,bk = "none", ln = "none",
            dn = "none", endDate = "",doctorId="0";
    ImageView medIcon;
    EditText medicineName,notes,endDose;
    TableRow iconRow;
    LabelledImage morning, noon, night, custom;
    SharedPreferences inputPref;
    Boolean is24hr, menuSelected = false;
    Context context;
    Spinner spinner;
    Medicine medicine;

    private View mView;
    long medId;
    private final int[] checkBoxIds = {R.id.check_sun, R.id.check_mon, R.id.check_tue,
            R.id.check_wed, R.id.check_thu, R.id.check_fri, R.id.check_sat};
    int dd,mm,yy;
    int pos = 0;
    boolean[] daySelected = {false, false, false, false, false, false, false};
    int bbh, bbm, abh, abm, blh, blm, alh, alm, bdh, bdm, adh, adm;
    int customHour = 0, customMinute = 0;
    int day, month, year;
    private static final String TAG="MedicineDetailFragment";

    public static MedicineDetailFragment newInstance(Medicine medicine) {
        MedicineDetailFragment fragment = new MedicineDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.BUNDLE_MEDICINE_TAG, medicine);
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
        iconRow=(TableRow)v.findViewById(R.id.iconRow);

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


        this.morning = (LabelledImage) v.findViewById(R.id.morning);
        this.noon = (LabelledImage) v.findViewById(R.id.noon);
        this.night = (LabelledImage) v.findViewById(R.id.night);
        this.custom = (LabelledImage) v.findViewById(R.id.custom);
        this.spinner=(Spinner)v.findViewById(R.id.doctorSpinner);


        this.medIcon = (ImageView) v.findViewById(R.id.medIcon);
        this.medicine = getArguments().getParcelable(Constants.BUNDLE_MEDICINE_TAG);
        if(medicine==null){
            Toast.makeText(getActivity(),"Some Problem",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Medicine is null");
            getActivity().finish();
            return v;
        }
        changedName = medicine.getMedName();

        DataHandler handler = new DataHandler(getActivity().getApplicationContext());
        this.medicine = getArguments().getParcelable(Constants.BUNDLE_MEDICINE_TAG);
        this.medId=medicine.getId();

        ArrayList<Doctor> doctors=new ArrayList<>();
//        Log.e(TAG,"Medicine has doctor: "+medicine.getDoctorId());
        Doctor doctor=handler.getDoctor(medicine.getDoctorId());
        if(doctor!=null){
            doctors.add(doctor);
        }
        DoctorSpinnerAdapter adapter=new DoctorSpinnerAdapter(context,doctors);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.setEnabled(false);

        ((CheckBox) v.findViewById(checkBoxIds[0])).setChecked(medicine.getSun());
        ((CheckBox) v.findViewById(checkBoxIds[1])).setChecked(medicine.getMon());
        ((CheckBox) v.findViewById(checkBoxIds[2])).setChecked(medicine.getTue());
        ((CheckBox) v.findViewById(checkBoxIds[3])).setChecked(medicine.getWed());
        ((CheckBox) v.findViewById(checkBoxIds[4])).setChecked(medicine.getThu());
        ((CheckBox) v.findViewById(checkBoxIds[5])).setChecked(medicine.getFri());
        ((CheckBox) v.findViewById(checkBoxIds[6])).setChecked(medicine.getSat());


        setCheckboxEnabled(false);
        medicineName = (EditText) v.findViewById(R.id.medName);
        medicineName.setText(medicine.getMedName());


//        Log.e(TAG,"Medicine: "+medicine.getJSON());
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
//        Log.e("MedicineDetailFragment", "Custom Hour: " + this.customTimeHour + " Custom Minute: " + this.customTimeMinute);
        if (!this.customTimeHour.equals("-1")) {
//                Log.e("MedicineDetailFragment", "Integer Custom Hour: " + Integer.parseInt(ch) + " Custom Minute: " + Integer.parseInt(cm));
                custom.setText(TimingClass.getTime(Integer.parseInt(this.customTimeHour), Integer.parseInt(this.customTimeMinute), is24hr), "Custom Time");
        }else
            custom.setGrayScale();


        this.icon = String.valueOf(medicine.getIcon());
        this.notes = (EditText) mView.findViewById(R.id.medNotes);
        endDose = (EditText) v.findViewById(R.id.endDate);
        String note = medicine.getNote();
        notes.setText(note);
        notes.setEnabled(false);

        medIcon.setImageResource(ImageAdapter.emojis[medicine.getIcon()]);



        Constants.scaleEditTextImage(getActivity(), endDose, R.drawable.ic_action_calendar_month);
        Constants.scaleEditTextImage(getActivity(), notes, R.drawable.ic_action_edit);

        if (!medicine.getEndDate().isEmpty()) {
            if (medicine.getEndDate().equalsIgnoreCase(Constants.INDEFINITE_SCHEDULE) || medicine.getEndDate().isEmpty()) {
                endDate = medicine.getEndDate();
                endDose.setText("");
                ((CheckBox) mView.findViewById(R.id.endIndefinite)).setChecked(true);
            } else {
                String[] d = medicine.getEndDate().split("-");
                int dd = Integer.parseInt(d[0]), mm = Integer.parseInt(d[1]), yyyy = Integer.parseInt(d[2]);
                this.dd=dd;
                this.mm=mm;
                this.yy=yyyy;
                ((CheckBox) mView.findViewById(R.id.endIndefinite)).setChecked(false);
                endDose.setText(dd + " " + TimingClass.getEquivalentMonth(mm) + " " + yyyy);
                endDate = endDose.getText().toString();
            }
        }
        this.endDose.setEnabled(false);
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


        this.iconRow.setOnClickListener(emojiSelectListener);
        this.medicineName.addTextChangedListener(new TextWatcher() {
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
                    changedName = medicine.getMedName();
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


        DataHandler handler=new DataHandler(context);
        final ArrayList<Doctor> doctors=handler.getDoctorList();
        DoctorSpinnerAdapter adapter=new DoctorSpinnerAdapter(context,doctors);
        int selection=0;
        for(int i=0;i<doctors.size();i++){
            if(doctors.get(i).getId().equals(medicine.getDoctorId())){
                selection=i+1;
                break;
            }
        }
        spinner.setAdapter(adapter);
        spinner.setSelection(selection);
        spinner.setEnabled(true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    doctorId = "0";
                } else {
                    doctorId = doctors.get(position - 1).getId();
                }
//                Log.e(TAG, "Selected doctor: position:" + position + " with id:" + (position == 0 ? 0 : doctors.get(position - 1).getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
//                    customTimeHour = ch;
//                    customTimeMinute = cm;
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
        spinner.setEnabled(false);

        morning.removeViewClickListener();
        noon.removeViewClickListener();
        night.removeViewClickListener();

        iconRow.setOnClickListener(null);

        morning.setGrayScale();
        noon.setGrayScale();
        night.setGrayScale();
        custom.setGrayScale();

        mView.findViewById(R.id.endIndefinite).setEnabled(false);
        custom.removeViewClickListener();
        saveToDatabase();
    }

    public void saveToDatabase() {
        DataHandler handler = new DataHandler(context);
        handler.updateMedicine(getModifiedMedicine());
        Toast.makeText(context, "Medicine Schedule Modified", Toast.LENGTH_LONG).show();
        handler.close();
    }

    private Medicine getModifiedMedicine(){
        Medicine medicine=new Medicine();
        medicine.setId(this.medId);
        medicine.setMedName(this.changedName);
        medicine.setSun(((CheckBox) mView.findViewById(this.checkBoxIds[0])).isChecked());
        medicine.setMon(((CheckBox) mView.findViewById(this.checkBoxIds[1])).isChecked());
        medicine.setTue(((CheckBox) mView.findViewById(this.checkBoxIds[2])).isChecked());
        medicine.setWed(((CheckBox) mView.findViewById(this.checkBoxIds[3])).isChecked());
        medicine.setThu(((CheckBox) mView.findViewById(this.checkBoxIds[4])).isChecked());
        medicine.setFri(((CheckBox) mView.findViewById(this.checkBoxIds[5])).isChecked());
        medicine.setSat(((CheckBox) mView.findViewById(this.checkBoxIds[6])).isChecked());
        String endDate=Constants.INDEFINITE_SCHEDULE;
        if(dd!=0){
            endDate=dd+"-"+mm+"-"+yy;
        }
        medicine.setEndDate(endDate);
        medicine.setBreakfast(this.bk);
        medicine.setLunch(this.ln);
        medicine.setDinner(this.dn);
//        medicine.setIcon(this.pos);
        medicine.setIcon(this.medicine.getIcon());
        medicine.setDoctorId(this.doctorId);
        medicine.setCustomTime(new MedTime(this.customTimeHour, this.customTimeMinute));
        medicine.setNote(this.notes.getText().toString());
//        Log.e(TAG,"Medicine updated: "+medicine.getJSON());
        return medicine;
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
                dd=dayOfMonth;
                mm=monthOfYear+1;
                yy=year;
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

    public void discard(Medicine medicine) {
        Log.e(TAG,"Discarding Changes");
        ((CheckBox) v.findViewById(checkBoxIds[0])).setChecked(medicine.getSun());
        ((CheckBox) v.findViewById(checkBoxIds[1])).setChecked(medicine.getMon());
        ((CheckBox) v.findViewById(checkBoxIds[2])).setChecked(medicine.getTue());
        ((CheckBox) v.findViewById(checkBoxIds[3])).setChecked(medicine.getWed());
        ((CheckBox) v.findViewById(checkBoxIds[4])).setChecked(medicine.getThu());
        ((CheckBox) v.findViewById(checkBoxIds[5])).setChecked(medicine.getFri());
        ((CheckBox) v.findViewById(checkBoxIds[6])).setChecked(medicine.getSat());

        setCheckboxEnabled(false);
        this.medicineName.setText(medicine.getMedName());
        this.medicineName.setEnabled(false);
        this.medIcon.setImageResource(ImageAdapter.emojis[Integer.parseInt(this.icon)]);

        ArrayList<Doctor> doctors=new ArrayList<>();
        DataHandler handler=new DataHandler(getActivity());
        Doctor doctor=handler.getDoctor(medicine.getDoctorId());
        if(doctor!=null){
            doctors.add(doctor);
        }
        DoctorSpinnerAdapter adapter=new DoctorSpinnerAdapter(context,doctors);
        this.spinner.setAdapter(adapter);
        this.spinner.setSelection(1);
        this.spinner.setEnabled(false);

        this.morning.setNormalColor();
        if (medicine.getBreakfast().equals("before"))
            morning.setText(TimingClass.getTime(bbh, bbm, is24hr), "Before Breakfast");
        else if (medicine.getBreakfast().equals("after"))
            morning.setText(TimingClass.getTime(abh, abm, is24hr), "After Breakfast");
        else
            morning.setGrayScale();

        this.noon.setNormalColor();
        if (medicine.getLunch().equals("before"))
            noon.setText(TimingClass.getTime(blh, blm, is24hr), "Before Lunch");
        else if (medicine.getLunch().equals("after"))
            noon.setText(TimingClass.getTime(alh, alm, is24hr), "After Lunch");
        else
            noon.setGrayScale();

        this.night.setNormalColor();
        if (medicine.getDinner().equals("before"))
            night.setText(TimingClass.getTime(bdh, bdm, is24hr), "Before Dinner");
        else if (medicine.getDinner().equals("after"))
            night.setText(TimingClass.getTime(adh, adm, is24hr), "After Dinner");
        else
            night.setGrayScale();

        this.customTimeHour = String.valueOf(medicine.getCustomTime().getHour());
        this.customTimeMinute = String.valueOf(medicine.getCustomTime().getMinute());
        this.custom.setNormalColor();
        if (!customTimeHour.equals("-1")) {
//                Log.e("MedicineDetailFragment", "Integer Custom Hour: " + Integer.parseInt(ch) + " Custom Minute: " + Integer.parseInt(cm));
            custom.setText(TimingClass.getTime(Integer.parseInt(this.customTimeHour), Integer.parseInt(this.customTimeMinute), is24hr), "Custom Time");
        }else
            custom.setGrayScale();


        this.mView.findViewById(R.id.endIndefinite).setEnabled(false);
        this.icon = String.valueOf(medicine.getIcon());
        this.notes = (EditText) mView.findViewById(R.id.medNotes);
        String note = medicine.getNote();
        this.notes.setText(note);
        this.notes.setEnabled(false);
        this.endDose = (EditText) v.findViewById(R.id.endDate);
        this.endDose.setText(medicine.getEndDate());

        handler.close();
    }

    public String getMedName() {
        return ((EditText) mView.findViewById(R.id.medName)).getText().toString();
    }

    public Medicine getMedicine(){
        return this.medicine;
    }

    View.OnClickListener emojiSelectListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EmojiSelectFragment fragment = new EmojiSelectFragment();
            fragment.show(getFragmentManager(), "DialogFragment");
        }
    };

    public void onEmojiSelected(int position) {
        Log.e(TAG, "Emoji Position selected: " + position + " for medicine: " + medicine.getMedName());
        this.medIcon.setImageResource(ImageAdapter.emojis[position]);
        this.pos = position;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MedicineDetails) activity).setEmojiSetListener(new MedicineDetails.onEmojiSetListener() {
            @Override
            public void onEmojiSet(int position) {
//                Log.e("MedicineDetailFragment", "Emoji set recieved " + medName);
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
