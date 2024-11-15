# jeap truststore maven plugin

Maven plugin to read X509 certificates from a git repository or a local directory tree and then assemble the
certificates in environment specific truststores. The certificates to add to the truststores must be specified by '
include' declarations. For every such declaration the certificate files in the directory {include} are added to all
truststores while the certificate files in the directory {include}/{environment} are only added to the truststore of the
corresponding environment.

The plugin has one goal: build-truststores.

The plugin respects the Maven offline mode (-o, --offline).

## Plugin configuration

| parameter                   | description                                                                                                              | default             |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------|---------------------|
| certificateRepositoryUrl    | URL of the git repository to fetch the certificates from                                                                 |                     |
| certificateRepositoryBranch | Name of the git repository branch to fetch the certificates from                                                         | master              |
| certificateRepositoryDir    | Path to a local directory tree to fetch the certificates from                                                            |                     |
| extensions.extension        | Only consider certificate files ending with one of the provided extensions                                               | cert, cer, crt, pem |
| includes.include            | Only consider certificates in directories matching the given include paths and their environment specific subdirectories | general             |
| environments.environment    | Only create truststores for the given environments                                                                       | dev, ref, abn, prod |
| trustStoreName              | Truststore base name, will be complemented with the corresponding environment suffix                                     | truststore          |
| trustStorePassword          | Password to create the trustore with                                                                                     | changeit            |
| trustStoreType              | Type of the truststore to create, one of JKS or PKCS12                                                                   | JKS                 |
| outputDirs.outputDir        | Write the truststores to the given directories                                                                           | target/classes      |

## Changes
This library is versioned using [Semantic Versioning](http://semver.org/) and all changes are documented in
[CHANGELOG.md](./CHANGELOG.md) following the format defined in [Keep a Changelog](http://keepachangelog.com/).

## Note

This repository is part the open source distribution of jEAP. See [github.com/jeap-admin-ch/jeap](https://github.com/jeap-admin-ch/jeap)
for more information.

## License

This repository is Open Source Software licensed under the [Apache License 2.0](./LICENSE).

## Note

This repository is part the open source distribution of jEAP. See [github.com/jeap-admin-ch/jeap](https://github.com/jeap-admin-ch/jeap)
for more information.

## License

This repository is Open Source Software licensed under the [Apache License 2.0](./LICENSE).
