export type DeliverMethod = {
    uuid: string;
    name: string;
    price: number;
};

export interface AddressRequest {
    street: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
}

export interface BasketItem {
    uuid: string;
    productId: string;
    name: string;
    quantity: number;
    imageUrl: string;
    price: number;
    summaryPrice: number;
    isActive: boolean;
}
