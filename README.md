# 

This API uses Alpha Vantage API.

To run the code, you can run 

```zsh
docker build . -t yourimagename
docker run -p 8080:8080 yourimagename
```

## Example Commands to Test

```zsh
curl 'http://localhost:8080/stock/ibm?from=2011-10-20'

curl 'http://localhost:8080/stock/ibm?to=2011-10-20'

curl 'http://localhost:8080/stock/ibm?to=2011-10-20&from=2020-10-20'

curl http://localhost:8080/stock/ibm?from=2020-10-20&to=2022-10-20
```
