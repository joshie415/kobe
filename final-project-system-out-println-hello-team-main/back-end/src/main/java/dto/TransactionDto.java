package dto;

import org.bson.Document;

import java.time.Instant;

public class TransactionDto extends BaseDto {

    private String userId;
    private String toId;
    private Double amount;
    private TransactionType transactionType;
    private Long timestamp;
    private String message;
    private String status; // Added status field

    public TransactionDto() {
        timestamp = Instant.now().toEpochMilli();
    }

    public TransactionDto(String uniqueId) {
        super(uniqueId);
        timestamp = Instant.now().toEpochMilli();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() { // Getter for status
        return status;
    }

    public void setStatus(String status) { // Setter for status
        this.status = status;
    }

    public Document toDocument() {
        var doc = new Document();
        doc.append("userId", userId);
        doc.append("toId", toId);
        doc.append("amount", amount);
        doc.append("transactionType", transactionType.toString());
        doc.append("timestamp", timestamp);
        doc.append("message", message);
        doc.append("status", status); // Include status in Document
        return doc;
    }

    public static TransactionDto fromDocument(Document document) {
        var transaction = new TransactionDto();
        transaction.loadUniqueId(document); // Load unique ID
        transaction.timestamp = document.getLong("timestamp");
        transaction.transactionType = TransactionType.valueOf(document.getString("transactionType"));
        transaction.amount = document.getDouble("amount");
        transaction.toId = document.getString("toId");
        transaction.userId = document.getString("userId");
        transaction.message = document.getString("message");
        transaction.status = document.getString("status"); // Load status from Document
        return transaction;
    }

    public String toJson() {
        return String.format(
                "{\"userId\": \"%s\", \"toId\": \"%s\", \"amount\": %f, \"transactionType\": \"%s\", \"timestamp\": %d, \"message\": \"%s\", \"status\": \"%s\", \"uniqueId\": \"%s\"}",
                userId, toId, amount, transactionType, timestamp, message, status, getUniqueId()
        );
    }
}
