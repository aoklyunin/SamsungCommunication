package com.alexey.samsung;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBHelper implements AutoCloseable {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "students";
    static final String DB_URL = "jdbc:mysql://localhost/?useUnicode=true&characterEncoding=utf8";

    private static final String KEY_VAL = "value";
    private static final String KEY_CKEY = "ckey";

    public static final String TABLE_SCOOLERS = "schoolers";

    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_MAIL = "mail";
    static final String KEY_VK = "vk";
    static final String KEY_GITHUB = "github";
    static final String KEY_M_LOGIN = "mlogin";
    static final String KEY_M_PASSWORD = "mpassword";
    static final String KEY_TEL = "tel";
    static final String KEY_ADDITIONAL = "additional";
    static final String KEY_PROJECT = "project";
    static final String KEY_GROUP = "groupNumber";
    static final String KEY_ATTEMPT_TYPE = "a_type";
    static final String KEY_STATE = "state";
    static final String KEY_STARTS = "starts";
    static final String KEY_ENDS = "ends";
    static final String KEY_TM = "tm";
    static final String KEY_EVAULATION = "evaulation";
    static final String KEY_HREF = "href";
    static final String KEY_SUM = "sum";
    static final String KEY_ADD_TIME = "addTime";

    static final String KEY_STUDENT_ID = "student_id";
    static final String KEY_TASK_ID = "task_id";
    // таблица заданий
    public static final String taskTable = "tasks";
    static final String KEY_TEST_NAME = "test_name";
    static final String KEY_TEST_TYPE = "t_type";
    static final String KEY_WORK_TYPE = "w_type";
    static final String KEY_DATE = "date";
    static final String KEY_EST_1 = "est1";
    static final String KEY_EST_2 = "est2";
    static final String KEY_EST_3 = "est3";
    static final String KEY_EST_4 = "est4";
    static final String KEY_EST_5 = "est5";

    // таблица учеников информатики
    public static final String TABLE_INFORMATIC = "informatic";



    static final String USER = "root";
    static final String PASS = "toor";

    static final String confTable = "CONF_TABLE";
    static final String attemptTable = "attempts";

    Connection conn = null;
    Statement stmt = null;

    private void query(String sql) throws SQLException {
        //System.out.println(sql);
        stmt.executeUpdate(sql);
    }

    public void createDB(String dbName) throws SQLException {
        query("CREATE DATABASE " + dbName);
    }

    public ArrayList<ArrayList<String>> getValsRegister() throws SQLException {
        String query = "SELECT * FROM " + TABLE_SCOOLERS;
        ResultSet rs = stmt.executeQuery(query);
        //String login,String password, String name, String mail
        ArrayList<ArrayList<String>> lst = new ArrayList<>();
        while (rs.next()) {
            ArrayList<String> ls = new ArrayList<>();
            lst.add(ls);
            ls.add(rs.getString(KEY_M_LOGIN));
            ls.add(rs.getString(KEY_M_PASSWORD));
            ls.add(rs.getString(KEY_NAME));
            ls.add(rs.getString(KEY_MAIL));
        }
        return lst;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        // загружаем класс
        Class.forName(JDBC_DRIVER);
        // подклюаемся к базе
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Подключились к базе");
        stmt = conn.createStatement();
        query("SET NAMES 'utf8';");
        query("SET CHARACTER SET 'utf8';");
        query("SET SESSION collation_connection = 'utf8_general_ci';");

        query("USE " + DB_NAME + ";");
    }

    public void createTable(String tableName, Map<String, String> m) throws SQLException {
        String q = "CREATE TABLE " + tableName +
                "(id INTEGER not NULL, ";
        for (Map.Entry<String, String> e : m.entrySet()) {
            q += e.getKey() + " " + e.getValue() + ", ";
        }
        q += " PRIMARY KEY ( id ))";
        query(q);
        System.out.println("Создали таблицу");
    }

    // генерация из массива строк sql записей
    private String getQueryValues(String[][] values) {
        String q = " VALUES ";
        for (String[] sArr : values) {
            q += "(";
            for (String key : sArr
                    ) {
                q += key + ", ";
            }
            q = q.substring(0, q.length() - 2);
            q += "), ";
        }
        q = q.substring(0, q.length() - 2);
        q += ";";
        return q;
    }

    // генерация из массива строк sql записей
    private String getQueryValues(String[] values) {
        String q = " VALUES ";
        q += "(";
        for (String key : values
                ) {
            q += key + ", ";
        }
        q = q.substring(0, q.length() - 2);
        q += "), ";
        q = q.substring(0, q.length() - 2);
        q += ";";
        return q;
    }

    // добавить одну запись с явным указанием ключей через словарь
    public void addRecord(String tableName, Map<String, String> m) throws SQLException {
        String[][] sArr = new String[1][m.size()];
        String[] keys = new String[m.size()];
        int i = 0;
        for (Map.Entry<String, String> e : m.entrySet()) {
            sArr[0][i] = e.getValue();
            keys[i] = e.getKey();
        }
        addRecords(tableName, keys, sArr);
        System.out.println("Значения добавлены");
    }

    // добавить одну запись с явным указанием ключей через массивы
    private void addRecord(String tableName, String[] keys, String[] values) throws SQLException {
        String[][] sArr = new String[1][];
        sArr[0] = values;
        addRecords(tableName, keys, sArr);
    }

    public int getIdByStudentName(String name) throws SQLException, UnsupportedEncodingException {
        //name = new String(name.getBytes("Cp1251"), "UTF-8");
        String query = "SELECT * FROM " + TABLE_INFORMATIC +" WHERE NAME="+ toSQLString(name);
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);

        int id = -1;
        while (rs.next()) {
            id =  rs.getInt(KEY_ID);
        }
        return id;
    }
    public int getIdByTaskName(String name) throws SQLException, UnsupportedEncodingException {
        //name = new String(name.getBytes("Cp1251"), "UTF-8");
        String query = "SELECT * FROM " + taskTable +" WHERE "+KEY_TEST_NAME+"="+ toSQLString(name);
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        int id = -1;
        while (rs.next()) {
            id =  rs.getInt(KEY_ID);
        }
        return id;
    }

    public int getEstimateByVal(Attempt at) throws SQLException {
        if (Math.max(at.sum,at.evaluation)<0.1) return 1;

        Task task = getTaskById(at.task_id);
        int est = 0;
        int i = 0;
        int max = 0;
        for (String e:task.ests){
            i++;
            String [] es = e.split(",");
            for(String s:es){
                try{
                    int n = Integer.parseInt(s);
                    max = Math.max(n,max);
                    if (Math.round(Math.max(at.sum,at.evaluation))==n) return i;
                }catch (Exception ex){

                }
            }

        }
        if (Math.max(at.sum,at.evaluation)>max) return 5;
        return -1;
    }

    // добавить одну запись с явным указанием ключей через массивы
    public void addAttemptRecord(String tableName, String[] values, String condition) throws SQLException {
        String q = "INSERT INTO " + tableName + " SELECT * FROM (SELECT ";
        q+= values[0]+", ";
        q+= values[1]+" AS us_id ,";
        q+= values[2]+" AS task_id ,";
        q+= values[3]+" ,";
        q += values[4] + " AS starttime" + ", ";
        q += values[5] + " AS endtime" + ", ";
        q += values[6] + ", ";
        q += values[7] + " AS evaluation" + ", ";
        q += values[8] + ", ";
        q += values[9] + " AS sum" + ", ";
        q += values[10];
        q += ") AS tmp WHERE NOT EXISTS ( SELECT id FROM " + tableName + " WHERE " + condition + ") LIMIT 1";
        System.out.println(q);
        query(q);
    }


    // добавить записи с явным указанием ключей
    public void addRecords(String tableName, String[] keys, String[][] values) throws SQLException {
        String q = "INSERT INTO " + tableName + " (";
        for (String s : keys) {
            q += s + ", ";
        }
        q = q.substring(0, q.length() - 2);
        q += ")" + getQueryValues(values);
        System.out.println(q);
        query(q);
    }

    public Task getTaskById(int id) throws SQLException {
            //name = new String(name.getBytes("Cp1251"), "UTF-8");
            String query = "SELECT * FROM " + taskTable +" WHERE "+KEY_ID+"="+ id;
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next())
                return new Task(rs);
            else
                return null;
    }
    public Schooler getScoolerById(int id) throws SQLException {
        //name = new String(name.getBytes("Cp1251"), "UTF-8");
        String query = "SELECT * FROM " + TABLE_INFORMATIC +" WHERE "+KEY_ID+"="+ id;
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next())
            return new Schooler(rs);
        else
            return null;
    }
    public Attempt getAttemptByIds(int taskId, int schoolerId) throws SQLException {
        //name = new String(name.getBytes("Cp1251"), "UTF-8");
        String query = "SELECT * FROM " + attemptTable +" WHERE "+KEY_TASK_ID+"="+ taskId+" AND "+KEY_STUDENT_ID+"="+schoolerId;
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next())
            return new Attempt(rs);
        else
            return null;

    }
    public ArrayList<Attempt> getAttemptByTaskId(int taskId) throws SQLException {
        //name = new String(name.getBytes("Cp1251"), "UTF-8");
        String query = "SELECT * FROM " + attemptTable +" WHERE "+KEY_TASK_ID+"="+ taskId;
        System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        ArrayList<Attempt> attempts = new ArrayList<>();
        while(rs.next())
            attempts.add(new Attempt(rs));
        return attempts;
    }

    // добавить записи без указания ключей
    public void addRecords(String tableName, String[][] values) throws SQLException {
        String q = "INSERT INTO " + tableName + " " + getQueryValues(values);
        System.out.println(q);
        query(q);
    }

    // добавить записи без указания ключей
    public void addRecords(String tableName, String[][] values, String condition) throws SQLException {
        String q = "INSERT INTO " + tableName + " " + getQueryValues(values);
        System.out.println(q);
        query(q);
    }

    // добавить запись без указания ключей
    public void addRecord(String tableName, String[] values) throws SQLException {
        String[][] sArr = new String[1][];
        sArr[0] = values;
        addRecords(tableName, sArr);
    }

    // получаем все записи из таблицы конфигураций
    public void getAllConf() throws SQLException {
        String query = "SELECT * FROM " + confTable;
        ResultSet rs = stmt.executeQuery(query);
        boolean flgNotFound = true;
        while (rs.next()) {
            flgNotFound = false;
            //String coffeeName = rs.getString("COF_NAME");
            System.out.println(rs.getString(KEY_VAL));
        }
        if (flgNotFound) System.out.println("Не найдено ни одной записи");
    }

    // получить запись по условию
    private ResultSet getRecord(String condition, String tableName) throws SQLException {
        String q = "SELECT * FROM " + tableName + " WHERE " + condition;
        //System.out.println(q);
        ResultSet rs = stmt.executeQuery(q);
        if (rs.next()) {
            return rs;
        } else {
            return null;
        }
    }


    public ArrayList<HashMap<String, String>> getStudentRecs() throws SQLException {
        String q = "SELECT * FROM " + TABLE_SCOOLERS;
        //System.out.println(q);
        ResultSet rs = stmt.executeQuery(q);
        ArrayList<HashMap<String, String>> lst = new ArrayList<>();
        while (rs.next()) {
            HashMap<String, String> hm = new HashMap<>();
            lst.add(hm);
            hm.put(KEY_GITHUB, rs.getString(KEY_GITHUB));
            hm.put(KEY_M_LOGIN, rs.getString(KEY_M_LOGIN));
            hm.put(KEY_M_PASSWORD, rs.getString(KEY_M_PASSWORD));
            hm.put(KEY_NAME, rs.getString(KEY_NAME));
            hm.put(KEY_VK, rs.getString(KEY_VK));
            hm.put(KEY_TEL, rs.getString(KEY_TEL));
        }
        return lst;
    }

    public boolean chechStudentRecordByMail(String mail) throws SQLException {
        return (getRecord("mail=" + toSQLString(mail), TABLE_SCOOLERS) != null);
    }

    public boolean chechStudentRecordByVK(String mail) throws SQLException {
        return (getRecord("vk=" + toSQLString(mail), TABLE_SCOOLERS) != null);
    }

    public void checkMails() throws SQLException {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/source/mails.txt")))) {
            String commandstring;
            while ((commandstring = bufferedReader.readLine()) != null) {
                if (chechStudentRecordByMail(commandstring)) {
                    System.out.println(commandstring);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // просматриваем список всех адресов почты, и на те, которых в базе нет
    // отправляется предупредительное письмо
    public void parceMailList() throws Exception {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/source/mails.txt")))) {
            String commandstring;
            ArrayList<ArrayList<String>> sList = new ArrayList<>();
            GMailSender sender = new GMailSender("aoklyunin@gmail.com", "aoklyunin1990");
            String subject = "Обучение в IT школе Samsung";
            String body = "Уважаемый Учащийся,\n\n" +
                    "Вы до сих пор не заполнили гугл-форму. Если до конца недели Вы" +
                    " не заполните её, я буду вынужден отчислить Вас, т.к. из-за незаполненной формы Вы " +
                    "не можете выполнять задания.\n\n" +
                    "С уважением, \n Алексей Клюнин";
            while ((commandstring = bufferedReader.readLine()) != null) {
                try {
                    if (!chechStudentRecordByMail(commandstring.replace(" ", ""))) {

                        sender.sendMail(subject,
                                body,
                                "aoklyunin@gmail.com",
                                commandstring);
                        System.out.println(commandstring);
                    }
                } catch (SQLException e) {
                    System.out.println("SQL Ошибка " + e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fuck");
        }
        System.out.println("eagasg");
        // Questions.generateVariants();
    }

    public void parceCsv() throws SQLException {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/source/form.csv")))) {
            String commandstring;
            ArrayList<ArrayList<String>> sList = new ArrayList<>();
            int cnt = 0;
            while ((commandstring = bufferedReader.readLine()) != null) {
                String ls_regex = "\".*?\"";
                Pattern pattern = Pattern.compile(ls_regex);
                Matcher matcher = pattern.matcher(commandstring);
                ArrayList<String> sl = new ArrayList<>();

                int i = 0;

                boolean flgAdd = false;
                while (matcher.find()) {
                    String tmp = matcher.group();
                    //System.out.println(tmp);
                    sl.add(tmp);
                    if (i == 5) {
                        //System.out.println(tmp);
                        try {
                            if (!chechStudentRecordByMail(tmp.replace("\"", "").replace(" ", ""))) {
                                System.out.println("Не найдено");
                                flgAdd = true;
                            }
                        } catch (SQLException e) {
                            System.out.println("SQL Ошибка " + e);
                        }
                    }
                    i++;

                }
                cnt++;
                if (flgAdd) {
                    sList.add(sl);
                }
            }
            //System.out.println(cnt);
            if (sList.size() > 0) {
                int size1 = sList.size();
                int size2 = sList.get(0).size() - 1;
                String sArr[][] = new String[size1][size2];
                for (int i = 0; i < size1; i++) {
                    sArr[i] = new String[size2];
                    for (int j = 0; j < size2; j++) {
                        sArr[i][j] = sList.get(i).get(j + 1).replace(" ", "");
                    }
                }

                String kArr[] = {
                        KEY_GITHUB, KEY_M_LOGIN, KEY_M_PASSWORD, KEY_NAME, KEY_MAIL, KEY_VK, KEY_TEL
                };
                addRecords(TABLE_SCOOLERS, kArr, sArr);
                for (int i = 0; i < size1; i++) {
                    for (int j = 0; j < size2; j++) {
                        System.out.print(sArr[i][j] + " ");
                    }
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fuck");
        }
        System.out.println("eagasg");
        // Questions.generateVariants();
    }

    class InformaticStudent {
        int group;
        String name;

        InformaticStudent(String s) {
            String[] sArr = s.split(" ");
            group = Integer.parseInt(sArr[0]);
            name = sArr[1] + " " + sArr[2];
        }

    }

    public static String toSQLDTString(DateTime dt) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-d HH:mm:ss");
        return "\'" + fmt.print(dt) + "\'";
    }

    public static String toSQLDString(Date dt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return "\'" + dateFormat.format(dt) + "\'";
    }


    public Attempt getAFromResultSet(ResultSet rs) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("y-MM-dd HH:mm:ss");
        String dateTimeStr = rs.getString(KEY_ADD_TIME);
        DateTime dt = formatter.parseDateTime(dateTimeStr.contains(".")?
                dateTimeStr.substring(0, dateTimeStr.indexOf(".")-1):dateTimeStr);
        return  new Attempt(rs);
    }


    public ArrayList<Task> getTasks(Date start, Date end) throws SQLException {
        ArrayList<Task> aLst = new ArrayList<>();
        String query = "SELECT * FROM " + taskTable + " WHERE " +
                "date >= " + toSQLDString(start) + " AND date <= " + toSQLDString(end);
        ResultSet rs = stmt.executeQuery(query);
        //String login,String password, String name, String mail
        while (rs.next()) {
            aLst.add(new Task(rs));
        }
        return aLst;
    }

    public ArrayList<Schooler> getSchoolers(int group) throws SQLException {
        ArrayList<Schooler> aLst = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_INFORMATIC +" WHERE ";
        String condition  = "";
        switch (group){
            case 0:
                condition = "FALSE";
                break;
            case 1:
            case 2:
                condition = KEY_GROUP+"="+group;
                break;
            case 3:
                condition = KEY_GROUP+"=1 OR "+KEY_GROUP+"=2";
                break;
        }
        query += condition;
        ResultSet rs = stmt.executeQuery(query);
        //String login,String password, String name, String mail
        while (rs.next()) {
            aLst.add(new Schooler(group,
                    CustomOperations.reverseName(rs.getString(KEY_NAME)),
                    rs.getInt(KEY_ID)));
        }
        return aLst;
    }



    public ArrayList<Attempt> getAttempts(ArrayList<Task> lst, ArrayList<Schooler> scoolerList) throws SQLException {
        ArrayList<Attempt> aLst = new ArrayList<>();
        if (scoolerList.size()==0)return  aLst;
        String query = "SELECT * FROM " + attemptTable +" WHERE (";
        for (Task task:lst){
            query += KEY_TASK_ID+ "="+task.id+" OR ";
        }
        query = query.substring(0,query.length()-4)+")AND(";
        for (Schooler sc:scoolerList){
            query += KEY_STUDENT_ID + "="+sc.id+" OR ";
        }
        query = query.substring(0,query.length()-4)+")";
        //System.out.println(query.length());
        //System.out.println(query);
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            aLst.add(getAFromResultSet(rs));
        }
        return aLst;
    }

    public void loadInfromaticFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/source/informatic.txt")))) {
            String commandstring;
            ArrayList<InformaticStudent> iLst = new ArrayList<>();
            while ((commandstring = bufferedReader.readLine()) != null) {
                iLst.add(new InformaticStudent(commandstring));
            }
            String[][] sArr = new String[iLst.size()][3];
            for (int i = 0; i < iLst.size(); i++) {
                sArr[i][0] = "NULL";
                sArr[i][1] = toSQLString(iLst.get(i).name);
                sArr[i][2] = iLst.get(i).group + "";
            }
            String kArr[] = {
                    KEY_ID, KEY_NAME, KEY_GROUP
            };

            addRecords(TABLE_INFORMATIC, kArr, sArr);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Fuck");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getInformaticSchooler() throws SQLException {
        String query = "SELECT * FROM " + TABLE_INFORMATIC;
        ResultSet rs = stmt.executeQuery(query);
        //String login,String password, String name, String mail
        ArrayList<String[]> lst = new ArrayList<>();
        while (rs.next()) {
            String[] sArr = new String[2];
            lst.add(sArr);
            sArr[0] = rs.getString(KEY_NAME);
            sArr[1] = rs.getInt(KEY_ID) + "";
        }
        return lst;
    }

    // записать значение в базу
    public void setConfVal(String key, String val) throws SQLException {
        if (getConfVal(key) == null) {
            String[] keys = {KEY_CKEY, KEY_VAL};
            String[] sArr = {toSQLString(key), toSQLString(val)};
            addRecord(confTable, keys, sArr);
        } else {
            System.out.println("Запись уже есть");
            updateRecord(confTable, KEY_CKEY + "=" + toSQLString(key), KEY_VAL + "=" + toSQLString(val));
        }
    }

    // обновить запись в таблице по условию
    void updateRecord(String table, String condition, String operation) throws SQLException {
        String q = "UPDATE " + table + " SET " + operation + " WHERE " + condition;
        query(q);
    }

    // получить значение из конфигурационной таблицы
    String getConfVal(String Key) throws SQLException {
        ResultSet rs = getRecord("ckey=" + toSQLString(Key), confTable);
        if (rs != null)    //String coffeeName = rs.getString("COF_NAME");
            return rs.getString(KEY_VAL);
        else
            return null;
    }

    // переаод в  SQL строку
    public static String toSQLString(String s) {
        return "\'" + s + "\'";
    }

    @Override
    public void close() throws Exception {
        //finally block used to close resources
        try {
            if (stmt != null)
                conn.close();
        } catch (SQLException ignored) {
        }// do nothing
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException ignored) {
        }//end finally try
    }
}
