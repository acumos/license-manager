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

===============
License Manager
===============

License Manager Client Library
------------------------------

The license manager client library provides a java jar to perform the following functions:
  - Provide a schema for license profile
  - Validate license profiles against the schema
  - Register ML models with License Usage Manager 
  - Verify entitlement with License Usage Manager
  - Support Portal back end

.. toctree::
       :maxdepth: 2

       api-docs.rst
       user-guide.rst
       developer-guide.rst
       release-notes.rst

License Profile editor
----------------------

The license profile editor provides a micro front end for the creation of the license profile.
  - Integrates into Portal Front end via iframe
  - Can work standalone 

.. toctree::
       :maxdepth: 2

       user-guide-license-profile-editor.rst
       dev-guide-license-profile-editor.rst
       release-notes-license-profile-editor.rst


License Right To Use (RTU) editor
---------------------------------

The license right to use (RTU) editor provides a micro front end for the managment of agreements and right.
 - suppliers create an agreement with a subscriber.
 - subscriber can review the agreement from the suppliers and save into license usage manager
 - subscriber can add restrictions on top of the agreement from the supplier such as specifing specific users who can use the software.
 

.. toctree::
       :maxdepth: 2
       user-guide-license-rtu-editor.rst
       dev-guide-license-rtu-editor.rst
       release-notes-license-rtu-editor.rst


License Usage Management (LUM)
------------------------------

The license usage management (LUM) documentation is located here: 
TODO add link here