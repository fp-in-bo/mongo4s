# mongo4s - a functional wrapper on mongo driver
[![Build Status](https://travis-ci.com/fp-in-bo/mongo4s.svg?branch=master)](https://travis-ci.com/fp-in-bo/mongo4s) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.fpinbo/mongo4s_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.fpinbo/mongo4s_2.12) 
![Code of Consuct](https://img.shields.io/badge/Code%20of%20Conduct-Scala-blue.svg)

# Another mongo client for scala?

The purpose of this lib is to fit the gap between the async java driver and cats-effects/fs2.
Converting from the driver to fs2 is fairly simple with the usage of `fs2.interop.reactivestreams`.
We believe we can avoid that dependency, saving one indirection.

## NB

This is a WIP, everything is likely to change till the design will settle.

## [Head on over to the microsite](https://fp-in-bo.github.io/mongo4s)

## Quick Start

To use mongo4s in an existing SBT project with Scala 2.12 or a later version, add the following dependencies to your
`build.sbt` depending on your needs:

```scala
libraryDependencies ++= Seq(
  "dev.fpinbo" %% "mongo4s" % "<version>"
)
```

## Local dev

### run tests

- `docker-compose up -d`
- `sbt test`

### site

- build site

```
docker run \
  -v $PWD:/$PWD \
  -v ~/.sbt:/root/.sbt \
  -v ~/.ivy2:/root/.ivy2 \
  -v ~/.m2:/root/.m2 \
  -v ~/.coursier:/root/.coursier \
  -w /$PWD \
  -it k3vin/sbt-java8-jekyll \
  sbt site/clean site/makeMicrosite
```

- run at localhost:4000/mongo4s/

```
docker run \
  -v $PWD:/$PWD \
  -w /$PWD/site/target/site \
  -p 4000:4000 \
  -it k3vin/sbt-java8-jekyll \
  jekyll serve -b /mongo4s --host 0.0.0.0
```
