package application;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*; 

public class qaGUI {

    private backendQA back;
    private Scene qaScene;
    private int currentUserId; // Store the logged-in user's id

    // Updated constructor: pass in the databaseHelper if needed.
    public qaGUI(DatabaseHelper databaseHelper, int currentUserId) {
        // Initialize backendQA (and pass in the user id if your backend supports that)
        back = new backendQA();
        // Set the current user id in qaGUI (and possibly in backendQA)
        this.currentUserId = currentUserId;
        back.setCurrentUserId(currentUserId);  // <-- Ensure backendQA has a setter for currentUserId
    }

    /**
     * Displays the Q&A scene directly.
     * @param primaryStage   The primary stage on which to display the scene.
     * @param databaseHelper The database helper instance.
     */
    public void show(Stage primaryStage, DatabaseHelper databaseHelper) {
        qaScene = createQAScene(primaryStage);
        primaryStage.setScene(qaScene);
        primaryStage.setTitle("Q&A Application");
        primaryStage.show();
    }

    // --- Creates the Q&A Scene ---
    private Scene createQAScene(Stage primaryStage) {
        VBox qaPane = new VBox(10);
        qaPane.setStyle("-fx-padding: 20;");
        qaPane.setAlignment(Pos.CENTER);

        TreeView<String> treeView = new TreeView<>();
        TreeItem<String> rootItem = new TreeItem<>("Posts");
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        refreshTree(rootItem);

        treeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem != rootItem) {
                    String selectedText = selectedItem.getValue();
                    String type = "";
                    if(selectedText.startsWith("Question"))
                        type = "question";
                    else if(selectedText.startsWith("Answer"))
                        type = "answer";
                    else if(selectedText.startsWith("Reply"))
                        type = "reply";
                    
                    int id = parseId(selectedText);
                    showReplyDialog(id, type);
                    refreshTree(rootItem);
                }
            }
        });

        // --- Post New Question ---
        Button postQuestionButton = new Button("Post New Question");
        postQuestionButton.setOnAction(e -> {
            // Ensure that currentUserId is valid
            if (currentUserId == -1) {
                showAlert("Error", "No user logged in.");
                return;
            }
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Question");
            dialog.setHeaderText("Enter your question:");
            dialog.setContentText("Question:");
            dialog.showAndWait().ifPresent(text -> {
                int newQuestionId = back.addQuestion(text);
                if(newQuestionId != -1) {
                    showAlert("Success", "Question added with ID " + newQuestionId);
                    refreshTree(rootItem);
                }
            });
        });

        // --- Delete Selected Post ---
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if(selectedItem != null && selectedItem != rootItem) {
                String selectedText = selectedItem.getValue();
                String type = "";
                if(selectedText.startsWith("Question"))
                    type = "question";
                else if(selectedText.startsWith("Answer"))
                    type = "answer";
                else if(selectedText.startsWith("Reply"))
                    type = "reply";
                
                int id = parseId(selectedText);
                boolean success = false;
                if(type.equals("question"))
                    success = back.deleteQuestion(id);
                else if(type.equals("answer"))
                    success = back.deleteAnswer(id);
                else if(type.equals("reply"))
                    success = back.deleteReply(id);
                
                if(success) {
                    showAlert("Success", "Post deleted (text replaced with 'Deleted').");
                    refreshTree(rootItem);
                } else {
                    showAlert("Error", "Deletion failed.");
                }
            } else {
                showAlert("Error", "No post selected for deletion.");
            }
        });

        // --- Edit Selected Question ---
        Button editButton = new Button("Edit Selected Question");
        editButton.setOnAction(e -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem != rootItem && selectedItem.getValue().startsWith("Question")) {
                int id = parseId(selectedItem.getValue());
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Edit Question");
                dialog.setHeaderText("Enter new text for the question:");
                dialog.setContentText("New Question Text:");
                dialog.showAndWait().ifPresent(newText -> {
                    if (newText.isEmpty()) {
                        showAlert("Error", "New text cannot be empty.");
                    } else {
                        boolean success = back.editQuestion(id, newText);
                        if (success) {
                            showAlert("Success", "Question updated successfully!");
                            refreshTree(rootItem);
                        } else {
                            showAlert("Error", "Failed to update question.");
                        }
                    }
                });
            } else {
                showAlert("Error", "Please select a question to edit.");
            }
        });

        // --- Refresh ---
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshTree(rootItem));

        // --- Logout ---
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        logoutButton.setOnAction(a -> {
            primaryStage.close();
        });

        qaPane.getChildren().addAll(new Label("Q&A Page"), treeView, postQuestionButton, deleteButton, editButton, refreshButton, logoutButton);
        return new Scene(qaPane, 600, 400);
    }

    // --- Refresh the TreeView with updated posts ---
    private void refreshTree(TreeItem<String> rootItem) {
        rootItem.getChildren().clear();
        try {
            ObservableList<String> questions = back.getAllQuestions();
            for (String q : questions) {
                int questionId = parseId(q);
                TreeItem<String> questionItem = new TreeItem<>(q);
                
                // Add replies to question.
                ObservableList<String> qReplies = back.getRepliesForQuestion(questionId);
                for (String rep : qReplies) {
                    TreeItem<String> replyItem = new TreeItem<>(rep);
                    questionItem.getChildren().add(replyItem);
                }
                
                // Add answers and their replies.
                ObservableList<String> answers = back.getAnswersForQuestion(questionId);
                for (String ans : answers) {
                    int answerId = parseId(ans);
                    TreeItem<String> answerItem = new TreeItem<>(ans);
                    ObservableList<String> aReplies = back.getRepliesForAnswer(answerId);
                    for (String rep : aReplies) {
                        TreeItem<String> replyItem = new TreeItem<>(rep);
                        answerItem.getChildren().add(replyItem);
                    }
                    questionItem.getChildren().add(answerItem);
                }
                rootItem.getChildren().add(questionItem);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // --- Parses the ID from a post string (assumes format "Type <id> ...") ---
    private int parseId(String text) {
        try {
            String[] parts = text.split(" ");
            String idStr = parts[1];
            return Integer.parseInt(idStr);
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // --- Show a dialog for replying or answering ---
    private void showReplyDialog(int id, String type) {
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Answer", "Answer", "Reply");
        choiceDialog.setTitle("Choose Action");
        choiceDialog.setHeaderText("Choose your action for this " + type + ":");
        choiceDialog.setContentText("Select:");
        choiceDialog.showAndWait().ifPresent(choice -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(choice);
            dialog.setHeaderText("Enter your " + choice + " for the " + type + ":");
            dialog.setContentText("Text:");
            dialog.showAndWait().ifPresent(text -> {
                if (choice.equals("Answer")) {
                    int newAnswerId = back.addAnswer(id, text);
                    if (newAnswerId != -1)
                        showAlert("Success", "Answer added with ID " + newAnswerId);
                } else {
                    int newReplyId = back.addReply(id, type, text);
                    if (newReplyId != -1)
                        showAlert("Success", "Reply added with ID " + newReplyId);
                }
            });
        });
    }

    // --- Utility method to show an alert ---
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
