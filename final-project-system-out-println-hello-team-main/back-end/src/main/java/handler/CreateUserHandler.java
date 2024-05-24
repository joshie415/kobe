package handler;

import dao.UserDao;
import dto.UserDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class CreateUserHandler implements BaseHandler {

    @Override
    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        UserDto userDto = GsonTool.GSON.fromJson(request.getBody(), UserDto.class);

        UserDao userDao = UserDao.getInstance();
        var queryResult = userDao.query(new Document("userName", userDto.getUserName()));

        var resBuilder = new HttpResponseBuilder().setStatus("200 OK");
        if(queryResult.size() == 0){
            // username not taken
            userDto.setPassword(DigestUtils.sha256Hex(userDto.getPassword()));
            userDao.put(userDto);
            resBuilder.setBody(new RestApiAppResponse<>(true, null, "User Created"));
        }else{
            // username taken
            resBuilder.setBody(new RestApiAppResponse<>(false, null, "Username already taken"));
        }
        return resBuilder;
    }
}
