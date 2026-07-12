package org.ispw.eventi;

import org.ispw.eventi.controller.viewcontroller.HomeController;
import org.ispw.eventi.model.SessioneUtente;
import org.ispw.eventi.model.dao.DAOFactory;
import org.ispw.eventi.navigation.NavigationService;
import org.ispw.eventi.view.cli.PrenotaEventoCLI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ispw.eventi.model.dao.dbms.DatabaseConnection;

import java.util.Scanner;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws Exception {
        SessioneUtente sessione = new SessioneUtente();
        NavigationService nav = new NavigationService(stage, sessione);

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/ispw/eventi/fxml/home.fxml")
        );
        loader.setController(new HomeController(sessione, nav));

        Parent root = loader.load();
        stage.setTitle("Weekender");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DAOFactory.Tipo tipoPersistenza = chiediTipoPersistenza(scanner);
        DAOFactory.setTipo(tipoPersistenza);

        if (tipoPersistenza == DAOFactory.Tipo.DBMS) {
            DatabaseConnection.inizializzaSchema();
        }

        boolean modalitaGrafica = chiediModalita(scanner);

        if (modalitaGrafica) {
            launch(args);
        } else {
            new PrenotaEventoCLI().start();
        }
    }

    private static DAOFactory.Tipo chiediTipoPersistenza(Scanner scanner) {
        while (true) {
            String menu = """
                          ==================================
                                SELEZIONE VERSIONE
                          ==================================
                          1. MEMORY      (no persistenza)
                          2. DBMS        (persistenza)
                          3. FILESYSTEM  (persistenza)
                          Scegli un opzione (1-3): """.stripTrailing();
            LOGGER.info(menu);

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> {
                    return DAOFactory.Tipo.MEMORY;
                }
                case "2" -> {
                    return DAOFactory.Tipo.DBMS;
                }
                case "3" -> {
                    return DAOFactory.Tipo.FILESYSTEM;
                }
                default -> LOGGER.warning("Opzione non valida. Riprova.");
            }
        }
    }

    private static boolean chiediModalita(Scanner scanner) {
        while (true) {
            String menu = """
                          ==================================
                                SELEZIONE INTERFACCIA
                          ==================================
                          1. GRAFICA (JavaFX)
                          2. CLI     (console)
                          Scegli un opzione (1-2): """.stripTrailing();
            LOGGER.info(menu);

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> {
                    return true;
                }
                case "2" -> {
                    return false;
                }
                default -> LOGGER.warning("Opzione non valida. Riprova.");
            }
        }
    }
}