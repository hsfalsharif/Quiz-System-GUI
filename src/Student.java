public class Student {
    private String ID;
    private String password;
    private int correctAnswers;
    private int incorrectAnswers;
    public Student(String ID,String password , int correctAnswers , int incorrectAnswers) {
        this.ID = ID;
        this.password = password;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
    }
    public String toString() {
        return "ID: " + ID + "\nPassword: " + password + "\n";
    }
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (this.getClass() != obj.getClass())
            return false;
        else {
            Student that = (Student) obj;
            return (this.ID.equals(that.ID) && this.password.equals(that.password));
        }
    }
    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }
    public String getID() {
        return ID;
    }
    public String getPassword() {
        return password;
    }
    public void giveMark() {
        correctAnswers++;
    }
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    public double getCorrectAnswerPercentage() {
        if (correctAnswers == 0 && incorrectAnswers == 0)
            return 0;
        return (double) correctAnswers/(incorrectAnswers + correctAnswers) * 100;
    }
    public void incrementIncorrectAnswers() {
        incorrectAnswers++;
    }
}