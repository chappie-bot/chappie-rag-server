import { LitElement, html, css} from 'lit';
import { productState } from './state/product-state.js';
import '@vaadin/select';
import '@vaadin/progress-bar';


/**
 * This component shows the product and version
 */
export class ChappieProduct extends LitElement {

    static styles = css`
        .products {
            display: flex;
            flex-direction: column;
            padding-left: 10px;
            padding-right: 30px;
        }
    `;

    static properties = {
        _selectedVersion: {state: true},
        _products: {state: true},
        _versions: {state: true}
    }

    constructor() {
        super();
        this._products = null;
        this._versions = null;
        this._selectedProduct = null;
        this._selectedVersion = null;
    }

    connectedCallback() {
        super.connectedCallback();
        this._fetchProducts();    
    }

    _fetchProducts(){
        fetch('/api/product').then(response => {
                                    if (!response.ok) {
                                        throw new Error('Error fetching all available products');
                                    }
                                    return response.json();
                            }).then(data => {
                                this._products = data.map(obj => ({
                                    label: obj.name,
                                    value: obj.name
                                }));
                            }).catch(error => {
                                throw new Error('Error fetching all available versions' + error);
                            });
    }
    
    _fetchVersions(product){
        let url = "/api/product/" + product + "/tags";
        fetch(url).then(response => {
                                    if (!response.ok) {
                                        throw new Error('Error fetching all available versions');
                                    }
                                    return response.json();
                            }).then(data => {
                                this._versions = data.map(obj => ({
                                    label: obj.name,
                                    value: obj.name
                                }));
                            }).catch(error => {
                                throw new Error('Error fetching all available versions' + error);
                            });
    }

    render() {
        return html`<div class="products">
                ${this._renderProducts()}
                ${this._renderVersions()}
            </div>`;
    }

    _renderProducts() {
        
        if(this._products){
            return html`<vaadin-select label="Product" @value-changed="${this._handleProductChanged}"
                        .items="${this._products}"
                        .value="${this._products[0].value}"
                      ></vaadin-select>
                  `;
        }else{
            return html`Loading products
            <vaadin-progress-bar indeterminate></vaadin-progress-bar>`;
        }
    }

    _renderVersions() {
        
        if(this._versions){
            return html`<vaadin-select label="Version" @value-changed="${this._handleVersionChanged}"
                        .items="${this._versions}"
                        .value="${this._versions[0].value}"
                      ></vaadin-select>
                  `;
        }else{
            return html`Loading versions
            <vaadin-progress-bar indeterminate></vaadin-progress-bar>`;
        }
    }

    _handleProductChanged(event){
        this._selectedProduct = event.detail.value;
        this._fetchVersions(this._selectedProduct);
    }
    
    
    _handleVersionChanged(event){
        this._selectedVersion = event.detail.value;
        productState.setProductInfo(this._selectedProduct, this._selectedVersion);
    }
}
customElements.define('chappie-product', ChappieProduct);