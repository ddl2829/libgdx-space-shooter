package com.dalesmithwebdev.galaxia.tools;

import com.dalesmithwebdev.galaxia.tools.ui.LevelListView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for Galaxia level editor tools
 */
public class ToolsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Launch level list view as main window
        LevelListView levelListView = new LevelListView();
        levelListView.show();

        // Close application when main window closes
        levelListView.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
