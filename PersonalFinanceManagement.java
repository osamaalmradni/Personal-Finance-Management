import javax.swing.*;

/**
 * PersonalFinanceManagement
 *
 * This class serves as the entry point for the Personal Finance Management application.
 * It initializes the Model-View-Controller (MVC) components and launches the application.
 */

public class PersonalFinanceManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceModel model = new FinanceModel();
            FinanceView view = new FinanceView();
            FinanceController controller = new FinanceController(model, view);
            controller.init();
        });
    }
}