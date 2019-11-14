import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Student List");
        this.primaryStage.getIcons().add(new Image(getClass().getResource("image/icon.png").toString()));

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("StudentList.fxml"));
            rootLayout = (AnchorPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            StudentListController controller = loader.getController();
            controller.setMain(this);

            this.primaryStage.setOnCloseRequest(event -> {
                event.consume();
                controller.showConfirmExitWindow();
            });

            primaryStage.show();

            readProfilesNamesAndQuotas(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showStudentEditDialog(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("StudentEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            StudentEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStudent(student);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Читаем из файла profiles.txt количество и название профилей, а также квоту для каждой отдельной группы
     * внутри профиля.
     *
     * @param controller
     */
    private void readProfilesNamesAndQuotas(StudentListController controller) {
        try{
            InputStream fstream = this.getClass().getResourceAsStream("/profiles.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            ArrayList<String> profilesNameList = new ArrayList<>();
            ArrayList<Integer> subgroupProfileQuota = new ArrayList<>();
            ArrayList<Integer> profileQuota = new ArrayList<>();
            if((strLine = br.readLine()) != null)
                controller.getProfiling().setProfilesNumber(Integer.parseInt(strLine));
            for(int i = 0; i < controller.getProfiling().getProfilesNumber(); i++) {
                if((strLine = br.readLine()) != null)
                    profilesNameList.add(strLine);
                if((strLine = br.readLine()) != null)
                    subgroupProfileQuota.add(Integer.parseInt(strLine));
                if((strLine = br.readLine()) != null)
                    subgroupProfileQuota.add(Integer.parseInt(strLine));
            }
            for(int i = 0; i < controller.getProfiling().getProfilesNumber()*2; i = i + 2) {
                profileQuota.add(subgroupProfileQuota.get(i) + subgroupProfileQuota.get(i + 1));
            }

            controller.getProfiling().setSubgroupProfileQuota(subgroupProfileQuota);
            controller.getProfiling().setProfilesNameList(profilesNameList);
            controller.getProfiling().setProfileQuota(profileQuota);

            System.out.println(profileQuota);
            System.out.println(profilesNameList);
            System.out.println(subgroupProfileQuota);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ObservableList<Student> getStudentList() {
        return studentList;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
