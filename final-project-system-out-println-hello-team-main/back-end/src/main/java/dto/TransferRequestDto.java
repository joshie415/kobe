package dto;

public class TransferRequestDto {

    public final double amount;
    public final String fromId;
    public final String toId;
    public final String message;
    public final String status;

    public TransferRequestDto(double amount, String fromId, String toId, String message, String status) {
        this.amount = amount;
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.status = status;
    }
}
