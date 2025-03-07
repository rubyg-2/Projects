package defaul;
import java.util.*;

public class QASystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Questions questions = new Questions();
    private static Answers answers = new Answers();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            try {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer
                
                switch (choice) {
                    case 1:
                        addQuestion();
                        break;
                    case 2:
                        addAnswer();
                        break;
                    case 3:
                        searchQuestions();
                        break;
                    case 4:
                        viewUnresolvedQuestions();
                        break;
                    case 5:
                        markQuestionResolved();
                        break;
                    case 6:
                        viewAllQuestions();
                        break;
                    case 7:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear the buffer
            } catch (Exception e) {
                System.out.println("An error occurred. Please try again.");
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n=== QA System Menu ===");
        System.out.println("1. Add New Question");
        System.out.println("2. Add Answer to Question");
        System.out.println("3. Search Questions");
        System.out.println("4. View Unresolved Questions");
        System.out.println("5. Mark Question as Resolved");
        System.out.println("6. View All Questions");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addQuestion() {
        System.out.println("\nEnter your question:");
        String content = scanner.nextLine();
        Question question = new Question(content);
        
        try {
            questions.addQuestion(question);
            System.out.println("Question #" + question.getQuestionID() + " added successfully");
        } catch (IllegalArgumentException e) {
            System.out.println("Unable to add question - similar question exists");
        }
    }

    private static void addAnswer() {
        viewAllQuestions();
        System.out.println("\nEnter question ID to answer:");
        try {
            int questionId = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            Question targetQuestion = findQuestionById(questionId);
            if (targetQuestion != null) {
                System.out.println("Enter your answer:");
                String content = scanner.nextLine();
                Answer answer = new Answer(content, targetQuestion);
                
                try {
                    answers.addAnswer(answer);
                    System.out.println("Answer added successfully");
                } catch (IllegalArgumentException e) {
                    System.out.println("Unable to add answer - not relevant to question");
                }
            } else {
                System.out.println("Question not found");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid question ID");
            scanner.nextLine(); // Clear buffer
        }
    }

    private static void searchQuestions() {
        System.out.println("\nEnter search term:");
        String searchTerm = scanner.nextLine();
        List<Question> searchResults = questions.searchQuestions(searchTerm);
        
        if (!searchResults.isEmpty()) {
            System.out.println("\nFound " + searchResults.size() + " matching questions:");
            displayQuestions(searchResults);
        } else {
            System.out.println("No matching questions found");
        }
    }

    private static void viewUnresolvedQuestions() {
        List<Question> unresolvedQuestions = questions.getUnresolvedQuestions();
        if (!unresolvedQuestions.isEmpty()) {
            System.out.println("\nUnresolved Questions:");
            displayQuestions(unresolvedQuestions);
        } else {
            System.out.println("No unresolved questions");
        }
    }

    private static void markQuestionResolved() {
        viewAllQuestions();
        System.out.println("\nEnter question ID to mark as resolved:");
        try {
            int questionId = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            Question targetQuestion = findQuestionById(questionId);
            if (targetQuestion != null) {
                if (!targetQuestion.isResolved()) {
                    questions.markQuestionResolved(targetQuestion);
                    System.out.println("Question #" + questionId + " marked as resolved");
                } else {
                    System.out.println("Question is already resolved");
                }
            } else {
                System.out.println("Question not found");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid question ID");
            scanner.nextLine(); // Clear buffer
        }
    }

    private static void viewAllQuestions() {
        if (!questions.searchQuestions("").isEmpty()) {
            System.out.println("\nAll Questions:");
            displayQuestions(questions.searchQuestions(""));
        } else {
            System.out.println("No questions found");
        }
    }

    private static void displayQuestions(List<Question> questionList) {
        for (Question q : questionList) {
            System.out.println("Question #" + q.getQuestionID() + ": " + q.getContent());
            System.out.println("Status: " + q.getStatus());
            List<Answer> answers = q.getPotentialAnswers();
            if (!answers.isEmpty()) {
                System.out.println("Answers:");
                for (Answer a : answers) {
                    System.out.println("  - " + a.getContent());
                }
            }
            System.out.println();
        }
    }

    private static Question findQuestionById(int id) {
        return questions.searchQuestions("").stream()
                .filter(q -> q.getQuestionID() == id)
                .findFirst()
                .orElse(null);
    }
}
