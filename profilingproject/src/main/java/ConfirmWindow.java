import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow {
    static boolean answer;

    public boolean showConfirmWindow(String title, String message) {
        Stage stage = new Stage();
        stage.setTitle(title);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        Label lbl = new Label(message);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(25);
        Button btnOk = new Button("OK");
        btnOk.setPrefWidth(60);
        Button btnCancel = new Button("Cancel");
        btnCancel.setPrefWidth(60);
        btnOk.setOnAction(event -> {
            answer = true;
            stage.close();
        });
        btnCancel.setOnAction(event -> {
            answer = false;
            stage.close();
        });
        hBox.getChildren().addAll(btnOk, btnCancel);
        vBox.getChildren().addAll(lbl, hBox);
        Scene scene = new Scene(vBox, 400, 100);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return answer;
    }
}
