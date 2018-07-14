Swing Explorer Developer's Guide
================================

#  Release Checklist

These are the actions that need to be done every time we make a release.

* Update VERSION to remove the "-SNAPSHOT" suffix. Must be done in:
 * `/VERSION`
 * `pom.xml`
* Create a "vX.Y.Z" tag and push the tag to GitHub.
 * This must be done by Maxim, since only repo owners can push tags.
* Run `mvn clean package publish` to publish the release to Maven Central.
 * This currently must be done by Andrew, since he has the keys for the Sonatype account.
* Run `make_dist` to create the release archives (`.tar.gz` and `.zip` files).
* "Draft" a new release on the project's GitHub Releases page, using the newly created tag.
* Upload the release archives to that Release on GitHub (as part of drafting it).
* Update VERSION again to be "X.(Y+1).0-SNAPSHOT"
 * In the same places as above.
