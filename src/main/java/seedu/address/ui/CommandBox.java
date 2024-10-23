package seedu.address.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {
    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    // Store available commands for autocomplete
    private static final Map<String, String> commandSyntaxMap = new HashMap<>();
    private static final Map<String, List<String>> commandParametersMap = new HashMap<>();
    private final CommandExecutor commandExecutor;
    private String currentSuggestion = ""; // Tracks current suggestion part
    private int tabIndex = 0; // Tracks the current position for tab completion

    @FXML
    private TextField commandTextField; //input field
    @FXML
    private Label suggestionLabel; // For greyed-out suggestion -> refer to fxml file

    static {
        commandSyntaxMap.put("add", "add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS edu/EDUCATION tele/TELEGRAM_HANDLE");
        commandSyntaxMap.put("clear", "clear");
        commandSyntaxMap.put("delete", "delete INDEX");
        commandSyntaxMap.put("edit", "edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] "
                + "[edu/EDUCATION] [tele/TELEGRAM_HANDLE]");
        commandSyntaxMap.put("find", "find KEYWORD [MORE_KEYWORDS]");
        commandSyntaxMap.put("undo", "undo");
        commandSyntaxMap.put("redo", "redo");
        commandSyntaxMap.put("list", "list");
        commandSyntaxMap.put("help", "help");
        initializeCommandParameters();
    }

    /**
     * Creates a CommandBox with the given CommandExecutor.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleTextChanged(newValue);
            // Reset tab index when user types
            if (!oldValue.equals(newValue)) {
                tabIndex = 0;
            }
        });

        // Handle space and tab press for completion
        commandTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                handleSpaceCompletion();
                event.consume();
            } else if (event.getCode() == KeyCode.TAB) {
                handleTabCompletion();
                event.consume();
            }
        });
    }

    /**
     * Extract all parameters ending with "/"
     */
    private static void initializeCommandParameters() {
        for (Map.Entry<String, String> entry : commandSyntaxMap.entrySet()) {
            String command = entry.getKey();
            String syntax = entry.getValue();

            // Extract all parameters ending with "/"
            List<String> parameters = new ArrayList<>();
            String[] parts = syntax.split(" ");

            for (String part : parts) {
                if (part.contains("/") && part.indexOf("/") > 0) {
                    // Extract just the parameter prefix (including the slash)
                    String prefix = part.substring(0, part.indexOf("/") + 1);
                    parameters.add(prefix);
                }
            }

            commandParametersMap.put(command, parameters);
        }
    }


    /**
     * Handles tab completion based on current input state
     */
    private void handleTabCompletion() {
        String input = commandTextField.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        if (!input.contains(" ")) {
            handleCommandCompletion(input);
        } else {
            handleParameterCompletion(input);
        }
    }

    private void handleCommandCompletion(String input) {
        for (String command : commandSyntaxMap.keySet()) {
            if (command.startsWith(input)) {
                List<String> parameters = commandParametersMap.get(command);
                if (!parameters.isEmpty()) {
                    commandTextField.setText(command + " " + parameters.get(0));
                    commandTextField.positionCaret(commandTextField.getText().length());
                    currentSuggestion = commandSyntaxMap.get(command);
                    tabIndex = 1;
                }
                return;
            }
        }
    }

    /**
     * This method helps with auto-completion of command parameters when the user presses the Tab key.
     * It takes the input string (the current command and any parameters the user has typed) and
     * completes the next parameter by examining what has already been typed.
     * This will be used to suggest commands as the user types.
     */
    private void handleParameterCompletion(String input) {
        String[] parts = input.split("\\s+", 2); //match 1 or more whitespace characters in a str
        String command = parts[0];
        //parts[0] = "add"; command
        //parts[1] = "n/Saajid p/PHONE_NUMBER"; parameters

        if (commandParametersMap.containsKey(command)) {
            List<String> parameters = commandParametersMap.get(command);
            String currentInput = parts.length > 1 ? parts[1] : "";
            //For the "add" command, the parameters list might look like: ["n/", "p/", "e/", "a/"].
            //If parts[1] exists, ("n/Saajid p/PHONE_NUMBER"), then currentInput will store, else ->empty string

            // Find current parameter index by looking for the last parameter used
            int currentParamIndex = -1;
            for (int i = 0; i < parameters.size(); i++) {
                if (currentInput.endsWith(parameters.get(i))) {
                    currentParamIndex = i;
                    break;
                }
            }
            //Let's say the user has typed: "add n/Saajid ".The currentInput is "n/Saajid ".
            //The loop compares currentInput with each parameter in ["n/", "p/", "e/"].
            //Since currentInput ends with "n/Saajid", it will identify that the current parameter is n/ (index 0)

            // Check if we're in the middle of a parameter
            for (int i = 0; i < parameters.size(); i++) {
                String param = parameters.get(i);
                if (currentInput.contains(param)) {
                    String afterParam = currentInput.substring(
                            currentInput.indexOf(param) + param.length()
                    );
                    if (!afterParam.contains("/")) {
                        currentParamIndex = i;
                        break;
                    }
                }
            }
            //Input: "add n/Saajid p/9876. The method finds that the user is in the middle of typing the phone number.
            //Since "p/" is alr present but there’s no / after 9876, it identifies the user is working on the p/ param

            // Move to next parameter
            if (currentParamIndex < parameters.size() - 1) {
                StringBuilder newText = new StringBuilder(command + " ");

                // Keep existing parameters
                if (parts.length > 1) {
                    String[] existingParams = parts[1].split("\\s+");
                    for (String param : existingParams) {
                        if (!param.isEmpty() && !param.equals(parameters.get(currentParamIndex + 1))) {
                            newText.append(param).append(" ");
                        }
                    }
                }

                // Add next parameter
                newText.append(parameters.get(currentParamIndex + 1));

                commandTextField.setText(newText.toString());
                commandTextField.positionCaret(commandTextField.getText().length());
            }
        }
    }

    /**
     * Handles the text change event for the input field.
     * This will be used to suggest commands as the user types.
     */
    private void handleTextChanged(String input) {
        if (input.endsWith("/")) {
            suggestionLabel.setVisible(false);
            return;
        }

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];

        if (!input.contains(" ")) {
            handleCommandSuggestion(input, command);
        } else if (input.endsWith(" ")) {
            handleParameterSuggestion(input, command);
        }
    }
    private void handleCommandSuggestion(String input, String currentInput) {
        for (String command : commandSyntaxMap.keySet()) {
            if (command.startsWith(currentInput)) {
                String fullSyntax = commandSyntaxMap.get(command);
                String remainingSuggestion = fullSyntax.substring(currentInput.length());

                if (currentInput.equals(command) && !remainingSuggestion.startsWith(" ")) {
                    remainingSuggestion = " " + remainingSuggestion;
                } else if (!currentInput.equals(command) && remainingSuggestion.startsWith(" ")) {
                    remainingSuggestion = remainingSuggestion.substring(1);
                }

                suggestionLabel.setText(input + remainingSuggestion);
                suggestionLabel.setVisible(true);
                currentSuggestion = fullSyntax;
                break;
            }
        }
    }

    /**
     * This method is responsible for generating and showing parameter suggestions for a command
     * after the user starts typing it in the input field. Once a command is typed, this method helps show
     * suggestions for the remaining parameters that haven't been used yet.
     */
    private void handleParameterSuggestion(String input, String command) {
        if (commandSyntaxMap.containsKey(command)) {
            String fullSyntax = commandSyntaxMap.get(command);
            List<String> parameters = commandParametersMap.get(command);

            if (parameters != null) {
                // Find used parameters
                Set<String> usedParams = new HashSet<>();
                String[] inputParts = input.split("\\s+");
                for (String part : inputParts) {
                    for (String param : parameters) {
                        if (part.startsWith(param)) {
                            usedParams.add(param);
                        }
                    }
                }

                // Build remaining syntax
                StringBuilder remainingSyntax = new StringBuilder();
                boolean isFirst = true;
                for (String param : parameters) {
                    if (!usedParams.contains(param)) {
                        if (!isFirst) {
                            remainingSyntax.append(" ");
                        }
                        // Get the parameter description from the full syntax
                        String paramDescription = getParameterDescription(fullSyntax, param);
                        remainingSyntax.append(param).append(paramDescription);
                        isFirst = false;
                    }
                }

                String remainingText = remainingSyntax.toString().trim();
                if (!remainingText.isEmpty()) {
                    suggestionLabel.setText(input + remainingText);
                    suggestionLabel.setVisible(true);
                }

                // Example: If user typed add n/Saajid and the command syntax is add n/NAME p/PHONE_NUMBER e/EMAIL ...,
                //  the remaining suggestion will be: p/PHONE_NUMBER e/EMAIL a/ADDRESS ...
            }
        }
    }

    /**
     * Extracts the parameter description from the full syntax
     */
    private String getParameterDescription(String syntax, String paramPrefix) {
        String[] parts = syntax.split(" ");
        for (String part : parts) {
            if (part.startsWith(paramPrefix)) {
                return part.substring(paramPrefix.length());
            }
        }
        return "VALUE";
    }

    /**
     * Handles space completion to move to the next part of the suggestion.
     * After a space is detected, it uses the slash as a pointer to pinpoint
     * where the suggestion should resume from.
     */
    private void handleSpaceCompletion() {
        String input = commandTextField.getText().trim();

        // Do nothing if the input is empty
        if (input.isEmpty()) {
            return;
        }
        // Parse input to find the last '/' to append the remaining suggestion
        int lastSlashIndex = input.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            // Find the next part of the suggestion after the last slash
            String currentInputPart = input.substring(0, lastSlashIndex + 1); // Get everything before the last slash
            String remainingSuggestion = currentSuggestion.substring(currentInputPart.length()).trim();

            // Concat current input append remaining suggestion after the '  '
            suggestionLabel.setText(input + " " + remainingSuggestion);
            suggestionLabel.setVisible(true);
        }
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText(""); // Clear after successful command execution
            suggestionLabel.setVisible(false); // Hide the suggestion after execution
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
