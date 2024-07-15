import { LitElement, html, css} from 'lit';
import { productState } from './state/product-state.js';
import { observeState } from 'lit-element-state';

const indicator = document.querySelectorAll('.loading-indicator');
if (indicator.length > 0) {
  indicator[0].remove();
}

export class ChappieApp extends observeState(LitElement) {
    static webSocket;
    static serverUri;
    static initQueue = [];
    
    static styles = css`
        :host {
            display: flex;
            gap: 10px;
            width: 100%;
            height: 100%;
            justify-content: space-around;
            flex-direction: column;
        }
    `;
    
    constructor() {
        super();
        if (!ChappieApp.webSocket) {
            if (window.location.protocol === "https:") {
                ChappieApp.serverUri = "wss:";
            } else {
                ChappieApp.serverUri = "ws:";
            }
            ChappieApp.serverUri += "//" + window.location.host + "/ws/chappie/chat";
          
            ChappieApp.connect();
        }
    }

    connectedCallback() {
        super.connectedCallback();
        document.addEventListener('server-request', this._handleServerRequest);
    }

    disconnectedCallback() {
        document.removeEventListener('server-request', this._handleServerRequest);
        super.disconnectedCallback();
    }

    _handleServerRequest(event){
        
        const message = {
            type: "chat",
            product: "Quarkus",
            version: "3.12.2",
            message: event.detail
        };
        
        console.log(message);
        if(ChappieApp.webSocket){
            ChappieApp.webSocket.send(JSON.stringify(message));
        }else{
            ChappieApp.initQueue.push(JSON.stringify(message));
        }
    }
    
    static connect() {
        ChappieApp.webSocket = new WebSocket(ChappieApp.serverUri);

        ChappieApp.webSocket.onopen = function (event) {
            while (ChappieApp.initQueue.length > 0) {
                ChappieApp.webSocket.send(ChappieApp.initQueue.pop());
            }
        };

        ChappieApp.webSocket.onmessage = function (event) {
            let detail = new Object();
            detail.status = "ok";
            detail.data = event.data;
            
            console.log("Message " + detail.data);
            
            const mesageEvent = new CustomEvent('server-response', {detail: detail});
            document.dispatchEvent(mesageEvent);
        }

        ChappieApp.webSocket.onclose = function (event) {
            setTimeout(function () {
                ChappieApp.connect();
            }, 100);
        };

        ChappieApp.webSocket.onerror = function (error) {
            let detail = new Object();
            detail.status = "error";
            detail.data = error;
            const mesageEvent = new CustomEvent('server-response', {detail: detail});
            document.dispatchEvent(mesageEvent);
            ChappieApp.webSocket.close();
        }
    }

    render() {
        return html`<slot></slot>`;
    }
}

customElements.define('chappie-app', ChappieApp);