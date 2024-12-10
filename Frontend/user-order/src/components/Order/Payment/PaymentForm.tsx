import {CardElement, useElements, useStripe} from "@stripe/react-stripe-js";
import {Button, Text} from "@fluentui/react-components";
import {useState} from "react";

interface PaymentFormProps {
    clientSecret: string;
    onPaymentSuccess: () => void;
    onError: (errorMsg: string) => void;
    payLabel: string;
}

export function PaymentForm({ clientSecret, onPaymentSuccess, onError, payLabel }: PaymentFormProps) {
    const stripe = useStripe();
    const elements = useElements();
    const [processing, setProcessing] = useState(false);

    const handlePayment = async () => {
        if (!stripe || !elements) return;
        const cardElement = elements.getElement(CardElement);
        if (!cardElement) return;

        setProcessing(true);
        const { paymentIntent, error } = await stripe.confirmCardPayment(clientSecret, {
            payment_method: { card: cardElement }
        });

        if (error) {
            onError(error.message || 'Payment failed');
        } else if (paymentIntent && paymentIntent.status === 'succeeded') {
            onPaymentSuccess();
        }
        setProcessing(false);
    }

    return (
        <div>
            <Text>Enter your card details:</Text>
            <CardElement />
            <Button appearance="primary" disabled={processing} onClick={handlePayment}>
                {processing ? 'Processing...' : payLabel}
            </Button>
        </div>
    );
}