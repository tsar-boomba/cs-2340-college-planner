package com.example.college_planner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.college_planner.databinding.FragmentAddTodoBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTodoFragment extends Fragment {

    private FragmentAddTodoBinding binding;
    private MainActivity activity;
    private Todo deafultTodo;
    private LocalDate dueDate = null;
    private LocalTime startTime = null;
    private LocalTime endTime = null;

    public AddTodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddTodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTodoFragment newInstance() {
        AddTodoFragment fragment = new AddTodoFragment();
        fragment.setArguments(new Bundle());
        System.out.println("New add todo!!");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) requireActivity();

        if (savedInstanceState == null) {
            int defaultTodoIdx = AddTodoFragmentArgs.fromBundle(getArguments()).getIndex();
            if (defaultTodoIdx != -1)
                deafultTodo = activity.getDataStore().getTodos().get(defaultTodoIdx);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false);

        FloatingActionButton fab = (FloatingActionButton) requireActivity().findViewById(R.id.fab);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);

        efab.show();

        if (deafultTodo != null) {
            binding.todoName.setText(deafultTodo.getTask());
            binding.todoDescription.setText(deafultTodo.getDescription());
            dueDate = deafultTodo.getDate().orElse(null);

            if (dueDate != null) {
                binding.textDueDate.setText(dueDate.getDayOfMonth() + "-" + (dueDate.getMonthValue() + 1) + "-" + dueDate.getYear());
            }

            deafultTodo.getStartTimeAndEndTime().ifPresent((startTimeEndTime) -> {
                startTime = startTimeEndTime.first;
                DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
                binding.textStartTime.setText(startTimeEndTime.first.format(formatter));

                startTimeEndTime.second.ifPresent((endTime) -> {
                    this.endTime = endTime;
                    binding.textEndTime.setText(endTime.format(formatter));
                });
            });
        }

        efab.setOnClickListener((view) -> {
            String todoName = binding.todoName.getText().toString();
            String todoDesc = binding.todoDescription.getText().toString();

            if (checkValues(todoName, todoDesc)) {
                Snackbar.make(binding.addTodoFragment, ">:( NAME AND DESCRIPTION MUST BE FILLED TO SUBMIT TASK", BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }

            Todo newTodo = new Todo(todoName, todoDesc, Optional.ofNullable(dueDate), Optional.ofNullable(startTime == null ? null : Pair.create(startTime, Optional.ofNullable(endTime))));
            List<Todo> todos = activity.getDataStore().getTodos();

            if (deafultTodo != null) {
                // Edited
                todos.set(todos.indexOf(deafultTodo), newTodo);
            } else {
                todos.add(newTodo);
            }


            NavHostFragment.findNavController(this).navigate(AddTodoFragmentDirections.actionAddTodoFragmentToFirstFragment());
        });

        binding.todoDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                binding.textDueDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
                dueDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                System.out.println(dueDate.toString());
            }, year, month, day);
            datePickerDialog.show();
        });


        binding.startTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
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

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
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