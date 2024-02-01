package com.example.college_planner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataStore {
    Gson gson;
    SharedPreferences sharedPreferences;

    public DataStore(Context context) {
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addClass(Class _class) {
        add("classes", _class);
    }

    public ArrayList<Class> getClasses() {
        return get("classes", Class.class);
    }

    public void addAssignment(Assignment assignment) {
        add("assignments", assignment);
    }

    public ArrayList<Assignment> getAssignments() {
        return get("assignments", Assignment.class);
    }

    public void addExam(Exam exam) {
        add("exams", exam);
    }

    public ArrayList<Exam> getExams() {
        return get("exams", Exam.class);
    }

    public void addTodo(Todo todo) {
        add("todos", todo);
    }

    public ArrayList<Todo> getTodos() {
        return get("todos", Todo.class);
    }

    private void add(String key, Object o) {
        HashSet<String> current = new HashSet<>(sharedPreferences.getStringSet(key, new HashSet<>()));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        current.add(gson.toJson(o));
        editor.putStringSet(key, current);
        editor.apply();
    }

    private <T> ArrayList<T> get(String key, java.lang.Class<T> classOfT) {
        Set<String> serialized = sharedPreferences.getStringSet(key, new HashSet<>());
        ArrayList<T> objects = new ArrayList<>(serialized.size());

        for (String serializedObj :
                serialized) {
            objects.add(gson.fromJson(serializedObj, classOfT));
        }

        return objects;
    }
}
