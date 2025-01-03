import { render } from "@testing-library/react";
import OrderHistory from "../OrderHistory"
import { axe, toHaveNoViolations } from "jest-axe";
import axios from "axios";
import { ItemResponse } from "../../Order.types";

expect.extend(toHaveNoViolations);

jest.mock('axios');

jest.mock('../../OrderHistoryTableRow/OrderHistoryTableRow', () => ({
    OrderHistoryTableRow: ({bgColor, item}: {bgColor: string;item: ItemResponse}) => (<div>TABLE ROW {item.uuid}-{bgColor}</div>)
}));

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Order History', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: [{
                orderId: 'testOrderId',
                status: 'TEST_STATUS',
                items: [{
                    uuid: "testUUID",
                    name: "testName",
                    imageUrl: "",
                    quantity: 1,
                    priceUnit: 12,
                    priceSummary: 12
                }],
                summaryPrice: 12
            },{
                orderId: 'testOrderId2',
                status: 'TEST_STATUS',
                items: [{
                    uuid: "testUUID2",
                    name: "testName",
                    imageUrl: "",
                    quantity: 1,
                    priceUnit: 12,
                    priceSummary: 12
                }],
                summaryPrice: 12
            },]
        });
        const {container, getByText, findByText} = render(<OrderHistory />);
        expect(getByText('orderHistory.loading'));
        expect(await findByText('orderHistory.title'));
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should display "No Orders" in case no orders present', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: []
        });

        const {findByText} = render(<OrderHistory />);
        expect(await findByText('orderHistory.noOrders'));
    });

    it('Should handle the network failuers', async() => {
        mockedAxios.get.mockRejectedValueOnce(new Error('network failure'));

        const {findByText} = render(<OrderHistory />);
        expect(await findByText('orderHistory.loadingError'));
    });
})