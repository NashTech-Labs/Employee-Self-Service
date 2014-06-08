name := "employee-self-service"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(jdbc, anorm, cache, ws)

libraryDependencies ++= Seq(
  "org.webjars" 		%% 	"webjars-play" 		% "2.3.0-RC1",
  "org.webjars" 		%	"bootstrap" 		% "3.1.1-1",
  "org.webjars" 		% 	"bootswatch-yeti" 	% "3.1.1",
  "org.webjars" 		% 	"html5shiv" 		% "3.7.0",
  "org.webjars" 		% 	"respond" 			% "1.4.2"
)
