import React, { useEffect, useState } from 'react';

function TransactionFeed() {
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        fetch('/transactions')
            .then(response => response.json())
            .then(data => {
                setTransactions(data);
            })
            .catch(error => {
                console.error("There was an error fetching the transactions!", error);
            });
    }, []);

    return (
        <div>
            <h2>Recent Transactions</h2>
            <ul>
                {transactions.map(transaction => (
                    <li key={transaction.uniqueId}>
                        {transaction.description}: ${transaction.amount} - {new Date(transaction.timestamp).toLocaleString()}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default TransactionFeed;
