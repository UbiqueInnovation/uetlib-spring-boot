# CHANGELOG

## 1.2.2

### Improvements

- New `RestClientUtils` with `RestClientUtils#getQueryParamsFromRecord(Record)` to convert all the fields from a record
  to `MultiValueMap<String, String>` since the Spring Boot RestClient is annoying and does not provide a method to pass
  a class/record as `queryParams` directly and adding all parameters individually is error-prone as new fields get
  added.

## 1.2.1

### Improvements

- Latest uetlib dependencies with Spring Boot 3.5 support

## 1.2.0

- Upgrade uetlib-parent to 1.2.0. for Spring Boot 3.5

## 1.1.0

- Spring Boot 3.4

## 1.0.0

First release. The code was moved
from [UbiqueInnovation/backend-jvm-lib](https://github.com/UbiqueInnovation/backend-jvm-lib).

