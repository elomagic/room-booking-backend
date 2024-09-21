# Room Booking Backend

This is a prototype for displaying appointments from an Exchange instance.

---

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![build workflow](https://github.com/elomagic/room-booking-backend/actions/workflows/build.yml/badge.svg)](https://github.com/elomagic/room-booking-backend/actions)
[![GitHub issues](https://img.shields.io/github/issues-raw/elomagic/room-booking-backend)](https://github.com/elomagic/room-booking-backend/issues)
[![Latest](https://img.shields.io/github/release/elomagic/room-booking-backend.svg)](https://github.com/elomagic/room-booking-backend/releases)
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/elomagic/room-booking-backend/graphs/commit-activity)
[![Buymeacoffee](https://badgen.net/badge/icon/buymeacoffee?icon=buymeacoffee&label)](https://www.buymeacoffee.com/elomagic)

![UI-example1.png](docs%2FUI-example1.png)

## Requirements

* The latest version of Docker or something similar to run a Docker image.
* Runtime Microsoft Exchange instance with enabled EWS endpoints

## Installation And Setup

### Run demo mode

```shell
docker run --name room-display -p 48080:8080 -d elo2017/remote-booking
```

Frontend now accessible on http://localhost:48080

## Configuration

### Backend

Connectivity to the Exchange server will be done in the backend and the rest in the frontend.

tbc.

```properties
# Change this to your api key. The key will be required by the frontend to access the backend
rb.apiKey=<CreateYourOwnApiKey>
# Change this to your pin. This will be required by the frontend to enter settings page
rb.pin=123456

# Supported api types are "ews" and "graph".
rb.ext.apiType=ews
# Regular expression to filter resources
rb.resourcesFilter=.*

# MS Graph setup
rb.ext.graph.uri=https://graph.microsoft.com

# MS EWS setup
rb.ext.ews.uri=https://<Exchange Server>/EWS/Exchange.asmx
rb.ext.ews.autoDiscover=false
rb.ext.ews.credentials.username=<YourUsername>
rb.ext.ews.credentials.password=<YourSecret>
```

### Frontend

The enter the configuration page, enter the pin. The Pin in the demonstration mode is still ```123456``` and can differ
from the productive mode 

## Using the library

## Contributing

Pull requests and stars are always welcome. For bugs and feature requests, [please create an issue](../../issues/new).

### Versioning

Versioning follows the semantic of [Semantic Versioning 2.0.0](https://semver.org/)

## License

The dt-tool is distributed under [Apache License, Version 2.0][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0