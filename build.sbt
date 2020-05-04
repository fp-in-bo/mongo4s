val catsV                  = "2.1.1"
val catsEffectV            = "2.1.3"
val fs2V                   = "2.0.0"
val log4catsV              = "1.0.1"
val log4jSlf4jImplV        = "2.13.2"
val specs2V                = "4.8.1"
val catsEffectScalaTestV   = "0.4.0"
val mongoDbReactiveDriverV = "4.0.2"

val kindProjectorV    = "0.11.0"
val betterMonadicForV = "0.3.1"

// Projects
lazy val `mongo4s` = project
  .in(file("."))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .aggregate(core)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(name := "mongo4s")

lazy val site = project
  .in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings)
  .dependsOn(core)
  .settings {
    import microsites._
    Seq(
      micrositeName := "mongo4s",
      micrositeDescription := "a functional wrapper on mongo driver",
      micrositeAuthor := "fp-in-bo",
      micrositeGithubOwner := "fp-in-bo",
      micrositeGithubRepo := "mongo4s",
      micrositeBaseUrl := "/mongo4s",
      micrositeDocumentationUrl := "https://www.javadoc.io/doc/dev.fpinbo/mongo4s_2.12",
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary"   -> "#4DB33D",
        "brand-secondary" -> "#3FA037",
        "brand-tertiary"  -> "#3FA037",
        "gray-dark"       -> "#3F3E42",
        "gray"            -> "#C1BEBC",
        "gray-light"      -> "#E8E7D5",
        "gray-lighter"    -> "#E8E7D5",
        "white-color"     -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      scalacOptions in Tut --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-numeric-widen",
        "-Ywarn-dead-code",
        "-Ywarn-unused:imports",
        "-Xlint:-missing-interpolator,_"
      ),
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
        file("CODE_OF_CONDUCT.md") -> ExtraMdFileConfig(
          "code-of-conduct.md",
          "page",
          Map(
            "title"    -> "code of conduct",
            "section"  -> "code of conduct",
            "position" -> "100"
          )
        ),
        file("LICENSE") -> ExtraMdFileConfig(
          "license.md",
          "page",
          Map("title" -> "license", "section" -> "license", "position" -> "101")
        )
      )
    )
  }

// General Settings
lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq(scalaVersion.value, "2.12.10"),
  scalafmtOnCompile := true,
  addCompilerPlugin(
    "org.typelevel" %% "kind-projector" % kindProjectorV cross CrossVersion.full
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForV),
  libraryDependencies ++= Seq(
    "org.typelevel"            %% "cats-core"                     % catsV,
    "org.typelevel"            %% "cats-effect"                   % catsEffectV,
    "co.fs2"                   %% "fs2-core"                      % fs2V,
    "co.fs2"                   %% "fs2-io"                        % fs2V,
    "co.fs2"                   %% "fs2-reactive-streams"          % fs2V,
    "org.mongodb"              % "mongodb-driver-reactivestreams" % mongoDbReactiveDriverV,
    "io.chrisdavenport"        %% "log4cats-slf4j"                % log4catsV,
    "org.apache.logging.log4j" % "log4j-slf4j-impl"               % log4jSlf4jImplV % Runtime,
    "com.codecommit"           %% "cats-effect-testing-scalatest" % catsEffectScalaTestV % Test
  )
)

// General Settings
inThisBuild(
  List(
    organization := "dev.fpinbo",
    developers := List(
      Developer("azanin", "Alessandro Zanin", "ale.zanin90@gmail.com", url("https://github.com/azanin")),
      Developer("al333z", "Alessandro Zoffoli", "alessandro.zoffoli@gmail.com", url("https://github.com/al333z")),
      Developer("r-tomassetti", "Renato Tomassetti", "r.tomas1989@gmail.com", url("https://github.com/r-tomassetti"))
    ),
    homepage := Some(url("https://github.com/fp-in-bo/mongo4s")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    pomIncludeRepository := { _ => false },
    scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-sourcepath",
      (baseDirectory in LocalRootProject).value.getAbsolutePath,
      "-doc-source-url",
      "https://github.com/fp-in-bo/mongo4s/blob/v" + version.value + "€{FILE_PATH}.scala"
    )
  )
)
