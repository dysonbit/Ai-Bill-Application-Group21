package model;
//Transaction time, transaction type, transaction counterparty,
// commodity receipt/payment amount (yuan), payment method, current status,
// transaction order number, merchant order number, remarks
public class Transaction {
    private String transactionTime;
    private String transactionType;
    private String counterparty;
    private String commodity;
    private double paymentAmount;
    private String paymentMethod;
    private String currentStatus;
    private String orderNumbe;
    private String merchantNumber;
    private String remarks;

    public Transaction() {
    }

    public Transaction(String transactionTime, String transactionType, String counterparty, String commodity, double paymentAmount, String paymentMethod, String currentStatus, String orderNumbe, String merchantNumber, String remarks) {
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
        this.counterparty = counterparty;
        this.commodity = commodity;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.currentStatus = currentStatus;
        this.orderNumbe = orderNumbe;
        this.merchantNumber = merchantNumber;
        this.remarks = remarks;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setOrderNumbe(String orderNumbe) {
        this.orderNumbe = orderNumbe;
    }

    public void setMerchantNumber(String merchantNumber) {
        this.merchantNumber = merchantNumber;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getCommodity() {
        return commodity;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getOrderNumbe() {
        return orderNumbe;
    }

    public String getMerchantNumber() {
        return merchantNumber;
    }

    public String getRemarks() {
        return remarks;
    }
}
