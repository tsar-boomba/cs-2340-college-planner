package com.example.college_planner;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.college_planner.databinding.ClassDialogBinding;
import com.example.college_planner.databinding.ClassDialogItemBinding;
import com.example.college_planner.databinding.ClassListViewBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder> {
    private final List<Class> data;
    private final List<Assignment> assignments;
    private final List<Exam> exams;
    private final DataStore dataStore;

    public ClassListAdapter(DataStore dataStore, List<Class> data, List<Assignment> assignments, List<Exam> exams) {
        this.dataStore = dataStore;
        this.data = data;
        this.assignments = assignments;
        this.exams = exams;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ClassListViewBinding binding = ClassListViewBinding.inflate(inflater, viewGroup, false);

        return new ViewHolder(dataStore, binding, data, assignments, exams);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.populate(data.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ClassListViewBinding binding;
        private final MainActivity activity;
        private final List<Class> classes;
        private final List<Assignment> assignments;
        private final List<Exam> exams;
        private final DataStore dataStore;
        private AlertDialog dialog;

        public ViewHolder(DataStore dataStore, ClassListViewBinding binding, List<Class> classes, List<Assignment> assignments, List<Exam> exams) {
            super(binding.getRoot());
            this.dataStore = dataStore;
            this.binding = binding;
            this.activity = (MainActivity) binding.getRoot().getContext();
            this.classes = classes;
            this.assignments = assignments;
            this.exams = exams;
        }

        public void populate(Class _class) {
            binding.className.setText(_class.getName());
            binding.classLecturer.setText(_class.getTeacher());

            binding.classInfo.setOnClickListener((view) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                ClassDialogBinding classDialogBinding = ClassDialogBinding.inflate(inflater);
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);

                classDialogBinding.classDialogSortSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("None", "Soonest", "Latest")));

                List<Assignment> filteredAssignments = new ArrayList<>(exams.size() + assignments.size());
                filteredAssignments.addAll(exams);
                filteredAssignments.addAll(assignments);
                filteredAssignments.removeIf((assign) -> !assign._class.equals(_class));

                classDialogBinding.classDialogSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Remove all but spinner
                        classDialogBinding.getRoot().removeViews(1, classDialogBinding.getRoot().getChildCount() - 1);
                        System.out.println("Selected Sort");
                        filteredAssignments.sort((a, b) -> {
                            String selectedSort = (String) classDialogBinding.classDialogSortSpinner.getSelectedItem();
                            if (selectedSort.isEmpty() || selectedSort.equals("None")) {
                                // Do nothing
                                System.out.println("None sort");
                                return 0;
                            } else if (selectedSort.equals("Soonest")) {
                                System.out.println("Soonest sort");
                                return a.getDueDate().compareTo(b.getDueDate());
                            } else {
                                System.out.println("Latest sort");
                                return b.getDueDate().compareTo(a.getDueDate());
                            }
                        });
                        System.out.println("Sorted assignments " + filteredAssignments);

                        // Go through assignments for this class
                        for (Assignment assignment : filteredAssignments) {
                            ClassDialogItemBinding classDialogItemBinding = ClassDialogItemBinding.inflate(inflater, classDialogBinding.getRoot(), false);
                            classDialogItemBinding.dialogItemText.setText(assignment.title());
                            classDialogItemBinding.dialogItemTime.setText(assignment.time().orElse(""));

                            classDialogItemBinding.dialogClassDelete.setOnClickListener((_view) -> {
                                if (assignment.getClass() == Exam.class) {
                                    Exam exam = (Exam) assignment;
                                    exams.remove(exam);
                                    dataStore.setExams(exams);
                                } else {
                                    assignments.remove(assignment);
                                    dataStore.setAssignments(assignments);
                                }
                                activity.recreate();
                            });

                            classDialogItemBinding.dialogClassEdit.setOnClickListener((_view) -> {
                                if (assignment.getClass() == Exam.class) {
                                    Exam exam = (Exam) assignment;
                                    MainFragmentDirections.ActionFirstFragmentToAddExamFragment action = MainFragmentDirections.actionFirstFragmentToAddExamFragment();
                                    action.setIndex(exams.indexOf(exam));
                                    dialog.dismiss();
                                    navController.navigate(action);
                                } else {
                                    MainFragmentDirections.ActionFirstFragmentToAddAssignmentFragment action = MainFragmentDirections.actionFirstFragmentToAddAssignmentFragment();
                                    action.setIndex(activity.getDataStore().getAssignments().indexOf(assignment));
                                    dialog.dismiss();
                                    navController.navigate(action);
                                }
                            });

                            classDialogBinding.getRoot().addView(classDialogItemBinding.getRoot());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                classDialogBinding.classDialogSortSpinner.setSelection(0);
                dialog = builder.setTitle(_class.getName() + " Assignments").setView(classDialogBinding.getRoot()).setNeutralButton("Delete", (dialog1, which) -> {
                    classes.remove(_class);
                    exams.removeIf((exam) -> exam._class.equals(_class));
                    assignments.removeIf((assignment) -> assignment._class.equals(_class));
                    dataStore.setClasses(classes);
                    dataStore.setExams(exams);
                    dataStore.setAssignments(assignments);
                    activity.recreate();
                }).setNegativeButton("Edit", (dialog1, which) -> {
                    MainFragmentDirections.ActionFirstFragmentToAddClassFragment action = MainFragmentDirections.actionFirstFragmentToAddClassFragment();
                    action.setIndex(activity.getDataStore().getClasses().indexOf(_class));
                    dialog1.dismiss();
                    navController.navigate(action);
                }).setPositiveButton("Close", (dialog1, which) -> {
                }).setOnDismissListener((dialog1) -> {
                    dialog = null;
                }).create();
                dialog.show();
            });

            GradientDrawable border = (GradientDrawable) ResourcesCompat.getDrawable(binding.getRoot().getContext().getResources(), R.drawable.class_border, null);
            border.mutate();
            border.setStroke(8, _class.color().toArgb());
            binding.getRoot().setBackground(border);
        }
    }
}
