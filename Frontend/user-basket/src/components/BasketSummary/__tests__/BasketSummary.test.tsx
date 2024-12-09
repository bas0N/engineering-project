import { axe, toHaveNoViolations } from "jest-axe";
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BasketSummary } from "../BasketSummary";

expect.extend(toHaveNoViolations);

const MOCK_ORDER_VALUE = 800;

describe('Basket Summary', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<BasketSummary orderValue={MOCK_ORDER_VALUE} />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('renders the component correctly', () => {
        render(<BasketSummary orderValue={MOCK_ORDER_VALUE} />);
    
        expect(screen.getByRole('heading', { level: 2 })).toHaveTextContent('basket.summary.title');
    
        const orderLabel = screen.getByText('basket.summary.order');
        expect(orderLabel).toBeInTheDocument();
    
        const deliveryLabel = screen.getByText('basket.summary.delivery');
        expect(deliveryLabel).toBeInTheDocument();
    
        const totalLabel = screen.getByText('basket.summary.total');
        expect(totalLabel).toBeInTheDocument();

        const buyButton = screen.getByRole('button', { name: 'basket.summary.buy' });
        expect(buyButton).toBeInTheDocument();
      });
});