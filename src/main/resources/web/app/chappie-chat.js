import { LitElement, html, css} from 'lit';
import { productState } from './state/product-state.js';
import { observeState } from 'lit-element-state';

import '@vaadin/message-list';
import '@vaadin/message-input';
import '@vaadin/progress-bar';

/**
 * This component shows the chat screen
 */
export class ChappieChat extends observeState(LitElement) {
    static styles = css`
        :host {
            display: flex;
            gap: 10px;
            width: 100%;
            height: 100%;
            justify-content: space-around;
            flex-direction: column;
        }
    
        .inputBar {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            align-items: center;
            width: 90%;
        }
    
        vaadin-message-list {
            height: 100%;
        }
    
        vaadin-message-input {
            width: 100%;
        }
    `;
    
    static properties = {
        _waiting: {state: true}
    }

    constructor() {
        super();
        this._handleServerResponse = (event) => this._receiveMessage(event.detail);
    }

    connectedCallback() {
        super.connectedCallback();
        document.addEventListener('server-response', this._handleServerResponse, false);
    }

    disconnectedCallback() {
        document.removeEventListener('server-response', this._handleServerResponse, false);
        super.disconnectedCallback();
    }
    
    render() {
        
        return html`
            <vaadin-message-list .items="${productState.chats}"></vaadin-message-list>
            
            ${this._renderLoadingMessage()}
            <div class="inputBar">
                <vaadin-message-input @submit="${this._sendMessage}"></vaadin-message-input>
            </div>`;
    }

    _renderLoadingMessage(){
        if(this._waiting){
            return html`${this._message}
            <vaadin-progress-bar indeterminate></vaadin-progress-bar>`;
        }
    }
    
    _sendMessage(event){
        this._waiting = true;
        let message = this._createServerRequestChatEntry(event.detail.value);
        this._addToChat(message);
        const mesageEvent = new CustomEvent('server-request', {detail: event.detail.value});
        document.dispatchEvent(mesageEvent);
    }

    _receiveMessage(event) {
        this._waiting = false;
        let status = event.status;
        let data = event.data;
        if(status === "ok"){
            let message = this._createServerResponseChatEntry(data);
            this._addToChat(message);
        }else {
            let error = this._createServerErrorChatEntry(data);
            this._addToChat(error);
        }
    }

    _createServerRequestChatEntry(message){
        return this._createChatEntry(message, "Me", 3);
    }

    _createServerResponseChatEntry(message){
        return this._createChatEntry(message, "Chappie", 2, "/static/logo.png");
    }

    _createServerErrorChatEntry(message){
        return this._createChatEntry(JSON.stringify(message), "Error", 1);
    }

    _createChatEntry(message, name, index, imgUrl){
        let entry = new Object();
        entry.text = message;
        entry.time = new Date().toLocaleTimeString();
        entry.userName= name;
        entry.userColorIndex = index;
        entry.userImg = imgUrl;
        return entry;
    }

    _addToChat(item) {
        if(this._isJsonString(item.text)){
            let r = JSON.parse(item.text);
//            console.log("type=" + r.type);
//            console.log("uuid=" + r.uuid);
//            console.log("product=" + r.product);
//            console.log("version=" + r.version);
//            console.log("title=" + r.title);
//            console.log("categories=" + r.categories);
//            console.log("filename=" + r.filename);
//            console.log("url=" + r.url);
//            console.log("answer=" + r.answer);
            item.text = r.answer;
        }
        
        productState.addChatMessage(item);
    }

    _isJsonString(str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    }

}
customElements.define('chappie-chat', ChappieChat);