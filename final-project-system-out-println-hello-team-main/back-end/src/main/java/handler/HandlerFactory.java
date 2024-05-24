package handler;

import org.apache.maven.plugin.logging.Log;
import request.ParsedRequest;

public class HandlerFactory {
    // routes based on the path. Add your custom handlers here
    public static BaseHandler getHandler(ParsedRequest request) {

       return switch (request.getPath()){
           case "/createUser" -> new CreateUserHandler();
           case "/login" -> new LoginHandler();
           case "/getTransactions" -> new GetTransactionsHandler();
           case "/createDeposit" -> new CreateDepositHandler();
           case "/withdraw" -> new WithdrawHandler();
           case "/transfer" -> new TransferHandler();
           case "/requestFunds" -> new RequestFunds();
           case "/getCurrentUser" -> new GetCurrentUser();
           case "/logout" -> new LogoutHandler();
           default -> new FallbackHandler();
       };
    }
}