package com.budgetapp.ui;

import com.budgetapp.dao.DebtDao;
import com.budgetapp.dao.ExpenseDao;
import com.budgetapp.dao.IncomeDao;
import com.budgetapp.model.*;
import com.budgetapp.service.DebtCalculationService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import com.budgetapp.model.ExpenseCategory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.geometry.Insets;
public class DashboardController {

    // ===== Existing Labels =====
    @FXML private Label incomeValueLabel;
    @FXML private Label expensesValueLabel;
    @FXML private Label debtPaymentsValueLabel;
    @FXML private Label remainingCashValueLabel;
    @FXML private Label payoffMonthsLabel;
    @FXML private Label payoffInterestLabel;
    @FXML private Label negativeAmortizationLabel;


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
    @FXML private TableView<Debt> debtTable;
    @FXML private TableColumn<Debt, String> debtNameColumn;
    @FXML private TableColumn<Debt, BigDecimal> debtAmountColumn;
    @FXML private TableColumn<Debt, BigDecimal> debtRateColumn;
    @FXML private TableColumn<Debt, BigDecimal> debtMinPaymentColumn;
    @FXML private TableColumn<Debt, Recurrence> debtRecurrenceColumn;
    @FXML private TableColumn<Debt, Integer> debtMonthsColumn;
    @FXML private TableColumn<Debt, String> debtInterestPaidColumn;
    @FXML private TableColumn<Debt, String> debtNegativeAmColumn;
    @FXML private TableColumn<Income, String> incomeReceivedDateColumn;

