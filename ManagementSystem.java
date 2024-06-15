import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Custom ObjectOutputStream to avoid header issues when appending
class MyObjectOutputStream extends ObjectOutputStream {
    public MyObjectOutputStream() throws IOException {
        super();
    }

    public MyObjectOutputStream(OutputStream o) throws IOException {
        super(o);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        reset();
    }
}

// Base Person class
abstract class Person implements Serializable {
    protected String name;
    protected String address;

    public Person() {}

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}

// Employee class inheriting Person
class Employee extends Person {
    protected double salary;

    public Employee() {}

    public Employee(String name, String address, double salary) {
        super(name, address);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void paySalary() {
        System.out.println(name + " has been paid " + salary);
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', address='" + address + "', salary=" + salary + '}';
    }
}

// Admin class inheriting Employee
class Admin extends Employee {
    public Admin() {}

    public Admin(String name, String address, double salary) {
        super(name, address, salary);
    }

    public void addEmployee(CarRentalManagementSystem system, String name, String address, double salary) {
        Employee employee = new Employee(name, address, salary);
        system.addEmployee(employee);
        System.out.println("Employee added successfully!");
    }

    public void viewAllCars(CarRentalManagementSystem system) {
        system.displayAllCars();
    }

    public void viewAllBookings(CarRentalManagementSystem system) {
        system.displayAllBookings();
    }

    public void viewAllEmployees(CarRentalManagementSystem system) {
        system.displayAllEmployees();
    }

}

// Mechanic class inheriting Employee
class Mechanic extends Employee {
    public Mechanic() {}

    public Mechanic(String name, String address, double salary) {
        super(name, address, salary);
    }
}

// SecurityGuard class inheriting Employee
class SecurityGuard extends Employee {
    public SecurityGuard() {}

    public SecurityGuard(String name, String address, double salary) {
        super(name, address, salary);
    }
}

// Customer class inheriting Person
class Customer extends Person {
    public Customer() {}

    public Customer(String name, String address) {
        super(name, address);
    }

    @Override
    public String toString() {
        return "Customer{name='" + name + "', address='" + address + "'}";
    }
}

// Car class
class Car implements Serializable {
    private String model;
    private String registrationNumber;
    private double rentalCost;
    private boolean isAvailable;

    public Car() {}

    public Car(String model, String registrationNumber, double rentalCost) {
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.rentalCost = rentalCost;
        this.isAvailable = true;
    }

