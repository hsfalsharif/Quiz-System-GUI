import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
public class QuizSystem extends JFrame implements ActionListener {
    private JLabel userLabel = new JLabel("User: ");
    private JLabel passLabel = new JLabel("Password: ");
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JPanel entryPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JPanel outerPanel = new JPanel();
    private JLabel enterData = new JLabel("Enter Credentials: ");
    private JButton login = new JButton("Login");
    private JButton clear = new JButton("Clear");
    private JButton exit = new JButton("Exit");
    private Student[] studentArray = generateStudentArray();
    private Student testingStudent;
    private QuizMenu qm;
    private AdminScreen as;
    public QuizSystem() throws FileNotFoundException{
        super("ICS-201 Online Quiz System");
        setSize(400,200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,1));
        add(enterData);
        enterData.setHorizontalAlignment(SwingConstants.CENTER);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outerPanel.setLayout(new FlowLayout());
        entryPanel.setLayout(new GridLayout(2,2));
        entryPanel.add(userLabel);
        entryPanel.add(userField);
        entryPanel.add(passLabel);
        entryPanel.add(passField);
        outerPanel.add(entryPanel);
        buttonPanel.setLayout(new FlowLayout());
        login.addActionListener(this);
        clear.addActionListener(this);
        exit.addActionListener(this);
        buttonPanel.add(login);
        buttonPanel.add(clear);
        buttonPanel.add(exit);
        add(outerPanel);
        add(buttonPanel);
        for (int i = 0 ; i < studentArray.length ; i++)
            System.out.println(studentArray[i].toString());
        setVisible(true);
    }
    public void actionPerformed(ActionEvent ae)  {
        if (ae.getSource() == login) {
            for (int i = 0 ; i < studentArray.length ; i++) {
                if (userField.getText().equals(studentArray[i].getID()) && passField.getText().equals(studentArray[i].getPassword())) {
                    testingStudent = studentArray[i];
                    if (testingStudent.getCorrectAnswers() + testingStudent.getIncorrectAnswers() != 0) {
                        enterData.setText("Login Unsuccessful: Student has already taken the quiz");
                        i = studentArray.length;
                    }
                    else {
                        try {
                            qm = new QuizMenu();
                            this.dispose();
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                else if (userField.getText().equals("admin") && passField.getText().equals("iamtheadmin")) {
                    as = new AdminScreen();
                    this.dispose();
                    i = studentArray.length;
                }
                else {
                    enterData.setText("Login unsuccessful: Invalid Username/Password");
                }
            }
        }
        if(ae.getSource() == exit)
            System.exit(1);
        if(ae.getSource() == clear) {
            userField.setText("");
            passField.setText("");
            enterData.setText("Enter Credentials: ");

        }
    }
    private Student[] generateStudentArray() throws FileNotFoundException {
        Scanner fScan = new Scanner(new FileInputStream("password.txt"));
        int numberOfLines = 0;
        while (fScan.hasNextLine() && !fScan.nextLine().trim().equals(""))
            numberOfLines++;
        Student[] studentArray = new Student[numberOfLines];
        fScan.close();
        fScan = new Scanner(new FileInputStream("password.txt"));
        for (int i = 0; fScan.hasNextLine() ; i++) {
            String line = fScan.nextLine();
            Scanner lineScanner = new Scanner(line);
            studentArray[i] = new Student(lineScanner.next() , lineScanner.next() , Integer.parseInt(lineScanner.next()) , Integer.parseInt(lineScanner.next()));
            lineScanner.close();
        }
        fScan.close();
        return studentArray;
    }
    private class QuizMenu extends JFrame implements ActionListener{
        private Scanner qScan;
        private PrintWriter fileUpdater;
        private int correctAnswer;
        private boolean selected;
        private int studentAnswer;
        private String option;
        private JTextArea questionArea = new JTextArea(2,30);
        private JRadioButton[] optionArray = {new JRadioButton(),new JRadioButton(),new JRadioButton(),new JRadioButton()};
        private ButtonGroup bGroup = new ButtonGroup();
        private JPanel radioPanel = new JPanel();
        private JPanel questionPanel = new JPanel();
        private JPanel nextQuestionPanel = new JPanel();
        private JButton nextQuestion = new JButton("Next Question");
        private QuizResult qr;
        private QuizMenu() throws FileNotFoundException {
            super("Quiz----Student Screen");
            qScan = new Scanner(new FileInputStream("questions.txt"));
            questionArea.setEditable(false);
            setSize(400,350);
            setLayout(new GridLayout(3,1));
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            radioPanel.setLayout(new GridLayout(4,1));
            questionPanel.setLayout(new FlowLayout());
            nextQuestion.setLayout(new FlowLayout());
            questionPanel.add(questionArea);
            nextQuestionPanel.add(nextQuestion);
            for (int i = 0 ; i < 4 ; i++) {
                bGroup.add(optionArray[i]);
                radioPanel.add(optionArray[i]);
            }
            add(questionPanel);
            add(radioPanel);
            add(nextQuestionPanel);
            nextQuestion.addActionListener(this);
            questionArea.setText(qScan.nextLine());
            for (int i = 0; i < 4 ; i++) {
                option = qScan.nextLine();
                if (option.charAt(option.length() - 1) == 'T') {
                    option = option.substring(0, option.length() - 2);
                    correctAnswer = i + 1;
                }
                optionArray[i].setText(option);
            }
            setVisible(true);
        }
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == nextQuestion) {
                for (int i = 0 ; i < 4 ; i++) {
                    selected = optionArray[i].isSelected();
                    if (selected) {
                        studentAnswer = i + 1;
                        i = 4;
                    }
                }
                if (selected) {
                    if (studentAnswer == correctAnswer)
                        testingStudent.giveMark();
                    else
                        testingStudent.incrementIncorrectAnswers();
                    if (qScan.hasNextLine()) {
                        questionArea.setText(qScan.nextLine());
                        for (int i = 0; i < 4; i++) {
                            option = qScan.nextLine();
                            if (option.charAt(option.length() - 1) == 'T') {
                                option = option.substring(0, option.length() - 2);
                                correctAnswer = i + 1;
                            }
                            optionArray[i].setText(option);
                        }
                    }
                    else {
                        qr = new QuizResult();
                        this.dispose();
                        try {
                            fileUpdater = new PrintWriter(new FileOutputStream("password.txt"));
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        for (int i = 0 ; i < studentArray.length ; i++) {
                            if (i == studentArray.length - 1)
                                fileUpdater.print(studentArray[i].getID() + "\t" + studentArray[i].getPassword() + "\t" + studentArray[i].getCorrectAnswers() + "\t" + studentArray[i].getIncorrectAnswers());
                            else
                                fileUpdater.println(studentArray[i].getID() + "\t" + studentArray[i].getPassword() + "\t" + studentArray[i].getCorrectAnswers() + "\t" + studentArray[i].getIncorrectAnswers());
                        }
                        fileUpdater.close();
                    }
                    bGroup.clearSelection();
                    selected = false;
                }
                else
                    JOptionPane.showMessageDialog(null , "Error: An answer has not been selected");
            }
        }
        private class QuizResult extends JFrame implements ActionListener {
            private JLabel idLabel = new JLabel("Student ID");
            private JLabel correctLabel = new JLabel("# of Correct Answers");
            private JLabel incorrectLabel = new JLabel("# of Incorrect Answers");
            private JLabel percentLabel = new JLabel("% of Correct Answers");
            private JButton closeButton = new JButton("Close");
            private JLabel theID = new JLabel();
            private JLabel theCorrect = new JLabel();
            private JLabel theIncorrect = new JLabel();
            private JLabel thePercent = new JLabel();
            private Border border = BorderFactory.createLineBorder(Color.BLACK);
            private JPanel dataPanel = new JPanel();
            private QuizResult() {
                super("Test Result");
                setSize(300,200);
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setLayout(new FlowLayout());
                dataPanel.setLayout(new GridLayout(4,2));
                theID.setBorder(border);
                theCorrect.setBorder(border);
                theIncorrect.setBorder(border);
                thePercent.setBorder(border);
                dataPanel.add(idLabel);
                dataPanel.add(theID);
                dataPanel.add(correctLabel);
                dataPanel.add(theCorrect);
                dataPanel.add(incorrectLabel);
                dataPanel.add(theIncorrect);
                dataPanel.add(percentLabel);
                dataPanel.add(thePercent);
                add(dataPanel);
                closeButton.addActionListener(this);
                theID.setText(testingStudent.getID());
                theCorrect.setText("" + testingStudent.getCorrectAnswers());
                theIncorrect.setText("" + testingStudent.getIncorrectAnswers());
                thePercent.setText(String.format("%.2f ", testingStudent.getCorrectAnswerPercentage()) + "%");
                add(closeButton);
                setVisible(true);
            }
            public void actionPerformed(ActionEvent ae) {
                if(ae.getSource() == closeButton)
                    System.exit(1);
            }
        }
    }
    private class AdminScreen extends JFrame implements ActionListener {
        private JMenuBar mBar = new JMenuBar();
        private JMenu quiz = new JMenu("Quiz");
        private JMenu user = new JMenu("User");
        private JMenu report = new JMenu("Report");
        private JMenuItem viewReport = new JMenuItem("View Report");
        private JMenuItem addQuestion = new JMenuItem("Add Question");
        private JMenuItem deleteQuestion = new JMenuItem("Delete Question");
        private JMenuItem addUser = new JMenuItem("Add User");
        private JMenuItem deleteUser = new JMenuItem("Delete User");
        private JMenu exitInner = new JMenu("Exit");
        private JMenuItem letsExit = new JMenuItem("Exit");
        private QuestionAddition qa;
        private QuestionDeletion qd;
        private UserAddition ua;
        private UserDeletion ud;
        private ReportScreen rs;
        private AdminScreen() {
            super("Administrator");
            setSize(500,100);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            quiz.add(addQuestion);
            quiz.add(deleteQuestion);
            mBar.add(quiz);
            user.add(addUser);
            user.add(deleteUser);
            mBar.add(user);
            report.add(viewReport);
            mBar.add(report);
            exitInner.add(letsExit);
            mBar.add(exitInner);
            addQuestion.addActionListener(this);
            deleteQuestion.addActionListener(this);
            addUser.addActionListener(this);
            deleteUser.addActionListener(this);
            viewReport.addActionListener(this);
            letsExit.addActionListener(this);
            setJMenuBar(mBar);
            setVisible(true);
        }
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == addQuestion)
                qa = new QuestionAddition();
            if (ae.getSource() == deleteQuestion)
                qd = new QuestionDeletion();
            if (ae.getSource() == addUser)
                ua = new UserAddition();
            if (ae.getSource() == deleteUser)
                ud = new UserDeletion();
            if (ae.getSource() == viewReport)
                rs = new ReportScreen();
            if (ae.getSource() == letsExit)
                System.exit(1);
        }
        private class QuestionAddition extends JFrame implements ActionListener {
            private JLabel enterQuestion = new JLabel("Enter Question: ");
            private JTextField questionField = new JTextField(15);
            private ButtonGroup bGroup2 = new ButtonGroup();
            private JButton addQuestion = new JButton("Add Question");
            private JButton chanceToLeave = new JButton("Exit");
            private JPanel additionPanel = new JPanel();
            private JPanel enterPanel = new JPanel();
            private JPanel buttonPanel = new JPanel();
            private JRadioButton[] optionArray = {new JRadioButton("Option 1"),new JRadioButton("Option 2"),new JRadioButton("Option 3"),new JRadioButton("Option 4")};
            private JTextField[] optionTextArray = {new JTextField(15) , new JTextField(15) , new JTextField(15) , new JTextField(15)};
            private PrintWriter questionWriter;
            private Scanner checkLine1;
            private boolean optionCheck = false;
            private QuestionAddition() {
                super("Add a Question");
                setSize(350,250);
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                setLayout(new FlowLayout());
                setResizable(false);
                enterPanel.setLayout(new FlowLayout());
                enterPanel.add(enterQuestion);
                enterPanel.add(questionField);
                add(enterPanel);
                additionPanel.setLayout(new GridLayout(4,2));
                for (int i = 0 ; i < 4 ; i++) {
                    bGroup2.add(optionArray[i]);
                    additionPanel.add(optionArray[i]);
                    additionPanel.add(optionTextArray[i]);
                }
                add(additionPanel);
                buttonPanel.setLayout(new FlowLayout());
                buttonPanel.add(addQuestion);
                buttonPanel.add(chanceToLeave);
                add(buttonPanel);
                addQuestion.addActionListener(this);
                chanceToLeave.addActionListener(this);
                setVisible(true);
            }
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == addQuestion) {
                    for (int i = 0 ; i < 4 ; i++) {
                        optionCheck = optionArray[i].isSelected();
                        if (optionCheck)
                            i = 4;
                    }
                    if (questionField.getText().equals(""))
                        JOptionPane.showMessageDialog(null , "Warning: Question or options not provided");
                    else if (optionTextArray[0].getText().equals("") || optionTextArray[1].getText().equals("") || optionTextArray[2].getText().equals("") || optionTextArray[3].getText().equals(""))
                        JOptionPane.showMessageDialog(null,"Warning: One or more answers not provided");
                    else if (!optionCheck)
                        JOptionPane.showMessageDialog(null , "Warning: Correct answer not selected");
                    else {
                        try {
                            questionWriter = new PrintWriter(new FileOutputStream("questions.txt" , true));
                            checkLine1 = new Scanner(new FileInputStream("questions.txt"));
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        if (checkLine1.hasNextLine()) {
                            questionWriter.println();
                            questionWriter.println(questionField.getText());
                        }
                        else {
                            questionWriter.print(questionField.getText());
                            questionWriter.println();
                        }
                        for (int i = 0 ; i < 4 ; i++) {
                            if (i == 3) {
                                if (optionArray[i].isSelected())
                                    questionWriter.print(optionTextArray[i].getText() + " T");
                                else
                                    questionWriter.print(optionTextArray[i].getText());
                            }
                            else {
                                if (optionArray[i].isSelected())
                                    questionWriter.println(optionTextArray[i].getText() + " T");
                                else
                                    questionWriter.println(optionTextArray[i].getText());
                            }
                        }
                        questionWriter.close();
                        checkLine1.close();
                        questionField.setText("");
                        for (int i = 0 ; i < 4 ; i++ )
                            optionTextArray[i].setText("");
                    }
                }
                if (ae.getSource() == chanceToLeave)
                    System.exit(1);
            }
        }
        private class UserAddition extends JFrame implements ActionListener {
            private JLabel newUser = new JLabel("New User: ");
            private JLabel passLabel = new JLabel("Password: ");
            private JLabel confirmPassLabel = new JLabel("Confirm Password: ");
            private JTextField newUserField = new JTextField(15);
            private JPasswordField passField = new JPasswordField(15);
            private JPasswordField confirmPassField = new JPasswordField(15);
            private JButton addUser = new JButton("Add User");
            private JButton clear = new JButton("Clear");
            private JButton justLeave = new JButton("Exit");
            private JPanel centrePanel = new JPanel();
            private JPanel buttonPanel = new JPanel();
            private PrintWriter userWriter;
            private Scanner checkLine1;
            private UserAddition() {
                super("Add New User");
                setSize(400,175);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                setLayout(new FlowLayout());
                centrePanel.setLayout(new GridLayout(3,2));
                centrePanel.add(newUser);
                centrePanel.add(newUserField);
                centrePanel.add(passLabel);
                centrePanel.add(passField);
                centrePanel.add(confirmPassLabel);
                centrePanel.add(confirmPassField);
                add(centrePanel);
                buttonPanel.add(addUser);
                buttonPanel.add(clear);
                buttonPanel.add(justLeave);
                add(buttonPanel);
                addUser.addActionListener(this);
                clear.addActionListener(this);
                justLeave.addActionListener(this);
                setVisible(true);
            }
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == addUser) {
                    if (newUserField.getText().equals("") || passField.getText().equals("") || confirmPassField.getText().equals(""))
                        JOptionPane.showMessageDialog(null , "Warning: New user info not entered");
                    else if (!passField.getText().equals(confirmPassField.getText()))
                        JOptionPane.showMessageDialog(null , "Warning: Password fields do not match");
                    else {
                        try {
                            userWriter = new PrintWriter(new FileOutputStream("password.txt" , true));
                            checkLine1 = new Scanner(new FileInputStream("password.txt"));
                        } catch (FileNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                        if (checkLine1.hasNextLine()) {
                            userWriter.println();
                            userWriter.print(newUserField.getText() + "\t" + passField.getText() + "\t0\t0" );
                        }
                        else
                            userWriter.print(newUserField.getText() + "\t" + passField.getText() + "\t0\t0");
                        userWriter.close();
                        checkLine1.close();
                    }
                    this.dispose();
                }
                if (ae.getSource() == clear) {
                    newUserField.setText("");
                    passField.setText("");
                    confirmPassField.setText("");
                }
                if (ae.getSource() == justLeave)
                    System.exit(1);
            }
        }
        private class QuestionDeletion extends JFrame implements ActionListener{
            private JLabel selectQuestion = new JLabel("Select Question: ");
            private JComboBox questions;
            private JButton deleteQuestion = new JButton("Delete Question");
            private Scanner qScanToArray;
            private PrintWriter updateQuestions;
            private ArrayList<String> questionArray = new ArrayList<>();
            private ArrayList<String> totalArray = new ArrayList<>();
            private String theLine;
            private QuestionDeletion() {
                super("Remove a Question");
                try {
                    qScanToArray = new Scanner(new FileInputStream("questions.txt"));
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                for (int i = 0; qScanToArray.hasNextLine(); i++) {
                    theLine = qScanToArray.nextLine();
                    totalArray.add(theLine);
                    if (i % 5 == 0)
                        questionArray.add(theLine);
                }
                qScanToArray.close();
                questions = new JComboBox(questionArray.toArray());
                setSize(500,150);
                setLayout(new FlowLayout());
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                add(selectQuestion);
                add(questions);
                add(deleteQuestion);
                deleteQuestion.addActionListener(this);
                setVisible(true);
            }
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == deleteQuestion) {
                    for (int i = 0 ; i < totalArray.size() ; i++)
                        if (totalArray.get(i).equals(questions.getSelectedItem())) {
                            totalArray.remove(i);
                            totalArray.remove(i);
                            totalArray.remove(i);
                            totalArray.remove(i);
                            totalArray.remove(i);
                            i = totalArray.size();
                        }
                    try {
                        updateQuestions = new PrintWriter(new FileOutputStream("questions.txt"));
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    for (int i = 0; i < totalArray.size(); i++) {
                        if (i == totalArray.size() - 1)
                            updateQuestions.print(totalArray.get(i));
                        else
                            updateQuestions.println(totalArray.get(i));
                    }
                    updateQuestions.close();
                    this.dispose();
                }
            }
        }
        private class UserDeletion extends JFrame implements ActionListener{
            private JLabel selectUser = new JLabel("Select User");
            private JComboBox users;
            private JButton deleteUser = new JButton("Delete User");
            private Scanner uScanToArray;
            private String theLine;
            private Scanner lineScanner;
            private PrintWriter updateUsers;
            private ArrayList<String> userArray = new ArrayList<>();
            private ArrayList<String> totalUserDataArray = new ArrayList<>();
            private UserDeletion() {
                super("Remove a User");
                setSize(500,150);
                setLayout(new FlowLayout());
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                try {
                    uScanToArray = new Scanner(new FileInputStream("password.txt"));
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                for (int i = 0 ; uScanToArray.hasNextLine() ; i++) {
                    theLine = uScanToArray.nextLine();
                    totalUserDataArray.add(theLine);
                    lineScanner = new Scanner(theLine);
                    userArray.add(lineScanner.next());
                }
                uScanToArray.close();
                lineScanner.close();
                users = new JComboBox(userArray.toArray());
                add(selectUser);
                add(users);
                add(deleteUser);
                deleteUser.addActionListener(this);
                setVisible(true);
            }
            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == deleteUser) {
                    for (int i = 0; i < totalUserDataArray.size() ; i++) {
                        lineScanner = new Scanner(totalUserDataArray.get(i));
                        if (lineScanner.next().equals(users.getSelectedItem())) {
                            totalUserDataArray.remove(i);
                            i = totalUserDataArray.size();
                        }
                    }
                    try {
                        updateUsers = new PrintWriter(new FileOutputStream("password.txt"));
                    } catch (FileNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    for (int i = 0 ; i < totalUserDataArray.size() ; i++) {
                        if (i == totalUserDataArray.size() - 1)
                            updateUsers.print(totalUserDataArray.get(i));
                        else
                            updateUsers.println(totalUserDataArray.get(i));
                    }
                    updateUsers.close();
                    this.dispose();
                }
            }
        }
        private class ReportScreen extends JFrame{
            private JTextArea reportArea = new JTextArea();
            private Student[] students;
            private ReportScreen() {
                super("Student Report");
                setSize(500,500);
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                reportArea.setEditable(false);
                add(reportArea);
                reportArea.setText(String.format("%20s" ,"ID\tCORRECT\tWRONG\tPERCENTAGE\n\n"));
                try {
                    students = generateStudentArray();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                for (int i = 0; i < students.length ; i++) {
                    reportArea.setText(reportArea.getText() + String.format("%20s\n" , studentArray[i].getID() + "\t" + studentArray[i].getCorrectAnswers() + "\t" + studentArray[i].getIncorrectAnswers() + "\t" + studentArray[i].getCorrectAnswerPercentage()));
                }
                setVisible(true);
        }
    }
}
    public static void main(String[] args) {
        try {
            QuizSystem qSystem = new QuizSystem();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.exit(1);
        }
    }
}