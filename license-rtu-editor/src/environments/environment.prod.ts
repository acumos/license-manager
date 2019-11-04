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

export const environment = {
  production: true,
  // Dev & PROD refers to same path
  // but this can defer if we host the rtu schema somewhere
  // tslint:disable-next-line:max-line-length
  schemaUrl: 'https://raw.githubusercontent.com/acumos/license-manager/master/license-rtu-editor/src/assets/schema/1.0.1/rtu-agreement.json',
  restrictionsSchemaUrl: 'https://raw.githubusercontent.com/acumos/license-manager/master/license-rtu-editor/src/assets/schema/1.0.1/rtu-restrictions.json',
  layoutVersionToUrlMap: {
    '1.0.0': 'assets/layouts/1.0.0/layout.json',
    '1.0.1-rtu': 'assets/layouts/1.0.1/layout-rtu.json',
    '1.0.1-restrictions': 'assets/layouts/1.0.1/layout-restrictions.json'
  }
};