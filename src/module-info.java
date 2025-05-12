/**
 * Module-info.
 */
module TP3_Pre_Final {
   	exports databasePart1;
    exports application;

    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires junit;
    requires org.junit.jupiter.api;
    exports test to junit;

    opens application to javafx.graphics, javafx.fxml;
}