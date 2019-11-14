import java.util.ArrayList;
import java.util.stream.Collectors;

public class Profiling {
    private int profilesNumber; // Количество профилей
    private ArrayList<Integer> profileCount = new ArrayList<>(); // Счетчик для нахождения наиболее приоритетного профиля
    private ArrayList<Integer> profileQuota = new ArrayList<>();
    private ArrayList<Integer> subgroupProfileQuota = new ArrayList<>();
    private ArrayList<Student> studentList = new ArrayList<>(); // Список студентов-контрактников
    private ArrayList<String> profilesNameList = new ArrayList<>(); // Список названий профилей
    private ArrayList<ArrayList<Student>> groupList = new ArrayList<>();
    //private ArrayList<Student> notDistributed = new ArrayList<>();
    private ArrayList<ArrayList<Student>> subgroupList = new ArrayList<>(); // Список подгрупп студентов по профилям
    private ArrayList<Student> groupRTS = new ArrayList<>(); // Список студентов, желающих перевода на РТС
    private Main main;

    /**
     * Конструктор по умолчанию.
     */
    public Profiling() {
        this(4);
    }

    /**
     * Конструктор с параметрами.
     *
     * @param profilesNumber количество профилей.
     */
    public Profiling(int profilesNumber) {
        this.profilesNumber = profilesNumber;
    }

    public int getProfilesNumber() {
        return profilesNumber;
    }

    public void setProfilesNumber(int profilesNumber) {
        this.profilesNumber = profilesNumber;
    }

    public ArrayList<String> getProfilesNameList() {
        return profilesNameList;
    }

    public void setProfilesNameList(ArrayList<String> profilesNameList) {
        this.profilesNameList = profilesNameList;
    }

    public ArrayList<Integer> getProfileCount() {
        return profileCount;
    }

    public void setProfileCount(ArrayList<Integer> profileCount) {
        this.profileCount = profileCount;
    }

    public ArrayList<Integer> getProfileQuota() {
        return profileQuota;
    }

    public void setProfileQuota(ArrayList<Integer> profileQuota) {
        this.profileQuota = profileQuota;
    }

    public ArrayList<Integer> getSubgroupProfileQuota() {
        return subgroupProfileQuota;
    }

