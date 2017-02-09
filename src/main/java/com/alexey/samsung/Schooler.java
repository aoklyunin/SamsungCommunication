package com.alexey.samsung;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by teacher on 31.10.16.
 */
public  class Schooler{
    public int group;
    public String name;
    public int id;

    public Schooler(int group, String name, int id) {
        this.group = group;
        this.name = name;
        this.id = id;
    }

    public Schooler(ResultSet rs){
        try {
            this.group = rs.getInt(DBHelper.KEY_GROUP);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_GROUP);
        }
        try {
            this.name = rs.getString(DBHelper.KEY_NAME);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_NAME);
        }
        try {
            this.id = rs.getInt(DBHelper.KEY_ID);
        } catch (SQLException e) {
            System.out.println("Нет ключа: "+DBHelper.KEY_ID);
        }
    }

}