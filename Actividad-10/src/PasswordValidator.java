import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String LOG_FILE = "password_validation.log";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al validador de contraseñas.");

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            while (true) {
                System.out.print("Ingrese una contraseña (o 'exit' para salir): ");
                String password = scanner.nextLine();

                if (password.equalsIgnoreCase("exit")) {
                    break;
                }

                executorService.execute(() -> {
                    boolean isValid = validatePassword(password);
                    logResult(password, isValid, writer);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z].*[a-z])(?=.*[A-Z].*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private static void logResult(String password, boolean isValid, BufferedWriter writer) {
        try {
            String result = isValid ? "Válida" : "No cumple con los requisitos";
            String logEntry = "Contraseña: " + password + ", Resultado: " + result + "\n";
            writer.write(logEntry);
            System.out.println("La contraseña '" + password + "' es " + result + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}