    public void setSubgroupProfileQuota(ArrayList<Integer> subgroupProfileQuota) {
        this.subgroupProfileQuota = subgroupProfileQuota;
    }

    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    public ArrayList<ArrayList<Student>> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<ArrayList<Student>> groupList) {
        this.groupList = groupList;
    }

    public void setSubgroupList(ArrayList<ArrayList<Student>> subgroupList) {
        this.subgroupList = subgroupList;
    }

    public void setGroupRTS(ArrayList<Student> groupRTS) {
        this.groupRTS = groupRTS;
    }

    public Main getMain() {
        return main;
    }

    public ArrayList<ArrayList<Student>> getSubgroupList() {
        return subgroupList;
    }

    public ArrayList<Student> getGroupRTS() {
        return groupRTS;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Находим наиболее приоритетный профиль среди студентов.
     * Создаем список студентов, желающих на РТС.
     *
     * @return индекс наиболее приоритетного профиля.
     */
    private int findCommonProfileOfFirstPriority() {
        profileCount.removeAll(profileCount);
        for(int i = 0; i < profilesNumber; i++) {
            profileCount.add(0);
        }
        for(Student student : studentList) {
            switch (student.getPriority().get(0)) {
                case 1:
                    //profileCount.set(0, profileCount.get(0) + 1);
                    groupRTS.add(student);
                    break;
                case 2:
                    //profileCount.set(1, profileCount.get(1) + 1);
                    groupRTS.add(student);
                    break;
                case 3:
                    profileCount.set(2, profileCount.get(2) + 1);
                    break;
                case 4:
                    profileCount.set(3, profileCount.get(3) + 1);
                    break;
                case 5:
                    profileCount.set(4, profileCount.get(4) + 1);
                    break;
                case 6:
                    profileCount.set(5, profileCount.get(5) + 1);
                    break;
                default:
                    break;
            }
        }

        studentList.removeAll(groupRTS);
        groupRTS = groupRTS.stream().sorted((s1, s2) -> s1.getFullname().compareToIgnoreCase(s2.getFullname())).collect(Collectors.toCollection(ArrayList::new));

        return indexOfMaxValueOfArray(profileCount, profilesNumber);
    }

    /**
     * Находим индекс максимального числа в массиве.
     *
     * @param array массив чисел типа Integer.
     * @param size размер массива.
     * @return индекс максимального числа в массиве.
     */
    private int indexOfMaxValueOfArray(ArrayList<Integer> array, int size) {
        int maxI = 0;
        int max = array.get(0);
        for(int i = 1; i < size; i++) {
            if(max < array.get(i)) {
                max = array.get(i);
                maxI = i;
            }
        }
        return maxI;
    }

    /**
     * Формирование групп по профилям.
     * Изменение списка профилей для каждого студента.
     */
    public void formingGroup() {
        int commonProfile = findCommonProfileOfFirstPriority();
        ArrayList<Student> group = new ArrayList<>();

        studentList = studentList.stream().sorted((s1, s2) -> Double.compare(s2.getAverageGrade(), s1.getAverageGrade())).collect(Collectors.toCollection(ArrayList::new));

        for(Student student : studentList) {
            if(student.getPriority().get(0) == (commonProfile + 1)) {
                if(group.size() < profileQuota.get(commonProfile)) {
                    group.add(student);
                }
                else {
                    ArrayList<Integer> tempPriority = student.getPriority();
                    tempPriority.remove(0);
                    tempPriority.add(0);

                    student.setPriority(tempPriority);
                }
            }
            if(student.getPriority().get(1) == (commonProfile + 1)) {
                ArrayList<Integer> tempPriority = student.getPriority();
                tempPriority.remove(1);
                tempPriority.add(0);

                student.setPriority(tempPriority);
            }
            if(student.getPriority().get(2) == (commonProfile + 1)) {
                ArrayList<Integer> tempPriority = student.getPriority();
                tempPriority.remove(2);
                tempPriority.add(0);

                student.setPriority(tempPriority);
            }
        }

        studentList.removeAll(group);
        groupList.add(group);
    }

    /**
     * Формирование групп внутри одного профиля.
     */
    public void formingSubgroup() {
        for(int i = 0; i < groupList.size(); i++) {
            ArrayList<Student> subgroup1 = new ArrayList<>();
            ArrayList<Student> subgroup2 = new ArrayList<>();

            groupList.set(i, groupList.get(i).stream().sorted((s1, s2) -> s1.getGroup().compareToIgnoreCase(s2.getGroup())).collect(Collectors.toCollection(ArrayList::new)));
            //groupList.get(i) = Collections.list(groupList.get(i).stream().sorted((s1, s2) -> s1.getGroup().compareToIgnoreCase(s2.getGroup()))).collect(Collectors.toList());

            for(Student student : groupList.get(i)) {
                if((groupList.get(i).size()+1)/2 <= findMin(subgroupProfileQuota.get(student.getPriority().get(0)*2-2),
                        subgroupProfileQuota.get(student.getPriority().get(0)*2-1))) {
                    if(subgroup1.size() < (groupList.get(i).size()+1)/2) {
                        subgroup1.add(student);
                    } else {
                        subgroup2.add(student);
                    }
                } else {
                    if(subgroup1.size() < findMin(subgroupProfileQuota.get(student.getPriority().get(0)*2-2),
                            subgroupProfileQuota.get(student.getPriority().get(0)*2-1))) {
                        subgroup1.add(student);
                    } else {
                        subgroup2.add(student);
                    }
                }
            }

            subgroup1 = subgroup1.stream().sorted((s1, s2) -> s1.getFullname().compareToIgnoreCase(s2.getFullname())).collect(Collectors.toCollection(ArrayList::new));
            subgroup2 = subgroup2.stream().sorted((s1, s2) -> s1.getFullname().compareToIgnoreCase(s2.getFullname())).collect(Collectors.toCollection(ArrayList::new));

            subgroupList.add(subgroup1);
            subgroupList.add(subgroup2);
        }
    }

    private int findMin(int a, int b) {
        return (a < b) ? a : b;
    }
}
