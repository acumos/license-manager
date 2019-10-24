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

When onboarding a model or in the license profile tab under my models you can:

- Upload a license profile json file
- Select from default list of license profiles templates and modify
- Create a new license profile

When you modify or create a new license profile the portal will present you with the license profile editor.

License Profile Json Schema 1.0.0
---------------------------------

This is a schema that we have in progress. In Clio release
the schema will change.

.. literalinclude:: ../license-manager-client-library/src/main/resources/schema/1.0.0/license-profile.json
   :language: json


License Profile Examples
------------------------

We have 3 different examples of license profiles here:

- Company B Proprietary License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/company-b-proprietary.json
   :language: json

- Vendor A OSS License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/vendor-a-oss.json
   :language: json

- Apache 2.0 License Profile
.. literalinclude:: ../license-profile-editor/src/assets/examples/apache-2.0-company-a.json
   :language: json

These should be customized for your licensing requirements.

Add new or update license profile templates
-------------------------------------------

Prerequisite: Must have access to Common data service swagger.

Steps:
1. get admin CDS user Id (not user name)
2. POST /lic/templ

From the CDS swagger find the license controller 
.. image:: images/license-controller-cds.png

Example payload creating a new license profile template for Company Z
.. code-block:: json

  {
    "priority": 10, 
    "template": "{\"$schema\":\"https://raw.githubusercontent.com/acumos/license-manager/master/license-manager-client-library/src/main/resources/schema/1.0.0/license-profile.json\",\"keyword\":\"Company-Z-Commercial\",\"licenseName\":\"Company Z Commmercial License\",\"copyright\":{\"year\":2019,\"company\":\"Company Z\",\"suffix\":\"All Rights Reserved\"},\"softwareType\":\"Machine Learning Model\",\"companyName\":\"Company Z\",\"contact\":{\"name\":\"Company Z Team Member\",\"URL\":\"http://Company-Z.com\",\"email\":\"support@Company-Z.com\"},\"additionalInfo\":\"http://Company-Z.com/licenses/Company-Z-Commercial\",\"rtuRequired\":true}",
    "templateId": 4,
    "templateName": "Company Z License",
    "userId": "12345678-abcd-90ab-cdef-1234567890ab"
  }

priority - can allow you to order license profile templates

3. Go back to Onboarding and notice new license profile template

.. image:: images/company-z-new-license-profile-template.png

4. If you have security verification license checking enabled make sure you have the correct configuration.
:doc:`License Scanning setup<../../security-verification/security-verification-service/docs/user-guide.html#for-admins>`


Attaching a License Profile with a model
----------------------------------------

- **During onboarding** you can onboard a model with a license.json file.

.. image:: images/attach-license.gif

- **After onboarding** or after creating a composite model you can
  add a license.json.

.. image:: images/attach-before-publishing.gif

Editing A license profile with the license profile editor

Viewing the License Profile in portal
----------------------------------

.. image:: images/view-license-in-portal.png

