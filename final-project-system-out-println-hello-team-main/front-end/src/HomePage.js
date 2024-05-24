import React from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from './Modal';
import TransactionFeed from './TransactionFeed';

function App() {
    const [amount, setAmount] = React.useState('');
    const [transactions, setTransactions] = React.useState([]);
    const [showTable, setShowTable] = React.useState(true);
    const [toId, setToId] = React.useState('');
    const [currUser, setCurrUser] = React.useState('');
    const [userInfo, setUserInfo] = React.useState([]);

    const [showRequestFunds, setRequestFunds] = React.useState(false);
    const [showMessagePopup, setShowMessagePopup] = React.useState(false);
    const [message, setMessage] = React.useState('');
    const [loading, setLoading] = React.useState(true);
    const navigate = useNavigate();

    function updateAmount(event) {
        const newValue = event.target.value.replaceAll(/[^0-9]/g, '');
        setAmount(newValue.substring(0, 10));
    }

    function loadTransactions() {
        console.log("Loading transactions");
        const options = { method: "GET" };
        fetch('/getTransactions', options)
            .then(res => res.json())
            .then(apiResponse => {                
                // console.log(apiResponse.data)
                setCurrUser(apiResponse.message)
                setTransactions(apiResponse.data);
            });

        return true;        
    }

    function getUserInfo() {
        console.log("Loading User Info");
        const options = { method: "GET" };
        fetch('/getCurrentUser', options)
            .then(res => res.json())
            .then(apiResponse => {
                setUserInfo(apiResponse.data);
            })
            .catch(error => console.error("Failed to load user info:", error));
    }

    React.useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            await loadTransactions();
            await getUserInfo();
            setLoading(false);
        };
        fetchData();
    }, []);

    function handleTransaction(event, transactionType) {
        console.log("Handling transaction:", transactionType);
        const options = {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ amount, toId, message })
        };
        fetch(`/${transactionType}`, options)
            .then(res => res.json())
            .then(apiResponse => {
                setAmount('');
                setToId('');
                setMessage('');
                loadTransactions();
                getUserInfo();
            })
            .catch(error => console.error(`Failed to complete ${transactionType}:`, error));
    }

    const handleTransferClick = () => {
        handleTransaction(null, 'transfer');
        setShowMessagePopup(false);
    };

    function handleExit() {
        setAmount('');
        setToId('');
        setMessage('');
        setRequestFunds(false);
    }

    function changeScene() {
        setAmount('');
        setToId('');
        setMessage('');
        setRequestFunds(true);
    }

    function handleRequest(transaction) {
        return transaction.transactionType === "RequestFunds" && transaction.toId === currUser;
    }

    function handleAcceptFunds(event) {
        const [userId, amount] = event.target.value.split(",");
        handleTransaction({ userId, amount, message: "Sent requested money" }, 'transfer');
    }

    function handleAcceptFunds(event) {
        let transactions = event.target.value.split(",");
        // console.log(transactions[0])
        // console.log("clicked to send funds")
        const options = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                amount: transactions[1],
                toId: transactions[0],
                message: "Sent requested money"               
            })
        };
        fetch('/transfer', options)
            .then(res => res.json())
            .then(apiResponse => {
                setAmount('');
                setToId('');
                setMessage('');
                getUserInfo(); 
                loadTransactions(); // eventually refresh logs
            });
    }

    function handleLogout() {
        const options = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            }
        };
        fetch('/logout', options)
            .then(res => {
                if(res.status === 200){
                    navigate("/");
                }
            });
    }

    if(loading) {
        return <div>Loading ... </div>
    }

    return (
        <div className="App">
            <h1>Home Page</h1>
            <button onClick={handleLogout}>Logout</button> {/* Add logout button */}
            <br/>
            {userInfo.map(info => (
                <h3 key={info.id}>Available balance: ${info.balance}</h3>
            ))}

            {showRequestFunds ? (
                <div>
                    <button onClick={handleExit}>Exit</button>
                    <br/><br/>
                    <label>Amount:</label>
                    <input value={amount} type='number' min={1} onChange={updateAmount} required />
                    <br/>
                    <label>Select Recipient:</label>
                    <input value={toId} onChange={(e) => setToId(e.target.value)} required />
                    <br/>
                    <label>Message to recipient:</label>
                    <textarea value={message} onChange={(e) => setMessage(e.target.value)} />
                    <br/>
                    <button onClick={(e) => handleTransaction(e, 'requestFunds')}>Submit Request</button>
                </div>
            ) : (
                <div>
                    <input value={amount} onChange={updateAmount} />
                    <button onClick={(e) => handleTransaction(e, 'createDeposit')} disabled={amount.length === 0}>Deposit</button>
                    <button onClick={(e) => handleTransaction(e, 'withdraw')} disabled={amount.length === 0}>Withdraw</button>
                    <button onClick={() => setShowMessagePopup(true)} disabled={amount.length === 0}>Transfer</button>
                    <span> to user: </span>
                    <input value={toId} onChange={(e) => setToId(e.target.value)} />
                    <Modal open={showMessagePopup} onClose={handleTransferClick}>
                        <input value={message} onChange={(e) => setMessage(e.target.value)}/>
                    </Modal>
                    <br/>
                    <button onClick={changeScene}>Request Funds</button>
                </div>
            )}

            <TransactionFeed transactions={transactions} />

            <div>
                <table>
                    <thead>
                        <tr onClick={() => setShowTable(!showTable)}>
                            <th>Amount</th>
                            <th>Time</th>
                            <th>Type</th>
                            <th>ID</th>
                            <th>Message</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {showTable && transactions.map(transaction => (
                            <tr key={transaction.uniqueId}>
                                <td>${transaction.amount.toLocaleString()}</td>
                                <td>{new Date(transaction.timestamp).toLocaleString()}</td>
                                <td>{transaction.transactionType}</td>
                                <td>{transaction.uniqueId.substring(14)}</td>
                                <td>{transaction.message}</td>
                                <td>
                                    {handleRequest(transaction) ? (
                                        <>
                                            {transaction.status}
                                            <br/>
                                            <button onClick={() => console.log('Rejecting funds')}>X</button>
                                            <button value={[transaction.userId, transaction.amount]} onClick={handleAcceptFunds}>Yes</button>
                                        </>
                                    ) : transaction.status}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
    </div>
    );
}

export default App;
