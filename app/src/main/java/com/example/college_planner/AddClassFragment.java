package com.example.college_planner;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.college_planner.databinding.FragmentAddClassBinding;
import com.example.college_planner.databinding.FragmentMainBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddClassFragment extends Fragment {
    private FragmentAddClassBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddClassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddClassFragment newInstance(String param1, String param2) {
        AddClassFragment fragment = new AddClassFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddClassBinding.inflate(inflater, container, false);

        binding.startTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute1) -> {
                        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                        binding.SelectedTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));

                    }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.submitClass.setOnClickListener(view -> {
            //check if all fields are filled
            EditText classNameText = binding.enterClassName;
            String className = classNameText.getText().toString();

            EditText teacherNameText = binding.enterTeacherName;
            String teachName = teacherNameText.getText().toString();

            EditText classLocationText = binding.enterClassLocation;
            String classLoc = classLocationText.getText().toString();

            HashSet<DayOfWeek> daysOfWeek = new HashSet<>();

            if (binding.Monday.isChecked()) {
                daysOfWeek.add(DayOfWeek.MONDAY);
            }
            if (binding.Tuesday.isChecked()) {
                daysOfWeek.add(DayOfWeek.TUESDAY);
            }
            if (binding.Wendesday.isChecked()) {
                daysOfWeek.add(DayOfWeek.WEDNESDAY);
            }
            if (binding.Thursday.isChecked()) {
                daysOfWeek.add(DayOfWeek.THURSDAY);
            }
            if (binding.Friday.isChecked()) {
                daysOfWeek.add(DayOfWeek.FRIDAY);
            }

//            TimePicker classStartText = binding.classStartTime;
//            String classStarts = classStartText.toString();
//
//            EditText classEndText = binding.classEndTime;
//            String classEnds = classEndText.getText().toString();
//
//            boolean isEmpty = !checkAllVals(className, classLoc, teachName, daysOfWeek, classStarts, classEnds);
//            if (isEmpty) {
//                Snackbar showErrorMsg = Snackbar.make(binding.addClassFragment, R.string.not_all_vals_filled, Snackbar.LENGTH_SHORT);
//                showErrorMsg.show();
//                System.out.println("show snackbar msg");
//                return;
//            }
            NavHostFragment.findNavController(AddClassFragment.this).navigate(AddClassFragmentDirections.actionAddClassFragmentToFirstFragment());
        });

        return binding.getRoot();
    }

    private boolean checkAllVals(String className, String classLoc, String teachName, HashSet<DayOfWeek> daysOfWeek, String classStarts, String classEnds) {
        return className.trim().length() > 0 && classLoc.trim().length() > 0 && teachName.trim().length() > 0 && !daysOfWeek.isEmpty() && classStarts.trim().length() > 0 && classEnds.trim().length() > 0;
    }

}