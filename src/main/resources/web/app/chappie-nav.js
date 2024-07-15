import { LitElement, html, css} from 'lit';
import { Router } from '@vaadin/router';
import '@vaadin/tabs';

const router = new Router(document.getElementById('outlet'));
router.setRoutes([
    {path: '/', component: 'chappie-chat', name: 'Chat'},
    {path: '/status', component: 'chappie-status', name: 'Status'},
]);

/**
 * This component shows the navigation
 */
export class ChappieNav extends LitElement {

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
        const routes = router.getRoutes();
        return html`<vaadin-tabs> 
                        ${routes.map((r) => {
                            return html`<vaadin-tab>
                                    <a href="${r.path}">
                                        <span>${r.name}</span>
                                    </a>
                                </vaadin-tab>`;
                        })}
                    </vaadin-tabs>`;
    }

 }
 customElements.define('chappie-nav', ChappieNav);