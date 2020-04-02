# Release Protocol for the stationxml-seed-conveter
The stationxml-seed-conveter uses the Maven archetecture as it's distrubution manager. The converter's pom file contains both the maven-release-plugin and nexus-staging-maven-plugin to help with the release process to the oss.sonatype.org repositiory. A user's ~/.m2/setting.xml file must be configured with the correct user names and passwords for this process to work. The pom.xml must also have the correct distribution management information. The converter uses: 
	<distributionManagement>
		<repository>
			<id>ossrh</id>
			<url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
		</repository>
		<snapshotRepository>
			<id>ossrh</id>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
		</snapshotRepository>
	</distributionManagement>

Once the pom file is correctly configured, a converter instanse can be released using the maven-release-plugin command. SNAPSHOT must be appended to the the version for the release-plugin to work correctly.

To release run the following commands:
mvn release:clean
mvn release:prepare
mvn release:perform

Before every minor version of a stationxml-seed-converter release, a release canidate must be generated. The release candiate follows the same protocols of any release except that the qualifier -RC-# is appended to the end of the version. The release canidate step allows all outstanding issues to be closed and for the software to have a final stage of testing before a minor version release is performed. 



See the article below for further instructions. 
https://central.sonatype.org/pages/apache-maven.html
https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN400