    private final ObservableList<Income> incomes = FXCollections.observableArrayList();

    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ExpenseDao expenseDao = new ExpenseDao();
    private final IncomeDao incomeDao = new IncomeDao();
    private final DebtDao debtDao = new DebtDao();
    private final ObservableList<Debt> debts = FXCollections.observableArrayList();
    private final StringConverter<BigDecimal> bigDecimalConverter = new StringConverter<>() {
        @Override
        public String toString(BigDecimal value) {
            return value == null ? "" : value.toPlainString();
        }

        @Override
        public BigDecimal fromString(String text) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Value cannot be empty");
            }
            return new BigDecimal(text.trim());
        }
    };
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
        incomeTable.setEditable(true);
        incomeAmountColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new BigDecimalStringConverter())
        );
        incomeAmountColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
        incomeAmountColumn.setOnEditCommit(event -> {
            Income income = event.getRowValue();
            income.setAmount(event.getNewValue());
            incomeDao.update(income);

            incomeTable.refresh();
            updateTotals();
        });
        incomeRecurrenceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRecurrence()));
        incomeReceivedDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getReceivedDate() == null
                                ? ""
                                : data.getValue().getReceivedDate().toString()
                )
        );

        incomes.addAll(incomeDao.findAll());
        incomeTable.setItems(incomes);

        debtNameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        debtAmountColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));

        debtRateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getInterestRate()));

        debtMinPaymentColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getMinimumPayment()));

        debtRecurrenceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRecurrence()));
        DebtCalculationService service = new DebtCalculationService();

        debtMonthsColumn.setCellValueFactory(data -> {
            try {
                PayoffResult result = service.calculatePayoff(data.getValue());
                return new javafx.beans.property.SimpleObjectProperty<>(result.monthsToPayoff());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleObjectProperty<>(null);
            }
        });

        debtInterestPaidColumn.setCellValueFactory(data -> {
            try {
                PayoffResult result = service.calculatePayoff(data.getValue());
                return new javafx.beans.property.SimpleStringProperty(
                        "$" + String.format("%.2f", result.totalInterestPaid())
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
        });

        debtNegativeAmColumn.setCellValueFactory(data -> {
            try {
                PayoffResult result = service.calculatePayoff(data.getValue());
                return new javafx.beans.property.SimpleStringProperty(
                        result.negativeAmortization() ? "Yes" : "No"
                );
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
        });

        debts.clear();
        debts.addAll(debtDao.findAll());
        debtTable.setItems(debts);
        debtTable.setEditable(true);

        System.out.println("Debt table initialized. Loaded debts = " + debts.size());


        updateTotals();
        debtAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(bigDecimalConverter));
        debtRateColumn.setCellFactory(TextFieldTableCell.forTableColumn(bigDecimalConverter));
        debtMinPaymentColumn.setCellFactory(TextFieldTableCell.forTableColumn(bigDecimalConverter));

        debtAmountColumn.setOnEditCommit(event -> {
            Debt debt = event.getRowValue();
            BigDecimal oldValue = event.getOldValue();

            try {
                debt.setAmount(event.getNewValue());
                debtDao.save(debt);
                refreshDebts();
                updateTotals();
            } catch (Exception e) {
                debt.setAmount(oldValue);
                showAlert("Invalid debt amount");
                refreshDebts();
            }
        });

        debtRateColumn.setOnEditCommit(event -> {
            Debt debt = event.getRowValue();
            BigDecimal oldValue = event.getOldValue();

            try {
                debt.setInterestRate(event.getNewValue());
                debtDao.save(debt);
                refreshDebts();
                updateTotals();
            } catch (Exception e) {
                debt.setInterestRate(oldValue);
                showAlert("Invalid interest rate");
                refreshDebts();
            }
        });

        debtMinPaymentColumn.setOnEditCommit(event -> {
            Debt debt = event.getRowValue();
            BigDecimal oldValue = event.getOldValue();

            try {
                debt.setMinimumPayment(event.getNewValue());
                debtDao.save(debt);
                refreshDebts();
                updateTotals();
            } catch (Exception e) {
                debt.setMinimumPayment(oldValue);
                showAlert("Invalid minimum payment");
                refreshDebts();
            }
        });
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
            amount = new BigDecimal(amountField.getText().trim());
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

        Label receivedDateLabel = new Label("Date income is scheduled / received");
        DatePicker receivedDatePicker = new DatePicker();

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
            income.setReceivedDate(receivedDatePicker.getValue());
            incomeDao.save(income);
            incomes.clear();
            incomes.addAll(incomeDao.findAll());
            System.out.println("---- INCOMES ----");
            for (Income i : incomes) {
                System.out.println(i.getId() + " | " + i.getSource() + " | " + i.getAmount() + " | " + i.getRecurrence());
            }

            incomeTable.refresh();
            updateTotals();

            stage.close();
        });

        VBox root = new VBox(10,
                sourceLabel, sourceField,
                amountLabel, incomeAmountField,
                recurrenceLabel, incomeRecurrenceBox,
                receivedDateLabel, receivedDatePicker,
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
        TextField nameField = new TextField();
        nameField.setPromptText("Credit Card, Car Loan");

        Label amountLabel = new Label("Amount");
        TextField debtAmountField = new TextField();
        debtAmountField.setPromptText("Balance amount");

        Label rateLabel = new Label("Interest Rate");
        TextField debtRateField = new TextField();
        debtRateField.setPromptText("APR, e.g. 18.99");

        Label minPaymentLabel = new Label("Minimum Payment");
        TextField debtMinPaymentField = new TextField();
        debtMinPaymentField.setPromptText("Minimum payment");

        Label recurrenceLabel = new Label("Frequency");
        ComboBox<Recurrence> debtRecurrenceBox = new ComboBox<>();
        debtRecurrenceBox.getItems().addAll(Recurrence.values());

        Button saveButton = new Button("Save Debt");
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            Recurrence recurrence = debtRecurrenceBox.getValue();

            BigDecimal amount;
            BigDecimal interestRate;
            BigDecimal minimumPayment;

            try {
                amount = new BigDecimal(debtAmountField.getText().trim());
                interestRate = new BigDecimal(debtRateField.getText().trim());
                minimumPayment = new BigDecimal(debtMinPaymentField.getText().trim());
            } catch (Exception ex) {
                showAlert("Enter valid numbers for amount, interest rate, and minimum payment.");
                return;
            }

            if (name == null || name.isBlank() || recurrence == null) {
                showAlert("Fill all debt fields.");
                return;
            }

            Debt debt = new Debt();
            debt.setName(name);
            debt.setAmount(amount);
            debt.setInterestRate(interestRate);
            debt.setMinimumPayment(minimumPayment);
            debt.setRecurrence(recurrence);

            debtDao.save(debt);
            refreshDebts();
            updateTotals();
            stage.close();
        });

        HBox buttonRow = new HBox(10, saveButton, cancelButton);

        VBox layout = new VBox(10,
                nameLabel, nameField,
                amountLabel, debtAmountField,
                rateLabel, debtRateField,
                minPaymentLabel, debtMinPaymentField,
                recurrenceLabel, debtRecurrenceBox,
                buttonRow
        );

        layout.setPadding(new Insets(15));

        stage.setScene(new Scene(layout));
        stage.show();
    }
    @FXML
    private void handleDeleteDebt() {
        Debt selected = debtTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        debtDao.delete(selected.getId());
        debts.remove(selected);

        debtTable.refresh();
        updateTotals();
    }

    @FXML
    private void handleRunPayoffReport() {
        Debt selected = debtTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No debt selected");
            return;
        }

        DebtCalculationService service = new DebtCalculationService();
        PayoffResult result = service.calculatePayoff(selected);

        System.out.println("Months to payoff: " + result.monthsToPayoff());
        System.out.println("Total interest: $" + result.totalInterestPaid());
        System.out.println("Negative amortization: " + result.negativeAmortization());
    }
    private void refreshDebts() {
        debts.clear();
        debts.addAll(debtDao.findAll());
        debtTable.refresh();
        System.out.println("Debt table refreshed. Loaded debts = " + debts.size());
    }
    // ===== CORE LOGIC (important) =====
    private void updateTotals() {
        BigDecimal totalMonthlyIncome = BigDecimal.ZERO;
        BigDecimal totalMonthlyExpenses = BigDecimal.ZERO;
        BigDecimal totalMonthlyDebtPayments = BigDecimal.ZERO;

        for (Income income : incomes) {
            if (income.getAmount() != null) {
                totalMonthlyIncome = totalMonthlyIncome.add(income.getAmount());
            }
        }
        for (Expense e : expenses) {
            if (e.getAmount() != null) {

                totalMonthlyExpenses = totalMonthlyExpenses.add(e.getAmount());
            }
        }
        BigDecimal remainingCash = totalMonthlyIncome
                .subtract(totalMonthlyExpenses)
                .subtract(totalMonthlyDebtPayments);
        System.out.println("---- INCOMES COUNTED ----");
        for (Income income : incomes) {
            System.out.println(
                    income.getId() + " | " +
                            income.getSource() + " | " +
                            income.getAmount() + " | " +
                            income.getRecurrence()
            );
        }
        System.out.println("-------------------------");

        incomeValueLabel.setText("$" + totalMonthlyIncome.setScale(2, RoundingMode.HALF_UP));
        expensesValueLabel.setText("$" + totalMonthlyExpenses.setScale(2, RoundingMode.HALF_UP));
        debtPaymentsValueLabel.setText("$" + totalMonthlyDebtPayments.setScale(2, RoundingMode.HALF_UP));
        remainingCashValueLabel.setText("$" + remainingCash.setScale(2, RoundingMode.HALF_UP));
    }
    @FXML
    private void handleOpenExpenseWindow() {
        Stage stage = new Stage();
        stage.setTitle("Add Expense");

        Label nameLabel = new Label("Expense Name");
        TextField expenseNameField = new TextField();
        expenseNameField.setPromptText("Rent, Food, Insurance");

        Label amountLabel = new Label("Amount");
        TextField expenseAmountField = new TextField();
        expenseAmountField.setPromptText("Amount");

        Label recurrenceLabel = new Label("Frequency");
        ComboBox<Recurrence> expenseRecurrenceBox = new ComboBox<>();
        expenseRecurrenceBox.getItems().addAll(Recurrence.values());

        Label dueDateLabel = new Label("Due Date");
        DatePicker expenseDueDatePicker = new DatePicker();

        Button saveButton = new Button("Save Expense");
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        saveButton.setOnAction(e -> {
            String name = expenseNameField.getText();
            Recurrence recurrence = expenseRecurrenceBox.getValue();
            LocalDate dueDate = expenseDueDatePicker.getValue();

            BigDecimal amount;
            try {
                amount = new BigDecimal(expenseAmountField.getText().trim());
            } catch (Exception ex) {
                showAlert("Invalid expense amount");
                return;
            }

            if (name == null || name.isBlank() || recurrence == null || dueDate == null) {
                showAlert("Fill all expense fields");
                return;
            }

            Expense expense = new Expense();
            expense.setName(name);
            expense.setAmount(amount);
            expense.setRecurrence(recurrence);
            expense.setDueDate(dueDate);

            expense.setCategory(ExpenseCategory.OTHER);
            expenseDao.save(expense);
            expenses.clear();
            expenses.addAll(expenseDao.findAll());
            expenseTable.refresh();
            updateTotals();

            stage.close();
        });

        HBox buttonRow = new HBox(10, saveButton, cancelButton);

        VBox root = new VBox(10,
                nameLabel, expenseNameField,
                amountLabel, expenseAmountField,
                recurrenceLabel, expenseRecurrenceBox,
                dueDateLabel, expenseDueDatePicker,
                buttonRow
        );

        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 320, 320));
        stage.show();
    }

    private BigDecimal convertToMonthly(BigDecimal amount, Recurrence recurrence) {
        return switch (recurrence) {
            case DAILY -> amount.multiply(BigDecimal.valueOf(365))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            case WEEKLY -> amount.multiply(BigDecimal.valueOf(52))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            case BIWEEKLY -> amount.multiply(BigDecimal.valueOf(26))
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

            case MONTHLY -> amount;

            case QUARTERLY -> amount.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            case YEARLY -> amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        };
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}