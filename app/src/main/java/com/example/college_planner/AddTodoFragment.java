package com.example.college_planner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.college_planner.databinding.FragmentAddExamBinding;
import com.example.college_planner.databinding.FragmentAddTodoBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTodoFragment extends Fragment {

    private FragmentAddTodoBinding binding;
    private LocalDate dueDate = null;
    private LocalTime startTime = null;
    private LocalTime endTime = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddTodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTodoFragment newInstance(String param1, String param2) {
        AddTodoFragment fragment = new AddTodoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = binding = FragmentAddTodoBinding.inflate(inflater, container, false);

        FloatingActionButton fab = (FloatingActionButton) requireActivity().findViewById(R.id.fab);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);

        efab.show();

        efab.setOnClickListener((view) -> {
            String todoName = binding.todoName.getText().toString();
            String todoDesc = binding.todoDescription.getText().toString();

            if (checkValues(todoName, todoDesc)) {
                Snackbar.make(binding.addTodoFragment, ">:( NAME AND DESCRIPTION MUST BE FILLED TO SUBMIT TASK", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            DataStore ds = new DataStore(requireContext());
            ds.addTodo(new Todo(todoName, todoDesc, Optional.ofNullable(dueDate), Optional.ofNullable(Pair.create(startTime, Optional.ofNullable(endTime)))));

            NavHostFragment.findNavController(this).navigate(AddTodoFragmentDirections.actionAddTodoFragmentToFirstFragment());
        });

        binding.todoDate.setOnClickListener(v -> {
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

                        startTime = LocalTime.of(hourOfDay, minute1);

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

        return binding.getRoot();
    }

    public boolean checkValues(String todoName, String todoDesc) {
        return todoName.trim().length() == 0 || todoDesc.trim().length() == 0;
    }
}