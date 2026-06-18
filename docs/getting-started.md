# Getting started

This page shows how to add `jeap-truststore-maven-plugin` to a Maven build and have it assemble
environment-specific truststores from a certificate repository. For the full goal and parameter
reference see [Goal & configuration reference](configuration.md); for how the plugin selects
certificates see [Certificate repository layout & filtering](certificate-repository.md).

## 1. Add the plugin

Add the plugin to the `build/plugins` section of your `pom.xml` and bind its single goal
`build-truststores`. The goal is bound by default to the `generate-resources` phase, so the
truststores are available before the resources are packaged.

```xml
<plugin>
    <groupId>ch.admin.bit.jeap</groupId>
    <artifactId>truststore-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>build-truststores</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <certificateRepositoryUrl>https://example.org/scm/myteam/my-certificates.git</certificateRepositoryUrl>
    </configuration>
</plugin>
```

The only mandatory configuration is the source of the certificates. Configure exactly one of:

- `certificateRepositoryUrl` — a Git repository the plugin clones, or
- `certificateRepositoryDir` — a local directory tree.

Configuring both, or neither, fails the build. With no further configuration the plugin applies its
[defaults](configuration.md).

## 2. Run the build

```bash
./mvnw generate-resources
```

With the defaults, the plugin writes one `JKS` truststore per environment (`dev`, `ref`, `abn`,
`prod`) to `target/classes`, named `truststore-<environment>.jks` (for example
`truststore-dev.jks`). It only includes certificates found under the `general` include path and its
environment-specific subdirectories, considering files ending in `cert`, `cer`, `crt` or `pem`.

## 3. Authenticating the Git clone

When `certificateRepositoryUrl` points at a private repository, the plugin clones it as follows:

- If the environment variable named by `tokenEnvironmentPropertyName` (default
  `CERTIFICATES_REPO_GIT_TOKEN`) is set, its value is used as a personal access token for the clone.
  This is the typical setup for CI/CD pipelines.
- Otherwise the plugin falls back to the system-installed Git client, relying on locally configured
  credentials (SSH keys, credential helpers, Git configuration). This is the typical setup for local
  development.

## 4. Offline builds

The plugin respects Maven offline mode (`-o`, `--offline`). When the build is offline and no
`certificateRepositoryDir` is configured, the plugin logs a warning and skips truststore creation
instead of attempting a clone. Configure `certificateRepositoryDir` to build truststores from a local
directory while offline.

## Related

- [jeap-truststore-maven-plugin](../README.md)
- [Goal & configuration reference](configuration.md)
- [Certificate repository layout & filtering](certificate-repository.md)
