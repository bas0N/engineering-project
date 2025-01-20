import { render, fireEvent } from "@testing-library/react"
import axios from 'axios';
import { axe, toHaveNoViolations } from 'jest-axe'
import { Search } from "../Search"
import { LastItemsProps } from "../lastItems/LastItems";
import { useNavigate } from "react-router-dom";

expect.extend(toHaveNoViolations);

jest.mock('axios');

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"), 
    useNavigate: jest.fn()
}));

jest.mock('../lastItems/LastItems', () => ({
    ...jest.requireActual('../lastItems/LastItems'),
    LastItems: (props: LastItemsProps) => (<>
        {props.items.map((elem) => <div>{JSON.stringify(elem)}</div>)}
        <button onClick={() => props.closeLastItems()}>CLOSE ITEMS</button>
    </>)
}))

const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Search', () => {

    beforeEach(() => {
        jest.clearAllMocks();
        mockedAxios.get.mockResolvedValue({
            data: {
                content: [{
                    title: 'jest'
                }]
            }
        })
    })

    it('Should have no a11y violations', async() => {
        const {container} = render(<Search />);
        expect(await axe(container)).toHaveNoViolations();
    });

    it('Should be able to call the backend', async() => {
        const {findByPlaceholderText, findByText} = render(<Search />);
        const input = await findByPlaceholderText('searchBox.placeholder') as HTMLInputElement;
        expect(input);
        fireEvent.change(input, {target: {value: 'jest'}});

        expect(mockedAxios.get).toHaveBeenCalled();

        expect(await findByText(JSON.stringify({
            title: 'jest'
        })));
        fireEvent.change(input, {target: {value: ''}});
        expect(mockedAxios.get).toHaveBeenCalledTimes(3);
    });

    it('Should be able to go to the products browsing', async() => {
        const mockedNavigate = jest.fn();
        (useNavigate as jest.Mock).mockReturnValue(mockedNavigate);
        const {findByPlaceholderText, findByText, findByLabelText} = render(<Search />);
        const input = await findByPlaceholderText('searchBox.placeholder') as HTMLInputElement;
        expect(input);
        fireEvent.change(input, {target: {value: 'jest'}});

        expect(await findByText(JSON.stringify({
            title: 'jest'
        })));

        const searchButton = await findByLabelText('searchBox.searchButton') as HTMLButtonElement;

        expect(searchButton);
        fireEvent.click(searchButton);
        expect(mockedNavigate).toHaveBeenCalled();
    });

    it('Should be able to handle the case where no items are matching', async() => {
        const {findByPlaceholderText, findByText} = render(<Search />);
        const input = await findByPlaceholderText('searchBox.placeholder') as HTMLInputElement;
        expect(input);
        fireEvent.change(input, {target: {value: 'brak'}});

        expect(mockedAxios.get).toHaveBeenCalled();

        expect(await findByText('searchBox.noItems'));
    });
})