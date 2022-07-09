# RabobankAssignment
## Usage 
### create account
```bash
curl -X POST localhost:8080/api/account/create -H 'Content-type:application/json' -d '{"accountId": "0", "accountNumber": "3", "card": "DEBIT","currentBalance": "10.00"}'
```

### withdraw account  an amount
```bash
curl -X POST localhost:8080/api/account/withdraw -H 'Content-type:application/json' -d '{"accountNumber": "3", "amount": "2.00"}'
```

### transfer account A to account B an amount
```bash
curl -X POST localhost:8080/api/account/transfer -H 'Content-type:application/json' -d '{"fromAccountNumber": "3", "toAccountNumber": "1", "amount": "2.00"}'
```

###  see current available balance in all accounts
```bash
curl -X GET localhost:8080/api/account/all-balance -H 'Content-type:application/json'
```
###  see current balance of an account
```bash
curl -X POST localhost:8080/api/account/balance -H 'Content-type:application/json' -d '{"accountNumber": "2"}'
```
