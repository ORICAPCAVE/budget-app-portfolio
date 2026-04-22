package com.budgetapp.ui;

import com.budgetapp.dao.ExpenseDao;
import com.budgetapp.dao.IncomeDao;
import com.budgetapp.model.Expense;
import com.budgetapp.model.Income;
import com.budgetapp.model.Recurrence;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import javafx.geometry.Insets;
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
    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, String> incomeSourceColumn;
    @FXML private TableColumn<Income, BigDecimal> incomeAmountColumn;
    @FXML private TableColumn<Income, Recurrence> incomeRecurrenceColumn;

    private final ObservableList<Income> incomes = FXCollections.observableArrayList();

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ExpenseDao expenseDao = new ExpenseDao();
    private final IncomeDao incomeDao = new IncomeDao();

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

        expenses.addAll(expenseDao.findAll());

        expenseTable.setItems(expenses);
        incomeSourceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getSource()));

        incomeAmountColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));

        incomeRecurrenceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRecurrence()));

        incomes.addAll(incomeDao.findAll());
        incomeTable.setItems(incomes);

        updateTotals();
    }
    @FXML
    private void handleDeleteIncome() {
        Income selected = incomeTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Select an income entry to delete");
            return;
        }

        incomeDao.delete(selected.getId());
        incomes.remove(selected);
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

    @FXML
    private void handleOpenIncomeWindow() {
        Stage stage = new Stage();
        stage.setTitle("Add Income");

        Label sourceLabel = new Label("Income Source");
        TextField sourceField = new TextField();
        sourceField.setPromptText("Paycheck, Side Job, Pension");

        Label amountLabel = new Label("Amount");
        TextField incomeAmountField = new TextField();
        incomeAmountField.setPromptText("Amount");

        Label recurrenceLabel = new Label("Frequency");
        ComboBox<Recurrence> incomeRecurrenceBox = new ComboBox<>();
        incomeRecurrenceBox.getItems().addAll(Recurrence.values());

        Button saveButton = new Button("Save Income");

        saveButton.setOnAction(e -> {
            String source = sourceField.getText();
            Recurrence recurrence = incomeRecurrenceBox.getValue();

            BigDecimal amount;
            try {
                amount = new BigDecimal(incomeAmountField.getText());
            } catch (Exception ex) {
                showAlert("Invalid income amount");
                return;
            }

            if (source == null || source.isBlank() || recurrence == null) {
                showAlert("Fill all income fields");
                return;
            }

            Income income = new Income();
            income.setSource(source);
            income.setAmount(amount);
            income.setRecurrence(recurrence);

            incomeDao.save(income);
            incomes.add(income);
            updateTotals();

            stage.close();
        });

        VBox root = new VBox(10,
                sourceLabel, sourceField,
                amountLabel, incomeAmountField,
                recurrenceLabel, incomeRecurrenceBox,
                saveButton
        );
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 320, 260);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleOpenDebtWindow() {
        Stage stage = new Stage();
        stage.setTitle("Add Debt");

        Label nameLabel = new Label("Debt Name");
        TextField debtNameField = new TextField();
        debtNameField.setPromptText("Credit Card, Car Loan");

        Label balanceLabel = new Label("Balance");
        TextField balanceField = new TextField();
        balanceField.setPromptText("Current Balance");

        Label paymentLabel = new Label("Monthly Payment");
        TextField paymentField = new TextField();
        paymentField.setPromptText("Monthly Payment");

        Button saveButton = new Button("Save Debt");

        saveButton.setOnAction(e -> {
            String debtName = debtNameField.getText();

            BigDecimal balance;
            BigDecimal payment;

            try {
                balance = new BigDecimal(balanceField.getText());
                payment = new BigDecimal(paymentField.getText());
            } catch (Exception ex) {
                showAlert("Invalid debt numbers");
                return;
            }

            if (debtName == null || debtName.isBlank()) {
                showAlert("Fill all debt fields");
                return;
            }

            // Later:
            // Debt debt = new Debt();
            // debt.setName(debtName);
            // debt.setBalance(balance);
            // debt.setMonthlyPayment(payment);
            // debtDao.save(debt);

            showAlert("Debt save wiring is next.");
            stage.close();
        });

        VBox root = new VBox(10,
                nameLabel, debtNameField,
                balanceLabel, balanceField,
                paymentLabel, paymentField,
                saveButton
        );
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleRunPayoffReport() {
        showAlert("Payoff report is not implemented yet.");
    }

    // ===== CORE LOGIC (important) =====
    private void updateTotals() {
        BigDecimal totalMonthlyIncome = BigDecimal.ZERO;
        BigDecimal totalMonthlyExpenses = BigDecimal.ZERO;
        BigDecimal totalMonthlyDebtPayments = BigDecimal.ZERO; // placeholder for later

        List<Income> incomes = incomeDao.findAll();

        for (Income income : incomes) {
            totalMonthlyIncome = totalMonthlyIncome.add(
                    convertToMonthly(income.getAmount(), income.getRecurrence())
            );
        }

        for (Expense e : expenses) {
            totalMonthlyExpenses = totalMonthlyExpenses.add(
                    convertToMonthly(e.getAmount(), e.getRecurrence())
            );
        }

        BigDecimal remainingCash = totalMonthlyIncome
                .subtract(totalMonthlyExpenses)
                .subtract(totalMonthlyDebtPayments);

        incomeValueLabel.setText("$" + totalMonthlyIncome.setScale(2, RoundingMode.HALF_UP));
        expensesValueLabel.setText("$" + totalMonthlyExpenses.setScale(2, RoundingMode.HALF_UP));
        debtPaymentsValueLabel.setText("$" + totalMonthlyDebtPayments.setScale(2, RoundingMode.HALF_UP));
        remainingCashValueLabel.setText("$" + remainingCash.setScale(2, RoundingMode.HALF_UP));
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