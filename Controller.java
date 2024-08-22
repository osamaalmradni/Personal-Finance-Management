import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * FinanceController
 *
 * This class acts as an intermediary between the FinanceModel and FinanceView.
 * It handles user actions, updates the model, and refreshes the view accordingly.
 */
class FinanceController {
    private FinanceModel model;
    private FinanceView view;

    /**
     * Constructor for the Controller class.
     * @param model The model containing business logic and data.
     * @param view The view representing the user interface.
     */
    public FinanceController(FinanceModel model, FinanceView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    /**
     * Initializes the controller by setting up event listeners and showing the login dialog.
     */
    public void init() {
        // Set up event listeners
        view.addIncomeButton.addActionListener(e -> addIncome());
        view.addExpenseButton.addActionListener(e -> addExpense());
        view.savePDFButton.addActionListener(e -> savePDF());
        view.saveButton.addActionListener(e -> saveChanges());
        view.loadButton.addActionListener(e -> loadPrevious());
        view.getDeleteItem().addActionListener(e -> deleteSelectedItem());
        view.setIncomeFieldsListener(e -> addIncome());
        view.setExpenseFieldsListener(e -> addExpense());
        view.setNameFieldListener(e -> handleLogin());

        // Show login dialog
        view.showLoginDialog();
    }

    /**
     * Handles the addition of a new income entry.
     * Reads data from UI, adds it to the model, and updates the view.
     */
    private void addIncome() {
        try {
            String name = view.incomeNameField.getText();
            double amount = Double.parseDouble(view.incomeAmountField.getText());
            model.addIncome(name, amount);
            updateView();
            view.incomeNameField.setText("");
            view.incomeAmountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view.mainFrame, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the addition of a new expense entry.
     * Reads data from UI, adds it to the model, and updates the view.
     */
    private void addExpense() {
        try {
            String name = view.expenseNameField.getText();
            double amount = Double.parseDouble(view.expenseAmountField.getText());
            model.addExpense(name, amount);
            updateView();
            view.expenseNameField.setText("");
            view.expenseAmountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view.mainFrame, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void setUserName(String name) {
        model.setUserName(name);
        updateWelcomeMessage();
    }

    /**
     * Processes the user login by validating the entered name.
     */
    public void handleLogin() {
        String name = view.nameField.getText().trim();
        if (name.isEmpty()) {
            view.showMessage("Please enter your name!");
            return;
        }
        setUserName(name);
        view.loginFrame.setVisible(false);
        view.mainFrame.setVisible(true);
    }

    /**
     * Updates the welcome message displayed in the View.
     */
    private void updateWelcomeMessage() {
        view.setWelcomeMessage(model.getUserName(), LocalDate.now());
    }

    /**
     * Updates the entire View, including balance, income list, and expense list.
     */
    private void updateView() {
        view.updateBalance(model.getBalance());
        view.updateIncomeList(model.incomes);
        view.updateExpenseList(model.expenses);
    }

    /**
     * Saves the current financial data to a PDF file.
     * Allows user to choose save location and generates a formatted PDF report.
     */
    private void savePDF() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select a folder to save the PDF");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showSaveDialog(view.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            String baseFileName = "Financial Report for " + model.userName + "_" + model.date;
            String fileName = baseFileName + ".pdf";
            File pdfFile = new File(selectedFolder, fileName);

            // Check if file already exists and modify the file name if necessary
            int fileCounter = 1;
            while (pdfFile.exists()) {
                fileName = baseFileName + " (" + fileCounter + ").pdf";
                pdfFile = new File(selectedFolder, fileName);
                fileCounter++;
            }

            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.open();
                document.add(new Paragraph("Financial Report for " + model.userName));
                document.add(new Paragraph("Date: " + model.date));
                document.add(new Paragraph("---------------------------------------------------"));
                document.add(new Paragraph("Incomes:"));
                for (FinanceEntry entry : model.incomes) {
                    document.add(new Paragraph(entry.name + ": $" + entry.amount));
                }
                document.add(new Paragraph("---------------------------------------------------"));
                document.add(new Paragraph("Expenses:"));
                for (FinanceEntry entry : model.expenses) {
                    document.add(new Paragraph(entry.name + ": $" + entry.amount));
                }
                document.add(new Paragraph("---------------------------------------------------"));
                document.add(new Paragraph("Balance: $" + model.getBalance()));
                document.close();
                JOptionPane.showMessageDialog(view.mainFrame, "PDF saved as " + pdfFile.getAbsolutePath());
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: Unable to create PDF file. Please check file permissions or available disk space.", "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (DocumentException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: There was a problem with creating the PDF document.", "Document Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Saves the current state of the finance model to a serialized file.
     * Allows user to choose save location.
     */
    private void saveChanges() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select a folder to save the data");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = folderChooser.showSaveDialog(view.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            String baseFileName = "Financial Report for " + model.userName + "_" + model.date;
            String fileName = baseFileName + ".ser";
            File dataFile = new File(selectedFolder, fileName);

            // Check if file already exists and modify the file name if necessary
            int fileCounter = 1;
            while (dataFile.exists()) {
                fileName = baseFileName + " (" + fileCounter + ").ser";
                dataFile = new File(selectedFolder, fileName);
                fileCounter++;
            }
            try (FileOutputStream fileOut = new FileOutputStream(dataFile);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(model);
                JOptionPane.showMessageDialog(view.mainFrame, "Data saved successfully at " + dataFile.getAbsolutePath());
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: Unable to save data. Please check file permissions or available disk space.", "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: There was an I/O problem while saving data.", "I/O Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Loads previously saved financial data from a serialized file.
     * Allows user to choose the file to load.
     */
    private void loadPrevious() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to load");
        int result = fileChooser.showOpenDialog(view.mainFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (FileInputStream fileIn = new FileInputStream(selectedFile);
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                model = (FinanceModel) in.readObject();
                updateView();
                view.setWelcomeMessage(model.userName, model.date);
                JOptionPane.showMessageDialog(view.mainFrame, "Data loaded successfully");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: Selected file not found. Please check the file path.", "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: Data format is incompatible. The file may be corrupted.", "Format Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(view.mainFrame, "Error: There was an I/O problem while loading data.", "I/O Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void deleteSelectedItem() {
        if (view.isIncomeListSelected()) {
            FinanceEntry selectedIncome = view.getSelectedIncome();
            if (selectedIncome != null) {
                model.removeIncome(selectedIncome);
                updateView();
            }
        } else if (view.isExpenseListSelected()) {
            FinanceEntry selectedExpense = view.getSelectedExpense();
            if (selectedExpense != null) {
                model.removeExpense(selectedExpense);
                updateView();
            }
        }
    }

}