package handler;

import com.google.gson.Gson;
import dao.UserDao;
import dto.UserDto;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;


public class GetCurrentUser implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {


        UserDao userDao = UserDao.getInstance();
        AuthFilter.AuthResult authResult = AuthFilter.doFilter(request);
        if(!authResult.isLoggedIn){
            return new HttpResponseBuilder().setStatus(StatusCodes.UNAUTHORIZED);
        }

        var filter = new Document("userName", request.getQueryParam("userName"));
        var filter2 = new Document("userName", authResult.userName);

        var res = new RestApiAppResponse<>(true, userDao.query(filter2) , null);
        return new HttpResponseBuilder().setBody(res).setStatus(StatusCodes.OK);

    }
}