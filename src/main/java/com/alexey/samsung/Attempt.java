package com.alexey.samsung;

import org.joda.time.DateTime;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by teacher on 20.10.16.
 */
public class Attempt{
    public String state;
    public DateTime starts;
    public DateTime ends;
    public String tm;
    public double evaluation;
    public String href;
    public double sum;
    public DateTime addDate;
    public int student_id;
    public int task_id;

    public Attempt(int task_id, String state, DateTime starts, DateTime ends,
                   String tm, String evaluation, String href, double sum,
                   int student_id, DateTime addDate) {
        this.task_id = task_id;
        this.state = state;
        this.starts = starts;
        this.ends = ends;
        this.tm = tm;
        Pattern p = Pattern.compile("[а-я]");
        Matcher m = p.matcher(evaluation);
        if (!m.find())
            this.evaluation = evaluation.length()<=1?0:Double.parseDouble(evaluation.replace(",","."));
        else
            this.evaluation = 0;
        this.href = href;
        this.sum = sum;
        this.student_id = student_id;
        this.addDate = addDate;
    }


    public Attempt(ResultSet rs) throws SQLException {
        this.task_id = rs.getInt(DBHelper.KEY_TASK_ID);
        this.state = rs.getString(DBHelper.KEY_STATE);
        Timestamp timestamp = rs.getTimestamp(DBHelper.KEY_STARTS);
        if (timestamp != null)
            this.starts = new DateTime(timestamp.getTime());
        else
            this.starts = new DateTime();
        timestamp = rs.getTimestamp(DBHelper.KEY_ENDS);
        if (timestamp != null)
            this.ends = new DateTime(timestamp.getTime());
        else
            this.ends = new DateTime();
        this.tm = rs.getString(DBHelper.KEY_TM);
        this.evaluation = rs.getFloat(DBHelper.KEY_EVAULATION);
        this.href = rs.getString(DBHelper.KEY_HREF);
        this.sum = rs.getFloat(DBHelper.KEY_SUM);
        this.student_id = rs.getInt(DBHelper.KEY_STUDENT_ID);
        timestamp = rs.getTimestamp(DBHelper.KEY_ADD_TIME);
        if (timestamp != null)
            this.addDate = new DateTime(timestamp.getTime());
        else
            this.addDate = new DateTime();
    }



    @Override
    public String toString() {
        return  "p: " +  evaluation + " |" +
                "sum: " + sum + "|" +
                ", href='" + href + '\'';
    }

    public int compareTo(Attempt a) {
        if (this.evaluation>0)
            return Double.compare(this.evaluation,a.evaluation);
        else if (a.evaluation>0) return -1;
        else return Double.compare(this.sum,a.sum);
    }

}