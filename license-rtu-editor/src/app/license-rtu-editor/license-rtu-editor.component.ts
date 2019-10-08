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

import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { LicenseRtuMetaService } from '../license-rtu-meta.service';
import { JsonSchemaFormComponent } from '@earlyster/angular6-json-schema-form';
import { APP_VERSION } from '../../environments/app.version';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-license-rtu-editor',
  templateUrl: './license-rtu-editor.component.html',
  styleUrls: ['./license-rtu-editor.component.scss']
})

/**
 * RTU form editor
 * Will construct a minimal ODRL RTU file for
 * use by LUM.
 */
export class LicenseRtuEditorComponent implements OnInit {

  title = 'Acumos Right to Use Editor';
  appVersion = APP_VERSION.app_git_version;
  isValid: any;
  downloadType = 'txt';
  errors: string[];
  rtuFormInput: any;
  rtuEditorForm: JsonSchemaFormComponent;
  queryParams: any = {};
  lumServerUrl = '/lum';

  @Input() mode: string;


  constructor(private service: LicenseRtuMetaService,
              private snackBar: MatSnackBar) { }

  /**
   * JavaScript Get URL Parameter parsing
   *
   * This is utility function defined here but can be movied to
   * Utils.ts if more usage required.
   */
  initQueryParams() {
    const search = decodeURIComponent(window.location.href.slice(window.location.href.indexOf('?') + 1));
    const definitions = search.split('&');

    definitions.forEach((val) => {
      const parts = val.split('=', 2);
      this.queryParams[parts[0]] = parts[1];
    });
  }

  // addEventListener and old browser support
  bindEvent(element, eventName, eventHandler) {
    if (element.addEventListener) {
      element.addEventListener(eventName, eventHandler, false);
    } else if (element.attachEvent) {
      element.attachEvent('on' + eventName, eventHandler);
    }
  }

  initIframeSetup() {
    const me = this;
    // Listen to messages from parent window
    me.bindEvent(window, 'message', (event) => {
      if (event.data.key === 'input') {
        me.doSetup(event.data.value);
      }
    });
    console.log('license-rtu-editor: iframe init - send message');
    me.sendMessage({
      key: 'init_iframe',
      value: ''
    });
  }

  // Send a message to the parent
  sendMessage(msgObj) {
    window.parent.postMessage(msgObj, '*');
  }

  ngOnInit() {

    // attempt to read mode value from the query param, if not given as input
    if (!this.mode) {
      this.initQueryParams();
      if (this.queryParams.mode) {
        this.mode = this.queryParams.mode;
      }
    }

    this.doSetup(this.service.getInitialData());

    if (this.mode === 'iframe') {
      this.initIframeSetup();
    }

  }

  doSetup(input: any) {

    const me = this;
    me.errors = [];

    me.service.getComponentInput(input).subscribe((compInput: any) => {
      me.rtuFormInput = {
        schema: compInput.schema,
        layout: compInput.layout,
        data: compInput.data,
        options: {
          addSubmit: false, // Add a submit button if layout does not have one
          //  debug: true, // Don't show inline debugging information
          //   loadExternalAssets: true, // Load external css and JavaScript for frameworks
          //   returnEmptyFields: false, // Don't return values for empty input fields
          setSchemaDefaults: true, // Always use schema defaults for empty fields
          defautWidgetOptions: {
            feedback: true, // Show inline feedback icons
            listItems: 0 // Number of list items to initially add to arrays with no default value
          }
        }
      };
    }, errors => {
      me.errors = errors;
    });

  }

  showExample(id: string) {
    const me = this;
    me.service.getSample(id).subscribe((data) => {
      me.rtuEditorForm.formInitialized = false;
      me.doSetup(data);
    });
  }

  isValidFn(isValid) {
    this.isValid = isValid;
  }

  @ViewChild(JsonSchemaFormComponent, { static: false })
  set rtuEditorFormCmp(cmp: JsonSchemaFormComponent) {
    if (!this.rtuEditorForm && cmp) {
      this.rtuEditorForm = cmp;
    }
  }

