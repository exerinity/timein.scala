# timein.scala
Scala remake of [timein](https://github.com/exerinity/timein), a small C program for instantly getting the time for anywhere in the world.

The C version is dead-simple and assumes you know exactly what you want. The Scala version is a bit more lenient and tolerates more ambiguity in commands

After I started to learn Scala in April, this is my first standalone Scala project.

## Prerequisites
- To run a built package: Java 17 or newer
- To build from source: a Java 17+ JDK and Scala CLI. Build commands use Scala 3.8.4 and may download it on the first build
- For the Linux `make` commands: GNU Make. `make install` also uses GNU `install` and puts the executable in `~/.local/bin`

## Install and run
From this directory:
```bash
make install
```
If `timein` is not found after installation, add `~/.local/bin` to your PATH. To run without installing, use `make build` followed by `./timein`

### Run directly from source
From this directory, with Scala CLI installed:

```bash
scala run . --server=false --scala 3.8.4 --main-class timein.Main -- melbourne
```

## Usage
```bash
timein <city/timezone/offset|here>
```

Examples:
```bash
timein melbourne # Melbourne, Australia
timein here # system time
timein new york # New York, USA
timein Europe/London # London, UK from the IANA ID
timein +5:30 # directly query a UTC offset; UTC+5:30
timein -4 # ditto; UTC-4
timein 5 # ditto; UTC+5, unsigned offsets default to positive
timein about
timein --help
```