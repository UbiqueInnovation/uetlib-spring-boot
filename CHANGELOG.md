# CHANGELOG

# 1.5.0

## Improvements

- New `SpringDocRfc9457Problem` with optional type RFC-9457.
- Keep `type` as optional in `SpringDocProblem` as the Ubique-specific extension of RFC-9457.

# 1.4.11

## Improvements

- Fix `GMT` usage in header date formatter string in `HeaderUtility`
- Deprecate methods in `HeaderUtility` in favour of `HttpHeaderUtils` in `uetlib-core`

# 1.4.9

## Improvements

- Use timing-attack-proof api key comparison for `ApiKeyAuthenticationFilter`

## 1.4.8

### New Features

#### uetlib-spring-boot-security

- add `ResourceIndicatorClientCredentialsRequestConverter` for RFC-8707 support

## 1.4.7

### New Features

#### uetlib-spring-boot-security

- Add new `MoreJwtValidators` with `MoreJwtValidators#issuerAndAudienceValidatingJwtDecoder(String, List<String>)` that
  validates the issuer and allowed audiences of a JWT.

## 1.4.5

### New Features

#### uetlib-spring-boot

- Moved `VersionToStringConverter` and `StringToVersionConverter` from `infobox-model` to `uetlib-spring-boot`.

## 1.4.4

### Improvements

#### uetlib-spring-boot-validation

- Add missing `Autoconfiguration.imports` file in `META-INF` for the auto configuration to actually work.

## 1.4.0

### New Features

#### uetlib-spring-boot-validation

- New `uetlib-spring-boot-validation` module for automatic response validation using Jakarta Bean Validation
    - Provides `@ValidateResponse` annotation to enable validation on controller methods or classes
    - Automatic validation of HTTP response bodies before serialization
    - Performance monitoring with validation timing logs
    - Comprehensive error handling with `ResponseValidationException`

## 1.3.0

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

