package org.example;
import org.junit.jupiter.api.*;
import java.util.logging.*;
import static org.junit.jupiter.api.Assertions.*;
public class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testPush() {
        calculator.executeCommand("PUSH 5");
        assertEquals(1, calculator.stack.size());
        assertEquals(5.0, calculator.stack.peek());
    }

    @Test
    public void testPushWithDefinition() {
        calculator.executeCommand("DEFINE x 10");
        calculator.executeCommand("PUSH x");
        assertEquals(1, calculator.stack.size());
        assertEquals(10.0, calculator.stack.peek());
    }

    @Test
    public void testPushInvalidValue() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH abc");
        }, "Invalid value for PUSH");
    }

    @Test
    public void testPop() {
        calculator.executeCommand("PUSH 5");
        calculator.executeCommand("POP");
        assertTrue(calculator.stack.isEmpty());
    }

    @Test
    public void testPopEmptyStack() {
        captureLogMessage(() -> {
            calculator.executeCommand("POP");
        }, "Stack is empty");
    }

    @Test
    public void testAdd() {
        calculator.executeCommand("PUSH 5");
        calculator.executeCommand("PUSH 10");
        calculator.executeCommand("+");
        assertEquals(1, calculator.stack.size());
        assertEquals(15.0, calculator.stack.peek());
    }

    @Test
    public void testAddNotEnoughElements() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH 5");
            calculator.executeCommand("+");
        }, "Not enough elements in stack for addition");
    }

    @Test
    public void testSubtract() {
        calculator.executeCommand("PUSH 10");
        calculator.executeCommand("PUSH 5");
        calculator.executeCommand("+");
        assertEquals(1, calculator.stack.size());
        assertEquals(15.0, calculator.stack.peek());
        calculator.executeCommand("PRINT");
    }

    @Test
    public void testSubtractNotEnoughElements() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH 10");
            calculator.executeCommand("-");
        }, "Not enough elements in stack for subtraction");
    }

    @Test
    public void testMultiply() {
        calculator.executeCommand("PUSH 2");
        calculator.executeCommand("PUSH 3");
        calculator.executeCommand("*");
        assertEquals(1, calculator.stack.size());
        assertEquals(6.0, calculator.stack.peek());
    }

    @Test
    public void testMultiplyNotEnoughElements() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH 5");
            calculator.executeCommand("*");
        }, "Not enough elements in stack for multiplication");
    }

    @Test
    public void testDivide() {
        calculator.executeCommand("PUSH 10");
        calculator.executeCommand("PUSH 2");
        calculator.executeCommand("/");
        assertEquals(1, calculator.stack.size());
        assertEquals(5.0, calculator.stack.peek());
    }

    @Test
    public void testDivideByZero() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH 10");
            calculator.executeCommand("PUSH 0");
            calculator.executeCommand("/");
        }, "Division by zero");
    }

    @Test
    public void testDivideNotEnoughElements() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH 10");
            calculator.executeCommand("/");
        }, "Not enough elements in stack for division");
    }

    @Test
    public void testSqrt() {
        calculator.executeCommand("PUSH 16");
        calculator.executeCommand("SQRT");
        assertEquals(1, calculator.stack.size());
        assertEquals(4.0, calculator.stack.peek());
    }

    @Test
    public void testSqrtNegativeNumber() {
        captureLogMessage(() -> {
            calculator.executeCommand("PUSH -1");
            calculator.executeCommand("SQRT");
        }, "Cannot calculate square root of negative number");
    }

    @Test
    public void testSqrtEmptyStack() {
        captureLogMessage(() -> {
            calculator.executeCommand("SQRT");
        }, "Stack is empty");
    }

    @Test
    public void testPrint() {
        calculator.executeCommand("PUSH 5");
        calculator.executeCommand("PRINT");
        assertEquals(1, calculator.stack.size());
        assertEquals(5.0, calculator.stack.peek());
    }

    @Test
    public void testPrintEmptyStack() {
        captureLogMessage(() -> {
            calculator.executeCommand("PRINT");
        }, "Stack is empty");
    }

    @Test
    public void testDefine() {
        calculator.executeCommand("DEFINE x 10");
        assertTrue(calculator.definitions.containsKey("x"));
        assertEquals(10.0, calculator.definitions.get("x"));
    }

    @Test
    public void testDefineInvalidValue() {
        captureLogMessage(() -> {
            calculator.executeCommand("DEFINE x abc");
        }, "Invalid value for DEFINE");
    }

    // Вспомогательная функция для перехвата логов
    private void captureLogMessage(Runnable command, String expectedLogMessage) {
        // Настраиваем логгер для перехвата сообщений
        Logger logger = Logger.getLogger(Calculator.class.getName());
        TestHandler handler = new TestHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false); // Отключаем вывод в консоль

        try {
            // Выполняем команду
            command.run();
        } catch (Exception e) {
            // Если исключение выброшено, проверяем сообщение в логе
            String logMessage = handler.getLastLogMessage();
            assertNotNull(logMessage);
            assertTrue(logMessage.contains(expectedLogMessage));
        } finally {
            // Удаляем обработчик логов
            logger.removeHandler(handler);
        }
    }

    // Вспомогательный класс для перехвата логов
    private static class TestHandler extends Handler {
        private String lastLogMessage;

        @Override
        public void publish(LogRecord record) {
            lastLogMessage = record.getMessage();
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        public String getLastLogMessage() {
            return lastLogMessage;
        }
    }
}