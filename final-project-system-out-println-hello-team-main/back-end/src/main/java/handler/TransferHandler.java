package handler;

import dao.TransactionDao;
import dao.UserDao;
import dto.TransactionDto;
import dto.TransactionType;
import dto.TransferRequestDto;

import dto.UserDto;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

import java.util.List;

public class TransferHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        TransferRequestDto transferRequestDto = GsonTool.GSON.fromJson(request.getBody(),
                TransferRequestDto.class);
        UserDao userDao = UserDao.getInstance();
        TransactionDao transactionDao = TransactionDao.getInstance();
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        UserDto fromUser = userDao.query(new Document("userName", authResult.userName)).get(0);
        UserDto toUser = userDao.query(new Document("userName", transferRequestDto.toId)).iterator().next();

        if(toUser == null){
            var res = new RestApiAppResponse<>(false, null, "Invalid user to transfer.");
            return new HttpResponseBuilder().setStatus("400 Bad Request")
                    .setBody(res);
        }

        if(fromUser.getBalance() < transferRequestDto.amount){
            var res = new RestApiAppResponse<>(false, null, "Not enough funds.");
            return new HttpResponseBuilder().setStatus("400 Bad Request")
                    .setBody(res);
        }

        fromUser.setBalance(fromUser.getBalance() - transferRequestDto.amount);
        toUser.setBalance(toUser.getBalance() + transferRequestDto.amount);
        userDao.put(fromUser);
        userDao.put(toUser);

        var transactionDto = new TransactionDto();
        transactionDto.setTransactionType(TransactionType.Transfer);
        transactionDto.setUserId(authResult.userName);
        transactionDto.setToId(transferRequestDto.toId);
        transactionDto.setAmount(transferRequestDto.amount);
        // todo might be to do null value check
        transactionDto.setMessage(transferRequestDto.message);
        transactionDao.put(transactionDto);


        var res = new RestApiAppResponse<>(true, List.of(transactionDto), null);
        return new HttpResponseBuilder().setStatus(StatusCodes.OK).setBody(res);
    }

}