package com.alexey.samsung;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by teacher on 20.10.16.
 */
public class Task {
    public int t_type;
    public int w_type;
    public Date date;
    public String [] ests;
    public String name;
    public int id;

    public Task(ResultSet rs){
        try {
            t_type = rs.getInt(DBHelper.KEY_TEST_TYPE);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_TEST_TYPE);
        }
        try {
            w_type = rs.getInt(DBHelper.KEY_WORK_TYPE);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_WORK_TYPE);
        }
        try {
            date = rs.getDate(DBHelper.KEY_DATE);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_DATE);
        }
        try {
            name = rs.getString(DBHelper.KEY_TEST_NAME);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_TEST_NAME);
        }
        try {
            id = rs.getInt(DBHelper.KEY_ID);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_ID);
        }
        ests = new String[5];
        for (int i = 0; i < 5; i++) {
            try {
                ests[i] = rs.getString("est"+(i+1));
            } catch (SQLException e) {
                System.out.println("Нет ключа: est"+(i+1));
            }
        }
    }
    public Task(int id,int t_type, int w_type, Date date, String[] ests, String name) {
        this.id = id;
        this.t_type = t_type;
        this.w_type = w_type;
        this.date = date;
        this.ests = ests;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Task{" +
                "date=" + date +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<String> getTaskNameList(ArrayList<Task> alst){
        List<String>lst = new ArrayList<>();
        for(Task at:alst){
            lst.add(at.name);
        }
        return lst;
    }
}
