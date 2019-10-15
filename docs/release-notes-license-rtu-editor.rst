.. ===============LICENSE_START================================================
.. Acumos CC-BY-4.0
.. ============================================================================
.. Copyright (C) 2019 Nordix Foundation
.. ============================================================================
.. This Acumos documentation file is distributed by Nordix Foundation.
.. under the Creative Commons Attribution 4.0 International License
.. (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
..      http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END==================================================
..

============================================
License RTU Editor Release Notes
============================================

Version 0.1.0 17 Oct 2019
-------------------------
- RTU editor changes for Supplier/Subscriber - `ACUMOS-3115 <https://jira.acumos.org/browse/ACUMOS-3115>`_

  - As a Supplier, I can

    - select / apply pre-defined RTU agreement examples
    - import / apply sample RTU agreement from a file
    - download RTU agreement editor changes to a file
    - save RTU agreement to a LUM server and receive/download
      RTU LUM agreement (asset usage agreement) from LUM

  - As a Subscriber, I can

    - import / apply RTU LUM agreement from a file
    - fetch RTU LUM agreement from LUM server
    - review (READ-ONLY) agreement from the RTU LUM agreement
    - add / edit restrictions to RTU LUM agreement
    - download RTU LUM agreement (restrictions) changes to a file
    - save restrictions (of RTU LUM agreement) to LUM server

  - schema changes - v1.0.1

    - added new attribute "schemaType" to distinguish between the
      RTU agreement and restrictions schema
    - used the value of "schemaType" to identify respective layout
    - added new schema for agreement "restrictions"

  - layout changes - v1.0.1

    - added layout for RTU agreement restrictions fields

Version 0.0.7 10 Oct 2019
-------------------------
- Remove redundant property under refinements - lum:swLicensor `ACUMOS-3553 <https://jira.acumos.org/browse/ACUMOS-3553>`_

Version 0.0.6 09 Oct 2019
-------------------------
-  Integration changes with clio `ACUMOS-3538 <https://jira.acumos.org/browse/ACUMOS-3538>`_

  - Show UI field to enter LUM server URL
  - Ability to save the RTU document to LUM server
  - Changes to RTU document as per LUM API expectations
  - Put the base version of app.version.ts in source repo so that
    editor can run in non-build environment like stackblitz

Version 0.0.5 03 Oct 2019
--------------------------
* License RTU editor - fix layout path - `ACUMOS-3494 <https://jira.acumos.org/browse/ACUMOS-3494>`_

Version 0.0.4 01 Oct 2019
--------------------------
* Support to process & render diff versions of license
  RTU documents - `ACUMOS-3494 <https://jira.acumos.org/browse/ACUMOS-3494>`_

Version 0.0.3 20 Sep 2019
--------------------------
* Provide a webform editor to create RTU Agreement `ACUMOS-3310 <https://jira.acumos.org/browse/ACUMOS-3310>`_
* Removed RTU prohibitions > constraints node `ACUMOS-3311 <https://jira.acumos.org/browse/ACUMOS-3311>`_
* Added RTU target > refinement node `ACUMOS-3312 <https://jira.acumos.org/browse/ACUMOS-3312>`_
* Added RTU assignee > refinement node `ACUMOS-3313 <https://jira.acumos.org/browse/ACUMOS-3313>`_
* Added RTU permission > constraints node `ACUMOS-3343 <https://jira.acumos.org/browse/ACUMOS-3343>`_
* RTU editor style improvements `ACUMOS-3468 <https://jira.acumos.org/browse/ACUMOS-3468>`_

Version 0.0.2 30 Aug 2019
--------------------------
* No change - but release along with other sub-project -
  License Manager Client Library

Version 0.0.1, 12 August 2019
-----------------------------

* Support loading as iframe doc `ACUMOS-3280 <https://jira.acumos.org/browse/ACUMOS-3280>`_
* Support using as web component `ACUMOS-3280 <https://jira.acumos.org/browse/ACUMOS-3280>`_
* License RTU Editor for creating RTU `ACUMOS-3079 <https://jira.acumos.org/browse/ACUMOS-3079>`_
