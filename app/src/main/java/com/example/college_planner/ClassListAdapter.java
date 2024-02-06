package com.example.college_planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.college_planner.databinding.ClassDialogBinding;
import com.example.college_planner.databinding.ClassDialogItemBinding;
import com.example.college_planner.databinding.ClassListViewBinding;

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
        private final Activity activity;
        private final List<Class> classes;
        private final List<Assignment> assignments;
        private final List<Exam> exams;
        private final DataStore dataStore;

        public ViewHolder(DataStore dataStore, ClassListViewBinding binding, List<Class> classes, List<Assignment> assignments, List<Exam> exams) {
            super(binding.getRoot());
            this.dataStore = dataStore;
            this.binding = binding;
            this.activity = (Activity) binding.getRoot().getContext();
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

                // Go through exams for this class
                for (Exam exam : exams.stream().filter((exam -> exam._class.equals(_class))).collect(Collectors.toList())) {
                    ClassDialogItemBinding classDialogItemBinding = ClassDialogItemBinding.bind(classDialogBinding.getRoot());
                    classDialogItemBinding.dialogItemText.setText(exam.title());
                    classDialogItemBinding.dialogItemTime.setText(exam.time().orElse(""));

                    classDialogItemBinding.dialogClassDelete.setOnClickListener((_view) -> {
                        exams.remove(exam);
                        dataStore.setExams(exams);
                        activity.recreate();
                    });

                    classDialogItemBinding.dialogClassEdit.setOnClickListener((_view) -> {
                        // TODO: edit exam
                    });

                    classDialogBinding.getRoot().addView(classDialogItemBinding.getRoot());
                }

                // Go through assignments for this class
                for (Assignment assignment : assignments.stream().filter((assignment -> assignment._class.equals(_class))).collect(Collectors.toList())) {
                    ClassDialogItemBinding classDialogItemBinding = ClassDialogItemBinding.bind(classDialogBinding.getRoot());
                    classDialogItemBinding.dialogItemText.setText(assignment.title());
                    classDialogItemBinding.dialogItemTime.setText(assignment.time().orElse(""));

                    classDialogItemBinding.dialogClassDelete.setOnClickListener((_view) -> {
                        assignments.remove(assignment);
                        dataStore.setAssignments(assignments);
                        activity.recreate();
                    });

                    classDialogItemBinding.dialogClassEdit.setOnClickListener((_view) -> {
                        // TODO: edit assignment
                    });

                    classDialogBinding.getRoot().addView(classDialogItemBinding.getRoot());
                }

                AlertDialog dialog = builder.setTitle(_class.getName() + " Assignments").setView(classDialogBinding.getRoot()).setNeutralButton("Delete", (dialog1, which) -> {
                    classes.remove(_class);
                    exams.removeIf((exam) -> exam._class.equals(_class));
                    assignments.removeIf((assignment) -> assignment._class.equals(_class));
                    dataStore.setClasses(classes);
                    dataStore.setExams(exams);
                    dataStore.setAssignments(assignments);
                    activity.recreate();
                }).setNegativeButton("Edit", (dialog1, which) -> {
                    // TODO: Edit class
                }).setPositiveButton("Close", (dialog1, which) -> {
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
