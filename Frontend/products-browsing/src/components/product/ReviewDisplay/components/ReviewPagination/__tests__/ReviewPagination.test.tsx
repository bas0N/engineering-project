import { render, fireEvent } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import { ReviewPagination } from "../ReviewPagination";

expect.extend(toHaveNoViolations);

const MOCK_PROCESS_LEFT_PAGE = jest.fn();
const MOCK_PROCESS_RIGHT_PAGE = jest.fn();

describe('Review Pagination', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    })

    it('Should have no a11y violations', async() => {
        const {container, getAllByRole} = render(<ReviewPagination 
            processLeftPage={MOCK_PROCESS_LEFT_PAGE}
            processRightPage={MOCK_PROCESS_RIGHT_PAGE}
            pageNumber={0}
            leftPaginationDisabled={false}
            rightPaginationDisabled={false}
        />);
        expect(await axe(container)).toHaveNoViolations();
        fireEvent.click(getAllByRole('button')[0] as HTMLButtonElement);
        expect(MOCK_PROCESS_LEFT_PAGE).toHaveBeenCalled();
        fireEvent.click(getAllByRole('button')[1] as HTMLButtonElement);
        expect(MOCK_PROCESS_LEFT_PAGE).toHaveBeenCalled();
    });
})