import { LitState } from 'lit-element-state';

/**
 * This keeps state of the selected product & version
 * TODO: Add a way to clear chats and start over. Maybe save previous chats ?
 */
class ProductState extends LitState {

    constructor() {
        super();
    }

    static get stateVars() {
        return {
            productInfo: {},
            chats:[]
        };
    }
    
    setProductInfo(product, version){
        const newState = new Object();
        newState.product = product;
        newState.version = version;
        productState.productInfo = newState;
    }
    
    addChatMessage(message){
        if (productState.chats && productState.chats.length > 0) {
            productState.chats = [...productState.chats, message];
        } else {
            productState.chats = [message];
        }
    }
}

export const productState = new ProductState();