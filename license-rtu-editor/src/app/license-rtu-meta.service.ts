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
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LicenseRtuMetaService {
  schemaRegex = /(.*)\/schema\/(.*)\/(.*)/;
  schemaVersionToLayoutPathMap = {
    '1.0.0': '/assets/layouts/layout-1.0.0.json'
  };
  schemaCache: any = {};
  schemaLayoutCache: any = {};

  exampleUrlBase: string;

  constructor(private http: HttpClient) {
    this.exampleUrlBase = `assets/exampledata/`;
  }


  /**
   * Schema URL scheme/pattern
   * http://{{HOST}}/{{SUB_PATH}}/schema/{{VERSION}}/rtu-agreement.json
   */
  getSchemaMetadata(schemaUrl: string): any {
    const matches = schemaUrl.match(this.schemaRegex);
    const metadata: any = {};
    if (matches && matches.length === 4) {
      metadata.fullUrl = matches[0];
      metadata.parentPath = matches[1];
      metadata.version = matches[2];
      metadata.fileName = matches[3];
    }
    return metadata;
  }

  /**
   */
  getSchemaUrl(schemaVersion: string): string {
    return environment.schemaVersionToUrlMap[schemaVersion];
  }

  /**
   * Schema URL scheme/pattern
   * http://{{HOST}}/{{SUB_PATH}}/schema/{{VERSION}}/rtu-agreement.json
   */
  getSchema(schemaVersion: string): Observable<any> {
    const me = this;
    return new Observable((subscriber) => {
      const schema = me.schemaCache[schemaVersion];
      if (schema) {
        subscriber.next(schema);
      } else {
        const schemaUrl = me.getSchemaUrl(schemaVersion);
        // load schema
        me.http.get(`${schemaUrl}`).subscribe(schemaData => {
          // set it to the local map for reuse
          me.schemaCache[schemaVersion] = schemaData;
          subscriber.next(schemaData);
        });
      }
      return { unsubscribe() { } };
    });
  }

  getLayout(schemaVersion: string): Observable<any> {
    const me = this;
    return new Observable((subscriber) => {
      const layout = me.schemaLayoutCache[schemaVersion];
      if (layout) {
        subscriber.next(layout);
      } else {
        // load layout
        const layoutUrl = me.schemaVersionToLayoutPathMap[schemaVersion];
        if (layoutUrl) {
          me.http.get(`${layoutUrl}`).subscribe(layoutData => {
            // set it to the local map for reuse
            me.schemaLayoutCache[schemaVersion] = layoutData;
            subscriber.next(layoutData);
          });
        }
      }
      return { unsubscribe() { } };
    });
  }

  getSample(id: string): any {
    return this
      .http
      .get(`${this.exampleUrlBase}/${id}`);
  }
}
