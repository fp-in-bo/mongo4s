val catsV = "2.0.0"
val catsEffectV = "2.0.0"
val shapelessV = "2.3.3"
val fs2V = "2.0.0"
val http4sV = "0.21.0-M6"
val circeV = "0.12.3"
val doobieV = "0.8.8"
val log4catsV = "1.0.1"
val specs2V = "4.8.1"
val catsEffectScalaTestV = "0.4.0"

val kindProjectorV = "0.11.0"
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
      micrositeAuthor := "Alessandro Zanin",
      micrositeGithubOwner := "azanin",
      micrositeGithubRepo := "mongo4s",
      micrositeBaseUrl := "/mongo4s",
      micrositeDocumentationUrl := "https://www.javadoc.io/doc/dev.fpinbo/mongo4s_2.12",
      micrositeGitterChannelUrl := "azanin/libraries", // Feel Free to Set To Something Else
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
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
            "title" -> "code of conduct",
            "section" -> "code of conduct",
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
    "org.typelevel" %% "cats-core" % catsV,
    "org.typelevel" %% "cats-effect" % catsEffectV,
    "co.fs2" %% "fs2-core" % fs2V,
    "co.fs2" %% "fs2-io" % fs2V,
    "org.mongodb" % "mongo-java-driver" % "3.12.3",
    "com.codecommit" %% "cats-effect-testing-scalatest" % catsEffectScalaTestV % Test
  )
)

// General Settings
inThisBuild(
  List(
    organization := "dev.fpinbo",
    developers := List(
      Developer(
        "azanin",
        "Alessandro Zanin",
        "ale.zanin90@gmail.com",
        url("https://github.com/azanin")
      )
    ),
    homepage := Some(url("https://github.com/azanin/mongo4s")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    pomIncludeRepository := { _ =>
      false
    },
    scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-sourcepath",
      (baseDirectory in LocalRootProject).value.getAbsolutePath,
      "-doc-source-url",
      "https://github.com/azanin/mongo4s/blob/v" + version.value + "€{FILE_PATH}.scala"
    )
  )
)
