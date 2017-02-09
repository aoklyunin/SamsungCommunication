package com.alexey.samsung.controller;

import com.alexey.samsung.*;
import com.vk.api.sdk.client.VkApiClient;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Created by aokly on 24.09.2016.
 */
public class MainController {

    public DatePicker startDP;
    public DatePicker endDP;
    public CheckBox group1enable;
    public CheckBox group2enable;
    public Button showBtn;
    public TableView attemptTable;
    public AnchorPane ap;
    public SplitPane sp;
    public AnchorPane apsp;
    public AnchorPane apsp2;
    public Button loadAttempts;
    public Button essay;
    public ChoiceBox essay_task;
    public Button all_essay;

    @FXML
    private Button button;
    @FXML
    public Button vkButton;


    double clientWidth = 1360;
    double clientHeight = 710;

    @FXML
    public void initialize() {
        ap.setPrefWidth(clientWidth);
        ap.setPrefHeight(clientHeight);
        sp.setPrefWidth(clientWidth);
        sp.setPrefHeight(clientHeight);
/*
        apsp.setPrefWidth(clientWidth);
        apsp.setPrefHeight(clientHeight*0.8);

        apsp2.setPrefWidth(clientWidth);
        apsp2.setPrefHeight(clientHeight*0.2);
        //attemptTable.setPrefHeight(clientHeight*0.8);
*/

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    AnchorPane root1 = (AnchorPane) FXMLLoader.load(Main.class.getResource("/fxml/taskDialog.fxml"));
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("ABC");
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch (IOException e) {
                    System.out.println(e + " ");
                }
            }
        });


        loadAttempts.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                ArrayList<Task> tasks = new ArrayList<Task>();
                try (DBHelper dbHelper = new DBHelper()) {
                    dbHelper.connect();
                    tasks = dbHelper.getTasks(
                            Date.valueOf(startDP.getValue()),
                            Date.valueOf(endDP.getValue())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    CustomOperations.getAttemps(tasks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        vkButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try (VkApi vk = new VkApi()) {
                    System.out.println(vk.getDialogs());
                } catch (Exception e) {
                    System.out.println("Ошибка подключения к VK");
                }
            }
        });
        showBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                showAttempts();
            }
        });
        startDP.setValue(LocalDate.of(2016, Month.SEPTEMBER, 1));
        endDP.setValue(LocalDate.now());
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();
            ArrayList<Task> tasks = dbHelper.getTasks(Date.valueOf(startDP.getValue()),
                    Date.valueOf(endDP.getValue()));
            ArrayList<String> eTasks = new ArrayList<>();
            for (Task t : tasks) {
                if (t.t_type == 1)
                    eTasks.add(t.name);
            }
            ObservableList<String> data = FXCollections.observableList(eTasks);
            essay_task.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        essay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String s = essay_task.getSelectionModel().getSelectedItem().toString();
                loadAttemtsToFile(s);
            }
        });
        all_essay.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try (DBHelper dbHelper = new DBHelper()) {
                    dbHelper.connect();
                    ArrayList<Task> tasks = dbHelper.getTasks(Date.valueOf(startDP.getValue()),
                            Date.valueOf(endDP.getValue()));
                    ArrayList<String> eTasks = new ArrayList<>();
                    for (Task t : tasks) {
                        if (t.t_type == 1)
                            eTasks.add(t.name);
                    }
                    for (String task : eTasks) {
                        loadAttemtsToFile(task);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadAttemtsToFile(String taskName) {
        try (DBHelper dbHelper = new DBHelper();
             WebSelenium webSelenium = new WebSelenium();
        ) {
            dbHelper.connect();
            int taskId = dbHelper.getIdByTaskName(taskName);
            ArrayList<Attempt> attemtps = dbHelper.getAttemptByTaskId(taskId);

            attemtps.sort(new Comparator<Attempt>() {
                @Override
                public int compare(Attempt o1, Attempt o2) {
                    int n = (o1.student_id) - (o2.student_id);
                    if (n != 0) return n;
                    return o1.addDate.compareTo(o2.addDate);
                }
            });
            webSelenium.loginToMdl();
            try (FileWriter writer = new FileWriter(taskName + ".txt", false)) {
                for (Attempt attempt : attemtps) {
                    writer.write("|" + dbHelper.getScoolerById(attempt.student_id).name + "\n");
                    writer.write(webSelenium.getAllAteemptText(attempt.href) + "\n");
                }
            } catch (Exception e) {

            }
            System.out.println("Загружены эссе теста " + taskName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAttempts() {
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();
            ArrayList<Task> tasks = dbHelper.getTasks(
                    Date.valueOf(startDP.getValue()),
                    Date.valueOf(endDP.getValue())
            );
            int columnCnt = tasks.size();

            int group = 0;
            group += group1enable.isSelected() ? 1 : 0;
            group += group2enable.isSelected() ? 2 : 0;
            // System.out.println(group);
            ArrayList<Schooler> lstNames = dbHelper.getSchoolers(group);

            int rowCnt = lstNames.size();

            ArrayList<Attempt> att = dbHelper.getAttempts(tasks, lstNames);
            System.out.println("Building table");

            buildTable(att, lstNames, tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void buildTable(ArrayList<Attempt> att, ArrayList<Schooler> lstNames, ArrayList<Task> tasks) {
        attemptTable.getItems().clear();
        attemptTable.getColumns().clear();
        // add columns
        HashMap<Integer, Integer> posByTask = new HashMap<>();

        List<String> columnNames = new ArrayList<>();
        int N_COLS = tasks.size();
        int ln = 110;
        columnNames.add("Имя Фамилия");
        for (int i = 0; i < N_COLS; i++) {
            columnNames.add(tasks.get(i).name+"\n"+tasks.get(i).date);
            posByTask.put(tasks.get(i).id, i + 1);
        }

        //attemptTable.setPrefWidth(ln * (N_COLS + 1));
        for (int i = 0; i < columnNames.size(); i++) {
            final int finalIdx = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(
                    columnNames.get(i)
            );
            column.setCellValueFactory(param ->
                    new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx))
            );
            column.setPrefWidth(ln);
            attemptTable.getColumns().add(column);
        }

        // заполняем таблицу
        att.sort(new Comparator<Attempt>() {
            @Override
            public int compare(Attempt o1, Attempt o2) {
                int n = (o1.student_id) - (o2.student_id);
                if (n != 0) return n;
                return (int)(Math.max(o1.sum,o1.evaluation)-Math.max(o2.sum,o2.evaluation));
            }
        });

        // add data
        HashMap<Integer, Integer> posByName = new HashMap<>();
        for (int i = 0; i < lstNames.size(); i++) {
            posByName.put(lstNames.get(i).id, i);
        }


        String data[][] = new String[lstNames.size()][tasks.size() + 1];
        for (int i = 0; i < lstNames.size(); i++) {
            data[i][0] = CustomOperations.reverseName(lstNames.get(i).name);
            for (int j = 1; j <= tasks.size(); j++) {
                data[i][j] = "-";
            }
        }
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();
            for (Attempt at : att) {
                int i = posByName.get(at.student_id);
                int j = posByTask.get(at.task_id);
                String s = dbHelper.getEstimateByVal(at)+"("+ (Math.round(Math.max(at.sum,at.evaluation)))+")";
                //String s = at.sum+" "+ at.evaluation;
                data[i][j] = s;
                // System.out.printf("%d %10s %3.1f\n",j,at.testName, (at.evaluation==0?at.sum:at.evaluation));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String[] ad : data
                ) {
            List<String> assetList = Arrays.asList(ad);
            attemptTable.getItems().add(FXCollections.observableArrayList(assetList));
        }

        attemptTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    @SuppressWarnings("rawtypes")
                    TablePosition pos = (TablePosition) attemptTable.getSelectionModel().getSelectedCells().get(0);
                    int row = pos.getRow();
                    int col = pos.getColumn();
                    @SuppressWarnings("rawtypes")
                    TableColumn column = pos.getTableColumn();
                    String val = column.getCellData(row).toString();
                    System.out.println("Selected Value, " + val + ", Column: " + col + ", Row: " + row);
                    System.out.println(row);
                    int taskId = -1;
                    for (Map.Entry<Integer, Integer> entry : posByTask.entrySet()) {
                        if (entry.getValue() == col)
                            taskId = entry.getKey();
                    }
                    int studentId = -1;
                    for (Map.Entry<Integer, Integer> entry : posByName.entrySet()) {
                        if (entry.getValue() == row)
                            studentId = entry.getKey();
                    }

                    try (DBHelper dbHelper = new DBHelper()) {
                        dbHelper.connect();
                        Attempt attempt = dbHelper.getAttemptByIds(taskId, studentId);
                        Task task = dbHelper.getTaskById(taskId);
                        if (attempt == null) {
                            System.out.println("Такой попыытки нет");
                            return;
                        }
                        WebSelenium webSelenium = new WebSelenium();
                        webSelenium.loginToMdl();
                        webSelenium.loadCurPageWithCloseWaiting(attempt.href);
                    } catch (Exception e) {
                        System.out.println("fuck " + e);
                    }
                }
            }
        });


        attemptTable.setPrefWidth(clientWidth);
        attemptTable.setPrefHeight(apsp.getHeight());
        System.out.println("table builded");
    }

}


