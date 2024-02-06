package com.example.college_planner;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.college_planner.databinding.FragmentAddClassBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddClassFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddClassFragment extends Fragment {
    private FragmentAddClassBinding binding;
    private MainActivity activity;
    private Class defaultClass;
    private LocalTime startTime;
    private LocalTime endTime;
    private int mDefaultColor = 0;
    private List<Class> currentClasses;

    public AddClassFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddClassFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddClassFragment newInstance() {
        AddClassFragment fragment = new AddClassFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) requireActivity();
        currentClasses = activity.getDataStore().getClasses();
        if (savedInstanceState == null) {
            int defaultClassIdx = AddClassFragmentArgs.fromBundle(getArguments()).getIndex();
            if (defaultClassIdx != -1)
                defaultClass = activity.getDataStore().getClasses().get(defaultClassIdx);
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddClassBinding.inflate(inflater, container, false);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);
        efab.show();

        if (defaultClass != null) {
            binding.enterClassName.setText(defaultClass.getName());
            binding.enterTeacherName.setText(defaultClass.getTeacher());
            binding.enterClassLocation.setText(defaultClass.getLectureLocation());
            binding.Monday.setChecked(defaultClass.getLectureDays().contains(DayOfWeek.MONDAY));
            binding.Tuesday.setChecked(defaultClass.getLectureDays().contains(DayOfWeek.TUESDAY));
            binding.Wendesday.setChecked(defaultClass.getLectureDays().contains(DayOfWeek.WEDNESDAY));
            binding.Thursday.setChecked(defaultClass.getLectureDays().contains(DayOfWeek.THURSDAY));
            binding.Friday.setChecked(defaultClass.getLectureDays().contains(DayOfWeek.FRIDAY));
            startTime = defaultClass.getLectureStartTime();
            mDefaultColor = defaultClass.getColor().toArgb();
            binding.pickColorButton.setBackgroundColor(mDefaultColor);
            endTime = defaultClass.getLectureEndTime();
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();

            binding.SelectedStartTime.setText(startTime.format(formatter));
            binding.SelectedEndTime.setText(endTime.format(formatter));
        }

        efab.setOnClickListener(view -> {
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

            String classStarts = binding.SelectedStartTime.getText().toString();
            String classEnds = binding.SelectedEndTime.getText().toString();

            boolean isEmpty = checkAllVals(className, classLoc, teachName, daysOfWeek, classStarts, classEnds, mDefaultColor);

            if (isEmpty) {
                Snackbar.make(binding.addClassFragment, ">:( ALL FIELDS MUST BE FILLED TO SUBMIT CLASS", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            if (isDupeClass(className)) {
                Snackbar.make(binding.addClassFragment, ">:( YOU CAN'T ADD A DUPLICATE CLASS >:(", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            Class newClass = new Class(className, teachName, classLoc, daysOfWeek, startTime, endTime, Color.valueOf(mDefaultColor));

            if (defaultClass != null) {
                // Edited
                currentClasses.set(currentClasses.indexOf(defaultClass), newClass);
                List<Exam> exams = activity.getDataStore().getExams();
                List<Assignment> assignments = activity.getDataStore().getAssignments();
                exams.stream().filter((exam) -> exam._class.equals(defaultClass)).forEach(((exam) -> exam._class = newClass));
                assignments.stream().filter((assign) -> assign._class.equals(defaultClass)).forEach(((assign) -> assign._class = newClass));
            } else {
                currentClasses.add(newClass);
            }

            NavHostFragment.findNavController(AddClassFragment.this).navigate(AddClassFragmentDirections.actionAddClassFragmentToFirstFragment());
        });

        binding.startTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
                DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                binding.SelectedStartTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));
                startTime = LocalTime.of(hourOfDay, minute1);
            }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.endTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
                DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                binding.SelectedEndTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));

                endTime = LocalTime.of(hourOfDay, minute1);

            }, hour, minute, false);
            timePickerDialog.show();
        });

        binding.pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialogue();
            }
        });

        return binding.getRoot();
    }

    private boolean checkAllVals(String className, String classLoc, String teachName, HashSet<DayOfWeek> daysOfWeek, String classStarts, String classEnds, int color) {
        return className.trim().length() == 0 || classLoc.trim().length() == 0 || teachName.trim().length() == 0 || daysOfWeek.isEmpty() || classStarts.equals("Time") || classEnds.equals("Time") || color == 0;
    }

    public void openColorPickerDialogue() {

        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(requireContext(), mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;

                binding.pickColorButton.setBackgroundColor(mDefaultColor);
            }
        });
        colorPickerDialogue.show();
    }

    public boolean isDupeClass(String className) {
        for (int i = 0; i < currentClasses.size(); i++) {
            if (defaultClass != null && className.equals(defaultClass.getName())) {
                // If we are editing and the input matches the existing class, we chill
                return false;
            }

            if (currentClasses.get(i).getName().equals(className)) {
                return true;
            }
        }
        return false;
    }

}