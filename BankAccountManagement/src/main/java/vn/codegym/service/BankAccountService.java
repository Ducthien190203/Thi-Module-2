package vn.codegym.service;

import vn.codegym.exception.NotFoundBankAccountException;
import vn.codegym.model.BankAccount;
import vn.codegym.model.PaymentAccount;
import vn.codegym.model.SavingAccount;
import vn.codegym.util.CSVUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class BankAccountService {
    private static final String FILE_PATH = "D:\\Thi-module-2\\BankAccountManagement\\src\\main\\resources\\data\\bank_accounts.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Helper for regex validation
    private String getValidatedInput(String prompt, String regex, String errorMessage, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (Pattern.matches(regex, input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    // Helper for positive double
    private double getPositiveDouble(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("❌ Giá trị phải là số dương.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Vui lòng nhập số hợp lệ.");
            }
        }
    }

    // Helper for positive long
    private long getPositiveLong(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("❌ Giá trị phải là số dương.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Vui lòng nhập số hợp lệ.");
            }
        }
    }

    // Helper for positive int
    private int getPositiveInt(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("❌ Giá trị phải là số nguyên dương.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Vui lòng nhập số nguyên hợp lệ.");
            }
        }
    }

    // Helper for date validation with regex and parse
    private String getValidatedDate(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!Pattern.matches("^\\d{2}/\\d{2}/\\d{4}$", input)) {
                System.out.println("❌ Ngày phải đúng định dạng dd/MM/yyyy.");
                continue;
            }
            try {
                LocalDate.parse(input, DATE_FORMAT);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Ngày không hợp lệ.");
            }
        }
    }

    public void addAccount(Scanner scanner) {
        List<BankAccount> accounts = readAccounts();
        int id = accounts.size() > 0 ? accounts.get(accounts.size() - 1).getId() + 1 : 1;

        String code = getValidatedInput(
            "Nhập mã tài khoản (bắt đầu bằng 003 và 6 số): ",
            "^003\\d{6}$",
            "❌ Mã tài khoản phải bắt đầu bằng 003 và theo sau là 6 chữ số.",
            scanner
        );

        String owner = getValidatedInput(
            "Nhập tên chủ tài khoản (chỉ chữ cái, kể cả dấu, và khoảng trắng): ",
            "^[\\p{L} ]+$",
            "❌ Tên chủ tài khoản chỉ được chứa chữ cái (có dấu) và khoảng trắng.",
            scanner
        );

        String createdDate = getValidatedDate(
            "Nhập ngày tạo tài khoản (dd/MM/yyyy): ",
            scanner
        );
        LocalDate createdLocalDate = LocalDate.parse(createdDate, DATE_FORMAT);

        String type;
        while (true) {
            System.out.print("Loại tài khoản (1: Tiết kiệm, 2: Thanh toán): ");
            type = scanner.nextLine().trim();
            if (type.equals("1") || type.equals("2")) break;
            System.out.println("❌ Loại tài khoản không hợp lệ.");
        }

        try {
            BankAccount account;
            if (type.equals("1")) {
                long amount = getPositiveLong("Số tiền gửi tiết kiệm: ", scanner);

                String sentDate;
                LocalDate sentLocalDate;
                while (true) {
                    sentDate = getValidatedDate("Ngày gửi tiết kiệm (dd/MM/yyyy): ", scanner);
                    sentLocalDate = LocalDate.parse(sentDate, DATE_FORMAT);
                    if (sentLocalDate.isBefore(createdLocalDate)) {
                        System.out.println("❌ Ngày gửi tiết kiệm không được trước ngày tạo tài khoản.");
                    } else {
                        break;
                    }
                }

                double rate = getPositiveDouble("Lãi suất: ", scanner);

                int term = getPositiveInt("Kỳ hạn (tháng): ", scanner);

                account = new SavingAccount(id, code, owner, createdDate, amount, sentDate, rate, term);
            } else {
                String card = getValidatedInput(
                    "Số thẻ (16 chữ số): ",
                    "^\\d{16}$",
                    "❌ Số thẻ phải gồm đúng 16 chữ số.",
                    scanner
                );

                long balance = getPositiveLong("Số dư tài khoản: ", scanner);

                account = new PaymentAccount(id, code, owner, createdDate, card, balance);
            }

            CSVUtils.writeLine(FILE_PATH, account.toCSV(), true);
            System.out.println("✅ Đã thêm tài khoản.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi nhập dữ liệu: " + e.getMessage());
        }
    }

    public void removeAccount(Scanner scanner) {
        List<BankAccount> accounts = readAccounts();
        while (true) {
            System.out.print("Nhập mã tài khoản cần xóa (bắt đầu bằng 003 và 6 số) (Enter để quay lại menu): ");
            String code = scanner.nextLine().trim();
            if (code.isEmpty()) {
                // User pressed Enter, return to menu
                return;
            }
            BankAccount foundAccount = null;
            for (int i = 0; i < accounts.size(); i++) {
                BankAccount acc = accounts.get(i);
                if (acc.getCode().equals(code)) {
                    foundAccount = acc;
                    break;
                }
            }

            if (foundAccount != null) {
                while (true) {
                    System.out.print("Bạn có chắc muốn xóa không? (Yes/No): ");
                    String confirm = scanner.nextLine().trim();
                    if (confirm.equalsIgnoreCase("Yes")) {
                        accounts.remove(foundAccount);
                        CSVUtils.writeAll(FILE_PATH, accounts);
                        System.out.println("✅ Đã xóa tài khoản.");
                        System.out.println("Cập nhật danh sách");
                        showAccounts();
                        return;
                    } else if (confirm.equalsIgnoreCase("No")) {
                        // Return to menu without deleting
                        return;
                    } else {
                        System.out.println("Vui lòng nhập Yes hoặc No.");
                    }
                }
            } else {
                try {
                    throw new NotFoundBankAccountException("Tài khoản không tồn tại.");
                } catch (NotFoundBankAccountException e) {
                    System.out.println(e.getMessage());
                }
                // Prompt again or allow Enter to return to menu
            }
        }
    }

    public void showAccounts() {
        List<BankAccount> accounts = readAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println(accounts.get(i));
        }
    }

    public void searchAccount(Scanner scanner) {
        List<BankAccount> accounts = readAccounts();
        System.out.print("Nhập từ khóa (mã hoặc tên): ");
        String keyword = scanner.nextLine().toLowerCase();
        boolean found = false;
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount a = accounts.get(i);
            if (a.getCode().contains(keyword) || a.toString().toLowerCase().contains(keyword)) {
                System.out.println(a);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Không tìm thấy tài khoản phù hợp.");
        }
    }

    private List<BankAccount> readAccounts() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        List<String> lines = CSVUtils.readAll(FILE_PATH);
        List<BankAccount> accounts = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");
            if (parts.length == 8) {
                accounts.add(new SavingAccount(
                        Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                        Long.parseLong(parts[4]), parts[5],
                        Double.parseDouble(parts[6]), Integer.parseInt(parts[7])
                ));
            } else if (parts.length == 6) {
                accounts.add(new PaymentAccount(
                        Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                        parts[4], Long.parseLong(parts[5])
                ));
            }
        }
        return accounts;
    }
}
