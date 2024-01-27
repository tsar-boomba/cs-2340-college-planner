package com.example.college_planner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.college_planner.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        NavController navController = NavHostFragment.findNavController(MainFragment.this);

        binding.fab.setExpandedComponentIdHint(R.id.sheet);

        binding.actionAddClass.setOnClickListener((view) -> {
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddClassFragment());
        });

        binding.actionAddAssignment.setOnClickListener((view) -> {
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddAssignmentFragment());
        });

        binding.actionAddExam.setOnClickListener((view) -> {
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddExamFragment());
        });

        binding.actionAddTodo.setOnClickListener((view) -> {
            navController.navigate(MainFragmentDirections.actionFirstFragmentToAddTodoFragment());
        });


        binding.fab.setOnClickListener((view) -> {
            binding.sheet.setVisibility(View.VISIBLE);
        });

        binding.menuClose.setOnClickListener((view) -> {
            binding.sheet.setVisibility(View.INVISIBLE);
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}