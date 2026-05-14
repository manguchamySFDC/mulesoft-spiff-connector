# Salesforce SPIFF Connector for MuleSoft

A custom MuleSoft connector that provides native integration with the **Salesforce SPIFF** Incentive Compensation Management platform. The connector supports three SPIFF API domains with two distinct authentication mechanisms, enabling MuleSoft developers to build commission management integrations directly within Anypoint Studio.

## Supported APIs

| API | Auth Method | Operations |
|---|---|---|
| **Import API** | HMAC-SHA256 | List Imports, Create/Delete Records, Get Import, Test Import |
| **Object API** | HMAC-SHA256 | Get All Objects, Create, Get, Update, Delete |
| **Reporting API** | OAuth 2.0 (Client Credentials) | Get Reports, Request Export, Get Export Status, Download Report |

**Total: 13 operations**

## Requirements

- **Mule Runtime**: 4.9.0 or later
- **Java**: 8, 11, or 17
- **Anypoint Studio**: 7.x
- **Maven**: 3.8+

## Project Structure

```
spiff-connector/
├── pom.xml
├── icon/
│   └── icon.svg
├── src/
│   ├── main/
│   │   ├── java/com/mulesoft/connectors/spiff/
│   │   │   ├── api/
│   │   │   │   └── SpiffAttributes.java
│   │   │   └── internal/
│   │   │       ├── SpiffExtension.java
│   │   │       ├── config/
│   │   │       │   ├── SpiffHmacConfiguration.java
│   │   │       │   └── SpiffReportingConfiguration.java
│   │   │       ├── connection/
│   │   │       │   ├── SpiffHmacConnection.java
│   │   │       │   ├── SpiffHmacConnectionProvider.java
│   │   │       │   ├── SpiffOAuthConnection.java
│   │   │       │   └── SpiffOAuthConnectionProvider.java
│   │   │       ├── error/
│   │   │       │   ├── SpiffErrorType.java
│   │   │       │   └── SpiffErrorTypeProvider.java
│   │   │       ├── operation/
│   │   │       │   ├── SpiffImportOperations.java
│   │   │       │   ├── SpiffObjectOperations.java
│   │   │       │   └── SpiffReportingOperations.java
│   │   │       └── util/
│   │   │           ├── HmacSignatureUtil.java
│   │   │           └── HttpClientUtil.java
│   │   └── resources/META-INF/
│   │       └── mule-artifact.json
│   └── test/
│       └── java/com/mulesoft/connectors/spiff/
│           ├── HmacSignatureUtilTest.java
│           ├── SpiffErrorTypeTest.java
│           ├── SpiffImportOperationsTest.java
│           ├── SpiffObjectOperationsTest.java
│           ├── SpiffReportingOperationsTest.java
│           └── internal/connection/
│               └── SpiffOAuthConnectionTest.java
```

## Architecture

The connector follows the standard MuleSoft SDK extension pattern:

```
SpiffExtension
├── SpiffHmacConfiguration (hmac-config)
│   ├── SpiffHmacConnectionProvider → SpiffHmacConnection
│   ├── SpiffImportOperations (4 ops)
│   └── SpiffObjectOperations (5 ops)
└── SpiffReportingConfiguration (oauth-config)
    ├── SpiffOAuthConnectionProvider → SpiffOAuthConnection
    └── SpiffReportingOperations (4 ops)
```

### Authentication

**HMAC-SHA256** (Import & Object APIs)
- Each request generates a fresh signature: `t={timestamp},v1={hmac_sha256_hex}`
- Payload format: `{timestamp}.{request_body}`
- Signature is sent via the `signature` HTTP header

**OAuth 2.0 Client Credentials** (Reporting API)
- Token obtained from SPIFF's `/oauth2/token` endpoint
- Automatic token refresh 60 seconds before expiry
- Bearer token sent via `Authorization` header

### Error Handling

The connector maps HTTP status codes to typed Mule errors:

| HTTP Status | Error Type |
|---|---|
| 400 | `SPIFF:INVALID_REQUEST` |
| 401, 403 | `SPIFF:AUTHENTICATION` |
| 404 | `SPIFF:NOT_FOUND` |
| 429 | `SPIFF:RATE_LIMIT` |
| 5xx | `SPIFF:SERVER_ERROR` |
| IOException | `SPIFF:CONNECTIVITY` |
| Timeout | `SPIFF:TIMEOUT` |

## Build & Install

### Prerequisites

Set `JAVA_HOME` to a Java 17 JDK (recommended: Azul Zulu):

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
```

### Build

```bash
mvn clean install
```

This compiles to Java 8 bytecode (compatible with Java 8, 11, and 17 runtimes), runs all 36 unit tests, and installs the connector to your local Maven repository (`~/.m2`).

### Install in Anypoint Studio

1. Build the connector with `mvn clean install`
2. In your Mule application's `pom.xml`, add:

```xml
<dependency>
    <groupId>com.mulesoft.connectors</groupId>
    <artifactId>spiff-connector</artifactId>
    <version>1.0.0</version>
    <classifier>mule-plugin</classifier>
