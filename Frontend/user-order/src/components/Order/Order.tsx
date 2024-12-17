import {useEffect, useState} from 'react';
import axios from 'axios';
import {Text, Button, Spinner} from '@fluentui/react-components';
import {DeliverMethod, AddressRequest, BasketItem} from '../../Order.types.ts';
import {AddressForm} from '../Address/AddressForm.tsx';
import {DeliveryMethods} from '../Delivery/DeliveryMethods.tsx';
import {OrderSummary} from './OrderSummary.tsx';
import {PaymentForm} from './Payment/PaymentForm.tsx';
import {BasketItemsList} from "../Items/BasketItemList.tsx";
import {Elements} from "@stripe/react-stripe-js";
import {loadStripe} from "@stripe/stripe-js";
import { useNavigate } from 'react-router-dom';

// interface CreateOrderResponse {
//     clientSecret: string;
//     orderId: string;
// }
export default function Order() {

    const [deliverMethods, setDeliverMethods] = useState<DeliverMethod[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedDeliverId, setSelectedDeliverId] = useState<string | null>(null);
    const [address, setAddress] = useState<AddressRequest>({
        street: '',
        city: '',
        state: '',
        postalCode: '',
        country: ''
    });
    const [creatingOrder, setCreatingOrder] = useState(false);
    const [clientSecret, setClientSecret] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [paymentSuccess, setPaymentSuccess] = useState(false);
    const [basketPrice, setBasketPrice] = useState<number>(0);
    const [basketId, setBasketId] = useState<string | null>(null);
    const [basketItems, setBasketItems] = useState<BasketItem[] | null>(null);
    const [orderId, setOrderId] = useState<string | null>(null);
    const stripePromise = loadStripe("pk_test_51QOewrFtvRjEnnd4SFW0dfoeQYg6zXdAsqLl0EDBhCsbccvoWRlbXWpSKYIe0NgbYfUv5UCDSmob7yGPG7jJ60qs00vtb1gSXK");
    const navigate = useNavigate();
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const deliverResult = await axios.get(`${import.meta.env.VITE_API_URL}order/deliver`, {
                    headers: {'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
                })
                console.log('deliverResults: ', deliverResult)
                setDeliverMethods(deliverResult.data as DeliverMethod[]);

                const basketResult = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
                    headers: {'Authorization': `Bearer ${token}`}
                });
                console.log(basketResult.data);
                setBasketPrice(basketResult.data.summaryPrice);
                setBasketId(basketResult.data.basketId);
                setBasketItems(basketResult.data.basketProducts);

                setLoading(false);
            } catch {
                setError('Failed to fetch data');
                setLoading(false);
            }
        };
        fetchData();
    }, [token]);

    const handleCreateOrder = async () => {
        if (!selectedDeliverId) {
            setError('Please select a delivery method');
            return;
        }
        if (!address.street || !address.city || !address.postalCode || !address.country || !address.state) {
            setError('Please fill in all address fields');
            return;
        }
        if (!basketId) {
            setError('No basket found');
            return;
        }
        setCreatingOrder(true);
        setError(null);

        try {
            const token = localStorage.getItem('authToken');
            const orderReq = {
                addressRequest: address,
                deliverId: selectedDeliverId,
                basketId: basketId
            };
            console.log(orderReq);
            const result = await axios.post(`${import.meta.env.VITE_API_URL}order`, orderReq, {
                headers: {'Authorization': `Bearer ${token}`}
            });
            console.log(result.data);
            const {clientSecret} = result.data;
            const orderId = result.data.orderId;
            setClientSecret(clientSecret);
            setOrderId(orderId);
            setCreatingOrder(false);
        } catch {
            setError('Failed to create order');
            setCreatingOrder(false);
        }
    };

    const handlePaymentSuccess = () => {
        setPaymentSuccess(true);
        console.log('Payment successful', orderId);
        if(orderId){
            notifyBackend(orderId, 'COMPLETED');
        }
        navigate('/order-history');
    };

    const notifyBackend = async (orderId: string, status: string) => {
        try {
            const token = localStorage.getItem('authToken');
            const updateStatusRequest = {
                status: status,
                orderId: orderId
            };
            console.log('Updating order status:', updateStatusRequest);
            await axios.post(`${import.meta.env.VITE_API_URL}order/notify`, updateStatusRequest, {
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            });
            console.log('Order status updated successfully');
        } catch (error) {
            console.error('Failed to update order status:', error);
            setError('Failed to update order status');
        }
    };
    console.log(!clientSecret, basketItems)

    if (loading) {
        return <Spinner label={'Loading...'}/>;
    }

    if (paymentSuccess) {
        return <Text>Payment successful</Text>;
    }


    return (
        <div style={{maxWidth: '500px', margin: '0 auto'}}>
            <Text as="h1">Your Order</Text>
            {error && <Text style={{color: 'red'}}>{error}</Text>}

            {!clientSecret && basketItems && basketItems.length > 0 && (
                <>
                    <BasketItemsList items={basketItems}/>

                    <AddressForm
                        address={address}
                        setAddress={setAddress}
                        labelStreet="Street"
                        labelCity="City"
                        labelState="State"
                        labelPostalCode="Postal Code"
                        labelCountry="Country"
                    />

                    <DeliveryMethods
                        deliverMethods={deliverMethods}
                        selectedDeliverId={selectedDeliverId}
                        onChange={setSelectedDeliverId}
                        label="Choose Delivery Method"
                    />

                    <OrderSummary
                        totalPrice={basketPrice}
                        summaryLabel="Basket Summary"
                        totalLabel="Total"
                    />

                    <Button appearance="primary" disabled={creatingOrder} onClick={handleCreateOrder}>
                        {creatingOrder ? 'Creating Order...' : 'Place Order'}
                    </Button>
                </>
            )}

            {clientSecret && !paymentSuccess && (

                <Elements stripe={stripePromise}>
                    <PaymentForm
                        clientSecret={clientSecret}
                        onPaymentSuccess={handlePaymentSuccess}
                        onError={(msg) => setError(msg)}
                        payLabel="Pay Now"
                    />
                </Elements>

            )}
        </div>
    );
}
