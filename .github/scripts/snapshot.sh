#!/usr/bin/env bash
set -euo pipefail

# Runs the file-specs-java snapshot build: publish a snapshot to Artifactory,
# publish build info, and distribute the dev release bundle.
#
# Expected environment variables:
#   JFROG_CLI_BUILD_PROJECT
#   ARTIFACTORY_URL, ARTIFACTORY_USER, ARTIFACTORY_APIKEY
#   GITHUB_RUN_NUMBER (set automatically by GitHub Actions; export a value manually when running locally)

# Configure JFrog CLI servers
jf c rm --quiet
jf c add internal --url=$ARTIFACTORY_URL --user=$ARTIFACTORY_USER --password=$ARTIFACTORY_APIKEY
jf gradlec --use-wrapper --repo-resolve ecosys-maven-remote --repo-deploy ecosys-oss-snapshot-local --deploy-maven-desc

# Run audit
jf audit --project $JFROG_CLI_BUILD_PROJECT

# Delete former snapshots
jf rt del "ecosys-oss-snapshot-local/org/jfrog/filespecs/file-specs-java/*" --quiet

# Build, test and publish snapshot
jf gradle clean build artifactoryPublish

# Publish build info
jf rt bag && jf rt bce
jf rt bp

# Distribute release bundle
jf ds rbc ecosystem-file-specs-java-snapshot $GITHUB_RUN_NUMBER --spec=./release/specs/dev-rbc-filespec.json --sign
jf ds rbd ecosystem-file-specs-java-snapshot $GITHUB_RUN_NUMBER --site="releases.jfrog.io" --sync
