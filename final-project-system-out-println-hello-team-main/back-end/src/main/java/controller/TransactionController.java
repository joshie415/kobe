package controller;

import dao.TransactionDao;
import dto.TransactionDto;

import java.util.List;

public class TransactionController {

    private final TransactionDao transactionDao = TransactionDao.getInstance();

    public String getRecentTransactions() {
        List<TransactionDto> transactions = transactionDao.getRecentTransactions();
        StringBuilder response = new StringBuilder();
        response.append("[");
        for (int i = 0; i < transactions.size(); i++) {
            TransactionDto transaction = transactions.get(i);
            response.append(transaction.toJson());
            if (i < transactions.size() - 1) {
                response.append(",");
            }
        }
        response.append("]");
        return response.toString();
    }
}
