package vn.codegym.service;

import vn.codegym.exception.NotFoundBankAccountException;
import vn.codegym.model.BankAccount;
import vn.codegym.model.PaymentAccount;
import vn.codegym.model.SavingAccount;
import vn.codegym.util.CSVUtils;

import java.io.File;
import java.util.*;

public class BankAccountService {
    // Use absolute path as requested
    private static final String FILE_PATH = "D:\\Thi-module-2\\BankAccountManagement\\src\\main\\resources\\data\\bank_accounts.csv";

    public void addAccount(Scanner scanner) {
        List<BankAccount> accounts = readAccounts();
        int id = accounts.size() > 0 ? accounts.get(accounts.size() - 1).getId() + 1 : 1;

        System.out.print("Nhập mã tài khoản: ");
        String code = scanner.nextLine();
        System.out.print("Nhập tên chủ tài khoản: ");
        String owner = scanner.nextLine();
        System.out.print("Nhập ngày tạo tài khoản (dd/MM/yyyy): ");
        String createdDate = scanner.nextLine();

        System.out.print("Loại tài khoản (1: Tiết kiệm, 2: Thanh toán): ");
        String type = scanner.nextLine();

        try {
            BankAccount account;
            if (type.equals("1")) {
                System.out.print("Số tiền gửi tiết kiệm: ");
                long amount = Long.parseLong(scanner.nextLine());
                System.out.print("Ngày gửi tiết kiệm: ");
                String sentDate = scanner.nextLine();
                System.out.print("Lãi suất: ");
                double rate = Double.parseDouble(scanner.nextLine());
                System.out.print("Kỳ hạn (tháng): ");
                int term = Integer.parseInt(scanner.nextLine());

                account = new SavingAccount(id, code, owner, createdDate, amount, sentDate, rate, term);
            } else {
                System.out.print("Số thẻ: ");
                String card = scanner.nextLine();
                System.out.print("Số dư tài khoản: ");
                long balance = Long.parseLong(scanner.nextLine());

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
        System.out.print("Nhập mã tài khoản cần xóa: ");
        String code = scanner.nextLine();

        Optional<BankAccount> opt = accounts.stream()
                .filter(acc -> acc.getCode().equals(code))
                .findFirst();

        if (opt.isPresent()) {
            System.out.print("Bạn có chắc muốn xóa không? (Yes/No): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("Yes")) {
                accounts.remove(opt.get());
                CSVUtils.writeAll(FILE_PATH, accounts);
                System.out.println("✅ Đã xóa tài khoản.");
            }
        } else {
            try {
                throw new NotFoundBankAccountException("Tài khoản không tồn tại.");
            } catch (NotFoundBankAccountException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void showAccounts() {
        List<BankAccount> accounts = readAccounts();
        accounts.forEach(System.out::println);
    }

    public void searchAccount(Scanner scanner) {
        List<BankAccount> accounts = readAccounts();
        System.out.print("Nhập từ khóa (mã hoặc tên): ");
        String keyword = scanner.nextLine().toLowerCase();
        accounts.stream()
                .filter(a -> a.getCode().contains(keyword) || a.toString().toLowerCase().contains(keyword))
                .forEach(System.out::println);
    }

    private List<BankAccount> readAccounts() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        List<String> lines = CSVUtils.readAll(FILE_PATH);
        List<BankAccount> accounts = new ArrayList<>();
        for (String line : lines) {
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