</dependency>
```

3. Refresh the project in Anypoint Studio — the connector appears in the Mule Palette

## Configuration

### HMAC Configuration (Import & Object APIs)

| Parameter | Description |
|---|---|
| **Subdomain** | SPIFF environment subdomain (`us1` for US, `eu1` for EU) |
| **Company ID** | UUID from Admin > Settings > Company Settings > Company Identifiers |
| **Secret Key** | HMAC-SHA256 secret key from Salesforce Customer Support |

XML example:

```xml
<spiff:hmac-config name="SPIFF_HMAC" doc:name="SPIFF HMAC Configuration">
    <spiff:hmac-connection subdomain="us1"
                           companyId="${spiff.companyId}"
                           secretKey="${spiff.secretKey}" />
</spiff:hmac-config>
```

### OAuth Configuration (Reporting API)

| Parameter | Description |
|---|---|
| **Subdomain** | SPIFF environment subdomain (`us1` for US, `eu1` for EU) |
| **Client ID** | OAuth Client ID from Admin > Settings > API Access Management |
| **Client Secret** | OAuth Client Secret from Admin > Settings > API Access Management |

XML example:

```xml
<spiff:oauth-config name="SPIFF_OAuth" doc:name="SPIFF OAuth Configuration">
    <spiff:oauth-connection subdomain="us1"
                            clientId="${spiff.oauth.clientId}"
                            clientSecret="${spiff.oauth.clientSecret}" />
</spiff:oauth-config>
```

## Usage Examples

### Import API — Upsert Team Members

```xml
<spiff:create-or-delete-records doc:name="Upsert Team Members"
    config-ref="SPIFF_HMAC"
    importTarget="team_members"
    importAction="upsert"
    payload='#[output application/json --- payload.records]' />
```

### Object API — Get All Objects

```xml
<spiff:get-all-objects doc:name="List SPIFF Objects" config-ref="SPIFF_HMAC" />
```

### Reporting API — Export a Report

```xml
<spiff:request-export doc:name="Request CSV Export"
    config-ref="SPIFF_OAuth"
    reportId="#[vars.reportId]"
    format="csv" />
```

### Error Handling

```xml
<error-handler>
    <on-error-propagate type="SPIFF:AUTHENTICATION">
        <set-payload value='{"error": "SPIFF authentication failed"}' />
    </on-error-propagate>
    <on-error-propagate type="SPIFF:RATE_LIMIT">
        <set-payload value='{"error": "SPIFF rate limit exceeded, retry later"}' />
    </on-error-propagate>
</error-handler>
```

## SPIFF API Endpoints

### Import API Base URL
```
https://{subdomain}.spiff.com/api/external_data/{companyId}
```

| Operation | Method | Path |
|---|---|---|
| List Imports | GET | `/imports` |
| Create/Delete Records | POST | `/imports` |
| Get Import | GET | `/imports/{importId}` |
| Test Import | POST | `/imports/test` |

### Object API Base URL
```
https://{subdomain}.spiff.com/api/external_data/{companyId}
```

| Operation | Method | Path |
|---|---|---|
| Get All Objects | GET | `/objects` |
| Create Object | POST | `/objects` |
| Get Object | GET | `/objects/{objectId}` |
| Update Object | PUT | `/objects/{objectId}` |
| Delete Object | DELETE | `/objects/{objectId}` |

### Reporting API Base URL
```
https://{subdomain}.spiff.com/api/v1
```

| Operation | Method | Path |
|---|---|---|
| Get Reports | GET | `/reports` |
| Request Export | POST | `/reports/{reportId}/exports` |
| Get Export Status | GET | `/reports/{reportId}/exports/{exportId}` |
| Download Report | GET | `/reports/{reportId}/exports/{exportId}/download` |

## Test Suite

36 unit tests covering:

| Test Class | Tests | Coverage |
|---|---|---|
| `HmacSignatureUtilTest` | 7 | Signature format, deterministic output, null/empty body handling |
| `SpiffErrorTypeTest` | 3 | Error type enumeration, provider, parent hierarchy |
| `SpiffImportOperationsTest` | 7 | Method signatures, URL construction |
| `SpiffObjectOperationsTest` | 6 | CRUD method signatures, URL construction |
| `SpiffReportingOperationsTest` | 5 | Reporting method signatures |
| `SpiffOAuthConnectionTest` | 8 | JSON parsing, OAuth URL construction |

Run tests:

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home mvn test
```

## Technology Stack

| Component | Technology |
|---|---|
| SDK | MuleSoft Mule SDK (`mule-modules-parent:1.9.0`) |
| HTTP Client | `java.net.HttpURLConnection` (Java 8 compatible) |
| HMAC | `javax.crypto.Mac` / `HmacSHA256` |
| JSON Handling | Manual construction and lightweight string parsing |
| Testing | JUnit 4.13.2, Mockito 4.11.0 |
| Bytecode Target | Java 8 (runs on 8, 11, 17) |

## Security Notes

- Never hardcode `secretKey`, `clientId`, or `clientSecret` in XML files
- Use Mule secure properties or a vault for credentials
- The `signature` header is generated per-request with a fresh timestamp
- OAuth tokens are cached in-memory and auto-refreshed before expiry
