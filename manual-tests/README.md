# Manual Tests Module

This module contains manual/real-server tests and is excluded from normal unit-test runs by default.

## Purpose

- Run tests that require:
  - a real gRPC server
  - a real Chrome browser/chromedriver
  - a real API token
- Keep these tests out of regular `mvn test` / `mvn install` in core modules.

## Configuration

1. Copy:
   - `src/test/resources/application.properties.example`
   - to `src/test/resources/application.properties`
2. Fill in local values:
   - `testrigor.apiToken`
   - `webdriver.chrome.driver`
   - `chrome.binary`
   - optionally `testrigor.grpc.uri`, `testrigor.grpc.port`, `testrigor.grpc.use-tls`, `chrome.args`, `test.page.url`
     (gRPC target is read into `GrpcEndpointConfig` and passed to the client; omitted URI/port default to production)
   - optionally `chromedriver.verbose=true` for verbose ChromeDriver logs
   - optionally `chromedriver.log.path=target/chromedriver.log` for a log file

You can also override values with JVM/system properties:

- `-Dtestrigor.apiToken=...`
- `-Dwebdriver.chrome.driver=...`
- `-Dchrome.binary=...`
- `-Dtestrigor.grpc.uri=...`
- `-Dtestrigor.grpc.port=...`
- `-Dtestrigor.grpc.use-tls=true|false` (optional; when omitted, TLS is used for port 443 only)
- `-Dchrome.args=--remote-allow-origins=*,--headless=new`
- `-Dtest.page.url=...`
- `-Dchromedriver.verbose=true`
- `-Dchromedriver.log.path=target/chromedriver.log`

Optional external file:

- `-Dconfig.file=/absolute/path/to/application.properties`

## Running

From repo root:

```bash
mvn -pl manual-tests -am test -DmanualTests.skip=false
```

From module:

```bash
cd manual-tests
mvn test -DmanualTests.skip=false
```

By default, tests are skipped in this module unless `-DmanualTests.skip=false` is set.
