import { fireEvent, render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Navbar } from '../Navbar';
import { axe, toHaveNoViolations } from 'jest-axe';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

expect.extend(toHaveNoViolations);

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"), 
    useNavigate: jest.fn()
}));

jest.mock('../Search/Search.tsx', () => ({
    Search: () => <div data-testid="search-component">Search Component</div>,
}));

jest.mock('axios');

jest.mock('react-i18next', () => ({
    ...jest.requireActual('react-i18next'),
    useTranslation: () => {
        return {
            t: (path: string) => path,
            i18n: {
                options: {
                    supportedLngs: ['en','pl'],
                },
                language: 'en',
                changeLanguage: jest.fn()
            }
        }
    }
}))

const mockedAxios = axios as jest.Mocked<typeof axios>;

const mockedNavigate = jest.fn();

describe('Navbar', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                basketProducts: [{
                    quantity: 14
                }]
            }
        })
    });

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

    it('renders the basket button with correct icon and badge', async() => {
        const {findByText} = render(<Navbar />);
        
        const basketButton = await findByText('14');
        expect(basketButton).toBeInTheDocument();
    });

    it('Should be able to handle logging out', async() => {
        const {findByText} = render(<Navbar />);
        
        const logoutButton = await findByText('navbar.logoutButton');
        fireEvent.click(logoutButton as HTMLButtonElement);
        expect(mockedNavigate).toHaveBeenCalled();
        expect(await findByText('navbar.signInButton'));
    });

    it('Should be able to handle the crash of the backend loading', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                basketProducts: [{
                    quantity: 14
                }]
            }
        });
        const {findByText} = render(<Navbar />);
        expect(await findByText('0'));
    });

    it('Should be able to change the language', async() => {
        mockedAxios.get.mockResolvedValueOnce({
            data: {
                basketProducts: [{
                    quantity: 14
                }]
            }
        });
        const {findByLabelText, findByText} = render(<Navbar />);
        const languageDropdown = await findByLabelText('navbar.languageDropdown');
        expect(languageDropdown);
        fireEvent.click(languageDropdown as HTMLButtonElement);
        fireEvent.click(await findByText('pl') as HTMLOptionElement);
    });
});