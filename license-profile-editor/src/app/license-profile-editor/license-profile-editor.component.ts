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

import { Component, OnInit, ViewEncapsulation, ViewChild } from '@angular/core';
import { LicenseProfileServiceService } from '../license-profile-service.service';
import { JsonSchemaFormComponent } from '@earlyster/angular6-json-schema-form';

@Component({
  selector: 'app-license-profile-editor',
  templateUrl: './license-profile-editor.component.html',
  styleUrls: ['./license-profile-editor.component.scss'],
  encapsulation: ViewEncapsulation.ShadowDom
})
export class LicenseProfileEditorComponent implements OnInit {

  title = 'Acumos License Editor';
  jsonSchema: any;
  formLayout: any = {};
  jsonData: any = {};
  isValid = false;
  downloadType = 'txt';
  jsonFormOptions: any;
  licenseProfileForm: JsonSchemaFormComponent;

  constructor(private service: LicenseProfileServiceService) { }

  ngOnInit() {
    this.service.getSchema().subscribe((data) => {

      // TODO add support for SPDX identifiers
      // TODO we can create enums (dropdowns) for known values ie allowedLicenses from siteConfig
      // TODO

      this.jsonFormOptions = {
        addSubmit: false, // Add a submit button if layout does not have one
      //  debug: true, // Don't show inline debugging information
      //   loadExternalAssets: true, // Load external css and JavaScript for frameworks
      //   returnEmptyFields: false, // Don't return values for empty input fields
      //   setSchemaDefaults: true, // Always use schema defaults for empty fields
        defautWidgetOptions: { feedback: true }, // Show inline feedback icons
      };

      this.jsonSchema = data;

      this.formLayout = [{
        type: 'flex', 'flex-flow': 'row wrap'
      }, {
        key: 'modelLicenses',
        type: 'array',
        items: [
          'modelLicenses[].keyword',
          'modelLicenses[].intro',
          {
            key: 'modelLicenses[].URL',
            title: 'URL'
          },
          'modelLicenses[].modelId',
          'modelLicenses[].swidTag',
          'modelLicenses[].warranty',
          {
            key: 'modelLicenses[].copyright',
            type: 'fieldset',
            items: [
              {
                key: 'modelLicenses[].copyright.year',
                type: 'number'
              },
              'modelLicenses[].copyright.company',
              'modelLicenses[].copyright.suffix'
            ]
          },
          {
            key: 'modelLicenses[].modelLimits',
            type: 'array',
            items: [
              'modelLicenses[].modelLimits[].id',
              'modelLicenses[].modelLimits[].name',
              'modelLicenses[].modelLimits[].desc',
              'modelLicenses[].modelLimits[].limit.type',
              'modelLicenses[].modelLimits[].limit.value'
            ]
          }]
        },
        {
          key: 'contentLicenses',
          type: 'array',
          listItems: 0,
          items: [
            'contentLicenses[].keyword',
            'contentLicenses[].path',
            'contentLicenses[].source'
          ]
        }
      ];

    });

  }

  isValidFn(isValid) {
    this.isValid = isValid;
  }

  @ViewChild(JsonSchemaFormComponent, {static: false})
  set licenseProfileFormCmp(cmp: JsonSchemaFormComponent) {
    if (!this.licenseProfileForm && cmp) {
      this.licenseProfileForm = cmp;
    }
  }

  async downloadLicenseProfile(downloadType) {
    const formData = this.licenseProfileForm.jsf.validData;
    this.download(formData, downloadType);
  }

  // create a yaml (text) or json file from the json model
  async download(formData, downloadType) {
    this.downloadType = downloadType;
    this.jsonData = formData;
    // save as json
    let mimeType;
    let data;
    let fileName;
    switch (this.downloadType) {
      case 'json':
        mimeType = { type: 'application/json' };
        data = JSON.stringify(this.jsonData);
        fileName = 'license.json';
        break;
      case 'txt':
        fileName = 'LICENSE';
        mimeType = { type: 'plain/text' };
        const json2yaml = await import('json2yaml');
        data = json2yaml.stringify(this.jsonData);
        break;
    }
    this.downloadFile(data, mimeType, fileName);
  }

  downloadFile(data: any, mimeType: { type: string }, filename: string) {
    const blob = new Blob([data], mimeType);
    const uri = window.URL.createObjectURL(blob);

    const link = document.createElement('a');
    if (typeof link.download === 'string') {
      document.body.appendChild(link); // Firefox requires the link to be in the body
      link.download = filename;
      link.href = uri;
      link.click();
      document.body.removeChild(link); // remove the link when done
    } else {
      location.replace(uri);
    }
  }

}
