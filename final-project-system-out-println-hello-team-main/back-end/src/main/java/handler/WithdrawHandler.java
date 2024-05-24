package handler;

import dao.TransactionDao;
import dao.UserDao;
import dto.TransactionDto;
import dto.TransactionType;
import dto.UserDto;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

import java.util.List;

public class WithdrawHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        TransactionDto transactionDto = GsonTool.GSON.fromJson(request.getBody(), TransactionDto.class);

        UserDao userDao = UserDao.getInstance();

        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);

        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        UserDto user = userDao.query(new Document("userName", authResult.userName)).get(0);

        if(user.getBalance() < transactionDto.getAmount()){
            var res = new RestApiAppResponse<>(false, null, "Not enough funds.");
            return new HttpResponseBuilder().setStatus("400 Bad Request")
                    .setBody(res);
        }

        transactionDto.setTransactionType(TransactionType.Withdraw);
        transactionDto.setUserId(authResult.userName);
        TransactionDao transactionDao = TransactionDao.getInstance();
        transactionDao.put(transactionDto); // saves the transaction

        user.setBalance(user.getBalance() - transactionDto.getAmount());
        userDao.put(user);

        var res = new RestApiAppResponse<>(true, List.of(transactionDto), null);
        return new HttpResponseBuilder().setStatus(StatusCodes.OK).setBody(res);
    }
}
