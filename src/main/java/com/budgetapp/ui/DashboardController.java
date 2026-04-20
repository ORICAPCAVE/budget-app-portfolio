package com.budgetapp.ui;

import com.budgetapp.dao.ExpenseDao;
import com.budgetapp.model.Expense;
import com.budgetapp.model.Recurrence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class DashboardController {

    // ===== Existing Labels =====
    @FXML private Label incomeValueLabel;
    @FXML private Label expensesValueLabel;
    @FXML private Label debtPaymentsValueLabel;
    @FXML private Label remainingCashValueLabel;

    // ===== New Input Fields =====
    @FXML private TextField nameField;
    @FXML private TextField amountField;
    @FXML private ComboBox<Recurrence> recurrenceBox;
    @FXML private DatePicker dueDatePicker;

    // ===== Table =====
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> nameColumn;
    @FXML private TableColumn<Expense, BigDecimal> amountColumn;
    @FXML private TableColumn<Expense, Recurrence> recurrenceColumn;

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ExpenseDao expenseDao = new ExpenseDao();


    @FXML
    public void initialize() {

        incomeValueLabel.setText("$0.00");
        expensesValueLabel.setText("$0.00");
        debtPaymentsValueLabel.setText("$0.00");
        remainingCashValueLabel.setText("$0.00");

        recurrenceBox.getItems().addAll(Recurrence.values());

        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        amountColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));

        recurrenceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRecurrence()));

        // 👇 LOAD FROM DATABASE
        expenses.addAll(expenseDao.findAll());

        expenseTable.setItems(expenses);

        updateTotals();
    }

    @FXML
    private void handleAddExpense() {
        String name = nameField.getText();
        Recurrence recurrence = recurrenceBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText());
        } catch (Exception e) {
            showAlert("Invalid amount");
            return;
        }

        if (name.isEmpty() || recurrence == null || dueDate == null) {
            showAlert("Fill all fields");
            return;
        }

        Expense expense = new Expense();
        expense.setName(name);
        expense.setAmount(amount);
        expense.setRecurrence(recurrence);
        expense.setDueDate(dueDate);

        expenses.add(expense);
        expenseDao.save(expense);

        updateTotals();

        nameField.clear();
        amountField.clear();
        recurrenceBox.getSelectionModel().clearSelection();
        dueDatePicker.setValue(null);
    }
    @FXML
    private void handleDeleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select an expense to delete");
            return;
        }
        expenseDao.delete(selected.getId());
        expenses.remove(selected);
        updateTotals();
    }

    // ===== CORE LOGIC (important) =====
    private void updateTotals() {
        BigDecimal totalMonthlyExpenses = BigDecimal.ZERO;

        for (Expense e : expenses) {
            totalMonthlyExpenses = totalMonthlyExpenses.add(
                    convertToMonthly(e.getAmount(), e.getRecurrence())
            );
        }

        expensesValueLabel.setText("$" + totalMonthlyExpenses.setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal convertToMonthly(BigDecimal amount, Recurrence recurrence) {
        return switch (recurrence) {
            case MONTHLY -> amount;
            case QUARTERLY -> amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            case YEARLY -> amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            case WEEKLY -> amount.multiply(BigDecimal.valueOf(52))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            default -> amount;
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}