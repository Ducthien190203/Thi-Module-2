package vn.codegym.model;

public class SavingAccount extends BankAccount {
    private long amount;
    private String sentDate;
    private double interestRate;
    private int term;

    public SavingAccount(int id, String code, String owner, String createdDate,
                         long amount, String sentDate, double interestRate, int term) {
        super(id, code, owner, createdDate);
        this.amount = amount;
        this.sentDate = sentDate;
        this.interestRate = interestRate;
        this.term = term;
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%d,%s,%.2f,%d", id, code, owner, createdDate,
                amount, sentDate, interestRate, term);
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Số tiền gửi: " + amount +
                ", Ngày gửi: " + sentDate +
                ", Lãi suất: " + interestRate +
                ", Kỳ hạn: " + term + " tháng";
    }
}
