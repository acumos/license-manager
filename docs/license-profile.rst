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
License Profile
===============

Authoring a License Profile
---------------------------

License Profile or License.json must be a valid JSON file and must have the following attributes

.. jsonschema:: ../license-manager-client-library/main/resources/schema/1.0.0/license-profile.json

We have 3 different examples of license profiles here:

- Company B Proprietary License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/company-b-proprietary.json
   :language: json

- Vendor A OSS License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/vendor-a-oss.json
   :language: json

- Vendor A OSS License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/apache-2.0-company-a.json
   :language: json

Schema for License Profile
-----------------------------

This is a schema that we have in progress. In Clio release
the schema will change.

.. literalinclude:: ../license-manager-client-library/main/resources/schema/1.0.0/license-profile.json
   :language: json


Attaching a License.json with a model
-------------------------------------

- **During onboarding** you can onboard a model with a license.json file.

.. image:: images/attach-license.gif

- **After onboarding** or after creating a composite model you can
  add a license.json.

.. image:: images/attach-before-publishing.gif


Viewing the License Json in portal
----------------------------------

.. image:: images/view-license-in-portal.png

