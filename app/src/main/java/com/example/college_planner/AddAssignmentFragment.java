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

import com.example.college_planner.databinding.FragmentAddAssignmentBinding;
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
 * Use the {@link AddAssignmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAssignmentFragment extends Fragment {

    private FragmentAddAssignmentBinding binding;
    private MainActivity activity;
    private Assignment defaultAssignment;
    private Spinner classSelected;
    private Class assignedClass = null;
    private LocalDate dueDate = null;
    private LocalTime dueTime = null;
    private List<Class> classArr;

    public AddAssignmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddAssignmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAssignmentFragment newInstance() {
        AddAssignmentFragment fragment = new AddAssignmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) requireActivity();

        if (savedInstanceState == null) {
            int defaultAssignmentIdx = AddAssignmentFragmentArgs.fromBundle(getArguments()).getIndex();
            if (defaultAssignmentIdx != -1)
                defaultAssignment = activity.getDataStore().getAssignments().get(defaultAssignmentIdx);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAssignmentBinding.inflate(inflater, container, false);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);
        efab.show();
        createDropdown();

        if (defaultAssignment != null) {
            binding.enterAssignName.setText(defaultAssignment.getName());
            binding.classDropdown.setSelection(activity.getDataStore().getClasses().indexOf(defaultAssignment._class) + 1);
            binding.assignmentDescription.setText(defaultAssignment.getDescription());
            dueDate = defaultAssignment.getDueDate().toLocalDate();
            dueTime = defaultAssignment.getDueDate().toLocalTime();
            binding.textDueDate.setText(dueDate.getDayOfMonth() + "-" + (dueDate.getMonthValue() + 1) + "-" + dueDate.getYear());
            binding.textDueTime.setText(dueTime.format(new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter()));
        }

        efab.setOnClickListener((view) -> {
            String assignmentName = binding.enterAssignName.getText().toString();
            assignedClass = findClass(binding.classDropdown.getSelectedItemPosition());
            String assignDesc = binding.assignmentDescription.getText().toString();
            LocalDateTime officalDueDate = (dueDate == null || dueTime == null) ? null : LocalDateTime.of(dueDate, dueTime);

            if (checkValues(assignmentName, assignedClass, assignDesc, officalDueDate)) {
                Snackbar.make(binding.addAssignmentFragment, ">:( ALL FIELDS MUST BE FILLED TO SUBMIT ASSIGNMENT", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            Assignment newAssignment = new Assignment(assignmentName, assignedClass, assignDesc, officalDueDate);
            List<Assignment> assignments = activity.getDataStore().getAssignments();

            if (defaultAssignment != null) {
                // Edited
                assignments.set(assignments.indexOf(defaultAssignment), newAssignment);
            } else {
                assignments.add(newAssignment);
            }

            NavHostFragment.findNavController(this).navigate(AddAssignmentFragmentDirections.actionAddAssignmentFragmentToFirstFragment());
        });

        binding.assignmentDueDate.setOnClickListener(v -> {
            // on below line we are getting
            // the instance of our calendar.
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
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

        binding.dueTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute1) -> {
                        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                        binding.textDueTime.setText(LocalTime.of(hourOfDay, minute1).format(timeFormat));

                        dueTime = LocalTime.of(hourOfDay, minute1);

                    }, hour, minute, false);
            timePickerDialog.show();

        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void createDropdown() {
        classSelected = (Spinner) binding.classDropdown;
        String[] classes = getNames(((MainActivity) requireActivity()).getDataStore());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, classes);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSelected.setAdapter(adapter);
        classSelected.setPrompt("Select Class for Assignment");
    }

    public String[] getNames(DataStore ds) {
        classArr = ds.getClasses();
        String[] arr = new String[classArr.size() + 1];
        arr[0] = "Pick a class for this assignment.";
        for (int i = 1; i < arr.length; i++) {
            arr[i] = classArr.get(i - 1).getName();
        }

        return arr;
    }

    public Class findClass(int index) {
        System.out.println("this is item position: " + index);
        if (index > 0) {
            return classArr.get(index - 1);
        } else {
            return null;
        }
    }

    public boolean checkValues(String assignmentName, Class assignedClass, String description, LocalDateTime dueDate) {
        return assignmentName.trim().length() == 0 || assignedClass == null || description.trim().length() == 0 || dueDate == null;
    }
}