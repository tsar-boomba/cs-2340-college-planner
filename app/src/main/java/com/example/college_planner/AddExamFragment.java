package com.example.college_planner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.college_planner.databinding.FragmentAddExamBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExamFragment extends Fragment {
    private FragmentAddExamBinding binding;
    private MainActivity activity;
    private Exam defaultExam;
    private Spinner classSelected;
    private Class assignedClass = null;
    private LocalDate dueDate = null;
    private LocalTime dueTime = null;
    private LocalTime endTime = null;
    private List<Class> classArr;

    public AddExamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddExamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExamFragment newInstance() {
        AddExamFragment fragment = new AddExamFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) requireActivity();

        if (savedInstanceState == null) {
            int defaultExamIdx = AddExamFragmentArgs.fromBundle(getArguments()).getIndex();
            if (defaultExamIdx != -1)
                defaultExam = activity.getDataStore().getExams().get(defaultExamIdx);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddExamBinding.inflate(inflater, container, false);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);
        efab.show();
        createDropdown();

        if (defaultExam != null) {
            binding.examName.setText(defaultExam.getName());
            binding.classDropdown.setSelection(activity.getDataStore().getClasses().indexOf(defaultExam._class) + 1);
            binding.examLoc.setText(defaultExam.getLocation());
            binding.examDescription.setText(defaultExam.getDescription());
            dueDate = defaultExam.getDueDate().toLocalDate();
            dueTime = defaultExam.getDueDate().toLocalTime();
            endTime = defaultExam.getEndTime();
            binding.textDueDate.setText(dueDate.getDayOfMonth() + "-" + (dueDate.getMonthValue() + 1) + "-" + dueDate.getYear());
            binding.textStartTime.setText(dueTime.format(new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter()));
            binding.textEndTime.setText(endTime.format(new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter()));
        }

        efab.setOnClickListener((view) -> {
            String examName = binding.examName.getText().toString();
            assignedClass = findClass(binding.classDropdown.getSelectedItemPosition());
            String examDesc = binding.examDescription.getText().toString();
            LocalDateTime officalDueDate = (dueDate == null || dueTime == null) ? null : LocalDateTime.of(dueDate, dueTime);
            String examLoc = binding.examLoc.getText().toString();

            if (checkValues(examName, assignedClass, examDesc, officalDueDate, endTime, examLoc)) {
                Snackbar.make(binding.addExamFragment, ">:( ALL FIELDS MUST BE FILLED TO SUBMIT EXAM", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            Exam newExam = new Exam(examName, assignedClass, examDesc, officalDueDate, endTime, examLoc);
            List<Exam> exams = activity.getDataStore().getExams();

            if (defaultExam != null) {
                // Edited
                exams.set(exams.indexOf(defaultExam), newExam);
            } else {
                exams.add(newExam);
            }

            NavHostFragment.findNavController(this).navigate(AddExamFragmentDirections.actionAddExamFragmentToFirstFragment());
        });

        binding.examDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    binding.textDueDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    dueDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    System.out.println(dueDate.toString());
                }
            }, year, month, day);
            datePickerDialog.show();
        });

        binding.startTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute1) -> {
                        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                        binding.textStartTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));

                        dueTime = LocalTime.of(hourOfDay, minute1);

                    }, hour, minute, false);
            timePickerDialog.show();

        });

        binding.endTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute1) -> {
                        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                        binding.textEndTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));

                        endTime = LocalTime.of(hourOfDay, minute1);

                    }, hour, minute, false);
            timePickerDialog.show();
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void createDropdown() {
        classSelected = (Spinner) binding.classDropdown;
        DataStore ds = ((MainActivity) requireActivity()).getDataStore();
        String[] classes = getNames(ds);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, classes);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSelected.setAdapter(adapter);
    }

    public String[] getNames(DataStore ds) {
        classArr = ds.getClasses();
        String[] arr = new String[classArr.size() + 1];
        arr[0] = "Select Class for Exam";
        for (int i = 1; i < arr.length; i++) {
            arr[i] = classArr.get(i - 1).getName();
        }

        return arr;
    }

    public Class findClass(int index) {
        System.out.println("this is item position: " + index);
        if (index != 0) {
            return classArr.get(index - 1);
        } else {
            return null;
        }
    }

    public boolean checkValues(String assignmentName, Class assignedClass, String description, LocalDateTime dueDate, LocalTime endTime, String examLoc) {
        return assignmentName.trim().length() == 0 || assignedClass == null || description.trim().length() == 0 || dueDate == null || endTime == null || examLoc.trim().length() == 0;
    }
}