package com.example.college_planner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.college_planner.databinding.FragmentAddAssignmentBinding;
import com.example.college_planner.databinding.FragmentAddExamBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExamFragment extends Fragment {
    private FragmentAddExamBinding binding;
    private Spinner classSelected;
    private Class assignedClass = null;
    private LocalDate dueDate = null;
    private LocalTime dueTime = null;
    private LocalTime endTime = null;
    private ArrayList<Class> classArr;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddExamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddExamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExamFragment newInstance(String param1, String param2) {
        AddExamFragment fragment = new AddExamFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddExamBinding.inflate(inflater, container, false);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);
        efab.show();
        createDropdown();

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

            DataStore ds = new DataStore(requireContext());
            ds.addExam(new Exam(examName, assignedClass, examDesc, officalDueDate, endTime, examLoc));

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
                    dueDate = LocalDate.of(year, monthOfYear, dayOfMonth);
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
        DataStore ds = new DataStore(requireContext());
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