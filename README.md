# bug-free-meme

This API uses Alpha Vantage API.

To run the code, you can run 

```zsh
docker build . -t yourimagename
docker run -p 8080:8080 yourimagename
```

## Assumption

If a user is querying "from" and "to" on Saturday and Sunday, our API will get the closing price on the Friday before the day provided while the period will still include the day.

## Example Commands to Test

```zsh
curl 'http://localhost:8080/stock/ibm?from=2011-10-20'

curl 'http://localhost:8080/stock/ibm?to=2011-10-20'

curl 'http://localhost:8080/stock/ibm?to=2011-10-20&from=2020-10-20'

curl http://localhost:8080/stock/ibm?from=2020-10-20&to=2022-10-20
```

## Area for future Improvement

If more time is provided, I would use Cron job to insert financial data to a DB eg. MongoDB to support the querying operation (instead of calling Alpha Vantage API every time we receive a request).
