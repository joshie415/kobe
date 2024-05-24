package handler;

import dao.AuthDao;
import dao.UserDao;
import dto.AuthDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;

import java.time.Instant;

class LoginDto {
    String userName;
    String password;
}

public class LoginHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        LoginDto userDto = GsonTool.GSON.fromJson(request.getBody(), LoginDto.class);

        UserDao userDao = UserDao.getInstance();
        var userQuery = new Document("userName", userDto.userName)
                .append("password", DigestUtils.sha256Hex(userDto.password));

        var result = userDao.query(userQuery);
        var resBuilder = new HttpResponseBuilder();
        if(result.isEmpty()){
            // bad user name or password
            resBuilder.setStatus("401 Unauthorized");
        }else{
            AuthDto authDto = new AuthDto();
            authDto.setExpireTime(Instant.now().toEpochMilli() + 70000000);
            String hash = DigestUtils.sha256Hex(userDto.userName + authDto.getExpireTime());
            authDto.setHash(hash);
            authDto.setUserName(userDto.userName);
            AuthDao authDao = AuthDao.getInstance();
            authDao.put(authDto);

            resBuilder.setStatus("200 OK");
            resBuilder.setHeader("Set-Cookie", "auth=" + hash);
        }

        return resBuilder;
    }
}
