# Url Shortener
Url shortener using cats effect, tagless final and http4s. This project is using hexagonal architecture.

## TODO
- repository implementation (instead of the in-memory version)

## Run
```sbt run```

## Endpoints
### `POST http://localhost:8080/shorten`
Payload:
```
{
    "url": "http://google.com"
}
```
Response: `200 OK`
```
{
    "shortenedUrl": "http://localhost:8080/go/3fv14S",
    "originalUrl": "http://www.google.com"
}
```

### `GET http://localhost:8080/go/<code>`
Response: `301 Permanent Redirect` to `http://www.google.com`

## Test
```sbt test``` 

```sbt "IntegrationTest / test"``` 