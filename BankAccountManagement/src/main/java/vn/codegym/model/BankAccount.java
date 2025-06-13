package vn.codegym.model;

public abstract class BankAccount {
    protected int id;
    protected String code;
    protected String owner;
    protected String createdDate;

    public BankAccount(int id, String code, String owner, String createdDate) {
        this.id = id;
        this.code = code;
        this.owner = owner;
        this.createdDate = createdDate;
    }

    public String getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public abstract String toCSV();

    @Override
    public String toString() {
        return "ID: " + id + ", Mã TK: " + code + ", Chủ TK: " + owner + ", Ngày tạo: " + createdDate;
    }
}
