import { LitElement, html, css} from 'lit';
import { productState } from './state/product-state.js';

import '@vaadin/progress-bar';
import '@vaadin/horizontal-layout';
import '@vaadin/details';
import '@vaadin/vertical-layout';
import '@qomponent/qui-icons';

/**
 * This component shows the status screen
 */
export class ChappieStatus extends LitElement {

    intervalId;

    static styles = css`
        :host {
            display: flex;
            width: 100%;
        }
        .progressbar {
            width: 100%;
            padding-right: 20px;
        }
        .noprogressbar {
            width: 100%;
            padding-right: 20px;
            display: flex;
            gap: 15px;
            flex-direction: column;
        }
    `;
    
    static properties = {
        _status: {state: true},
        _visible: {state: true}
    }

    constructor() {
        super();
        this.productStateObserver = () => this.reload();
        productState.addObserver(this.productStateObserver);
        
        this._status = null;
        this._visible = false;
    }

    connectedCallback() {
        super.connectedCallback();
        this._visible = true;
        this.intervalId = setInterval(() => this.reload(), 5000); // Fetch data every 5 seconds
    }

    disconnectedCallback() {
        this._visible = false;
        clearInterval(this.intervalId);
        super.disconnectedCallback();
    }

    
    render() {
        if(this._status){
            let stage = this._status.runStage;
            if(stage === "INGESTED"){
                return this._renderNotBusy("Ingested","circle-check", "green" );
            }else if(stage === "IN_PROGRESS"){
                return html`<div class="progressbar">
                        ${this._renderProgress()}
                    </div>`;
            }else {
                return this._renderNotBusy("Not ingested yet","circle-xmark", "red" );
            }
        }else {
            return html`<div class="progressbar">
                            <vaadin-progress-bar indeterminate></vaadin-progress-bar>
                        </div>`;
        }
    }

    _renderNotBusy(heading, icon, color){
        return html`<div class="noprogressbar">
                                <vaadin-horizontal-layout theme="spacing padding">
                                    <far-icon icon="${icon}" size="50px" color="${color}"></far-icon> 
                                    <h2>${heading}</h2>
                                </vaadin-horizontal-layout>
                                <vaadin-vertical-layout>
                                    <span>Product: ${this._status.product.name}</span>
                                    <span>Version: ${this._status.version}</span>
                                    <span>Persentage ingested: ${this._status.persentageIngested} %</span>
                                    <span>Documents ingested: ${this._status.documentsIngested}</span>
                                    <span>Documents failed: ${this._getNumberOfFailedDocuments()}</span>
                                    <span>Ingestor: ${this._status.product.documentSet.ingestorName}</span>
                                    <span>Document loader: ${this._status.product.documentSet.documentLoaderName}</span>
                                    <span>Procecssing time: ${this._status.timeSinceStarted.toFixed(2)} seconds</span>
                                </vaadin-vertical-layout>
                                
                            </div>`;
    }

    _getNumberOfFailedDocuments(){
        if(this._status.failedQueue){
            return this._status.failedQueue.length;
        }
        return 0;
    }

    _renderProgress(){
        if(this._status.documentLocation){
            const bar = this._status.persentageIngested/100;
            return html`<vaadin-horizontal-layout style="justify-content: space-between;">
                        <label class="text-secondary" id="pblabel">Processing ${this._status.documentLocation.name}</label>
                        <span class="text-secondary">${this._status.persentageIngested.toFixed(2)}%</span>
                    </vaadin-horizontal-layout>

                    <vaadin-progress-bar aria-labelledby="pblabel" value="${bar}" theme="success"></vaadin-progress-bar>

                    <vaadin-details summary="Detailed information">
                        <vaadin-vertical-layout>
                            <span>Product: ${this._status.product.name}</span>
                            <span>Version: ${this._status.version}</span>
                            <span>Ingestor: ${this._status.product.documentSet.ingestorName}</span>
                            <span>Document Loader: ${this._status.product.documentSet.documentLoaderName}</span>
                            <span>Procecssing time: ${this._status.timeSinceStarted.toFixed(2)} seconds</span>
                        </vaadin-vertical-layout>
                      </vaadin-details>`;
        }
    }

    reload(){
        let url = "/api/status/" + productState.productInfo.product + "/" + productState.productInfo.version;

        fetch(url).then(response => {
                                if (response.ok) {
                                    return response.json();
                                }
                                return null;
                        }).then(data => {
                            if(data){
                                this._status = data;
                                console.log(this._status);
                            }
                        }).catch(error => {
                            throw new Error('Error fetching Status' + error);
                        });
        
    }

}
customElements.define('chappie-status', ChappieStatus);