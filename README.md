# Getting Started

### Documentation

RESTFul API spring-boot application that provides the following APIs:

* API to upload a file with a few meta-data fields. Persist meta-data in persistence store (In memory DB or file system and store the content on a file system)
    * `POST /api/v1/files`
* API to get file meta-data
    * `GET /api/v1/files/{file-id}/meta-data`
* API to download content stream (Optional)
    * `GET /api/v1/files/{file-id}/content`
* API to search for file IDs with a search criterion (Optional)
    * `GET /api/v1/files?q=<meta-data-field-name>=<meta-data-field-value>|*`
* Write a scheduler in the same app to poll for new items in the last hour and send an email (Optional)

### How it work
- Build
    > mvn clean package
- Run
    > java -jar web/target/web-0.1.0-SNAPSHOT.jar
- Post file with meta data information
    > curl -v -X POST -F "file=@path-to-file" -F meta-data=field1=value1,field2=value2 http://localhost:56777/api/v1/files
- Request file content
    > curl -v http://localhost:56777/api/v1/files/23c99c39-9334-d2ae-24ea-b08b05962e10/content
- Request file meta data
    > curl -v http://localhost:56777/api/v1/files/23c99c39-9334-d2ae-24ea-b08b05962e10/meta-data
- Search all files which is tagged by `field1` with any value and `field2` with value `value2`
    > curl -k http://localhost:56777/api/v1/files?q=field1=*,field2=value2
