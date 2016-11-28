package publicinfo;

/**
 * Created by Tyhj on 2016/11/28.
 */

public class StudentInfo {
    private static String stu_num,college,major,phone,grade,clas,id;

    public StudentInfo(String stu_num,String college,String major,String phone,String grade,String clas,String id) {
        this.stu_num=stu_num;
        this.college=college;
        this.major=major;
        this.phone=phone;
        this.grade=grade;
        this.clas=clas;
        this.id=id;
    }

    public static void setStudent(){

    }


    public static String getStu_num() {
        return stu_num;
    }

    public static void setStu_num(String stu_num) {
        StudentInfo.stu_num = stu_num;
    }

    public static String getCollege() {
        return college;
    }

    public static void setCollege(String college) {
        StudentInfo.college = college;
    }

    public static String getMajor() {
        return major;
    }

    public static void setMajor(String major) {
        StudentInfo.major = major;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        StudentInfo.phone = phone;
    }

    public static String getGrade() {
        return grade;
    }

    public static void setGrade(String grade) {
        StudentInfo.grade = grade;
    }

    public static String getClas() {
        return clas;
    }

    public static void setClas(String clas) {
        StudentInfo.clas = clas;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        StudentInfo.id = id;
    }
}
