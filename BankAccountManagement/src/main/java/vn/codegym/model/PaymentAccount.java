package vn.codegym.model;

public class PaymentAccount extends BankAccount {
    private String cardNumber;
    private long balance;

    public PaymentAccount(int id, String code, String owner, String createdDate,
                          String cardNumber, long balance) {
        super(id, code, owner, createdDate);
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,%d", id, code, owner, createdDate, cardNumber, balance);
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Số thẻ: " + cardNumber +
                ", Số dư: " + balance;
    }
}
