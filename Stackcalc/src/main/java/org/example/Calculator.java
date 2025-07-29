package org.example;
import java.io.*;
import java.util.*;

import java.util.logging.*;




public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class.getName());
    final Deque<Double> stack = new ArrayDeque<>();
    final Map<String, Double> definitions = new HashMap<>();

    public static void main(String[] args) {
        configureLogging();
        Calculator calculator = new Calculator();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите способ ввода команд:");
        System.out.println("1. Ввод из консоли");
        System.out.println("2. Ввод из файла");
        System.out.print("Введите номер (1 или 2):");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Для очистки буфера после ввода числа. Типа enter

        try {
            if (choice == 1) {
                calculator.executeFromConsole();
            } else if (choice == 2) {
                String fileName = "";
                boolean fileExist = false;
            // Повторяем ввод пока файл не найдется.

                while (!fileExist) {
                    System.out.print("Введите путь к файлу: ");
                    fileName = scanner.nextLine();

                    // Проверяем есть ли файл
                    File file = new File(fileName);
                    if (file.exists() && !file.isDirectory()) {
                        fileExist = true;
                    } else {
                        System.out.println("Файл не найден. Попробуйте снова.");
                    }
                }


                calculator.executeFromFile(fileName);
            }
            else {
                System.out.println("Вы что-то не то нажали) Завершение программы.");
            }

        } catch (Exception e) {
            logger.severe("Error: "+ e.getMessage());
        }
    }

    private static void configureLogging() {
        try {
            FileHandler fileHandler = new FileHandler("calculator.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not set up logger: " + e.getMessage());
        }
        logger.setLevel(Level.ALL);
    }

    public void executeFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                executeCommand(line);
            }
        }
    }

    public void executeFromConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            executeCommand(line);
        }
    }

    public void executeCommand(String line) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return; //
        }

        String[] parts = line.split(" ");
        String command = parts[0].toUpperCase();
        try {
            switch (command) {
                case "PUSH":
                    push(parts[1]);
                    break;
                case "POP":
                    pop();
                    break;
                case "+":
                    add();
                    break;
                case "-":
                    subtract();
                    break;
                case "*":
                    multiply();
                    break;
                case "/":
                    divide();
                    break;
                case "SQRT":
                    sqrt();
                    break;
                case "PRINT":
                    print();
                    break;
                case "DEFINE":
                    define(parts[1], parts[2]);
                    break;
                default:
                    logger.warning("Unknown command: " + command);
            }
        } catch (Exception e) {
            logger.severe("Error executing command '" + line + "': " + e.getMessage());
        }
    }

    private void push(String value) {
        try {
            if (definitions.containsKey(value)) {
                stack.push(definitions.get(value));
            } else {
                stack.push(Double.parseDouble(value));
            }
            logger.info("PUSH: " + value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for PUSH: " + value);
        }
    }

    private void pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        logger.info("POP: " + stack.pop());
    }

    private void add() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough elements in stack for addition");
        }
        double b = stack.pop();
        double a = stack.pop();
        stack.push(a + b);
        logger.info("ADD: " + a + " + " + b);
    }

    private void subtract() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough elements in stack for subtraction");
        }
        double b = stack.pop();
        double a = stack.pop();
        stack.push(a - b);
        logger.info("SUBTRACT: " + a + " - " + b);
    }

    private void multiply() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough elements in stack for multiplication");
        }
        double b = stack.pop();
        double a = stack.pop();
        stack.push(a * b);
        logger.info("MULTIPLY: " + a + " * " + b);
    }

    private void divide() {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough elements in stack for division");
        }
        double b = stack.pop();
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        double a = stack.pop();
        stack.push(a / b);
        logger.info("DIVIDE: " + a + " / " + b);
    }

    private void sqrt() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        double a = stack.pop();
        if (a < 0) {
            throw new ArithmeticException("Cannot calculate square root of negative number");
        }
        stack.push(Math.sqrt(a));
        logger.info("SQRT: " + a);
    }

    private void print() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        System.out.println(stack.peek());
        logger.info("PRINT: " + stack.peek());
    }

    private void define(String name, String value) {
        try {
            double number = Double.parseDouble(value);
            definitions.put(name, number);
            logger.info("DEFINE: " + name + " = " + number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for DEFINE: " + value);
        }
    }
}