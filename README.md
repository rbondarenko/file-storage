# file-storage

File storage demo service.

RESTFul API spring-boot application that provides the following APIs:

* API to upload a file with a few meta-data fields. Persist meta-data in persistence store (In memory DB or file system and store the content on a file system)
    * POST /api/v1/files
* API to get file meta-data
    * GET /api/v1/files/{file-id}/meta-data
* API to download content stream (Optional)
    * GET /api/v1/files/{file-id}
* API to search for file IDs with a search criterion (Optional)
    * GET /api/v1/files?criterion&page=N
* Write a scheduler in the same app to poll for new items in the last hour and send an email (Optional)

### How to test

    > curl -v http://localhost:56777/files
    > curl -v -X POST -F "file=@rest-api-0.1.0-SNAPSHOT.jar.original" -F "tag=tag1" -F "tag=tag2" http://localhost:56777/files
    > curl -v -o data.bin http://localhost:56777/files/rest-api-0.1.0-SNAPSHOT.jar.original
