module library.management.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires okhttp3;
    requires com.google.gson;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.logging;
    requires org.xerial.sqlitejdbc;
    
    exports com.library;
    exports com.library.controller;
    exports com.library.model;
    exports com.library.view;
    exports com.library.database;
    exports com.library.service;
    exports com.library.utils;
}
