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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LicenseProfileServiceService {
  schemaUrl: string;
  constructor(private http: HttpClient) {
    // tslint:disable-next-line:max-line-length
    this.schemaUrl = `https://raw.githubusercontent.com/acumos/license-manager/master/license-manager-client-library/src/main/resources/license.schema.json`;
  }

  getSchema(): any {
    return this
      .http
      .get(`${this.schemaUrl}`);
  }
}
