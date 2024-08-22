import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;



/**
 * FinanceView
 *
 * This class is responsible for creating and managing the user interface of the application.
 * It sets up the main application window, login dialog, and various UI components.
 */
class FinanceView {
    JFrame mainFrame, loginFrame;
    JTextField nameField, incomeNameField, incomeAmountField, expenseNameField, expenseAmountField;
    JButton addIncomeButton, addExpenseButton, savePDFButton, saveButton, loadButton;
    JLabel balanceLabel, welcomeLabel, dateLabel;
    JPanel incomePanel, expensePanel;
    DefaultListModel<String> incomeListModel, expenseListModel;
    JList<String> incomeList, expenseList;
    private FinanceController controller;
    private JPopupMenu popupMenu;
    private JMenuItem deleteItem;

    public void setController(FinanceController controller) {
        this.controller = controller;
    }

    public FinanceView() {
        setupLoginFrame();
        setupMainFrame();
        setupPopupMenu();
    }

    /**
     * Sets up the login frame where users enter their name.
     */
    private void setupLoginFrame() {
        loginFrame = new JFrame("Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new FlowLayout());
        loginFrame.setLocationRelativeTo(null);

        JLabel nameLabel = new JLabel("Enter your name:");
        nameField = new JTextField(20);
        JButton loginButton = new JButton("Login");

        loginFrame.add(nameLabel);
        loginFrame.add(nameField);
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> controller.handleLogin());
    }
    /**
     * Sets up the main application frame with income and expense panels, balance display, and action buttons.
     */
    private void setupMainFrame() {
        mainFrame = new JFrame("Personal Finance Management");
        mainFrame.setSize(900, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(10, 10));
        mainFrame.setLocationRelativeTo(null);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        welcomeLabel = new JLabel();
        dateLabel = new JLabel();
        topPanel.add(welcomeLabel, BorderLayout.NORTH);
        topPanel.add(dateLabel, BorderLayout.SOUTH);
        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));


        incomeNameField = new JTextField(20);
        incomeAmountField = new JTextField(10);
        addIncomeButton = new JButton("Add Income");

        expenseNameField = new JTextField(20);
        expenseAmountField = new JTextField(10);
        addExpenseButton = new JButton("Add Expense");

        // Income Panel
        incomePanel = createFinancePanel("Income", incomeNameField, incomeAmountField, addIncomeButton);
        centerPanel.add(incomePanel);

        // Expense Panel
        expensePanel = createFinancePanel("Expenses", expenseNameField, expenseAmountField, addExpenseButton);
        centerPanel.add(expensePanel);

        mainFrame.add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        savePDFButton = new JButton("Save as PDF");
        saveButton = new JButton("Save Changes");
        loadButton = new JButton("Load Previous");
        bottomPanel.add(balanceLabel);
        bottomPanel.add(savePDFButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        // Add some padding
        mainFrame.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


    }
    private void setupPopupMenu() {
        popupMenu = new JPopupMenu();
        deleteItem = new JMenuItem("Delete");
        popupMenu.add(deleteItem);

        addPopupToList(incomeList);
        addPopupToList(expenseList);
    }

    private void addPopupToList(JList<String> list) {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                if (list.getSelectedIndex() != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public JMenuItem getDeleteItem() {
        return deleteItem;
    }

    public boolean isIncomeListSelected() {
        return incomeList.getSelectedIndex() != -1;
    }

    public boolean isExpenseListSelected() {
        return expenseList.getSelectedIndex() != -1;
    }
    public FinanceEntry getSelectedIncome() {
        int index = incomeList.getSelectedIndex();
        if (index != -1) {
            String selectedItem = incomeListModel.getElementAt(index);
            return parseFinanceEntry(selectedItem);
        }
        return null;
    }

    public FinanceEntry getSelectedExpense() {
        int index = expenseList.getSelectedIndex();
        if (index != -1) {
            String selectedItem = expenseListModel.getElementAt(index);
            return parseFinanceEntry(selectedItem);
        }
        return null;
    }

    private FinanceEntry parseFinanceEntry(String item) {
        String[] parts = item.split(": \\$");
        if (parts.length == 2) {
            String name = parts[0];
            double amount = Double.parseDouble(parts[1]);
            return new FinanceEntry(name, amount);
        }
        return null;
    }


    private JPanel createFinancePanel(String title, JTextField nameField, JTextField amountField, JButton addButton) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 2, 2, 2);

        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        inputPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(nameField, gbc);
        gbc.gridy++;
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(addButton, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // List
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(200, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Store the list and its model
        if (title.equals("Income")) {
            incomeList = list;
            incomeListModel = listModel;
        } else {
            expenseList = list;
            expenseListModel = listModel;
        }

        return panel;
    }

    public void showLoginDialog() {
        loginFrame.setVisible(true);
    }

    /**
     * Updates the displayed balance in the UI.
     *
     * @param balance The new balance to display
     */
    public void updateBalance(double balance) {
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }

    public void setWelcomeMessage(String name, LocalDate date) {
        welcomeLabel.setText("Welcome " + name + "!");
        dateLabel.setText("Your accounts for " + date);
    }

    /**
     * Updates the list of incomes in the UI.
     *
     * @param incomes List of income entries to display
     */
    public void updateIncomeList(List<FinanceEntry> incomes) {
        updateList(incomeListModel, incomes);
    }

    public void updateExpenseList(List<FinanceEntry> expenses) {
        updateList(expenseListModel, expenses);
    }

    private void updateList(DefaultListModel<String> model, List<FinanceEntry> entries) {
        model.clear();
        for (FinanceEntry entry : entries) {
            model.addElement(entry.name + ": $" + entry.amount);
        }
    }
    public void setNameFieldListener(ActionListener listener) {
        nameField.addActionListener(listener);
    }
    private void setFieldListeners(JTextField nameField, JTextField amountField, ActionListener addListener) {
        nameField.addActionListener(e -> amountField.requestFocus());
        amountField.addActionListener(e -> {
            addListener.actionPerformed(e);
            nameField.requestFocus();
        });
    }

    public void setIncomeFieldsListener(ActionListener addListener) {
        setFieldListeners(incomeNameField, incomeAmountField, addListener);
    }

    public void setExpenseFieldsListener(ActionListener addListener) {
        setFieldListeners(expenseNameField, expenseAmountField, addListener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

}
