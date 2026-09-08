#!/usr/bin/env bash
set -euo pipefail

# Runs the file-specs-java release: version bump, build, publish to Artifactory,
# distribute release bundle, publish to Maven Central, then bump to the next
# development version and push.
#
# Expected environment variables:
#   NEXT_VERSION, NEXT_DEVELOPMENT_VERSION
#   ARTIFACTORY_URL, ARTIFACTORY_USER, ARTIFACTORY_APIKEY
#   ORG_GRADLE_PROJECT_sonatypeUsername, ORG_GRADLE_PROJECT_sonatypePassword
#   MVN_CENTRAL_SIGNING_KEY (base64-encoded GPG signing key), ORG_GRADLE_PROJECT_signingPassword

# Configure git identity
git config user.name "JFrog CI"
git config user.email "eco-system@jfrog.com"

# Check required inputs
test -n "$NEXT_VERSION" -a "$NEXT_VERSION" != "0.0.0"
test -n "$NEXT_DEVELOPMENT_VERSION" -a "$NEXT_DEVELOPMENT_VERSION" != "0.0.x-SNAPSHOT"

# Configure JFrog CLI servers
jf c rm --quiet
jf c add internal --url=$ARTIFACTORY_URL --user=$ARTIFACTORY_USER --password=$ARTIFACTORY_APIKEY
jf gradlec --use-wrapper --repo-resolve ecosys-maven-remote --repo-deploy ecosys-oss-release-local --deploy-maven-desc

# Run audit
jf audit

# Update release version
sed -i "s/\(currentVersion=\).*\$/\1${NEXT_VERSION}/" gradle.properties
git commit -am "[artifactory-release] Release version ${NEXT_VERSION} [skipRun]" --allow-empty
git tag ${NEXT_VERSION}

# Build and publish to Artifactory
jf gradle clean build -x test artifactoryPublish

# Publish build info
jf rt bag && jf rt bce
jf rt bp

# Distribute release bundle
jf ds rbc ecosystem-file-specs-java $NEXT_VERSION --spec=./release/specs/prod-rbc-filespec.json --spec-vars="version=$NEXT_VERSION" --sign
jf ds rbd ecosystem-file-specs-java $NEXT_VERSION --site="releases.jfrog.io" --sync

# Publish to Maven Central
export ORG_GRADLE_PROJECT_signingKey=$(echo "$MVN_CENTRAL_SIGNING_KEY" | base64 -d)
./gradlew clean build publishToSonatype closeAndReleaseSonatypeStagingRepository -x test -Psign

# Update next development version
sed -i "s/\(currentVersion=\).*\$/\1${NEXT_DEVELOPMENT_VERSION}/" gradle.properties
git commit -am "[artifactory-release] Next development version [skipRun]"

# Push changes
git push
git push --tags
