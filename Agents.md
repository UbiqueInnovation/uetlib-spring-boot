# Agent notes

## Request deserialization violations

The behavior is provided by `JacksonDeserializationAdviceTrait`. It and
`ViolationAdviceTrait` independently extend `ViolationProblemDetailTrait`, which builds
the shared violation response. Override `createDeserializationViolation` or
`deserializationMessage` to customize field mapping or messages.

`UetlibExceptionHandler` includes a `violations` entry for Jackson request binding errors
that identify a field. It preserves nested property names and array indices, for example
`invitees[0].name`, and provides messages for missing required properties, invalid enum values,
wrong JSON types, invalid scalar formats, rejected nulls, unknown or ignored properties,
and missing or invalid type discriminators. Rejected input values and raw parser messages
are not included.

Jackson stops at the first binding error, so these responses contain one violation.
Malformed JSON and errors without a field path retain Spring's generic bad-request response.
