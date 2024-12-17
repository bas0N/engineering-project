import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';

expect.extend(toHaveNoViolations);
import { OrderSummary } from "../OrderSummary"

const MOCK_TOTAL_PRICE = 0;
const MOCK_SUMMARY_LABEL = 'summaryLabel';
const MOCK_TOTAL_LABEL = 'totalLabel';

describe('Order summary', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<OrderSummary 
            totalPrice={MOCK_TOTAL_PRICE} 
            summaryLabel={MOCK_SUMMARY_LABEL} 
            totalLabel={MOCK_TOTAL_LABEL} 
        />);
        expect(await axe(container)).toHaveNoViolations();
    })
})