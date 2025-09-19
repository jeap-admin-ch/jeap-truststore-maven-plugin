# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.20.0] - 2025-09-19

### Changed

- Update parent from 5.13.0 to 5.14.0

## [1.19.0] - 2025-08-26

### Changed

- Update parent from 5.12.1 to 5.13.0

## [1.18.0] - 2025-08-14

### Changed

- Update parent from 5.12.0 to 5.12.1

## [1.17.0] - 2025-08-05

### Changed

- Update parent from 5.11.0 to 5.12.0

## [1.16.0] - 2025-07-04

### Changed

- Update parent from 5.10.2 to 5.11.0
- update maven-api from 3.9.0 to 3.10.0
- update commons-io from 2.17.0 to 2.19.0

## [1.15.2] - 2025-06-30

### Changed

- Reuse the current log instance

## [1.15.1] - 2025-06-24

### Fixed

- Set the target directory in the clone command

## [1.15.0] - 2025-06-20

### Changed

- Enhanced Git clone logic to support private GitHub repositories:
    - If the environment variable CERTIFICATES_REPO_GIT_TOKEN is set, it is used as a token to authenticate the Git
      clone operation (e.g., for accessing private GitHub repositories in CI/CD pipelines).
    - If the token is not provided, the system's installed Git client is used instead, relying on locally configured
      credentials (e.g., SSH keys or credential helpers) for cloning in developer environments.

## [1.14.0] - 2025-06-18

### Changed

- Update parent from 5.10.1 to 5.10.2

## [1.13.0] - 2025-06-17

### Changed

- Update parent from 5.10.0 to 5.10.1

## [1.12.0] - 2025-06-13

### Changed

- Update parent from 5.9.0 to 5.10.0

## [1.11.0] - 2025-06-04

### Changed

- Update parent from 5.8.1 to 5.9.0

## [1.10.0] - 2025-05-26

### Changed

- Update parent from 5.8.0 to 5.8.1

## [1.9.0] - 2025-04-30

### Changed

- Update parent from 5.6.0 to 5.8.0

## [1.8.0] - 2025-03-06

### Changed

- Update parent from 5.5.5 to 5.6.0

## [1.7.0] - 2025-03-05

### Changed

- Update parent from 5.5.1 to 5.5.5

## [1.6.0] - 10.02.2025

### Changed

- Updated internal parent to 5.5.1

## [1.5.0] - 07.02.2025

### Changed

- Updated internal parent to 5.5.0
- Publish to maven central

## [1.4.0] - 31.12.2024

### Changed

- Updated internal parent to 5.4.1

## [1.3.2] - 18.12.2024

### Changed

- Updated internal parent to 5.4.0

## [1.3.1] - 17.12.2024

### Added

- credential scan (trufflehog)

## [1.3.0] - 09.12.2024

### Changed

- Prepare OSS distribution
- Update parent from 5.2.5 to 5.3.0

## [1.2.0] - 14.11.2024

### Changed

- Prepare OSS distribution
- Cleanup tests

## [1.1.0] - 07.11.2024

### Changed

- Updated internal parent

## [1.0.3] - 05.11.2024

### Added

- License definition & license plugins

## [1.0.2] - 12.12.2023

### Added

- The plugin respects the Maven offline mode (-o, --offline)

## [1.0.1] - 03.04.2023

### Fixed

- Adapted unit tests to the latest certificates and the current jme certificate repository contents

## [1.0.0] - 08.02.2022

### Added

Initial version
