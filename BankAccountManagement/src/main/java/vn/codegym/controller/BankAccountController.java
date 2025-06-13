package vn.codegym.controller;

import vn.codegym.service.BankAccountService;

import java.util.Scanner;

public class BankAccountController {
    private final BankAccountService service = new BankAccountService();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Thêm mới");
            System.out.println("2. Xóa");
            System.out.println("3. Xem danh sách");
            System.out.println("4. Tìm kiếm");
            System.out.println("5. Thoát");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    service.addAccount(scanner);
                    break;
                case "2":
                    service.removeAccount(scanner);
                    break;
                case "3":
                    service.showAccounts();
                    break;
                case "4":
                    service.searchAccount(scanner);
                    break;
                case "5":
                    System.out.println("Thoát chương trình.");
                    return;
                default:
                    System.out.println("Vui lòng chọn lại.");
            }
        }
    }
}
