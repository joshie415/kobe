package handler;

import dao.TransactionDao;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

import java.util.ArrayList;
import java.util.List;

public class GetTransactionsHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        TransactionDao transactionDao = TransactionDao.getInstance();
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        // extra fancy filter
        List<Document> filterList = new ArrayList<>();
        filterList.add(new Document("userId", authResult.userName));
        filterList.add(new Document("toId", authResult.userName));

        var queryRes = transactionDao.query(new Document("$or", filterList));
        var res = new RestApiAppResponse<>(true, queryRes, authResult.userName);
        return new HttpResponseBuilder().setBody(res).setStatus(StatusCodes.OK);
    }

}