import { axe, toHaveNoViolations } from "jest-axe";
import { render } from '@testing-library/react';
import { Finalization } from "../Finalization"

expect.extend(toHaveNoViolations);

jest.mock('../../details/Details', () => ({
    processDetailName: (detail: string) => detail
}))

describe('Finalization', () => {
    it('Should have no a11y violations', async() => {
        const {container} = render(<Finalization
            title='testTitle'
            description='testDescription'
            details={{'LOREM': 'IPSUM'}}
            features={['cool product']}
            categories={['product']}    
        />);
        expect(await axe(container)).toHaveNoViolations();
    })
})