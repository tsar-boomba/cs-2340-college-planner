package com.example.college_planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.List;

public class TodayExpandableListAdapter extends BaseExpandableListAdapter {
    private final DataStore dataStore;
    private final Context context;
    private final String expandableTitleList;
    private final List<Event> expandableDetailList;

    // constructor
    public TodayExpandableListAdapter(Context context, DataStore dataStore, String expandableListTitle, List<Event> expandableListDetail) {
        this.context = context;
        this.dataStore = dataStore;
        this.expandableTitleList = expandableListTitle;
        this.expandableDetailList = expandableListDetail;
    }

    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int lstPosn, int expanded_ListPosition) {
        return this.expandableDetailList.get(expanded_ListPosition);
    }

    @Override
    // Gets the ID for the given child within the given group.
    // This ID must be unique across all children within the group. Hence we can pick the child uniquely
    public long getChildId(int listPosition, int expanded_ListPosition) {
        return expanded_ListPosition;
    }

    @Override
    // Gets a View that displays the data for the given child within the given group.
    public View getChildView(int lstPosn, final int expanded_ListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Event event = (Event) getChild(lstPosn, expanded_ListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView title = (TextView) convertView.findViewById(R.id.today_item_text);
        TextView description = (TextView) convertView.findViewById(R.id.today_item_description);
        TextView time = (TextView) convertView.findViewById(R.id.today_item_time);

        title.setText(event.title());
        description.setText(event.shortDescription());
        time.setText(event.time().orElse(""));

        convertView.findViewById(R.id.today_item_delete).setOnClickListener((_view) -> {
            Activity activity = (Activity) context;
            if (event.getClass() == Class.class) {
                Class _class = (Class) event;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                TextView text = new TextView(activity);
                text.setText("Deleting a class will delete all associated assignments and exams! Are you sure you want to do this?");
                text.setPadding(16, 16, 16, 16);
                builder.setTitle("Delete " + _class.getName() + " ?").setView(text).setPositiveButton("Delete", (dialog, which) -> {
                    List<Class> classes = dataStore.getClasses();
                    classes.remove(_class);
                    List<Exam> exams = dataStore.getExams();
                    List<Assignment> assignments = dataStore.getAssignments();
                    exams.removeIf((exam) -> exam._class.equals(_class));
                    assignments.removeIf((assignment -> assignment._class.equals(_class)));
                    activity.recreate();
                }).setNegativeButton("Keep", (dialog, which) -> {
                }).create().show();
            } else if (event.getClass() == Assignment.class) {
                Assignment assignment = (Assignment) event;
                List<Assignment> assignments = dataStore.getAssignments();
                assignments.remove(assignment);
                activity.recreate();
            } else if (event.getClass() == Exam.class) {
                Exam exam = (Exam) event;
                List<Exam> exams = dataStore.getExams();
                exams.remove(exam);
                activity.recreate();
            } else if (event.getClass() == Todo.class) {
                Todo todo = (Todo) event;
                List<Todo> todos = dataStore.getTodos();
                todos.remove(todo);
                activity.recreate();
            }
        });

        convertView.findViewById(R.id.today_item_edit).setOnClickListener((_view) -> {
            MainActivity activity = (MainActivity) context;
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);;

            if (event.getClass() == Class.class) {
                Class _class = (Class) event;
                MainFragmentDirections.ActionFirstFragmentToAddClassFragment action = MainFragmentDirections.actionFirstFragmentToAddClassFragment();
                action.setIndex(activity.getDataStore().getClasses().indexOf(_class));
                navController.navigate(action);
            } else if (event.getClass() == Assignment.class) {
                Assignment assignment = (Assignment) event;
                MainFragmentDirections.ActionFirstFragmentToAddAssignmentFragment action = MainFragmentDirections.actionFirstFragmentToAddAssignmentFragment();
                action.setIndex(activity.getDataStore().getAssignments().indexOf(assignment));
                navController.navigate(action);
            } else if (event.getClass() == Exam.class) {
                Exam exam = (Exam) event;
                MainFragmentDirections.ActionFirstFragmentToAddExamFragment action = MainFragmentDirections.actionFirstFragmentToAddExamFragment();
                action.setIndex(activity.getDataStore().getExams().indexOf(exam));
                navController.navigate(action);
            } else if (event.getClass() == Todo.class) {
                Todo todo = (Todo) event;
                MainFragmentDirections.ActionFirstFragmentToAddTodoFragment action = MainFragmentDirections.actionFirstFragmentToAddTodoFragment();
                action.setIndex(activity.getDataStore().getTodos().indexOf(todo));
                navController.navigate(action);
            }
        });

        return convertView;
    }

    @Override
    // Gets the number of children in a specified group.
    public int getChildrenCount(int listPosition) {
        return this.expandableDetailList.size();
    }

    @Override
    // Gets the data associated with the given group.
    public Object getGroup(int listPosition) {
        return this.expandableDetailList;
    }

    @Override
    // Gets the number of groups.
    public int getGroupCount() {
        return 1;
    }

    @Override
    // Gets the ID for the group at the given position. This group ID must be unique across groups.
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    // Gets a View that displays the given group.
    // This View is only for the group--the Views for the group's children
    // will be fetched using getChildView()
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO: Scroll only the expanded out list, not the whole thing
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(expandableTitleList);
        return convertView;
    }

    @Override
    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // Whether the child at the specified position is selectable.
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
