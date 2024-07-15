# Chappie Server

The Chappie Server ingests documents for a certain Product/Project and version and creates a index of summaries for all pages in the documentation.
It also expose a service that allows users to ask questions against this Product and version. Chappie will find the relavant documentation in the index 
summary and use that as input for RAG to answer the user question.

## Running this locally.

To run this locally, you will need a `OPEN_AI_API_KEY` that can be optained at openai. To start chappie in dev mode you can do:

```
export OPEN_AI_API_KEY=asd-as......hfd
mvn quarkus:dev -Dquarkus.profile=quarkus
```

The `-Dquarkus.profile=quarkus` will create an intial product named Quarkus, to enable injesting the Quarkus documentation.

## Ingesting the data.

This takes a while to do, and we will improve this in the future to distribute the already ingested data. For now, you can go to Dev UI and look at the 
swagger UI to execute the initialation of the ingestion, or you can just do:

```
curl -X 'GET' \
  'http://localhost:8080/api/ingest/Quarkus/3.12.2' \
  -H 'accept: */*'
```

This will start the process to ingest the Quarkus 3.12.2 documentation. The process runs on a schedule to not be rate limited by openai, and ingest one
page at a time. You can follow the process by going to the Chappie UI (localhost:8080). On the status page you will see the progress. 

![Chappie Ingestion](https://github.com/chappie-bot/chappie-server/blob/main/Screenshot_ingestion.png?raw=true)

You can also get the persentage ingestioned with:

```
curl -X 'GET' \
  'http://localhost:8080/api/status/persentageIngested' \
  -H 'accept: */*'
```

Once everything for a certain product/version combination is ingested, you can also use the Chappie UI to ask questions against the documentation.

![Chappie Ingestion](https://github.com/chappie-bot/chappie-server/blob/main/Screenshot_chat.png?raw=true)

There is still a LOT to be done. Please see the issues for a list.
