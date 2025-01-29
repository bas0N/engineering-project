import { fireEvent, render } from "@testing-library/react";
import '@testing-library/jest-dom';
import Order from "../Order";
import { axe, toHaveNoViolations } from "jest-axe";
import axios from "axios";
import { AddressFormProps } from "../../Address/AddressForm";
import { AddressRequest, BasketItem, DeliverMethod } from "../Order.types";
import { OrderSummaryProps } from "../OrderSummary/OrderSummary";
import { DeliveryMethodsProps } from "../../Delivery/DeliveryMethods";
import { PaymentFormProps } from "../Payment/PaymentForm";
import { useNavigate } from "react-router-dom";

expect.extend(toHaveNoViolations);

jest.mock('@stripe/stripe-js', () => ({
    loadStripe: jest.fn().mockResolvedValue({
      elements: jest.fn(),
      createToken: jest.fn(),
      createSource: jest.fn(),
      createPaymentMethod: jest.fn(),
      confirmCardPayment: jest.fn(),
    }),
}));
  
jest.mock('@stripe/react-stripe-js', () => {
    const Elements = ({ children }: { children: React.ReactNode }) => <div>{children}</div>;
    const useStripe = jest.fn().mockReturnValue({});
    const useElements = jest.fn().mockReturnValue({});

    return { Elements, useStripe, useElements };
});

jest.mock('react-router-dom', () => ({
    useNavigate: jest.fn(),
}));

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

const MOCK_ADDRESS:AddressRequest = {
    street: "newStreet",
    city: "newCity",
    state: "newState",
    postalCode: "newPostalCode",
    country: "newCountry"
};

const MOCK_DELIVERY:DeliverMethod = {
    uuid: 'deliverTestUUID',
    name: 'deliver1',
    price: 14.99
}

jest.mock('../../Items/BasketItemList', () => ({
    BasketItemsList: ({items}: {items: BasketItem[]}) => (<>
        {items.map((item) => <div key={item.uuid}>BASKET_PRODUCT_{item.uuid}</div>)}
    </>)
}));

jest.mock('../../Address/AddressForm', () => ({
    ...jest.requireActual('../../Address/AddressForm'),
    AddressForm: (props: AddressFormProps) => (<>
        <div>{JSON.stringify(props.address)}</div>
        <button onClick={() => props.setAddress(MOCK_ADDRESS)}>SET NEW ADDRESS</button>
        <div>LABELS: {props.labelCity}, {props.labelCountry}, {props.labelPostalCode}, {props.labelState}, {props.labelStreet}</div>
    </>)
}));

jest.mock('../../Delivery/DeliveryMethods', () => ({
    ...jest.requireActual('../../Delivery/DeliveryMethods'),
    DeliveryMethods: (props: DeliveryMethodsProps) => (<>
        <div>LABEL: {props.label}</div>
        <div>DELIVERY ID: {props.selectedDeliverId}</div>
        {props.deliverMethods.map((method) => <>
            <div>{JSON.stringify(method)}</div>
        </>)}
        <button onClick={() => props.onChange(MOCK_DELIVERY.uuid)}>SELECT DELIVERY METHOD</button>
    </>)
}))

jest.mock('../OrderSummary/OrderSummary', () => ({
    ...jest.requireActual('../OrderSummary/OrderSummary'),
    OrderSummary: (props: OrderSummaryProps) => (<>
        <div>TOTAL LABEL:{props.totalLabel}</div>
        <div>TOTAL PRICE:{props.totalPrice}</div>
        <div>SUMMARY LABEL:{props.summaryLabel}</div>
    </>)
}));

jest.mock('../Payment/PaymentForm', () => ({
    ...jest.requireActual('../Payment/PaymentForm'),
    PaymentForm: (props: PaymentFormProps) => (<>
        <div>CLIENT SECRET: {props.clientSecret}</div>
        <div>PAY LABEL: {props.payLabel}</div>
        <button onClick={() => props.onPaymentSuccess()}>PAYMENT SUCCESS</button>
        <button onClick={() => props.onError('TEST MSG')}>PAYMENT ERROR</button>
    </>)
}))

const MOCK_PRODUCT_BASKET_UUID = 'productUUID';

