package org.ispw.eventi;

import org.ispw.eventi.controller.viewcontroller.HomeController;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.navigation.NavigationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ispw.eventi.model.dao.dbms.DatabaseConnection;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //Connessione con Database
        DatabaseConnection.inizializzaSchema();

        // 1. Sessione — solo dati, niente nav
        SessioneUtente sessione = new SessioneUtente();

        // 2. NavigationService — conosce stage e sessione
        NavigationService nav = new NavigationService(stage, sessione);

        // 3. Carica la prima schermata
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/ispw/eventi/fxml/home.fxml")
        );
        loader.setController(new HomeController(sessione, nav));

        Parent root = loader.load();
        stage.setTitle("Weekender");
        stage.setScene(new Scene(root));
        stage.show();



        Stage stage2= new Stage();
        // 1. Sessione — solo dati, niente nav
        SessioneUtente sessione2 = new SessioneUtente();
        // 2. NavigationService — conosce stage e sessione
        NavigationService nav2 = new NavigationService(stage2, sessione2);

        // 3. Carica la prima schermata
        FXMLLoader loader2 = new FXMLLoader(
                getClass().getResource("/org/ispw/eventi/fxml/home.fxml")
        );
        loader2.setController(new HomeController(sessione2, nav2));

        Parent root2 = loader2.load();
        stage2.setTitle("Weekender");
        stage2.setScene(new Scene(root2));
        stage2.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}