# Certificate repository layout & filtering

The plugin reads certificates from a certificate repository — either a cloned Git repository
(`certificateRepositoryUrl`) or a local directory tree (`certificateRepositoryDir`). It does **not**
add every file it finds: a certificate is included in a truststore only if it passes the extension,
include-path and environment filters described below. This keeps each truststore explicit and limited
to the certificates that are genuinely required.

## Supported certificates

Only X.509 certificates are supported, encoded either as binary DER or as Base64 (PEM).

## Extensions

Files whose name does not end with one of the configured `extensions` are ignored. The match is
case-insensitive, so `gov-02.CERT` is treated the same as `gov-02.cert`. The default extensions are
`cert`, `cer`, `crt` and `pem`.

## Includes and environments

Each `include` is a relative path within the repository. For a given environment, an include selects
certificates from exactly two directories:

- `{include}` — certificates added to the truststores of **all** environments
- `{include}/{environment}` — certificates added **only** to the truststore of that environment

Subdirectories other than the environment directory are **not** searched. For example, with the
include `technology/kafka`, the `dev` truststore is built from certificates directly in:

- `technology/kafka`
- `technology/kafka/dev`

A certificate in `technology/kafka/abn` is therefore not added to the `dev` truststore, and a
certificate in a nested directory like `technology/kafka/extra/` is never selected unless that nested
path is itself configured as an include.

## Example

Given the repository tree:

```
general/
  root.cert                      # in every environment's truststore (include: general)
  admin/
    admin-01.cert                # in every environment's truststore (include: general/admin)
  swissgov/
    gov-01.cert                  # NOT selected unless general/swissgov is an include
technology/
  kafka/
    dev/kafka-dev.cert           # only in the dev truststore (include: technology/kafka)
    ref/kafka-ref.cert           # only in the ref truststore
    abn/kafka-abn.cert           # only in the abn truststore
```

With `includes` = `general`, `general/admin`, `technology/kafka` and the default environments, the
`dev` truststore contains `root.cert`, `admin-01.cert` and `kafka-dev.cert`; the `gov-01.cert` under
`general/swissgov` is excluded because `general/swissgov` is not an include and `swissgov` is not an
environment subdirectory of `general`.

## One truststore per environment

For each configured environment the plugin builds a separate truststore and writes it, named
`<trustStoreName>-<environment>.<extension>`, to every directory in `outputDirs`. See the
[configuration reference](configuration.md) for the naming and output details.

## Related

- [jeap-truststore-maven-plugin](../README.md)
- [Getting started](getting-started.md)
- [Goal & configuration reference](configuration.md)
