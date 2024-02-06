package com.example.college_planner;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStore {
    Gson gson;
    SharedPreferences sharedPreferences;

    public DataStore(Context context) {
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void addClass(Class _class) {
        add("classes", _class);
    }

    public List<Class> getClasses() {
        return get("classes", Class.class);
    }

    public void setClasses(List<Class> classes) {
        set("classes", classes);
    }

    public void addAssignment(Assignment assignment) {
        add("assignments", assignment);
    }

    public List<Assignment> getAssignments() {
        return get("assignments", Assignment.class);
    }

    public void setAssignments(List<Assignment> assignments) {
        set("assignments", assignments);
    }

    public void addExam(Exam exam) {
        add("exams", exam);
    }

    public List<Exam> getExams() {
        return get("exams", Exam.class);
    }

    public void setExams(List<Exam> exams) {
        set("exams", exams);
    }

    public void addTodo(Todo todo) {
        add("todos", todo);
    }

    public List<Todo> getTodos() {
        return get("todos", Todo.class);
    }

    public void setTodos(List<Todo> todos) {
        set("todos", todos);
    }

    private <T> void add(String key, T o) {
        List<T> current = get(key, (java.lang.Class<T>) o.getClass());
        current.add(o);
        set(key, current);
    }

    private <T> void set(String key, List<T> objects) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, gson.toJson(objects.toArray((T[]) new Object[0]))).apply();
    }

    private <T> List<T> get(String key, java.lang.Class<T> classOfT) {
        String serialized = sharedPreferences.getString(key, "[]");
        T[] objects = (T[]) gson.fromJson(serialized, Array.newInstance(classOfT, 0).getClass());
        return new ArrayList<>(Arrays.asList(objects));
    }
}
