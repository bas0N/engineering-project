import {useEffect, useState} from 'react';
import axios from 'axios';
import {Text, Button, Spinner} from '@fluentui/react-components';
import {DeliverMethod, AddressRequest, BasketItem} from './Order.types';
import {AddressForm} from '../Address/AddressForm';
import {DeliveryMethods} from '../Delivery/DeliveryMethods';
import {OrderSummary} from './OrderSummary/OrderSummary';
import {PaymentForm} from './Payment/PaymentForm';
import {BasketItemsList} from "../Items/BasketItemList";
import {Elements} from "@stripe/react-stripe-js";
import {loadStripe} from "@stripe/stripe-js";
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import '../../i18n/i18n';
import { OrderWrapper } from './Order.styled';

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
    const {t} = useTranslation();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const deliverResult = await axios.get(`${import.meta.env.VITE_API_URL}order/deliver`, {
                    headers: {'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
                })
                setDeliverMethods(deliverResult.data as DeliverMethod[]);

                const basketResult = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
                    headers: {'Authorization': `Bearer ${token}`}
                });
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
            const orderReq = {
                addressRequest: address,
                deliverId: selectedDeliverId,
                basketId: basketId
            };
            const result = await axios.post(`${import.meta.env.VITE_API_URL}order`, orderReq, {
                headers: {'Authorization': `Bearer ${token}`}
            });
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
        if(orderId){
            notifyBackend(orderId, 'COMPLETED');
        }
        navigate('/order-history');
    };

    const notifyBackend = async (orderId: string, status: string) => {
        try {
            const updateStatusRequest = {
                status: status,
                orderId: orderId
            };
            await axios.post(`${import.meta.env.VITE_API_URL}order/notify`, updateStatusRequest, {
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
            });
        } catch {
            setError('Failed to update order status');
        }
    };

    if (loading) {
        return <Spinner label={t('order.loading')}/>;
    }

    if (paymentSuccess) {
        return <Text>{t('order.paymentDone')}</Text>;
    }

    return (
        <OrderWrapper>
            <Text as="h1">{t('order.header')}</Text>
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
                        {creatingOrder ? t('order.creatingOrder') : t('order.placeOrder')}
                    </Button>
                </>
            )}

            {clientSecret && !paymentSuccess && (

                <Elements stripe={stripePromise}>
                    <PaymentForm
                        clientSecret={clientSecret}
                        onPaymentSuccess={handlePaymentSuccess}
                        onError={(msg) => setError(msg)}
                        payLabel={t('order.payNow')}
                    />
                </Elements>

            )}
        </OrderWrapper>
    );
}
