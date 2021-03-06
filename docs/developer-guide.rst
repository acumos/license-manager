
.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2019 Nordix Foundation
.. ===================================================================================
.. This Acumos documentation file is distributed by Nordix Foundation
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

===============
Developer Guide
===============

Uses of this library
--------------------

The license manager client library provides a java jar to perform the following functions:
  - Provide a schema for license profile
  - Validate license profiles against the schema
  - Register ML models with License Usage Manager
  - Verify entitlement with License Usage Manager
  - Support Portal back end
  - Fetch all the available SwidTags with License Usage Manager


How to build locally
--------------------

- to build and install

```
mvn clean install
```

This will require settings setup for nexus for acumos.
See
`CI / CD instructions`__.

__ https://wiki.acumos.org/display/AC/Acumos+Developer%27s+Guide+to+CI-CD+Resources+and+Processes+at+the+LF#AcumosDeveloper'sGuidetoCI-CDResourcesandProcessesattheLF-Quickstart:Createandsubmitachangeforreview

- To run license check and update headers

`Maven license plugin`__.

__ https://www.mojohaus.org/license-maven-plugin/

- Other goals:

Check license headers do this before review.
```
mvn license:check-file-header
```

To update license headers automatically.
Note that if you have a header it will not update the copyright or description.

```
mvn license:update-file-header
```

- To check java docs are working

```
mvn javadoc:javadoc
```

You can view javadocs in the path provided in console

- Fixing java docs

```
mvn javadoc:fix -DfixTags="param,return,throws,link"
```

- Unit test coverage should be above 40%

Check html page under here
```
license-manager-client-library/target/site/jacoco/org.acumos.licensemanager.client.model/index.source.html
```

- Check style

```
mvn checkstyle:check
```

Linux Foundation Build Jobs
---------------------------

- `Related LF Jenkins Jobs`__.

__ https://jenkins.acumos.org/view/license-manager/

- `LF Sonar reports`__.

__ https://sonar.acumos.org/dashboard?id=org.acumos.license-manager.license-manager%3Alicense-manager-client-library

- `Javadoc`__.

__ https://javadocs.acumos.org/org.acumos.license-manager/master/
