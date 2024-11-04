import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Navbar } from '../Navbar';
import { axe, toHaveNoViolations } from 'jest-axe';

expect.extend(toHaveNoViolations);

jest.mock('../Search/Search.tsx', () => ({
    Search: () => <div data-testid="search-component">Search Component</div>,
}));

describe('Navbar', () => {

    it('has no a11y violations', async() => {
        const {container} = render(<Navbar />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('renders without crashing', () => {
        render(<Navbar />);
        
        const navbarContainer = screen.getByRole('navigation');
        expect(navbarContainer).toBeInTheDocument();
    });

    it('renders the Search component', () => {
        render(<Navbar />);
        
        const searchComponent = screen.getByTestId('search-component');
        expect(searchComponent).toBeInTheDocument();
    });

    it('renders the basket button with correct icon and badge', () => {
        render(<Navbar />);
        
        const basketButton = screen.getByRole('link');
        expect(basketButton).toBeInTheDocument();
        
        const icon = screen.getByRole('button');
        expect(icon).toBeInTheDocument();

        const badge = screen.getByText('0');
        expect(badge).toBeInTheDocument();
    });

    it('basket button navigates to /basket', () => {
        render(<Navbar />);
        
        const basketButtonLink = screen.getByRole('link');
        expect(basketButtonLink).toHaveAttribute('href', '/basket');
    });
});