describe('Order', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        mockedAxios.get.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            if(processedUrl === 'order/deliver'){
                return Promise.resolve({
                    data: [MOCK_DELIVERY]
                })
            }
            if(processedUrl === 'basket') {
                return Promise.resolve({
                    data: {
                        summaryPrice: 120,
                        basketId: 'testId',
                        basketProducts: [{
                            uuid: MOCK_PRODUCT_BASKET_UUID,
                            productId: 'productId',
                            name: 'productName',
                            quantity: 1,
                            imageUrl: '',
                            price: 14,
                            summaryPrice: 14,
                            isActive: true
                        }]
                    }
                })
            }
            return Promise.reject(new Error('newtork failure'));
        })
    });
    it('Should have no a11y violations', async() => {
        const {container, findByText, getByText} = render(<Order />);
        expect(getByText('order.loading'));
        expect(await axe(container)).toHaveNoViolations();
        expect(await findByText('order.header'));
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
    });

    it('Should be able to handle the failure of the GET queries', async() => {
        mockedAxios.get.mockImplementation(() => {
            return Promise.reject(new Error('network failure'));
        });
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText} = render(<Order />);
        expect(await findByText('Failed to fetch data'));
    })

    it('Should be able to fill out the form, submit payment and finalize the payment with success', async() => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {
                clientSecret: 'testClientSecret',
                orderId: 'testOrderId'
            }
        });
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SET NEW ADDRESS') as HTMLButtonElement);
        fireEvent.click(getByText('SELECT DELIVERY METHOD') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('CLIENT SECRET: testClientSecret'));
        fireEvent.click(getByText('PAYMENT SUCCESS') as HTMLButtonElement);
        expect(mockedNavigate).toHaveBeenCalled()
    });

    it('Should be able to fill out the form, submit payment and handle the finalization error', async() => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {
                clientSecret: 'testClientSecret',
                orderId: 'testOrderId'
            }
        });
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SET NEW ADDRESS') as HTMLButtonElement);
        fireEvent.click(getByText('SELECT DELIVERY METHOD') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('CLIENT SECRET: testClientSecret'));
        fireEvent.click(getByText('PAYMENT ERROR') as HTMLButtonElement);
        expect(mockedNavigate).not.toHaveBeenCalled()
        expect(getByText('TEST MSG'));
    });

    it('Should be able to fill out the form and handle the payment network error', async() => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SET NEW ADDRESS') as HTMLButtonElement);
        fireEvent.click(getByText('SELECT DELIVERY METHOD') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('Failed to create order'));
    });

    it('Should not be able to submit the payment without the address form filled', async() => {
        mockedAxios.post.mockResolvedValueOnce({
            data: {
                clientSecret: 'testClientSecret',
                orderId: 'testOrderId'
            }
        });
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SELECT DELIVERY METHOD') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('Please fill in all address fields'));
    });

    it('Should not be able to submit the payment without the delivery method given', async() => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SET NEW ADDRESS') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('Please select a delivery method'));
    });

    it('Should not be able to submit the payment without the proper basketId given', async() => {
        mockedAxios.post.mockRejectedValueOnce(new Error('network failure'));

        mockedAxios.get.mockImplementation((url) => {
            const processedUrl = url.split('http://localhost:3001/')[1];
            console.log(url,processedUrl);
            if(processedUrl === 'order/deliver'){
                return Promise.resolve({
                    data: [MOCK_DELIVERY]
                })
            }
            if(processedUrl === 'basket') {
                return Promise.resolve({
                    data: {
                        summaryPrice: 120,
                        basketId: '',
                        basketProducts: [{
                            uuid: MOCK_PRODUCT_BASKET_UUID,
                            productId: 'productId',
                            name: 'productName',
                            quantity: 1,
                            imageUrl: '',
                            price: 14,
                            summaryPrice: 14,
                            isActive: true
                        }]
                    }
                })
            }
            return Promise.reject(new Error('newtork failure'));
        })
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);

        const {findByText, getByText} = render(<Order />);
        expect(await findByText(`BASKET_PRODUCT_${MOCK_PRODUCT_BASKET_UUID}`));
        fireEvent.click(getByText('SET NEW ADDRESS') as HTMLButtonElement);
        fireEvent.click(getByText('SELECT DELIVERY METHOD') as HTMLButtonElement);
        expect(getByText('order.placeOrder'));
        fireEvent.click(getByText('order.placeOrder') as HTMLButtonElement);
        expect(await findByText('No basket found'));
    });

});