import { axe, toHaveNoViolations } from "jest-axe";
import { render, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { ProductsPager } from "../ProductsPager";

expect.extend(toHaveNoViolations);

const MOCK_CHANGE_PAGE = jest.fn();

describe('Products pager', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container, getByLabelText} = render(<ProductsPager 
            page={1}
            changePage={MOCK_CHANGE_PAGE}
            currentProductsNumber={10}
        />)

        expect(await axe(container)).toHaveNoViolations();

        fireEvent.click(getByLabelText('productsList.leftPager') as HTMLButtonElement);
        expect(MOCK_CHANGE_PAGE).toHaveBeenCalledWith(0);
        
        fireEvent.click(getByLabelText('productsList.rightPager') as HTMLButtonElement);
        expect(MOCK_CHANGE_PAGE).toHaveBeenCalledWith(2);
    });

    it('Should be able to disable the buttons', () => {
        const {getByRole, getByLabelText} = render(<ProductsPager 
            page={0}
            changePage={MOCK_CHANGE_PAGE}
            currentProductsNumber={9}
        />)

        expect(getByRole('button', {name: 'productsList.leftPager'}) as HTMLButtonElement).toBeDisabled();
        expect(getByLabelText('productsList.rightPager') as HTMLButtonElement).toBeDisabled();
    })
});