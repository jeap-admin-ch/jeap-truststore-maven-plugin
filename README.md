# jeap-truststore-maven-plugin

`jeap-truststore-maven-plugin` reads X.509 certificates from a Git repository or a local directory
tree, filters them by configurable criteria, and assembles them into environment-specific Java
truststores (`JKS` or `PKCS12`) at build time. This lets a microservice derive its truststores from a
centrally managed certificate repository, so expiring certificates can be rolled out across services
without touching each service's source.

* Single goal `build-truststores` bound to the `generate-resources` phase
* Sources certificates from a Git repository (`certificateRepositoryUrl`) or a local directory
  (`certificateRepositoryDir`)
* Builds one truststore per configured environment, with shared and environment-specific certificates
* Authenticates Git clones via a personal-access-token environment variable, or falls back to the
  system Git client (SSH keys, credential helpers)
* Respects Maven offline mode (`-o`, `--offline`)

## Documentation

Start with [Getting started](docs/getting-started.md), then follow the links below.

| Topic                                                  | File                                                       |
|--------------------------------------------------------|------------------------------------------------------------|
| Getting started (add the plugin, build a truststore)   | [docs/getting-started.md](docs/getting-started.md)         |
| Goal & configuration reference                         | [docs/configuration.md](docs/configuration.md)             |
| Certificate repository layout & filtering              | [docs/certificate-repository.md](docs/certificate-repository.md) |

## Modules

Group id for the plugin is `ch.admin.bit.jeap`. The version is managed by the jEAP Spring Boot parent.

| Module                   | Purpose                                                                    |
|--------------------------|----------------------------------------------------------------------------|
| `truststore-maven-plugin`| Maven plugin that assembles environment-specific truststores from X.509 certificates |

## Changes

This library is versioned using [Semantic Versioning](http://semver.org/) and all changes are documented in
[CHANGELOG.md](./CHANGELOG.md) following the format defined in [Keep a Changelog](http://keepachangelog.com/).

## Note

This repository is part the open source distribution of jEAP.
See [github.com/jeap-admin-ch/jeap](https://github.com/jeap-admin-ch/jeap)
for more information.

## License

This repository is Open Source Software licensed under the [Apache License 2.0](./LICENSE).
