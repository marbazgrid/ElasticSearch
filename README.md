# Spring Boot + Elasticsearch Demo

This project demonstrates Elasticsearch product search using Spring Boot and Java.

## Features

- create a `products` index
- insert sample product documents
- search all products
- match queries on product names
- exact brand filters using `brand.keyword`
- range filtering and sorting by price

## Prerequisites

- Java 17+
- Maven
- Docker

## Start Elasticsearch and Kibana

```bash
docker compose up -d
```

This starts:

- Elasticsearch at http://localhost:9200
- Kibana at http://localhost:5601

## Run the app

```bash
mvn spring-boot:run
```

The app will run on:

- http://localhost:8081

## API examples

### Get all products

```http
GET http://localhost:8081/api/products
```

### Search by name

```http
GET http://localhost:8081/api/products/search?query=Nike%20shoes
```

### Filter by brand

```http
GET http://localhost:8081/api/products/brand?brand=Nike
```

### Filter by maximum price

```http
GET http://localhost:8081/api/products/price?maxPrice=4000
```

## Sample product data

The app seeds these products automatically:

- Nike Air Max, black, 4500
- Adidas Running Shoes, white, 3500
- Nike Revolution 7, black, 3200
- Puma Sport Sneakers, red, 2800

## Mapping concept demonstrated

- `name`: text field for search
- `brand`: text field + `brand.keyword` exact-match subfield
- `color`: keyword field
- `price`: numeric field

This matches the search-flow idea used in the MXP product-discovery model.
