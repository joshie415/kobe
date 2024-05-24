package handler;

import dao.AuthDao;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;

public class LogoutHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        HttpResponseBuilder resBuilder = new HttpResponseBuilder();

        String hash = request.getCookieValue("auth");
        if (hash != null) {
            AuthDao authDao = AuthDao.getInstance();
            Document filter = new Document("hash", hash);
            authDao.delete(filter);
            resBuilder.setHeader("Set-Cookie", "auth=; Max-Age=0; Path=/; HttpOnly");
        }

        resBuilder.setStatus("200 OK");
        resBuilder.setBody("You have been logged out successfully.");
        return resBuilder;
    }
}
