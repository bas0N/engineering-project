// Order.tsx
import { useEffect, useState } from 'react';
import axios from 'axios';
import { Text, Button, Spinner } from '@fluentui/react-components';
import { DeliverMethod, AddressRequest} from '../../Order.types.ts';
import { AddressForm } from '../Address/AddressForm.tsx';
import { DeliveryMethods } from '../Delivery/DeliveryMethods.tsx';
import { OrderSummary } from './OrderSummary.tsx';
import { PaymentForm } from './Payment/PaymentForm.tsx';

export default function Order() {

    const [deliverMethods, setDeliverMethods] = useState<DeliverMethod[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedDeliverId, setSelectedDeliverId] = useState<string | null>(null);
    const [address, setAddress] = useState<AddressRequest>({ street: '', city: '', state: '', postalCode: '', country: '' });
    const [creatingOrder, setCreatingOrder] = useState(false);
    const [clientSecret, setClientSecret] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [paymentSuccess, setPaymentSuccess] = useState(false);
    const [basketPrice, setBasketPrice] = useState<number>(0);
    const [basketId, setBasketId] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const deliverResult = await axios.get(`${import.meta.env.VITE_API_URL}order/deliver`);
                setDeliverMethods(deliverResult.data as DeliverMethod[]);

                const token = localStorage.getItem('authToken');
                const basketResult = await axios.get(`${import.meta.env.VITE_API_URL}basket`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                setBasketPrice(basketResult.data.summaryPrice);
                setBasketId(basketResult.data.basketId);

                setLoading(false);
            } catch (e) {
                setError('Failed to fetch data');
                setLoading(false);
            }
        };
        fetchData();
    }, []);

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
            const result = await axios.post(`${import.meta.env.VITE_API_URL}order`, orderReq, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            const { clientSecret } = result.data;
            setClientSecret(clientSecret);
            setCreatingOrder(false);
        } catch (e: any) {
            setError('Failed to create order');
            setCreatingOrder(false);
        }
    };

    const handlePaymentSuccess = () => {
        setPaymentSuccess(true);
    };

    if (loading) {
        return <Spinner label={'Loading...'} />;
    }

    if (paymentSuccess) {
        return <Text>Payment successful</Text>;
    }

    return (
        <div style={{ maxWidth: '500px', margin: '0 auto' }}>
            <Text as="h1">Place Your Order</Text>
            {error && <Text style={{color:'red'}}>{error}</Text>}

            {!clientSecret && (
                <>
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
                <PaymentForm
                    clientSecret={clientSecret}
                    onPaymentSuccess={handlePaymentSuccess}
                    onError={(msg) => setError(msg)}
                    payLabel="Pay Now"
                />
            )}
        </div>
    );
}
