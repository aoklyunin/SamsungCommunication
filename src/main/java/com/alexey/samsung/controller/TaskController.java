package com.alexey.samsung.controller;

import com.alexey.samsung.DBHelper;
import com.alexey.samsung.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by teacher on 20.10.16.
 */
public class TaskController {
    @FXML
    private Button defaultBtn;
    public Button addBtn;
    public Button addCloseBtn;
    public Button closeBtn;
    public DatePicker datePicker;
    public ChoiceBox classHome;
    public ChoiceBox testType;
    public TextField testName;
    public TextField ev1;
    public TextField ev2;
    public TextField ev3;
    public TextField ev4;
    public TextField ev5;

    private String[] evLstCode = {
            "0,1,2",
            "3,4,5",
            "6,7,8",
            "10,9",
            "11"
    };

    private String[] evLstText = {
            "0",
            "0",
            "1",
            "2",
            "3"
    };
    boolean flgText = false;

    @FXML
    public void initialize() {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
        datePicker.setValue(LocalDate.now());

        initFields(false);
        defaultBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setDefaultEvs(flgText);
            }
        });
        closeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Stage stage = (Stage) closeBtn.getScene().getWindow();
                stage.close();
            }
        });

        addCloseBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addValsToBace();
                Stage stage = (Stage) closeBtn.getScene().getWindow();
                stage.close();
            }
        });
        addBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                addValsToBace();
                initFields(false);
            }
        });

        testType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                flgText = (Integer)number!=1;
                setDefaultEvs(flgText);
            }
        });

    }

    private void addValsToBace() {
        try (DBHelper dbHelper = new DBHelper()) {
            dbHelper.connect();
            String sArr[] = {
                    "NULL",
                    DBHelper.toSQLString(testName.getText()),
                    testType.getSelectionModel().getSelectedIndex() + "",
                    classHome.getSelectionModel().getSelectedIndex() + "",
                    DBHelper.toSQLDString(java.sql.Date.valueOf( datePicker.getValue())),
                    DBHelper.toSQLString(ev1.getText()),
                    DBHelper.toSQLString(ev2.getText()),
                    DBHelper.toSQLString(ev3.getText()),
                    DBHelper.toSQLString(ev4.getText()),
                    DBHelper.toSQLString(ev5.getText())
            };
            dbHelper.addRecord(DBHelper.taskTable,sArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("add");
    }

    private void setDefaultEvs(boolean flg) {
        String [] evLst = flg?evLstText:evLstCode;
        ev1.setText(evLst[0]);
        ev2.setText(evLst[1]);
        ev3.setText(evLst[2]);
        ev4.setText(evLst[3]);
        ev5.setText(evLst[4]);
    }

    public void initFields(boolean flg) {
        setDefaultEvs(flg);
        testType.getSelectionModel().selectFirst();
        classHome.getSelectionModel().selectFirst();
        testName.setText("");
    }
}
