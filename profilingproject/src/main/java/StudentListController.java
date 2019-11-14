import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StudentListController {
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student, String> surnameTableColumn;
    @FXML
    private TableColumn<Student, String> nameTableColumn;
    @FXML
    private TableColumn<Student, String> patronymicTableColumn;
    @FXML
    private TableColumn<Student, String> groupTableColumn;
    @FXML
    private TableColumn<Student, Double> averageGradeTableColumn;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label errorLabel;

    private Main main;
    private Profiling profiling = new Profiling();
    ConfirmWindow confirmWindow = new ConfirmWindow();
    private boolean isSaved = true;

    public Profiling getProfiling() {
        return profiling;
    }

    @FXML
    private void initialize() {
        surnameTableColumn.setCellValueFactory(cellData -> cellData.getValue().surnameProperty());
        nameTableColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        patronymicTableColumn.setCellValueFactory(cellData -> cellData.getValue().patronymicProperty());
        groupTableColumn.setCellValueFactory(cellData -> cellData.getValue().groupProperty());
        averageGradeTableColumn.setCellValueFactory(cellData -> cellData.getValue().averageGradeProperty().asObject());

        statusLabel.setText("Elements in table: " + studentTableView.getItems().size());

        studentTableView.getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(Change<? extends TablePosition> c) {
                errorLabel.setText("");
            }
        });
    }

    public void setMain(Main main) {
        this.main = main;
        studentTableView.setItems(main.getStudentList());
    }

    @FXML
    private void handleNewFile() {
        if (!main.getStudentList().isEmpty()) {
            boolean answer = confirmWindow.showConfirmWindow("Confirm create a new file",
                    "Are you sure you want to create a new list without saving?");
            if (answer) {
                main.getStudentList().removeAll(main.getStudentList());
                errorLabel.setText("");
                statusLabel.setText("Elements in table: " + studentTableView.getItems().size());
            }
        }
    }

    @FXML
    private void handleOpenFile() {
        boolean answer = true;
        if (!main.getStudentList().isEmpty()) {
            answer = confirmWindow.showConfirmWindow("Confirm open file",
                    "Are you sure you want to open file without saving?");
        }
        if (answer) {
            main.getStudentList().removeAll(main.getStudentList());
            if(!profiling.getStudentList().isEmpty()) {
                profiling.getStudentList().removeAll(profiling.getStudentList());
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));

            List<File> files = fileChooser.showOpenMultipleDialog(null);
            if (files != null) {
                errorLabel.setText("");
                try {
                    readFromExcel(files);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleSaveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        try {
            writeIntoExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromExcel(List<File> files) throws IOException{
        String fullname = "";
        String group = "";
        double averageGrade = 0;

        for(File file : files) {
            XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = myExcelBook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Row row = rowIterator.next();
            row = rowIterator.next();

            while(rowIterator.hasNext()) {

                row = rowIterator.next();

                if (row.getCell(1).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(1) != null && !row.getCell(1).equals("")) {
                    fullname = row.getCell(1).getStringCellValue();
                } else {
                    continue;
                }

                if (row.getCell(2).getCellType() == XSSFCell.CELL_TYPE_STRING && row.getCell(2) != null && !row.getCell(2).equals("")) {
                    group = row.getCell(2).getStringCellValue();
                } else {
                    group = "Неизвестно";
                }

                if (row.getCell(3).getCellType() == XSSFCell.CELL_TYPE_NUMERIC && row.getCell(3) != null && !row.getCell(3).equals("")) {
                    averageGrade = row.getCell(3).getNumericCellValue();
                } else {
                    averageGrade = 0;
                }

                ArrayList<Integer> priority = new ArrayList<>();
                for (int i = 4; i < 7; i++) {
                    if (row.getCell(i).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                        priority.add((int)row.getCell(i).getNumericCellValue());
                    } else {
                        priority.add(0);
                    }
                }

                String name[] = fullname.split(" ");
                main.getStudentList().add(new Student(name[0], name[1], name[2], group, priority, averageGrade));
            }

            myExcelBook.close();
        }
    }

    /**
     * Записываем полученные по профилям группы в книгу Excel.
     *
     * @param file
     * @throws IOException
     */
    private void writeIntoExcel(File file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeSheets(workbook, main.getStudentList());

        if(file != null) {
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.write(outFile);
            outFile.close();
        }
    }

    private void makeSheets(XSSFWorkbook workbook, ObservableList<Student> list) {
        XSSFSheet sheet = workbook.createSheet("Лист 1");

        int rownum = 0;
        Cell cell;
        Row row;

        row = sheet.createRow(rownum++);

        cell = row.createCell(0);
        cell.setCellValue("№");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

        cell = row.createCell(1);
        cell.setCellValue("Фамилия Имя Отчество");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));

        cell = row.createCell(2);
        cell.setCellValue("Группа");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

        cell = row.createCell(3);
        cell.setCellValue("Средний балл");
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));

        cell = row.createCell(4);
        cell.setCellValue("№ профиля");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));

        row = sheet.createRow(rownum++);

        cell = row.createCell(4);
        cell.setCellValue("1-й приоритет");
        cell = row.createCell(5);
        cell.setCellValue("2-й приоритет");
        cell = row.createCell(6);
        cell.setCellValue("3-й приоритет");

        int counter = 0;

        for (Student student : list) {
            row = sheet.createRow(rownum++);

            cell = row.createCell(0);
            cell.setCellValue((counter++) + 1);
            cell = row.createCell(1);
            cell.setCellValue(student.getFullname());
            cell = row.createCell(2);
            cell.setCellValue(student.getGroup());
            cell = row.createCell(3);
            cell.setCellValue(student.getAverageGrade());
            cell = row.createCell(4);
            cell.setCellValue(student.getPriority().get(0));
            cell = row.createCell(5);
            cell.setCellValue(student.getPriority().get(1));
            cell = row.createCell(6);
            cell.setCellValue(student.getPriority().get(2));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    @FXML
    private void handleMergeWithFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));

        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            errorLabel.setText("");
            try {
                readFromExcel(files);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteStudent() {
        int selectedItem = studentTableView.getSelectionModel().getSelectedIndex();
        if (selectedItem >= 0) {
            boolean answer = confirmWindow.showConfirmWindow("Confirm remove selected person",
                    "Are you sure you want to remove selected item?");
            if (answer) {
                studentTableView.getItems().remove(selectedItem);
                statusLabel.setText("Elements in table: " + studentTableView.getItems().size());
            }
        } else {
            errorLabel.setText("No student selected!");
        }
    }

    @FXML
    private void handleAddStudent() {
        Student tempStudent = new Student();
        boolean okClicked = main.showStudentEditDialog(tempStudent);
        if (okClicked) {
            main.getStudentList().add(tempStudent);
            statusLabel.setText("Elements in table: " + studentTableView.getItems().size());
        }
    }

    @FXML
    private void handleEditStudent() {
        Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            main.showStudentEditDialog(selectedStudent);
        } else {
            errorLabel.setText("No student selected!");
        }
    }

    @FXML
    private void handleAboutProgram() {
        showAboutWindow();
    }

    private void showAboutWindow() {
        Stage stage = new Stage();
        stage.setTitle("About Program");
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        Label lbl1 = new Label("Developed by");
        Label lbl2 = new Label("group IKPI-61");
        Label lbl3 = new Label("in 2019");
        Button btnOk = new Button("OK");
        btnOk.setOnAction(event -> stage.close());
        vbox.getChildren().addAll(lbl1, lbl2, lbl3, btnOk);
        Scene scene = new Scene(vbox, 250, 140);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(main.getPrimaryStage());
        stage.showAndWait();
    }

    @FXML
    private void handleExitProgram() {
        showConfirmExitWindow();
    }

    public void showConfirmExitWindow() {
        boolean answer = confirmWindow.showConfirmWindow("Confirm exit",
                "Are you sure you want to exit the program without saving?");
        if (answer) {
            System.exit(1);
        }
    }

    @FXML
    private void handleComputeButton() {
        if(!main.getStudentList().isEmpty()) {
            profiling.getStudentList().removeAll(profiling.getStudentList());
            profiling.getStudentList().addAll(main.getStudentList());
            isSaved = false;
            for(int i = 0; i < profiling.getProfilesNumber(); i++) {
                if(profiling.getProfileQuota().get(i) != 0) {
                    profiling.formingGroup();
                }
            }
        } else {
            errorLabel.setText("Error: Files not open!");
        }

        profiling.formingSubgroup();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        try {
            writeProfilingIntoExcel(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
