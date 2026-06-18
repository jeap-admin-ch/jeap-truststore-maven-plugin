# AGENTS.md

Guidance for AI coding agents working **in this repository**. For how to *use* the plugin in a
consuming build, read [README.md](README.md) and the [docs/](docs/) folder instead.

## Project

`jeap-truststore-maven-plugin` is a single-module Maven plugin. It reads X.509 certificates from a Git
repository or a local directory tree, filters them by configured extensions, include paths and
environments, assembles the selected certificates into Java truststores (`JKS` or `PKCS12`), and writes
one truststore per environment to one or more output directories. This lets microservices derive their
truststores at build time from a centrally managed certificate repository.

## Repository layout

```
pom.xml                                                              # Single-module Maven plugin (packaging=maven-plugin)
src/main/java/ch/admin/bit/jeap/truststoreplugin/
  mojo/BuildTruststoresMojo.java                                    # @Mojo "build-truststores"; reads parameters, orchestrates the build
  gitcertrepo/GitCertRepo.java                                      # Clones the certificate Git repo (jgit with token, or system git client)
  filecertrepo/FileCertRepo.java                                    # Selects certificate files by include path, environment and extension
  truststore/TrustStore.java                                        # Wraps a java.security.KeyStore: add certificates, store to file
  truststore/TruststoreType.java                                    # Enum JKS ("jks") / PKCS12 ("p12")
src/test/java/...                                                   # JUnit 5 tests (maven-plugin-testing-harness)
src/test/resources/pom-*.xml, test-certs/                          # Test plugin configurations and a sample certificate tree
Jenkinsfile, publiccode.yml, CHANGELOG.md, LICENSE
```

The single goal `build-truststores` is implemented by `BuildTruststoresMojo` and bound by default to
the Maven `generate-resources` phase.

## Build & test

```bash
./mvnw verify        # full build incl. tests
./mvnw install       # build and install the plugin locally
```

- Parent: `ch.admin.bit.jeap:jeap-internal-spring-boot-parent`.
- Tests use `maven-plugin-testing-harness` and JUnit 5; fixture POMs live under `src/test/resources`
  and certificate fixtures under `src/test/resources/test-certs`.

## jEAP conventions

- Java packages live under `ch.admin.bit.jeap.truststoreplugin...`.
- Plugin parameters are declared with `@Parameter` on `BuildTruststoresMojo`; the goal name is set with
  `@Mojo(name = "build-truststores")`. Keep parameter names, defaults and the
  [configuration reference](docs/configuration.md) in sync.
- Only X.509 certificates are supported, either binary DER or Base64-encoded (PEM).
- The token for cloning the Git repository is read from an environment variable whose name is
  configurable via `tokenEnvironmentPropertyName` (default `CERTIFICATES_REPO_GIT_TOKEN`).

## Docs

When changing public behaviour (goal, parameters, defaults, repository layout rules), update the
matching focused file under [docs/](docs/) and the documentation index in the README.

## Versioning

- Semantic Versioning; all changes documented in [CHANGELOG.md](./CHANGELOG.md) (Keep a Changelog format).
- This repository has no child modules; bump the project `<version>` in `pom.xml` directly.
- When working on a feature branch, use `x.y.z-SNAPSHOT` in the POM. Always keep the `-SNAPSHOT`
  postfix in the POM; CI removes it when releasing. Do not use the SNAPSHOT postfix elsewhere
  (CHANGELOG, publiccode.yml).
- Keep changelog entries concise and follow existing patterns.
- Keep commit messages short, use the JIRA ID from the branch name as a prefix, do not use
  conventional commits (for example: "JEAP-1234 Added feature X").
- When bumping the version, also update the changelog and update version/date in `publiccode.yml`.
