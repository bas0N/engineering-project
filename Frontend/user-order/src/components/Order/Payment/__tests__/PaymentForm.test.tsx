import { axe, toHaveNoViolations } from "jest-axe";
import { fireEvent, render, waitFor } from '@testing-library/react';
import { PaymentForm } from "../PaymentForm";
import { useElements, useStripe } from "@stripe/react-stripe-js";

expect.extend(toHaveNoViolations);

jest.mock('@stripe/react-stripe-js', () => {
    const Elements = ({ children }: { children: React.ReactNode }) => <div>{children}</div>;
    const useStripe = jest.fn().mockReturnValue({
        confirmCardPayment: jest.fn().mockImplementation((secret) => {
            if(secret === 'failure') {
                return {
                    error: {
                        message: 'testError'
                    }
                }
            }
            return {
                paymentIndent: {
                    status: secret === 'succeeded' ? 'succeeded' : 'not'
                }
            }
        })
    });
    const useElements = jest.fn().mockReturnValue({
        getElement: jest.fn().mockReturnValue({
            data: {}
        })
    });
    const CardElement = jest.fn(() => <div>Mock CardElement</div>);

    return { CardElement, Elements, useStripe, useElements };
});

const MOCK_ON_PAYMENT_SUCCESS = jest.fn();
const MOCK_ON_ERROR = jest.fn();

describe('Payment Form', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })
    it('Should have no a11y violations', async() => {
        const {container, getByText} = render(<PaymentForm 
            clientSecret={"succeeded"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);
        expect(await axe(container)).toHaveNoViolations();
        expect(getByText('paymentForm.title'));
    });

    it('Should be able to handle the payment stuff', async() => {
        const {getByText} = render(<PaymentForm 
            clientSecret={"succeeded"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_PAYMENT_SUCCESS).toHaveBeenCalled();
        });
    });

    it('Should be able to handle the network failure', async() => {
        const {getByText} = render(<PaymentForm 
            clientSecret={"failure"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_ERROR).toHaveBeenCalled();
        });
    });

    it('Should not call the onPaymentSuccess in case the status is inappropriate', async() => {
        const {getByText} = render(<PaymentForm 
            clientSecret={"succeeded_failure"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_PAYMENT_SUCCESS).not.toHaveBeenCalled();
        });
    });

    it('Should not do anything in case the elements.getElement is failing', async() => {
        const mockUseElements = jest.fn(() => ({
            getElement: jest.fn(() => undefined)
        }));
    
        (useElements as jest.Mock).mockImplementation(mockUseElements);
        
        const {getByText} = render(<PaymentForm 
            clientSecret={"succeeded"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_PAYMENT_SUCCESS).not.toHaveBeenCalled();
            expect(MOCK_ON_ERROR).not.toHaveBeenCalled();
        });
    });

    it('Should not do anything in case the useStripe is failing', async() => {
        const mockUseStripe = jest.fn(() => undefined);
    
        (useStripe as jest.Mock).mockImplementation(mockUseStripe);
        
        const {getByText} = render(<PaymentForm 
            clientSecret={"succeeded"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_PAYMENT_SUCCESS).not.toHaveBeenCalled();
            expect(MOCK_ON_ERROR).not.toHaveBeenCalled();
        });
    });

    it('Should not do anything in case the useElements is failing', async() => {
        const mockUseElements = jest.fn(() => undefined);
    
        (useElements as jest.Mock).mockImplementation(mockUseElements);
        
        const {getByText} = render(<PaymentForm 
            clientSecret={"succeeded"} 
            onPaymentSuccess={MOCK_ON_PAYMENT_SUCCESS} 
            onError={MOCK_ON_ERROR} 
            payLabel={"testLabel"} 
        />);

        expect(getByText('testLabel'));
        fireEvent.click(getByText('testLabel') as HTMLButtonElement);
        waitFor(() => {
            expect(MOCK_ON_PAYMENT_SUCCESS).not.toHaveBeenCalled();
            expect(MOCK_ON_ERROR).not.toHaveBeenCalled();
        });
    });
})