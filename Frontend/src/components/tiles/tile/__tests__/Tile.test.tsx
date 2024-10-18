import { screen, render } from '@testing-library/react';
import { axe, toHaveNoViolations } from 'jest-axe';
import { Tile } from '../Tile';

expect.extend(toHaveNoViolations);

describe('Product tile', () => {
    it('Should have no a11y violations', async () => {
        const {container} = render(<Tile 
            id={'mockId'} 
            title={'mock title'} 
            images={[]} 
            price={'124'} 
            averageRating={null} 
            ratingNumber={null} 
        />);

        expect(await axe(container)).toHaveNoViolations();
        expect(screen.getByText('mock title'));
    });

    it('Should display the ratings display in case the proper props provided', () => {
        const {getByText} = render(<Tile 
            id={'mockId'} 
            title={'mock title'} 
            images={[]} 
            price={'124'} 
            averageRating={4.9} 
            ratingNumber={248} 
        />);

        expect(getByText('4.9'));
        expect(getByText('248'));
    });
});