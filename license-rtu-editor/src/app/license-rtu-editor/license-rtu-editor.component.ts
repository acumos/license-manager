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
  jsonSchema: any;
  formLayout: any = {};
  jsonData: any = {};
  downloadType = 'txt';
  jsonFormOptions: any;
  isValid: any;
  rtuEditorForm: JsonSchemaFormComponent;
  queryParams: any = {};

  @Input() mode: string;


  constructor(private service: LicenseRtuMetaService) { }

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
    // Listen to messages from parent window
    this.bindEvent(window, 'message', (event) => {
      if (event.data.key === 'input') {
        this.jsonData = event.data.value;
      }
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
    if (this.mode === 'iframe') {
      this.initIframeSetup();
    }

    // load assets/rtu-schema.json
    this.service.getSchema().subscribe((data) => {
      this.jsonSchema = data;
    });

    this.jsonFormOptions = {
      addSubmit: false, // Add a submit button if layout does not have one
      debug: false,
      setSchemaDefaults: true, // Always use schema defaults for empty fields
      defautWidgetOptions: {
        feedback: true, // Show inline feedback icons
        listItems: 0 // Number of list items to initially add to arrays with no default value
      }
    };

    this.formLayout = [
      { type: 'flex', 'flex-flow': 'row wrap' },
      // { key: 'uid' },
      {
        key: 'target',
        type: 'fieldset',
        items: [{
          key: 'target.refinement',
          type: 'array',
          listItems: 1,
          items: [{
            type: 'div',
            displayFlex: true,
            'flex-direction': 'col',
            items: [{
              key: 'target.refinement[].leftOperand'
            },
            {
              key: 'target.refinement[].operator'
            },
            {
              key: 'target.refinement[].rightOperand',
              type: 'array',
              items: [
                {
                  key: 'target.refinement[].rightOperand[]'
                }
              ]
            }]
          }]
        }]
      },
      {
        key: 'assigner',
        type: 'fieldset',
        items: [
          'assigner.vcard:fn',
          'assigner.vcard:hasUrl',
          'assigner.vcard:hasEmail',

        ]
      },
      {
        key: 'assignee',
        type: 'fieldset',
        items: [
          'assignee.vcard:fn',
          // 'assignee.uid',
          'assignee.vcard:hasUrl',
          'assignee.vcard:hasEmail',
          {
            key: 'assignee.refinement',
            type: 'array',
            listItems: 0,
            items: [{
              type: 'div',
              displayFlex: true,
              'flex-direction': 'col',
              items: [{
                key: 'assignee.refinement[].leftOperand'
              },
              {
                key: 'assignee.refinement[].operator'
              },
              {
                key: 'assignee.refinement[].rightOperand',
                type: 'fieldset',
                items: [{
                  key: 'assignee.refinement[].rightOperand.@value'
                }]
              }]
            }]
          }
        ]
      },
      {
        key: 'permission',
        type: 'fieldset',
        items: [{
          type: 'div',
          displayFlex: true,
          'flex-direction': 'row',
          items: [
            {
              key: 'permission[].action',
              type: 'array',
              listItems: 1,
              items: [
                // adding the @type with condition: false as there is layout issue in schema editor code
                // - layout:
                //   For the FormArray type, the layout system creates a fieldset if the list item has
                //   MORE than one property to display and under that fieldset it adds a FormGroup control
                //   for each list item entry.
                // - problem:
                //   If the array item is configured to show single property then
                //   the DOM structure is created without fieldset container for the child items.
                //   Instead the FormGroup entry directly gets set as child item (with @type & action form controls)
                //   - Clicking the Remove icon to remove the array item, removes the "action" form control
                //     from the FormGroup parent and leaves the "@type" form control in parent FormGroup.
                //     Now, as that FormControl remains/cached in FormArray, it results in
                //     form validation error as the FormGroup has "@type" form control
                //     but missing required "action" property.
                // - solution:
                //    Expected behavior: The FormGroup (with form controls for @type & action) array item entry
                //    shall be removed from the parent FormArray.
                //    To avoid above problem, adding the @type with "condition: false"
                //    - (a) To keep the items entry > 1 (so that fieldset container gets added)
                //    - (b) To keep the @type property hidden on screen
                // {
                //   key: 'permission[].action[].@type',
                //   condition: 'false'
                // },
                {
                  key: 'permission[].action'
                }
              ]
            }, {
              key: 'permission[].constraint',
              type: 'array',
              listItems: 0,
              items: [
                {
                  key: 'permission[].constraint[].leftOperand'
                },
                {
                  key: 'permission[].constraint[].operator'
                },
                {
                  key: 'permission[].constraint[].rightOperand',
                  type: 'fieldset',
                  items: [
                    {
                      key: 'permission[].constraint[].rightOperand.@value'
                    }, {
                      key: 'permission[].constraint[].rightOperand.@type'
                    }
                  ]
                },
              ]
            },
          ]
        }]
      },
      {
        key: 'prohibition',
        type: 'fieldset',
        items: [{
          type: 'div',
          displayFlex: true,
          'flex-direction': 'row',
          items: [
            {
              key: 'prohibition[].action'
            }
          ]
        }]
      }
    ];
  }

  showExample(id: string) {
    this.service.getSample(id).subscribe((data) => {
      this.rtuEditorForm.formInitialized = false;
      this.jsonData = data;
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
    const formData = this.getLicenseProfileDataToSave();
    // - post license profile JSON data
    this.sendMessage({
      key: 'output',
      value: formData
    });
  }

  cancelRTU() {
    this.sendMessage({
      key: 'action',
      value: 'cancel'
    });
  }

  async downloadRTU() {
    const formData = this.getLicenseProfileDataToSave();
    this.download(formData);
  }

  getLicenseProfileDataToSave() {
    const formData = this.rtuEditorForm.jsf.validData;
    if (formData) {
      this.addUIDIfMissing(formData);
    }
    return formData;
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
    this.jsonData = formData;
    // save as json
    const mimeType = { type: 'application/json' };
    const data = JSON.stringify(this.jsonData);
    const fileName = 'rtu.json';

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
