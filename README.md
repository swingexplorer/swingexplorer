swingexplorer-core
======================

Core files of the Swing Explorer tool.

##  Building from source

You must first have the swingexplorer-agent library built and installed in your local Maven repo with `mvn install` done in that repo. swingexplorer-agent is available [on GitHub](https://github.com/swingexplorer/swingexplorer-agent).

Do `mvn package` to build and package swingexplorer-core.

To test whether the build worked, you can run `dev-tools/launch_sample.sh` to run the sample application.
