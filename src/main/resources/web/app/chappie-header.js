import { LitElement, html, css} from 'lit';
import { productState } from './state/product-state.js';
import { observeState } from 'lit-element-state';

import './chappie-nav.js';

/**
 * This component shows the header
 */
export class ChappieHeader extends observeState(LitElement) {

    static styles = css`
        
        header {
            display: flex;
            align-items: center;
            flex-direction: row;
            justify-content: space-between;
        }
    
        header img{
            height: 50px;
            padding: 15px;
        }

        .banner {
            display: flex;
            align-items: center;
            gap: 10px;
            cursor: pointer;
        }
    
        h3 {
            padding-left: 20px;
        }
    `;

    constructor() {
        super();
    }    

    connectedCallback() {
        super.connectedCallback();
    }

    disconnectedCallback() {
        super.disconnectedCallback();
    }

    render() {
        return html`<header>
                <div class="banner" onclick="location.href='/';">
                    <img src="/static/logo.png" alt="chappie"/> <h1 style="font-family:'Future Now';font-weight:normal;font-size:42px">Chappie</h1>
                    <h3>${productState.productInfo.product} ${productState.productInfo.version}</h3>
                </div>
                
                <chappie-nav></chappie-nav>
            </header>`;

    }

 }
 customElements.define('chappie-header', ChappieHeader);