  saveRTU() {
    const formData = this.getLicenseRtuDataToSave();
    this.putData(formData);
  }

  cancelRTU() {
    this.sendMessage({
      key: 'action',
      value: 'cancel'
    });
  }

  async downloadRTU() {
    const formData = this.getLicenseRtuDataToSave();
    this.download(formData);
  }

  addLumWrapperObjects(formData) {
    // add wrapper object
    const licensor = formData.assigner['vcard:fn'];
    const agreementId = formData.uid;
    return {
      assetUsageAgreement: {
        softwareLicensorId: licensor,
        assetUsageAgreementId: agreementId,
        // supplier agreement
        agreement: formData
      }
    };
  }

  getLicenseRtuDataToSave() {
    const formData = this.rtuEditorForm.jsf.validData;
    let lumRtuData;
    if (formData) {
      const dataToSave = Object.assign({}, formData);
      this.addUIDIfMissing(dataToSave);
      lumRtuData = this.addLumWrapperObjects(dataToSave);
    }
    return lumRtuData;
  }

  /**
   * Auto generates the uid need by LUM
   * Makes sure that the values are encoded
   * @param formData
   */
  addUIDIfMissing(formData: any) {
    // add uid to: agreement
    // get company name of software licensor from Assigner
    const companyName = formData.assigner['vcard:fn'];
    let type = '';
    if (!formData.uid) {
      type = 'agreement';
      formData.uid = this.createUID(type, companyName);
    } else {
      formData.uid = encodeURI(formData.uid);
    }

    if (formData.permission) {
      formData.permission.forEach(rule => {
        if (!rule.uid) {
          rule.uid = this.createUID('permission', companyName);
        } else {
          rule.uid = encodeURI(rule.uid);
        }

      });
    }

    if (formData.prohibition) {
      formData.prohibition.forEach(rule => {
        if (!rule.uid) {
          rule.uid = this.createUID('prohibition', companyName);
        } else {
          rule.uid = encodeURI(rule.uid);
        }

      });
    }
  }

  createUID(type: string, companyName: string): string {
    return `acumos://software-licensor/${encodeURIComponent(companyName)}/${type}/${this.uuidv4()}`;
  }

  uuidv4() {
    // tslint:disable-next-line:whitespace
    return ([1e7] as any + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, (c: any) =>
      // tslint:disable-next-line:no-bitwise
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
  }

  // create a yaml (text) or json file from the json model
  async download(formData) {
    // save as json
    const mimeType = { type: 'application/json' };
    const data = JSON.stringify(formData);
    const fileName = 'rtu.json';

    this.downloadFile(data, mimeType, fileName);

  }

  async putData(formData) {
    const me = this;
    const url = this.lumServerUrl + '/api/v1/asset-usage-agreement/?assetUsageAgreementId=' + formData.assetUsageAgreement.assetUsageAgreementId;
    try {
      // Default options are marked with *
      const response = await fetch(url, {
        method: 'PUT', // *GET, POST, PUT, DELETE, etc.
        mode: 'cors', // no-cors, *cors, same-origin
        cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
        credentials: 'same-origin', // include, *same-origin, omit
        headers: {
          'Content-Type': 'application/json'
        },
        redirect: 'follow', // manual, *follow, error
        referrer: 'no-referrer', // no-referrer, *client
        body: JSON.stringify(formData) // body data type must match "Content-Type" header
      });
      const data = await response.json(); // parses JSON response into native JavaScript objects
      if (!data || data.error) {
        me.showSnackBar('Failed to save RTU document to LUM server', 'Retry');
      } else {
        me.showSnackBar('RTU document saved to LUM server');
      }
      return data;
    } catch (err) {
      me.showSnackBar('Failed to save RTU document to LUM server', 'Retry');
    }
  }

  showSnackBar(message: string, action: string = '') {
    this.snackBar.open(message, action, {
      duration: 3000,
      panelClass: 'acumos-snackbar'
    });
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
