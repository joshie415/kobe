package dao;

import com.mongodb.client.MongoCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dto.TransactionDto;
import org.bson.Document;

public class TransactionDao extends BaseDao<TransactionDto> {

    private static TransactionDao instance;

    private TransactionDao(MongoCollection<Document> collection) {
        super(collection);
    }

    public static TransactionDao getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new TransactionDao(MongoConnection.getCollection("transactions"));
        return instance;
    }

    public static TransactionDao getInstance(MongoCollection<Document> collection) {
        instance = new TransactionDao(collection);
        return instance;
    }

    @Override
    public List<TransactionDto> query(Document filter) {
        return collection.find(filter)
                .into(new ArrayList<>())
                .stream()
                .map(TransactionDto::fromDocument)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getRecentTransactions() {
        return collection.find().sort(new Document("timestamp", -1)).limit(10)
                .into(new ArrayList<>())
                .stream()
                .map(TransactionDto::fromDocument)
                .collect(Collectors.toList());
    }
}
