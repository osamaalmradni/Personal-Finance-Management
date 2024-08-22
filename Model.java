import java.time.LocalDate;
import java.util.*;
import java.io.*;



/**
 * FinanceModel
 *
 * This class represents the data model for the finance application.
 * It stores and manages financial entries (incomes and expenses) and provides methods to manipulate this data.
 */
class FinanceEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    double amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinanceEntry that = (FinanceEntry) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }


    public FinanceEntry(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }
}

class FinanceModel implements Serializable {
    private static final long serialVersionUID = 1L;
    String userName;
    List<FinanceEntry> incomes = new ArrayList<>();
    List<FinanceEntry> expenses = new ArrayList<>();
    LocalDate date;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public FinanceModel() {
        this.date = LocalDate.now();
    }

    /**
     * Adds a new income entry to the model.
     *
     * @param name The name or description of the income
     * @param amount The amount of the income
     */
    public void addIncome(String name, double amount) {
        incomes.add(new FinanceEntry(name, amount));
    }

    /**
     * Adds a new expense entry to the model.
     *
     * @param name The name or description of the expense
     * @param amount The amount of the expense
     */
    public void addExpense(String name, double amount) {
        expenses.add(new FinanceEntry(name, amount));
    }

    /**
     * Calculates and returns the current balance.
     *
     * @return The difference between total income and total expenses
     */
    public double getBalance() {
        double totalIncome = incomes.stream().mapToDouble(e -> e.amount).sum();
        double totalExpense = expenses.stream().mapToDouble(e -> e.amount).sum();
        return totalIncome - totalExpense;
    }
    public void removeIncome(FinanceEntry income) {
        incomes.remove(income);
    }

    public void removeExpense(FinanceEntry expense) {
        expenses.remove(expense);
    }

}