    public String getModel() {
        return model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public double getRentalCost() {
        return rentalCost;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Car{model='" + model + "', registrationNumber='" + registrationNumber + "', rentalCost=" + rentalCost + ", isAvailable=" + isAvailable + '}';
    }
}

// Order class
class Order implements Serializable {
    private Car car;
    private Customer customer;
    private String startDate;
    private String endDate;

    public Order() {}

    public Order(Car car, Customer customer, String startDate, String endDate) {
        this.car = car;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "Order{car=" + car + ", customer=" + customer + ", startDate='" + startDate + "', endDate='" + endDate + "'}";
    }
}

// CarRentalManagementSystem class
 class CarRentalManagementSystem {
    private static final int MAX_CARS = 10;
    private static final int MAX_BOOKINGS = 10;


    private List<Car> cars = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private static Scanner scanner = new Scanner(System.in);

    public CarRentalManagementSystem() {
        cars = readCarsFromFile();
        employees = readEmployeesFromFile();
        orders = readOrdersFromFile();
    }

    // File handling for cars
    public void addCar(String model, String registrationNumber, double rentalCost) {
        Car car = new Car(model, registrationNumber, rentalCost);
        if (findCar(registrationNumber) == null) {
            if (cars.size() < MAX_CARS) {
                cars.add(car);
                writeToFile("cars.ser", car);
                System.out.println("Car added successfully!");
            } else {
                System.out.println("Cannot add more cars. Car limit reached.");
            }
        } else {
            System.out.println("License plate already exists. Please enter a different license plate.");
        }
    }

    public String displayAvailableCars() {

        StringBuilder sb = new StringBuilder();
        sb.append("Available Cars:\n");

        boolean noAvailableCars = true;
        
        for (Car car : cars) {
            if (car.isAvailable()) {
                noAvailableCars = false;
                sb.append(car).append("\n");
            }
        }

        if (noAvailableCars) {
            sb.append("No cars available for booking.");
        }
        return sb.toString();
    }

    public String displayAllCars() {
        StringBuilder sb = new StringBuilder();
        sb.append("All Cars:\n");
        for (Car car : cars) {
            sb.append(car).append("\n");
        }
        return sb.toString();
    }

    public String displayAllBookings() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("All Bookings:\n");
        for (Order order : orders) {
            sb.append(order).append("\n");
        }
        return sb.toString();
    }

    public String displayAllEmployees() {
        StringBuilder sb = new StringBuilder();
        sb.append("All Employees:\n");
        for (Employee employee : employees) {
            sb.append(employee).append("\n");
        }
        return sb.toString();
    }


    public double calculateTotalRentalCost(String registrationNumber, String startDate, String endDate) {
        Car car = findCar(registrationNumber);

        if (car != null && car.isAvailable()) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                long numberOfDays = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                return car.getRentalCost() * numberOfDays;
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                return -1;
            }
        } else {
            System.out.println("License plate not available for booking or not found.");
            return -1;
        }
    }

    public void bookCar(String customerName, String registrationNumber, String startDate, String endDate) {
        if (orders.size() < MAX_BOOKINGS) {
            Car car = findCar(registrationNumber);

            if (car != null && car.isAvailable()) {
                double totalRentalCost = calculateTotalRentalCost(registrationNumber, startDate, endDate);
                if (totalRentalCost != -1) {
                    Customer customer = new Customer(customerName, "");
                    Order order = new Order(car, customer, startDate, endDate);
                    orders.add(order);
                    car.setAvailable(false);
                    writeToFile("orders.ser", order);
                    updateCarFile();
                    System.out.println("Booking successful! Total rental cost: $" + totalRentalCost);
                } else {
                    System.out.println("Booking failed. Please check the license plate and date information.");
                }
            } else {
                System.out.println("License plate not available for booking or not found.");
            }
        } else {
            System.out.println("Cannot book more cars. Booking limit reached.");
        }
    }

    // File handling methods
    public void writeToFile(String filename, Serializable obj) {
        try{
            File myfile = new File(filename);
            ObjectOutputStream oos;
            if(myfile.exists()){
                oos = new MyObjectOutputStream(new FileOutputStream(myfile,true));
            }else{
                oos = new ObjectOutputStream(new FileOutputStream(myfile));
            }
            oos.writeObject(obj);
            oos.close();
        }
        catch(IOException e){
            System.out.println("There is an issue with writing to the file");
        }
        
    }

    public void updateCarFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cars.ser"));
            for (Car car : cars) {
                oos.writeObject(car);
            }
            oos.close();
        } catch (IOException e) {
            System.out.println("Error updating car file: " + e.getMessage());
        }
    }

    public void updateEmployeeFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("employees.ser"));
            for (Employee employee : employees) {
                oos.writeObject(employee);
            }
            oos.close();
        } catch (IOException e) {
            System.out.println("Error updating employee file: " + e.getMessage());
        }
    }

    public List<Car> readCarsFromFile() {
        List<Car> carList = new ArrayList<>();
        File file = new File("cars.ser");
        if (!file.exists()) {
            System.out.println("Car file not found.");
            return carList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Car car = (Car) ois.readObject();
                    carList.add(car);
                } catch (EOFException e) {
                    break; // End of file reached
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return carList;
    }

    public List<Employee> readEmployeesFromFile() {
        List<Employee> employeeList = new ArrayList<>();
        File file = new File("employees.ser");
        if (!file.exists()) {
            System.out.println("Employee file not found.");
            return employeeList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Employee employee = (Employee) ois.readObject();
                    employeeList.add(employee);
                } catch (EOFException e) {
                    break; // End of file reached
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return employeeList;
    }

    public List<Order> readOrdersFromFile() {
        List<Order> orderList = new ArrayList<>();
        File file = new File("orders.ser");
        if (!file.exists()) {
            System.out.println("Order file not found.");
            return orderList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Order order = (Order) ois.readObject();
                    orderList.add(order);
                } catch (EOFException e) {
                    break; // End of file reached
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        return orderList;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        writeToFile("employees.ser", employee);
    }

    private Car findCar(String registrationNumber) {
        for (Car car : cars) {
            if (car.getRegistrationNumber().equals(registrationNumber)) {
                return car;
            }
        }
        return null;
    }

    public Employee findEmployee(String name) {
        for (Employee employee : employees) {
            if (employee.getName().equals(name)) {
                return employee;
            }
        }
        return null;
    }

}
class mainWindow extends JFrame{
        JButton b1,b2,b3;
        mainWindow(){
        setTitle("Car Rental System");
        // setSize(JFrame);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        b1 = new JButton("Customer");
        b2 = new JButton("Admin");
        b3 = new JButton("Exit");
        setLayout(new GridLayout());
        MyActionListner a = new MyActionListner();
        b1 .addActionListener(a);
        b2 .addActionListener(a);
        b3 .addActionListener(a);
        add(b1);
        add(b2);
        add(b3);
        }

        public class MyActionListner implements ActionListener{
            public void actionPerformed(ActionEvent ae){
                if(ae.getActionCommand().equals("Admin")){
                    dispose();
                    logingWindow w1 = new logingWindow();
            }else if(ae.getActionCommand().equals("Customer")){
                dispose();
                CustomerMenuWindow cmw = new CustomerMenuWindow();
            }else if(ae.getActionCommand().equals("Exit")){
                System.exit(0);
            }
        }
    }
}

class CustomerMenuWindow extends JFrame{
    JButton b1, b2,b3;
    CustomerMenuWindow(){
        setTitle("Car Rental System Customer Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        b1 = new JButton("View Available Cars");
        b2 = new JButton("Book Car");
        b3 = new JButton("Back");
        setLayout(new GridLayout());
        MyActionListener a = new MyActionListener();
        b1 .addActionListener(a);
        b2 .addActionListener(a);
        b3 .addActionListener(a);
        add(b1);
        add(b2);
        add(b3);
    }

    class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            CarRentalManagementSystem system = new CarRentalManagementSystem();
            if (ae.getActionCommand().equals("View Available Cars")) {
                new DisplayAvailableCarsWindow();
            } else if (ae.getActionCommand().equals("Book Car")) {
                String customerName = JOptionPane.showInputDialog("Enter your name:");
                String carRegistrationNumber = JOptionPane.showInputDialog("Enter car registration number:");
                String startDate = JOptionPane.showInputDialog("Enter the booking start date in (yyyy-mm-dd) formate:");
                String endDate = JOptionPane.showInputDialog("Enter the returning date in (yyyy-mm-dd) formate:");
    
                system.bookCar(customerName, carRegistrationNumber, startDate, endDate);
                JOptionPane.showMessageDialog(CustomerMenuWindow.this, "Car booked successfully with total cost of "+system.calculateTotalRentalCost(carRegistrationNumber, startDate, endDate), "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (ae.getActionCommand().equals("Back")) {
                dispose();
                new mainWindow();
            }
        }
    }

}

class AdminMenuWindow extends JFrame{
        JButton b1,b2,b3,b4,b5,b7;
        AdminMenuWindow(){
        setTitle("Car Rental System Admin menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        b1 = new JButton("Add Car");
        b2 = new JButton("View All Car");
        b3 = new JButton("View All Bookings");
        b4 = new JButton("Add Employee");
        b5 = new JButton("View All Employees");
        b7 = new JButton("Logout");
        setLayout(new GridLayout());
        MyActionListner a = new MyActionListner();
        b1 .addActionListener(a);
        b2 .addActionListener(a);
        b3 .addActionListener(a);
        b4 .addActionListener(a);
        b5 .addActionListener(a);
        b7 .addActionListener(a);
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        
        add(b7);
        }

        class MyActionListner implements ActionListener{
            public void actionPerformed(ActionEvent ae){
                if(ae.getActionCommand().equals("View All Employees")){
                DisplayEmployeesWindow dew = new DisplayEmployeesWindow();
                }
                else if(ae.getActionCommand().equals("Add Employee")){
                AddEmployeeWindow aew = new AddEmployeeWindow();
                }
                else if(ae.getActionCommand().equals("Add Car")){
                AddCarWindow aCw = new AddCarWindow();
                }
                else if(ae.getActionCommand().equals("View All Bookings")){
                DisplayBookingsWindow dww = new DisplayBookingsWindow();
                }
                else if(ae.getActionCommand().equals("View All Car")){
                DisplayCarsWindow dww = new DisplayCarsWindow();
                }
                
                else if(ae.getActionCommand().equals("Logout")){
                mainWindow mw = new mainWindow();
                }
                
            }
    }
}

class DisplayAvailableCarsWindow extends JFrame {
    private JTextArea textArea;

    public DisplayAvailableCarsWindow() {
        setTitle("Available Cars");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        CarRentalManagementSystem system = new CarRentalManagementSystem();
        textArea.setText(system.displayAvailableCars());

        setVisible(true);
    }
}
// DisplayCarsWindow class
class DisplayCarsWindow extends JFrame {
    private JTextArea textArea;

    public DisplayCarsWindow() {
        setTitle("Available Cars");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        CarRentalManagementSystem system = new CarRentalManagementSystem();
        textArea.setText(system.displayAllCars());

        setVisible(true);
    }
}

// DisplayBookingsWindow class
class DisplayBookingsWindow extends JFrame {
    private JTextArea textArea;

    public DisplayBookingsWindow() {
        setTitle("All Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        CarRentalManagementSystem system = new CarRentalManagementSystem();
        textArea.setText(system.displayAllBookings());

        setVisible(true);
    }
}

// DisplayEmployeesWindow class
class DisplayEmployeesWindow extends JFrame {
    private JTextArea textArea;

    public DisplayEmployeesWindow() {
        setTitle("All Employees");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        CarRentalManagementSystem system = new CarRentalManagementSystem();
        textArea.setText(system.displayAllEmployees());

        setVisible(true);
    }
}

// AddCarWindow class
class AddCarWindow extends JFrame {
    private JTextField modelField;
    private JTextField registrationNumberField;
    private JTextField rentalCostField;
    private JButton addButton;

    public AddCarWindow() {
        setTitle("Add Car");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField();
        JLabel registrationNumberLabel = new JLabel("Registration Number:");
        registrationNumberField = new JTextField();
        JLabel rentalCostLabel = new JLabel("Rental Cost:");
        rentalCostField = new JTextField();
        addButton = new JButton("Add");

        add(modelLabel);
        add(modelField);
        add(registrationNumberLabel);
        add(registrationNumberField);
        add(rentalCostLabel);
        add(rentalCostField);
        add(new JLabel());
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String model = modelField.getText();
                String registrationNumber = registrationNumberField.getText();
                double rentalCost = Double.parseDouble(rentalCostField.getText());

                CarRentalManagementSystem system = new CarRentalManagementSystem();
                system.addCar(model, registrationNumber, rentalCost);

                JOptionPane.showMessageDialog(AddCarWindow.this, "Car added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        setVisible(true);
    }
}

// AddEmployeeWindow class
class AddEmployeeWindow extends JFrame {
    private JTextField nameField;
    private JTextField addressField;
    private JTextField salaryField;
    private JButton addButton;

    public AddEmployeeWindow() {
        setTitle("Add Employee");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        JLabel salaryLabel = new JLabel("Salary:");
        salaryField = new JTextField();
        addButton = new JButton("Add");

        add(nameLabel);
        add(nameField);
        add(addressLabel);
        add(addressField);
        add(salaryLabel);
        add(salaryField);
        add(new JLabel());
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String address = addressField.getText();
                double salary = Double.parseDouble(salaryField.getText());

                CarRentalManagementSystem system = new CarRentalManagementSystem();
                Employee emp = new Employee(name, address, salary);
                system.addEmployee(emp);

                JOptionPane.showMessageDialog(AddEmployeeWindow.this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });

        setVisible(true);
    }
}


class logingWindow extends JFrame{
    JLabel l;
    JTextField t;
    JButton b1 ;
    JButton b2 ;
    logingWindow(){
        setSize(404, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        l = new JLabel("Password:");
        t = new JTextField(20);
        b1 = new JButton("Login");
        b2 = new JButton("Back");
        b1.setBackground(Color.cyan);
        b2.setBackground(Color.cyan);
        setLayout(new GridLayout(2,2));
        MyActionListner a = new MyActionListner();
        b1.addActionListener(a);
        b2.addActionListener(a);
        add(l);
        add(t);
        add(b1);
        add(b2);
    }

    public class MyActionListner implements ActionListener{
        public void actionPerformed(ActionEvent ae){
            if(ae.getActionCommand().equals("Login")){
                if (t.getText().equals("admin")){
                    dispose();
                    AdminMenuWindow adw = new AdminMenuWindow();
                }else{
                    JOptionPane.showMessageDialog(null,"Wrong password");
                }
            }else if(ae.getActionCommand().equals("Back")){
                dispose();
                new mainWindow();
            }
        }
    }

}



public class ManagementSystem{
    public static void main(String[] args) {
        mainWindow W1 = new mainWindow();

     }
}


   