import java.util.*; // Import the utility package for Scanner and Random classes.
import java.util.concurrent.atomic.AtomicBoolean; // Import AtomicBoolean for thread-safe boolean control.

public class MathGame {
    public static void main(String[] args) {
        // Create a Scanner object to read user input.
        Scanner scanner = new Scanner(System.in);
        // Create a Random object to generate random numbers.
        Random random = new Random();

        // Welcome message and instructions for the user.
        System.out.println("Welcome to The Simple Math Test!");
        System.out.println("You will be asked 20 questions and have 1 minute to answer them.");
        System.out.println("Please choose a difficulty: easy, medium, or hard");

        // Read the user's chosen difficulty and convert it to lowercase for case-insensitive comparison.
        String difficulty = scanner.nextLine().toLowerCase();

        // Validate the difficulty input; keep asking until a valid choice is entered.
        while (!difficulty.equals("easy") && !difficulty.equals("medium") && !difficulty.equals("hard")) {
            System.out.println("Invalid choice. Please type 'easy', 'medium', or 'hard':");
            difficulty = scanner.nextLine().toLowerCase();
        }

        // Initialize the user's score to 0.
        int score = 0;
        // Set the total number of questions to 20.
        int questionCount = 20;
        // Create an AtomicBoolean to track if the time is up (thread-safe).
        AtomicBoolean timeUp = new AtomicBoolean(false);

        // Create a timer thread that runs for 1 minute and then sets timeUp to true.
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(60 * 1000); // Sleep for 60,000 milliseconds (1 minute).
                timeUp.set(true); // Set the timeUp flag to true.
                System.out.println("\nTime's up!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status.
            }
        });

        timerThread.start(); // Start the timer thread.

        // Main game loop for asking 20 questions or stopping when time is up.
        for (int i = 1; i <= questionCount; i++) {
            if (timeUp.get()) break; // Stop the loop if time is up.

            // Generate a random question based on the chosen difficulty.
            String question = generateQuestion(difficulty, random);
            System.out.println("Question " + i + ": " + question);

            // Calculate the correct answer for the generated question.
            int correctAnswer = evaluateQuestion(question);

            System.out.print("Your answer: ");
            long startInputTime = System.currentTimeMillis(); // Record the time when input is requested.

            // Wait for the user to input an integer answer within the time limit.
            while (!scanner.hasNextInt() && (System.currentTimeMillis() - startInputTime) < (60 * 1000)) {
                if (timeUp.get()) break; // Stop if the time runs out while waiting for input.
            }

            if (timeUp.get()) break; // Exit if the time is up after waiting for input.

            int userAnswer = scanner.nextInt(); // Read the user's answer.
            if (userAnswer == correctAnswer) {
                score++; // Increase the score if the answer is correct.
                System.out.println("Correct!");
            } else {
                System.out.println("Wrong! The correct answer was: " + correctAnswer);
            }
        }

        // Display the final score after the game ends.
        System.out.println("Game over! Your score: " + score + "/" + questionCount);
        scanner.close(); // Close the Scanner to free up resources.
    }

    // Method to generate a random math question based on the selected difficulty.
    public static String generateQuestion(String difficulty, Random random) {
        int num1, num2; // Declare two integers for the operands.
        char operator = '+'; // Initialize the operator with a default value of '+'.

        switch (difficulty) {
            case "easy":
                // For easy difficulty, generate numbers between 1 and 15 and use addition.
                num1 = random.nextInt(15) + 1;
                num2 = random.nextInt(15) + 1;
                operator = '+';
                break;

            case "medium":
                // For medium difficulty, generate numbers between 1 and 50 and randomly use addition or subtraction.
                num1 = random.nextInt(50) + 1;
                num2 = random.nextInt(50) + 1;
                operator = random.nextBoolean() ? '+' : '-';
                break;

            case "hard":
                // For hard difficulty, generate numbers up to 50 and randomly use addition, subtraction, or multiplication.
                num1 = random.nextInt(50) + 1;
                // For multiplication, restrict the second number to a smaller range (1-12).
                num2 = operator == '*' ? random.nextInt(12) + 1 : random.nextInt(50) + 1;
                operator = switch (random.nextInt(3)) {
                    case 0 -> '+'; // 0 represents addition.
                    case 1 -> '-'; // 1 represents subtraction.
                    default -> '*'; // 2 represents multiplication.
                };
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + difficulty); // Handle unexpected difficulty levels.
        }

        // Return the generated question as a string.
        return num1 + " " + operator + " " + num2;
    }

    // Method to evaluate the generated math question and return the correct answer.
    public static int evaluateQuestion(String question) {
        // Split the question string into parts (e.g., "5 + 3" -> ["5", "+", "3"]).
        String[] parts = question.split(" ");
        int num1 = Integer.parseInt(parts[0]); // Parse the first number.
        char operator = parts[1].charAt(0); // Get the operator.
        int num2 = Integer.parseInt(parts[2]); // Parse the second number.

        // Calculate the result based on the operator.
        return switch (operator) {
            case '+' -> num1 + num2;
            case '-' -> num1 - num2;
            case '*' -> num1 * num2;
            default -> 0; // Default case (should never happen).
        };
    }
}
