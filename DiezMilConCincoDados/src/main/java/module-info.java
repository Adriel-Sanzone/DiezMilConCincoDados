module com.example.diezmilconcincodados {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.diezmilconcincodados to javafx.fxml;
    exports com.example.diezmilconcincodados;
}