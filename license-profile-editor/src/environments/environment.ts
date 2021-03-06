/*-
 * ===============LICENSE_START================================================
 * Acumos Apache-2.0
 * ============================================================================
 * Copyright (C) 2019 Nordix Foundation.
 * ============================================================================
 * This Acumos software file is distributed by Nordix Foundation
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END==================================================
 */

// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  schemaUrl: 'https://raw.githubusercontent.com/acumos/license-manager/master/license-manager-client-library/src/main/resources/schema/1.0.0/license-profile.json',
  boreasSchemaUrl: 'https://raw.githubusercontent.com/acumos/security-verification/boreas/license-manager-client-library/src/main/resources/license.schema.json',
  layoutVersionToUrlMap: {
    '1.0.0': 'assets/layouts/1.0.0/layout.json',
    boreas: 'assets/layouts/boreas/layout.json'
  },
  local: {
    schemaUrl: 'assets/schema/1.0.0/license-profile.json',
    boreasSchemaUrl: 'assets/schema/boreas/license-profile.json'
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
