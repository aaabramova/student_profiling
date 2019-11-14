import javafx.beans.property.*;

import java.util.ArrayList;

public class Student {
    private StringProperty surname;
    private StringProperty name;
    private StringProperty patronymic;
    private StringProperty group;
    private DoubleProperty averageGrade;

    public Student() {
        this(null, null, null, null, 0);
    }

    public Student(String surname, String name, String patronymic, String group, double averageGrade) {
        this.surname = new SimpleStringProperty(surname);
        this.name = new SimpleStringProperty(name);
        this.patronymic = new SimpleStringProperty(patronymic);
        this.group = new SimpleStringProperty(group);
        this.averageGrade = new SimpleDoubleProperty(averageGrade);
    }

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPatronymic() {
        return patronymic.get();
    }

    public StringProperty patronymicProperty() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic.set(patronymic);
    }

    public String getGroup() {
        return group.get();
    }

    public StringProperty groupProperty() {
        return group;
    }

    public void setGroup(String group) {
        this.group.set(group);
    }

    public double getAverageGrade() {
        return averageGrade.get();
    }

    public DoubleProperty averageGradeProperty() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade.set(averageGrade);
    }

    public String getFullname() {
        return this.surname.get() + " " + this.name.get() + " " + this.patronymic.get();
    }
}
