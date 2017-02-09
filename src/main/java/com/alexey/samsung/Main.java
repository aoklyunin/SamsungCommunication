package com.alexey.samsung;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: 25.09.2016  Нужно кнопку VK скрыть и открывать только когда подключение сделано
// http://stackoverflow.com/questions/30308065/changing-the-text-of-a-label-from-a-different-class-in-javafx
// подключить прокси к selenium

public class Main extends Application {
    Logger logger;
    PrintStream console = System.err;
    private void initErrLog() throws FileNotFoundException {
        File file = new File("err.log");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);
    }
    private Stage primaryStage;
    private AnchorPane rootLayout;
    public VkApi vk;
    @Override
    public void start(Stage primaryStage) throws SQLException, ClassNotFoundException {
       /* try {
            initErrLog();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        //vkHelper.save("303154598",0,1);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SamsungCommunication");

        initRootLayout();
        try {
          //  CustomOperations.dispAttempt("jhkjh");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(WebSelenium.loadCurPageHTTP("http://google.com"));
       /* GMailSender sender = new GMailSender("aoklyunin@gmail.com", "aoklyunin1990");
        sender.sendMail("Тестовое письмо",
                "Тестовое письмо\nВнезапно мдааааа",
                "aoklyunin@gmail.com",
                "aoklyunin@gmail.com");

        System.out.println("Completed");*/
        //vk = new VkApi();
     /*   try(DBHelper dbHelper = new DBHelper()){
            dbHelper.connect();
            dbHelper.loadInfromaticFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/hello.fxml"));
            rootLayout = (AnchorPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

