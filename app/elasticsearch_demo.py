from __future__ import annotations

from elasticsearch import Elasticsearch
from elasticsearch.exceptions import NotFoundError, TransportError


INDEX_NAME = "products"
HOST = "http://localhost:9200"


def get_client() -> Elasticsearch:
    client = Elasticsearch(HOST)
    if not client.ping():
        raise ConnectionError(
            f"Elasticsearch is not reachable at {HOST}. Start the service first."
        )
    return client


def create_index(client: Elasticsearch) -> None:
    body = {
        "settings": {
            "number_of_shards": 1,
            "number_of_replicas": 0,
        },
        "mappings": {
            "properties": {
                "name": {"type": "text"},
                "brand": {"type": "text", "fields": {"keyword": {"type": "keyword"}}},
                "color": {"type": "keyword"},
                "price": {"type": "float"},
            }
        },
    }

    try:
        if client.indices.exists(index=INDEX_NAME):
            print(f"Index '{INDEX_NAME}' already exists.")
            return
        client.indices.create(index=INDEX_NAME, body=body)
        print(f"Index '{INDEX_NAME}' created successfully.")
    except TransportError as exc:
        raise RuntimeError(f"Failed to create index '{INDEX_NAME}'.") from exc


def insert_products(client: Elasticsearch) -> None:
    products = [
        {
            "name": "Nike Air Max",
            "brand": "Nike",
            "color": "black",
            "price": 4500,
        },
        {
            "name": "Adidas Running Shoes",
            "brand": "Adidas",
            "color": "white",
            "price": 3500,
        },
        {
            "name": "Nike Revolution 7",
            "brand": "Nike",
            "color": "black",
            "price": 3200,
        },
        {
            "name": "Puma Sport Sneakers",
            "brand": "Puma",
            "color": "red",
            "price": 2800,
        },
    ]

    for index, product in enumerate(products, start=1):
        client.index(index=INDEX_NAME, id=index, body=product)

    client.indices.refresh(index=INDEX_NAME)
    print(f"Inserted {len(products)} product documents.")


def search_all(client: Elasticsearch):
    query = {"query": {"match_all": {}}}
    return client.search(index=INDEX_NAME, body=query)


def search_by_name(client: Elasticsearch, term: str):
    query = {"query": {"match": {"name": term}}}
    return client.search(index=INDEX_NAME, body=query)


def search_by_brand(client: Elasticsearch, brand: str):
    query = {"query": {"term": {"brand.keyword": brand}}}
    return client.search(index=INDEX_NAME, body=query)


def search_by_price_and_sort(client: Elasticsearch, max_price: float):
    query = {
        "query": {"range": {"price": {"lte": max_price}}},
        "sort": [{"price": "asc"}],
    }
    return client.search(index=INDEX_NAME, body=query)


def print_hits(label: str, result: dict) -> None:
    print(f"\n=== {label} ===")
    hits = result.get("hits", {}).get("hits", [])
    if not hits:
        print("No matching products found.")
        return

    for hit in hits:
        source = hit.get("_source", {})
        print(
            f"ID={hit.get('_id')} | name={source.get('name')} | "
            f"brand={source.get('brand')} | color={source.get('color')} | "
            f"price={source.get('price')}"
        )


def main() -> None:
    client = get_client()

    try:
        create_index(client)
        insert_products(client)

        all_results = search_all(client)
        print_hits("All Products", all_results)

        name_results = search_by_name(client, "Nike shoes")
        print_hits("Search by Name: Nike shoes", name_results)

        brand_results = search_by_brand(client, "Nike")
        print_hits("Exact Brand Filter: Nike", brand_results)

        price_results = search_by_price_and_sort(client, 4000)
        print_hits("Price <= 4000 with ascending sort", price_results)

    except (ConnectionError, RuntimeError) as exc:
        print(f"Error: {exc}")


if __name__ == "__main__":
    main()
