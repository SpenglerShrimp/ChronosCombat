package app;

import _2dhif.thegamereal.control.TheGameController;
import _2dhif.thegamereal.model.TheGameModel;
import _2dhif.thegamereal.view.TheGameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TheGameModel model = new TheGameModel();

        //model.loadDebugState(TheGameModel.DebugState.AFTER_GERIN, 3);

        TheGameView view = new TheGameView(model);

        TheGameController controller = new TheGameController(model, view);

        primaryStage.setTitle("2DHIF: Chronos Combat");
        primaryStage.setScene(view.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}