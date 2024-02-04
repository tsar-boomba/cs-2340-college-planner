package com.example.college_planner;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.college_planner.databinding.FragmentMainBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private DataStore dataStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        dataStore = new DataStore(requireActivity());
        NavController navController = NavHostFragment.findNavController(MainFragment.this);

        FloatingActionButton fab = (FloatingActionButton) requireActivity().findViewById(R.id.fab);
        ExtendedFloatingActionButton efab = (ExtendedFloatingActionButton) requireActivity().findViewById(R.id.submit_fab);
        TextView actionAddClass = (TextView) requireActivity().findViewById(R.id.action_add_class);
        TextView actionAddAssignment = (TextView) requireActivity().findViewById(R.id.action_add_assignment);
        TextView actionAddExam = (TextView) requireActivity().findViewById(R.id.action_add_exam);
        TextView actionAddTodo = (TextView) requireActivity().findViewById(R.id.action_add_todo);
        TextView menuClose = (TextView) requireActivity().findViewById(R.id.menu_close);

        showFab(fab);
        efab.hide();

        actionAddClass.setOnClickListener((view) -> {
            hideFab(fab);
            fab.setExpanded(false);
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddClassFragment());
        });

        actionAddAssignment.setOnClickListener((view) -> {
            fab.setExpanded(false);
            fab.hide();
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddAssignmentFragment());
        });

        actionAddExam.setOnClickListener((view) -> {
            fab.setExpanded(false);
            fab.hide();
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddExamFragment());
        });

        actionAddTodo.setOnClickListener((view) -> {
            fab.setExpanded(false);
            hideFab(fab);
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddTodoFragment());
        });

        fab.setOnClickListener((view) -> {
            fab.setExpanded(true);
        });

        menuClose.setOnClickListener((view) -> {
            fab.setExpanded(false);
        });

        return binding.getRoot();
    }

    private void hideFab(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(null); //should disable default animations
        p.setAnchorId(View.NO_ID); //should let you set visibility
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE); // View.INVISIBLE might also be worth trying
    }

    private void showFab(FloatingActionButton fab) {
        // to bring things back to normal state
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setBehavior(new FloatingActionButton.Behavior());
        fab.setLayoutParams(p);
        fab.show();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Class> classes = dataStore.getClasses();
        List<Exam> exams = dataStore.getExams();
        List<Assignment> assignments = dataStore.getAssignments();
        List<Todo> todos = dataStore.getTodos();
        ArrayList<Event> events = new ArrayList<>(classes.size() + exams.size() + assignments.size() + todos.size());

        events.addAll(classes);
        events.addAll(exams);
        events.addAll(assignments);
        events.addAll(todos);

        TodayExpandableListAdapter expandableListAdapter = new TodayExpandableListAdapter(requireActivity(), "Today", events);
        binding.todayList.setAdapter(expandableListAdapter);
        binding.todayList.expandGroup(0);

        binding.classList.setAdapter(new ClassListAdapter(classes, dataStore.getAssignments(), dataStore.getExams()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}