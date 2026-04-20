package com.budgetapp.app;

import com.budgetapp.dao.DatabaseManager;
import com.budgetapp.service.AppBootstrapService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        AppBootstrapService bootstrapService = new AppBootstrapService(databaseManager);
        bootstrapService.initialize();
        new DatabaseManager().initializeSchema();
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/budgetapp/ui/dashboard-view.fxml"));
        Scene scene = new Scene(loader.load(), 980, 640);
        stage.setTitle("Budget App Starter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
