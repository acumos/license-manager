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
export class LicenseProfileServiceService {

  urlCache: any = {};

  constructor(private http: HttpClient) {
  }

  getInitialData() {
    return {
      $schema: environment.schemaUrl
    };
  }

  getComponentInput(input: any) {

    const me = this;

    return new Observable(subscriber => {

      const compInput = {
        schema: '',
        layout: '',
        data: input
      };
      const errors = [];
      let schemaUrl: string;
      if (input && input.$schema) {
        schemaUrl = input.$schema;
      } else if (input && input.modelLicenses) {
        schemaUrl = environment.boreasSchemaUrl;
      } else {
        // applicable to default license profile templates OR
        // any input data (clio+) without $schema value
        // defaults to the schema supported by this editor instance
        schemaUrl = input.$schema = environment.schemaUrl;
      }

      me.getUrlData(schemaUrl).subscribe((schema) => {
        // console.log(schema);
        // schema loaded
        compInput.schema = schema;

        // find the respective layout based on the schema version
        let layoutUrl: string;
        if (schema.version) {
          layoutUrl = environment.layoutVersionToUrlMap[schema.version];
        } else if (input && input.modelLicenses) {
          layoutUrl = environment.layoutVersionToUrlMap.boreas;
        } else {
          errors.push('The schema (' + schemaUrl + ') referenced by the document'
            + ' is missing the version field.');
          subscriber.error(errors);
          return;
        }
        if (layoutUrl) {
          me.getUrlData(layoutUrl).subscribe((layout) => {
            compInput.layout = layout;
            subscriber.next(compInput);
          }, error => {
            console.log('Unable to load layout' + layoutUrl, error);
            errors.push('Unable to load the layout based on the schema version ' + schema.version);
            subscriber.error(errors);
          });
        } else {
          errors.push('Unable to find layout info based on the schema version ' + schema.version);
          errors.push('Please make sure that the document refers to supported schema.');
          subscriber.error(errors);
        }

      }, error => {
        console.log('Unable to load schema ' + schemaUrl, error);
        errors.push('Unable to load schema ' + schemaUrl);
        errors.push('Please make sure that the $schema URL given in the document is accessible.');
        subscriber.error(errors);
      });

    });
  }

  /**
   * Fetches data (GET request) for the given url
   *
   * @param url url to fetch the data
   */
  getUrlData(url: string): Observable<any> {
    const me = this;
    return new Observable((subscriber) => {
      const urlData = me.urlCache[url];
      if (urlData) {
        subscriber.next(urlData);
      } else {
        // load schema
        me.http.get(`${url}`).subscribe(urlResp => {
          // set it to the local map for reuse
          me.urlCache[url] = urlResp;
          subscriber.next(urlResp);
        }, error => {
          console.log('Error loading ' + url, error);
          subscriber.error(error);
        });
      }
      return { unsubscribe() { } };
    });
  }

}
