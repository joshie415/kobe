package dto;

import org.bson.Document;

public class UserDto extends BaseDto {

    private String userName;
    private String password;
    private Double balance = 0.0d;

    public UserDto() {
        super();
    }

    public UserDto(String uniqueId) {
        super(uniqueId);
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Document toDocument() {
        return new Document("balance", balance)
                .append("userName", userName)
                .append("password", password);
    }

    public static UserDto fromDocument(Document match) {
        var userDto = new UserDto();
        if(match.get("_id") != null){
            userDto.loadUniqueId(match);
        }

        userDto.balance = match.getDouble("balance");
        userDto.password = match.getString("password");
        userDto.userName = match.getString("userName");
        return userDto;
    }
}
