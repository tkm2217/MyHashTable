module com.example.dictionary {
    requires javafx.controls;
    requires javafx.fxml;


    opens hashing to javafx.fxml;
    exports hashing;
}