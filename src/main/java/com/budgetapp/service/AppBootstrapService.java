package com.budgetapp.service;

import com.budgetapp.dao.DatabaseManager;

public class AppBootstrapService {
    private final DatabaseManager databaseManager;

    public AppBootstrapService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() {
        databaseManager.initializeSchema();
    }
}
