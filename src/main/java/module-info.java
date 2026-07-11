module it.torvergata.ispw.realfinalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires org.xerial.sqlitejdbc;

    opens org.ispw.eventi to javafx.fxml, javafx.graphics;
    opens org.ispw.eventi.controller.viewcontroller to javafx.fxml;

    opens org.ispw.eventi.model.entity to com.google.gson;
    opens org.ispw.eventi.model.bean   to com.google.gson;
    opens org.ispw.eventi.model.state  to com.google.gson;


    exports org.ispw.eventi;
    exports org.ispw.eventi.controller.viewcontroller;
    exports org.ispw.eventi.navigation;
}