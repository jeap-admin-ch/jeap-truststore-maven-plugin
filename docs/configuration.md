# Goal & configuration reference

The plugin has a single goal, implemented by `BuildTruststoresMojo`.

## Goal

| Goal                | Default phase        | Purpose                                                              |
|---------------------|----------------------|---------------------------------------------------------------------|
| `build-truststores` | `generate-resources` | Read certificates from a repository and write one truststore per environment |

## Parameters

| Parameter                      | Default                       | Description                                                                                                                  |
|--------------------------------|-------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `certificateRepositoryUrl`     | —                             | URL of the Git repository to fetch the certificates from. Mutually exclusive with `certificateRepositoryDir`.               |
| `certificateRepositoryBranch`  | `master`                      | Name of the Git repository branch to clone.                                                                                 |
| `certificateRepositoryDir`     | —                             | Path to a local directory tree to fetch the certificates from. Mutually exclusive with `certificateRepositoryUrl`.          |
| `extensions` (`extension`)     | `cert`, `cer`, `crt`, `pem`   | Only consider certificate files whose name ends with one of the given extensions (case-insensitive).                       |
| `includes` (`include`)         | `general`                     | Only consider certificates in directories matching the given include paths and their environment-specific subdirectories.   |
| `environments` (`environment`) | `dev`, `ref`, `abn`, `prod`   | Build one truststore per listed environment.                                                                               |
| `trustStoreName`               | `truststore`                  | Truststore base name; the environment suffix and file extension are appended (e.g. `truststore-dev.jks`).                   |
| `trustStorePassword`           | `changeit`                    | Password used to create the truststore.                                                                                     |
| `trustStoreType`               | `JKS`                         | Type of truststore to create: `JKS` (`.jks`) or `PKCS12` (`.p12`).                                                          |
| `outputDirs` (`outputDir`)     | `target/classes`              | Write each truststore to all of the given directories (created if missing).                                                |
| `tokenEnvironmentPropertyName` | `CERTIFICATES_REPO_GIT_TOKEN` | Name of the environment variable holding the personal access token used to clone the Git repository.                       |

Exactly one of `certificateRepositoryUrl` and `certificateRepositoryDir` must be configured; setting
both or neither fails the build.

## Output file naming

Each truststore file is named `<trustStoreName>-<environment>.<extension>`, where the extension is
`jks` for `trustStoreType` `JKS` and `p12` for `PKCS12`. With the defaults this yields, for example,
`truststore-dev.jks` in `target/classes`.

## Full example

The configuration below sets every parameter to a non-default value. It is meant only as an example
of the available options, not necessarily a sensible configuration (the defaults exist for that).

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
        <certificateRepositoryBranch>feature-xy</certificateRepositoryBranch>
        <trustStoreType>PKCS12</trustStoreType>
        <trustStoreName>my-trust-store</trustStoreName>
        <trustStorePassword>my-password</trustStorePassword>
        <outputDirs>
            <outputDir>target/classes</outputDir>
            <outputDir>some/other/path</outputDir>
        </outputDirs>
        <environments>
            <environment>dev</environment>
            <environment>ref</environment>
            <environment>abn</environment>
        </environments>
        <includes>
            <include>general</include>
            <include>general/admin</include>
            <include>technology/kafka</include>
            <include>application/jme/provider</include>
        </includes>
        <extensions>
            <extension>cert</extension>
            <extension>pem</extension>
        </extensions>
    </configuration>
</plugin>
```

To build from a local directory instead of a Git repository, replace `certificateRepositoryUrl`
(and `certificateRepositoryBranch`) with `certificateRepositoryDir`:

```xml
<configuration>
    <certificateRepositoryDir>cert-repo</certificateRepositoryDir>
</configuration>
```

## Related

- [jeap-truststore-maven-plugin](../README.md)
- [Getting started](getting-started.md)
- [Certificate repository layout & filtering](certificate-repository.md)
