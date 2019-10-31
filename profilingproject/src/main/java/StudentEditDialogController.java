import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StudentEditDialogController {

    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField patronymicTextField;
    @FXML
    private TextField groupTextField;
    @FXML
    private TextField averageGradeTextField;
    @FXML
    private Label warningLabel;

    private Stage dialogStage;
    private Student student;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudent(Student student) {
        this.student = student;

        surnameTextField.setText(student.getSurname());
        nameTextField.setText(student.getName());
        patronymicTextField.setText(student.getPatronymic());
        groupTextField.setText(student.getGroup());
        averageGradeTextField.setText(Double.toString(student.getAverageGrade()));
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if(isInputValid()) {
            student.setSurname(surnameTextField.getText());
            student.setName(nameTextField.getText());
            student.setPatronymic(patronymicTextField.getText());
            student.setGroup(groupTextField.getText());
            student.setAverageGrade(Double.parseDouble(averageGradeTextField.getText()));

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        errorMessage += isFullnamePartValid(surnameTextField, "surname");
        errorMessage += isFullnamePartValid(nameTextField, "name");
        errorMessage += isFullnamePartValid(patronymicTextField, "patronymic");
        if(groupTextField.getText() == null || groupTextField.getText().length() == 0) {
            errorMessage += "No valid group!\n";
        }
        if(averageGradeTextField.getText() == null || averageGradeTextField.getText().length() == 0) {
            errorMessage += "No valid average grade!\n";
        } else {
            double avGrade = -1;
            try {
                avGrade = Double.parseDouble(averageGradeTextField.getText());
            } catch(NumberFormatException e) {
                errorMessage += "No valid average grade (must be a double)!\n";
            }
            if(avGrade < 0 || avGrade > 5) {
                errorMessage += "No valid average grade (must be from 0 to 5)!\n";
            }
        }

        if(errorMessage.length() == 0) {
            return true;
        } else {
            String[] errorMessageArray = errorMessage.split("\n");
            warningLabel.setText(errorMessageArray[0]);
            return false;
        }
    }

    private String isFullnamePartValid(TextField textField, String fullnamePart) {
        String errorMessage = "";
        if(textField.getText() == null || textField.getText().length() == 0) {
            errorMessage += "No valid " + fullnamePart + "!\n";
        } else if(!textField.getText().matches("^\\D*$")) {
            errorMessage += "No valid " + fullnamePart + " (should not contain numbers)!\n";
        }
        return errorMessage;
    }
}
